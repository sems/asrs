package View;

import Logic.BINR.BINR;
import Logic.Communication.*;
import Logic.BINR.BoxType;
import Logic.Communication.ASRCommunication;
import Logic.Communication.ASREventListener;
import Logic.Communication.BINREventListener;
import Logic.Communication.ErrorCode;
import Logic.Location;
import Logic.Order;
import Logic.OrderItem;
import View.ASR.Cell;
import View.ASR.Grid;
import View.ASR.LocationAdvanceStatus;
import View.ASR.LocationAdvancer;
import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

enum Progress{
    Picking,
    MovingToBinr,
    Unloading, Idling
}

public class HMIController implements ASREventListener, BINREventListener {
    /**
     * ASR view
     */
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;
    @FXML
    private TableView<Order> allOrdersTableView;
    @FXML
    private TableView<Order> pickedOrdersTableView;
    @FXML
    private Pane gridPane;

    /**
     * BINR view
     */
    @FXML
    private TableView<Order> waitingOrdersTableView;
    @FXML
    private TableView<Order> packedOrdersTableView;

    @FXML
    private AnchorPane leftBoxPane;

    @FXML
    private AnchorPane rightBoxPane;

    /**
     * DEBUG screen
     */
    @FXML
    private Button gotoBtn;
    @FXML
    private Button pickBtn;
    @FXML
    private Button dropBtn;
    @FXML
    private Button stopBtn;
    @FXML
    private Button startBtn;
    @FXML
    private Button getPosBtn;
    @FXML
    private TextField xDebugTextField;
    @FXML
    private TextField yDebugTextField;
    @FXML
    private TextArea logTextBox;

    @FXML
    private TextField asrCommTextfield;
    @FXML
    private TextField binrCommTextfield;

    // data bindings to view tables
    private ObservableList<Order> allOrdersObservableList;
    private ObservableList<Order> waitingOrdersObservableList;
    private ObservableList<Order> packedOrdersObservableList;

    public ASRCommunication asrCommunication;
    public BinrCommunication binrCommunication;
    private LocationAdvancer locationAdvancer;
    private BINR binr;
    private ObservableList<Order> ordersToPickObservableList;

    private static final int  CELL_SIZE = 160;
    private static final int GRID_HEIGHT = 800;
    private static final int GRID_WIDTH = 800;
    private static final int CELLS = 5;

    private ArrayList<Node> gridNodes = new ArrayList<>();
    private Progress processStatus;
    private int unloadedProducts = 0;

    /**
     * Initialize the screen by passing in the orders that will be displayed.
     * @param orders
     */
    public void Initialize(ArrayList<Order> orders) {
        this.allOrdersObservableList = FXCollections.observableArrayList();
        this.waitingOrdersObservableList = FXCollections.observableArrayList();
        this.packedOrdersObservableList = FXCollections.observableArrayList();
        this.ordersToPickObservableList = FXCollections.observableArrayList();

        Platform.runLater(() -> {
            this.allOrdersObservableList.addAll(orders);
        });

        InitializeGrid();
        InitializeTables();
        try{
            SerialPort asrPort = SerialPort.getCommPorts()[0];
            asrCommunication = new ASRCommunication(asrPort);
            asrCommunication.subscribeToResponses(this);
        }catch(Exception e){
            onLog(e.toString());
        }

        try{
            SerialPort binrPort = SerialPort.getCommPorts()[1];
            binrCommunication= new BinrCommunication(binrPort);
            binrCommunication.subscribeToResponses(this);
        }catch(Exception e){
            onLog(e.toString());
        }
        locationAdvancer = new LocationAdvancer(ordersToPickObservableList, asrCommunication);
        binr = new BINR((int)leftBoxPane.getHeight(), (int)rightBoxPane.getHeight());
        processStatus = Progress.Idling;
    }

    /**
     *  Display the storage items of the given order id to the screen.
     *  A line will connect all storage items based on the shortest path.
     */
    private void displayLocations(ArrayList<Location> locations) {
        System.out.println("Drawing locations, locations: " + locations.size());
        for (int i = 0; i < locations.size(); i++) {
            var current = locations.get(i);

            var itemLocation = mapToUIDimensions(current);

            Circle circle = new Circle();
            circle.setRadius(20);
            circle.setFill(Color.BLACK);
            circle.setLayoutX((itemLocation.getX()));
            circle.setLayoutY((itemLocation.getY()));

            // draw the line from current point to next point
            if(i + 1 < locations.size()) {
                var nextItemLocation = mapToUIDimensions(locations.get(i + 1));

                Line line = new Line(circle.getLayoutX(), circle.getLayoutY(), nextItemLocation.getX(), nextItemLocation.getY());
                line.setStrokeWidth(5);
                line.setFill(Color.BLUE);
                line.setStroke(Color.BLACK);
                gridNodes.add(line);
            }

            gridNodes.add(circle);
        }
    }

