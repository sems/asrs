package View.ASR;

import Data.Database.DataServer;
import Logic.Communication.ASRCommunication;
import Logic.Communication.ASRListener;
import Logic.Communication.ErrorCode;
import Logic.Location;
import Logic.Order;
import Logic.StorageItem;
import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ASRController implements ASRListener {
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
    private Rectangle box1;
    @FXML
    private Rectangle box2;
    @FXML
    private TableView<Order> waitingOrdersTableView;
    @FXML
    private TableView<Order> packedOrdersTableView;

    // data bindings to view tables
    private ObservableList<Order> allOrdersObservableList;
    private ObservableList<Order> waitingOrdersObservableList;
    private ObservableList<Order> packedOrdersObservableList;

    private ASRCommunication asrCommunication;
    private LocationAdvancer locationAdvancer;
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

        SerialPort port = SerialPort.getCommPorts()[0];
        asrCommunication = new ASRCommunication(port);
        locationAdvancer = new LocationAdvancer(ordersToPickObservableList, asrCommunication);
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
            circle.setStyle("black");
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
                Cell cell = new Cell(column, row);
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

    /**
     * This will map a location with the grid coords X and Y to the correct pixel location on the screen.
     * @param location
     * @return
     */
    private Location mapToUIDimensions(Location location) {
        return new Location(((location.getX() * CELL_SIZE)) + CELL_SIZE / 2, (GRID_HEIGHT  - (location.getY() * CELL_SIZE)) - CELL_SIZE / 2);
    }

    @Override
    public void onPositionResponseReceived(ErrorCode ec) {
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
}

class LocationAdvancer {
    private ObservableList<Order> orders;
    private ASRCommunication asrCommunication;
    private ArrayList<Location> currentRoute;

    private int currentOrderPickedIndex = 0;
    private int currentStorageItemPickedIndex = 0;

    public LocationAdvancer(ObservableList<Order> orders, ASRCommunication asrCommunication) {
        this.orders = orders;
        this.asrCommunication = asrCommunication;

        if (orders.size() > 0) {
            currentRoute = mapLocation(orders.get(0).getRoute());
        }else {
            System.out.println("There are no orders for picking.");
        }
    }

    /**
     * Advances to the next storage item, it will advance to the next order if all storage items of an order are picked .
     *
     * Returns true in case there are more storage items left.
     * Returns false in case there are no more orders and storage items.
      */
    public boolean advanceToNextStorageItem() {
        if (currentRoute.size() < currentStorageItemPickedIndex + 1) {
            currentStorageItemPickedIndex += 1;
            var nextLocation = currentRoute.get(currentStorageItemPickedIndex);
            asrCommunication.gotoPos(nextLocation.getX(), nextLocation.getY());
            return true;
        }else {
            System.out.println("No more elements in route");

            if (!advanceToNextOrder()) {
                // if there are no more orders return
                return false;
            } else {
                // advance to first storage item of new order
               return  advanceToNextStorageItem();
            }
        }
    }

    /**
     * Get the count of items in the current route
     * @return
     */
    public int getCurrentRouteItemsNumber() {
        return currentRoute.size();
    }

    public int getCurrentRoutePickedItem() {
        return currentStorageItemPickedIndex;
    }

    /**
     * Get the current route locations
     * @return
     */
    public ArrayList<Location> getCurrentRouteLocations() {
        return currentRoute;
    }

    /**
     * Advance to the next order if all storage items are picked of the current order
     */
    private boolean advanceToNextOrder() {
        if (orders.size() < currentOrderPickedIndex + 1) {
            currentOrderPickedIndex += 1;
            currentRoute = mapLocation(orders.get(currentOrderPickedIndex).getRoute());
            return true;
        } else {
            // no more orders
            return false;
        }
    }

    private ArrayList<Location> mapLocation(ArrayList<StorageItem> orders) {
        return orders.stream().map(x -> x.getLocation()).collect(Collectors.toCollection(ArrayList::new));
    }
}
