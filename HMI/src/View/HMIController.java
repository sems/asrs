package View;

import Logic.BINR.BINR;
import Logic.Communication.*;
import Logic.Location;
import Logic.Order;
import View.ASR.Cell;
import View.ASR.Grid;
import View.ASR.LocationAdvancer;
import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;

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
    private GridPane asrGrid;
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

        SerialPort asrPort = SerialPort.getCommPorts()[0];
        SerialPort binrPort = SerialPort.getCommPorts()[1];
        asrCommunication = new ASRCommunication(asrPort);
        asrCommunication.subscribeToResponses(this);
        binrCommunication= new BinrCommunication(binrPort);
        binrCommunication.subscribeToResponses(this);
        locationAdvancer = new LocationAdvancer(ordersToPickObservableList, asrCommunication);
        binr = new BINR((int)leftBoxPane.getHeight(), (int)rightBoxPane.getHeight());
    }

    /**
     *  Display the storage items of the given order id to the screen.
     *  A line will connect all storage items based on the shortest path.
     */
    private void displayLocations(ArrayList<Location> locations) {

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
                gridPane.getChildren().add(line);
            }

            gridPane.getChildren().add(circle);
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
        Order selectedItem = (Order)this.pickedOrdersTableView.getSelectionModel().getSelectedItem();
        Platform.runLater(() -> {
            allOrdersTableView.getItems().add(selectedItem);
            waitingOrdersObservableList.removeIf(x -> x.getId() == selectedItem.getId());
            this.ordersToPickObservableList.removeIf(x -> x.getId() == selectedItem.getId());
        });
    }

    @FXML
    protected void handlePickOrderAction(ActionEvent event) {
        if(locationAdvancer.advanceToNextStorageItem()) {

        } else {

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

        SerialPort newPortAsr = SerialPort.getCommPorts()[portAsr];
        newPortAsr.setBaudRate(115200);

        try{
            asrCommunication.close();
            asrCommunication = new ASRCommunication(newPortAsr);
            asrCommunication.subscribeToResponses(this);
        }
        catch (Exception e){
            onLog(e.toString());
        }
    }


    /** ======== robot events ========= **/

    @Override
    public void onPositionResponseReceived(ErrorCode errorCode) {
        var numberOfItems = locationAdvancer.getCurrentRouteItemsNumber();
        var currentItem = locationAdvancer.getCurrentRoutePickedItem();

        double progressBarValue = (double)(100 / numberOfItems * currentItem) / 100;
        progressBar.setProgress(progressBarValue);
        
        if (locationAdvancer.advanceToNextStorageItem()) {
            var currentRouteLocations = locationAdvancer.getCurrentRouteLocations();
            displayLocations(currentRouteLocations);
        }else {
            System.out.println("no more orders");
        }

        System.out.println(progressBarValue);

        int count = 1;
        for (var node: gridPane.getChildren()) {
            if (node instanceof Line) {
                count += 1;
                if (currentItem == count) {
                    ((Line) node).setStroke(Color.RED);
                }
            }
        }

        Platform.runLater(() -> progressLabel.setText("Product Items Opgehaald "+ currentItem + " van de "+ numberOfItems));
    }

    @Override
    public void onGetPositionReceived(byte x, byte y) { }

    @Override
    public void onLog(String log) {
        Platform.runLater(() -> logTextBox.appendText(log + "\n"));
    }

    @Override
    public void onUnloadResponseReceived() {
        // todo: advance to next unload
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
    public void responseReceived() {

    }
}