    /**
     * Draw the grid to the screen.
     */
    private void InitializeGrid() {

        // create grid and apply style
        Grid grid = new Grid(CELLS, CELLS, GRID_WIDTH, GRID_HEIGHT);
        grid.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        // fill grid with cells
        for (int row = 0; row < CELLS; row++) {
            for (int column = 0; column < CELLS; column++) {
                View.ASR.Cell cell = new Cell(column, row);
                grid.add(cell, column, row);
            }
        }

        gridPane.getChildren().add(grid);
    }

    /**
     * Draw the order tables to the screen and bind those to observable lists with order items.
     */
    private void InitializeTables() {
        // Initialize the table with orders
        TableColumn orderIdCol = new TableColumn("Order ID");
        orderIdCol.setMinWidth(100);
        orderIdCol.setCellValueFactory(
                new PropertyValueFactory<Order, String>("id"));

        TableColumn productIDCol = new TableColumn("Order Items");
        productIDCol.setMinWidth(100);
        productIDCol.setCellValueFactory(
                new PropertyValueFactory<Order, String>("orderItemsCount"));

        TableColumn buyerCol = new TableColumn("Koper");
        buyerCol.setMinWidth(200);
        buyerCol.setCellValueFactory(new PropertyValueFactory<Order, String>("buyer"));

        allOrdersTableView.setItems(allOrdersObservableList);
        allOrdersTableView.getColumns().addAll(orderIdCol, productIDCol, buyerCol);

        // Initialize the table with orders to pick
        TableColumn orderIdCol1 = new TableColumn("Order ID");
        orderIdCol1.setMinWidth(100);
        orderIdCol1.setCellValueFactory(
                new PropertyValueFactory<Order, String>("id"));

        TableColumn productIDCol1 = new TableColumn("Order Items");
        productIDCol1.setMinWidth(100);
        productIDCol1.setCellValueFactory(
                new PropertyValueFactory<Order, String>("orderItemsCount"));

        TableColumn buyerCol1 = new TableColumn("Koper");
        buyerCol1.setMinWidth(200);
        buyerCol1.setCellValueFactory(new PropertyValueFactory<Order, String>("buyer"));

        pickedOrdersTableView.setItems(ordersToPickObservableList);
        pickedOrdersTableView.getColumns().addAll(orderIdCol1, productIDCol1, buyerCol1);

        // Initialize the table with products waiting to be packed
        TableColumn orderIdCol2 = new TableColumn("Order ID");
        orderIdCol2.setMinWidth(100);
        orderIdCol2.setCellValueFactory(
                new PropertyValueFactory<Order, String>("id"));

        TableColumn productIDCol2 = new TableColumn("Order Items");
        productIDCol2.setMinWidth(100);
        productIDCol2.setCellValueFactory(
                new PropertyValueFactory<Order, String>("orderItemsCount"));

        TableColumn buyerCol2 = new TableColumn("Koper");
        buyerCol2.setMinWidth(200);
        buyerCol2.setCellValueFactory(new PropertyValueFactory<Order, String>("buyer"));

        waitingOrdersTableView.setItems(waitingOrdersObservableList);
        waitingOrdersTableView.getColumns().addAll(orderIdCol2, productIDCol2, buyerCol2);

        // Initialize the table with products waiting to be packed
        TableColumn orderIdCol3 = new TableColumn("Order ID");
        orderIdCol3.setMinWidth(100);
        orderIdCol3.setCellValueFactory(
                new PropertyValueFactory<Order, String>("id"));

        TableColumn productIDCol3 = new TableColumn("Order Items");
        productIDCol3.setMinWidth(100);
        productIDCol3.setCellValueFactory(
                new PropertyValueFactory<Order, String>("orderItemsCount"));

        TableColumn buyerCol3 = new TableColumn("Koper");
        buyerCol3.setMinWidth(200);
        buyerCol3.setCellValueFactory(new PropertyValueFactory<Order, String>("buyer"));

        packedOrdersTableView.setItems(packedOrdersObservableList);
        packedOrdersTableView.getColumns().addAll(orderIdCol3, productIDCol3, buyerCol3);
    }

    /** ======== button events ========= **/

