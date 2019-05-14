package View.ASR;

import Data.Database.DataServer;
import Logic.Location;
import Logic.Order;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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

public class ASRController {
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

    @FXML
    private Rectangle box1;
    @FXML
    private Rectangle box2;
    @FXML
    private TableView<Order> waitingOrdersTableView;
    @FXML
    private TableView<Order> packedOrdersTableView;


    private ObservableList<Order> allOrdersObservableList;
    private ObservableList<Order> pickedOrdersObservableList;
    private ObservableList<Order> waitingOrdersObservableList;
    private ObservableList<Order> packedOrdersObservableList;

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
        this.pickedOrdersObservableList = FXCollections.observableArrayList();
        this.waitingOrdersObservableList = FXCollections.observableArrayList();
        this.packedOrdersObservableList = FXCollections.observableArrayList();


        Platform.runLater(() -> {
            this.allOrdersObservableList.addAll(orders);
        });

        InitializeGrid();
        InitializeTables();

//        displayStorageItem(61);
    }

    /**
     *  Display the storage items of the given order id to the screen.
     *  A line will connect all storage items based on the shortest path.
      * @param orderId
     */
    private void displayStorageItem(int orderId) {
        DataServer ds = new DataServer();

        var route = ds.getOrder(orderId).getRoute();

        for (int i = 0; i < route.size(); i++) {
            var current = route.get(i);

            var itemLocation = mapToUIDimensions(current.getLocation());

            Circle circle = new Circle();
            circle.setRadius(20);
            circle.setStyle("black");
            circle.setLayoutX((itemLocation.getX()));
            circle.setLayoutY((itemLocation.getY()));

            // draw the line from current point to next point
            if(i + 1 < route.size()) {
                var nextItemLocation = mapToUIDimensions(route.get(i + 1).getLocation());

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

        pickedOrdersTableView.setItems(pickedOrdersObservableList);
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
            this.pickedOrdersObservableList.removeIf(x -> x.getId() == selectedItem.getId());
            waitingOrdersObservableList.removeIf(x -> x.getId() == selectedItem.getId());
        });
    }

    @FXML
    protected void handlePickOrderAction(ActionEvent event) {
        for (Order order: pickedOrdersObservableList) {
            displayStorageItem(order.getId());

           // todo:
            // send command to robot
            // wait for ack
            // update status
            // continue until order picked
            // repeat
        }
    }

    public void updateOrderItemsPickedStatus(int item, int maxItems) {
         double progressBarValue = (double)(100/ maxItems * item) / 100;
         progressBar.setProgress(progressBarValue);

         System.out.println(progressBarValue);
         int count = 1;
         for (var node: gridPane.getChildren()) {
            if (node instanceof Line) {
                count += 1;
                if (item == count) {
                    ((Line) node).setStroke(Color.RED);
                }
            }
         }

        Platform.runLater(() -> progressLabel.setText("Product Items Opgehaald "+ item + " van de "+ maxItems));
    }

    /**
     * This will map a location with the grid coords X and Y to the correct pixel location on the screen.
     * @param location
     * @return
     */
    private Location mapToUIDimensions(Location location) {
        return new Location(((location.getX() * CELL_SIZE)) + CELL_SIZE / 2, (GRID_HEIGHT  - (location.getY() * CELL_SIZE)) - CELL_SIZE / 2);
    }
}
