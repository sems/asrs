package View.ASR;

import Data.Database.DataServer;
import Logic.Location;
import Logic.Order;
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

    private ObservableList<Order> allOrdersObservableList;
    private ObservableList<Order> pickedOrdersObservableList;

    private static final int  CELL_SIZE = 160;

    /**
     * Initialize the screen by passing in the orders that will be displayed.
     * @param orders
     */
    public void Initialize(ArrayList<Order> orders) {
        this.allOrdersObservableList = FXCollections.observableArrayList();
        this.pickedOrdersObservableList = FXCollections.observableArrayList();

        Platform.runLater(() -> {
            this.allOrdersObservableList.addAll(orders);
        });

        InitializeGrid();
        InitializeTables();

        displayStorageItem(61);
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
            circle.setRadius(4);
            circle.setStyle("black");
            circle.setLayoutX((itemLocation.getX()));
            circle.setLayoutY((itemLocation.getY()));

            // draw the line from current point to next point
            if(i + 1 < route.size()) {
                var nextItemLocation = mapToUIDimensions(route.get(i + 1).getLocation());

                Line line = new Line(circle.getLayoutX(), circle.getLayoutY(), nextItemLocation.getX(), nextItemLocation.getY());
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
        int rows = 5;
        int columns = 5;
        double width = 800;
        double height = 800;

        // create grid and apply style
        Grid grid = new Grid( columns, rows, width, height);
        grid.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        // fill grid with cells
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
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
    }

    @FXML
    protected void handleAddOrderAction(ActionEvent event) {
        Order selectedItem = this.allOrdersTableView.getSelectionModel().getSelectedItem();
        Platform.runLater(() -> {
            pickedOrdersTableView.getItems().add(selectedItem);
            this.allOrdersObservableList.removeIf(x -> x.getId() == selectedItem.getId());
        });
    }

    @FXML
    protected void handleRemoveOrderAction(ActionEvent event) {
        Order selectedItem = (Order)this.pickedOrdersTableView.getSelectionModel().getSelectedItem();
        Platform.runLater(() -> {
            allOrdersTableView.getItems().add(selectedItem);
            this.pickedOrdersObservableList.removeIf(x -> x.getId() == selectedItem.getId());
        });
    }

    @FXML
    protected void handlePickOrderAction(ActionEvent event) {

    }

    public void updateOrderItemsPickedStatus(int item, int maxItems) {
         double progressBarValue = (double)(maxItems / 100 * item) / 100;
         progressBar.setProgress(progressBarValue);

        Platform.runLater(() -> progressLabel.setText("Product Items Opgehaald "+ item + " van de "+maxItems));
    }

    /**
     * This will map a location with the grid coords X and Y to the correct pixel location on the screen.
     * @param location
     * @return
     */
    private Location mapToUIDimensions(Location location) {
        return new Location((location.getX() * CELL_SIZE) + CELL_SIZE / 2, (location.getY() * CELL_SIZE) + CELL_SIZE / 2);
    }
}