    @FXML
    protected void handleAddOrderAction(ActionEvent event) {
        Order selectedItem = this.allOrdersTableView.getSelectionModel().getSelectedItem();
        Platform.runLater(() -> {
            pickedOrdersTableView.getItems().add(selectedItem);
            waitingOrdersTableView.getItems().add(selectedItem);
            this.allOrdersObservableList.removeIf(x -> x.getId() == selectedItem.getId());
        });
    }

    @FXML
    protected void handleRemoveOrderAction(ActionEvent event) {
        Order selectedItem = this.pickedOrdersTableView.getSelectionModel().getSelectedItem();
        Platform.runLater(() -> {
            allOrdersTableView.getItems().add(selectedItem);
            waitingOrdersObservableList.removeIf(x -> x.getId() == selectedItem.getId());
            this.ordersToPickObservableList.removeIf(x -> x.getId() == selectedItem.getId());
        });
    }

    @FXML
    protected void handlePickOrderAction(ActionEvent event) {
        // initialize the logic for moving the robot trough the picking process.
        locationAdvancer = new LocationAdvancer(ordersToPickObservableList, asrCommunication);
        gridNodes.clear();

        // calculate the most efficient boxes.
        binr = new BINR((int)leftBoxPane.getHeight(), (int)rightBoxPane.getHeight());

        // advance to the first storage item position
        if(locationAdvancer.advanceToNextStorageItem() == LocationAdvanceStatus.NoNewOrdersToPick) {
            onLog(">>>> Trying to pick order but not orders selected.");
        }else {
            onLog(">>>> Order picking process starting");
            // display the locations of all the products in the current route.
            var currentRouteLocations = locationAdvancer.getCurrentRouteLocations();
            System.out.println("count locations" +currentRouteLocations);
            updateASRView(currentRouteLocations);
            processStatus = Progress.Picking;
        }
    }

    @FXML
    protected void handleGotoPosDebugButton() {
        var x = Integer.parseInt(xDebugTextField.getText());
        var y = Integer.parseInt(yDebugTextField.getText());

        asrCommunication.gotoPos(x, y);
    }

    @FXML
    protected void handlePickDebugButton() {
        asrCommunication.pick();
    }

    @FXML
    protected void handleDropDebugButton() {
        asrCommunication.unload();
    }

    @FXML
    protected void handleStartDebugButton() {
        asrCommunication.start();
    }

    @FXML
    protected void handleStopDebugButton() {
        asrCommunication.stop();
    }

    @FXML
    protected void handleGetPosButton() {
        asrCommunication.getPos();
    }

    @FXML
    protected void handleHomeButton() {
        asrCommunication.home();
    }

    @FXML
    protected void handleLeft() {
        binrCommunication.moveLeft();
    }

    @FXML
    protected void handleRight() {
        binrCommunication.moveRight();
    }
  
    @FXML
    protected void handleSaveCommButton(){
        int portAsr;
        int portBinr;

        try{
            portAsr = Integer.parseInt(asrCommTextfield.getText());
            portBinr = Integer.parseInt(binrCommTextfield.getText());
        }
        catch (Exception e){
            e.printStackTrace();
            onLog(e.toString());

            portAsr = 0;
            portBinr = 1;
        }

        SerialPort newPortAsr;
        SerialPort newPortBinr;

        try{
            asrCommunication.close();
            newPortAsr = SerialPort.getCommPorts()[portAsr];
            newPortAsr.setBaudRate(115200);
            asrCommunication = new ASRCommunication(newPortAsr);
            asrCommunication.subscribeToResponses(this);
        } catch (Exception e){
            e.printStackTrace();
            onLog(e.toString());
        }

        try{
            binrCommunication.close();
            newPortBinr = SerialPort.getCommPorts()[portBinr];
            newPortBinr.setBaudRate(115200);
            binrCommunication = new BinrCommunication(newPortBinr);
            binrCommunication.subscribeToResponses(this);
        } catch (Exception e){
            e.printStackTrace();
            onLog(e.toString());
        }
    }


    /** ======== robot events ========= **/
    @Override
    public void onPositionResponseReceived(ErrorCode errorCode) {
        if(processStatus == Progress.Picking) {
            System.out.println("Picking load");
            asrCommunication.pick();
        }
        else if(processStatus == Progress.MovingToBinr) {
            System.out.println("Moving over to unload position");
            asrCommunication.gotoPos(5,3);
            processStatus = Progress.Unloading;
        }
        else if (processStatus == Progress.Unloading) {
            asrCommunication.unload();
        }
        else {
            System.out.println("Status process: " + processStatus);
        }
    }

    @Override
    public void onGetPositionReceived(byte x, byte y) { }

    @Override
    public void onLog(String log) {
        Platform.runLater(() -> logTextBox.appendText(log + "\n"));
    }

    @Override
    public void onUnloadResponseReceived() {
        if (unloadedProducts < 4) {
            binrCommunication.moveLeft();
            leftBoxPane.getChildren().clear();
            rightBoxPane.getChildren().clear();

            for (var box : binr.getClosedBoxes()) {
                for (var storedItem : box.getStoredItems()) {
                    drawProduct(box.getBoxType(), storedItem);
                }
            }

            unloadedProducts++;
        }else {
            unloadedProducts = 0;
        }
    }

    @Override
    public void onPickResponse() {
        var numberOfItems = locationAdvancer.getCurrentRouteItemsNumber();
        var currentItem = locationAdvancer.getCurrentRoutePickedItem();

        // update progress bar
        double progressBarValue = (double)(100 / numberOfItems * currentItem) / 100;
        progressBar.setProgress(progressBarValue);

        // advance to next storage item
        var status = locationAdvancer.advanceToNextStorageItem();

        updateASRView(locationAdvancer.getCurrentRouteLocations());

        if (status == LocationAdvanceStatus.NewStorageItemPicked) {
            System.out.println(">>>> Advancing to next storage item");
            onLog("Product Items Opgehaald "+ currentItem + " van de "+ numberOfItems);
        }
        else if (status == LocationAdvanceStatus.NoNewOrdersToPick) {
            System.out.println(">>>> There are no more storage items left for picking");

            if (processStatus == Progress.Picking) {
                asrCommunication.gotoPos(5, 4);
                processStatus = Progress.MovingToBinr;
            }

            ordersToPickObservableList.clear();
        }
    }

    /**
     * Update the ASR grid view with the grid, locations, and route.
     */
    private void updateASRView(ArrayList<Location> locations) {
        Platform.runLater(() -> {
            gridNodes.clear();
            gridPane.getChildren().clear();

            InitializeGrid();

            displayLocations(locations);
            drawGridAndRoute();

            gridPane.getChildren().addAll(gridNodes);
            progressLabel.setText("Product Items Opgehaald " + (locationAdvancer.getCurrentRoutePickedItem() - 1) + " van de " + locationAdvancer.getCurrentRouteItemsNumber());
        });
    }

    /**
     * Draw the product in the binr view.
     * @param boxType
     * @param orderItem
     */
    private void drawProduct(BoxType boxType, OrderItem orderItem) {
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(leftBoxPane.getWidth());
        rectangle.setHeight(orderItem.getPrdocutHeight());
        rectangle.setFill(Color.RED);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(4);

        AnchorPane boxPane = null;

        if (boxType== BoxType.Left) {
            boxPane = leftBoxPane;
        }
        if (boxType == BoxType.Right) {
            boxPane = rightBoxPane;
        }

        int maxHeight = boxPane.heightProperty().intValue();

        for (var node: boxPane.getChildren()) {
            if (node instanceof Rectangle) {
                maxHeight -= ((Rectangle)node).getHeight();
            }
        }

        maxHeight = maxHeight - (int)rectangle.getHeight();

        rectangle.setLayoutY(maxHeight);
        boxPane.getChildren().add(rectangle);

        onLog(orderItem.toString() + " height: " + orderItem.getPrdocutHeight());
    }

    /**
     * Draw the grid and color the grid nodes.
     */
    private void drawGridAndRoute() {
        int circleCount = 1;
        int lineCount = 1;

        for (var node : gridNodes) {
            if (node instanceof Circle) {
                circleCount += 1;
            }

            if (node instanceof Line) {
                lineCount += 1;
            }

            if (node instanceof Circle) {
                if (circleCount <= locationAdvancer.getCurrentRoutePickedItem()) {
                    ((Circle) node).setFill(Color.RED);
                }
            }

            if (node instanceof Line) {
                if (lineCount < locationAdvancer.getCurrentRoutePickedItem()) {
                    ((Line) node).setStroke(Color.RED);
                }
            }
        }
    }

    /**
     * This will map a location with the grid coords X and Y to the correct pixel location on the screen.
     * @param location
     * @return
     */
    private Location mapToUIDimensions(Location location) {
        return new Location(((location.getX() * CELL_SIZE)) + CELL_SIZE / 2, (GRID_HEIGHT  - (location.getY() * CELL_SIZE)) - CELL_SIZE / 2);
    }

    @Override
    public void dropResponseReceived() {
        asrCommunication.unload();
    }
}
