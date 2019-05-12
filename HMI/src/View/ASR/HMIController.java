package View.ASR;

import Data.Database.DataServer;
import Logic.Order;
import Logic.StorageItem;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;

public class HMIController {
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

    public void Initialize(ArrayList<Order> orders) {
        this.allOrdersObservableList = FXCollections.observableArrayList();
        this.pickedOrdersObservableList = FXCollections.observableArrayList();

        Platform.runLater(() -> {
            this.allOrdersObservableList.addAll(orders);
        });

        InitializeGrid();
        InitializeTables();

        DataServer ds = new DataServer();

        final int cellSize = 160;

        for (StorageItem item : ds.getStorageItems()) {
            var itemLocation = item.getLocation();
            System.out.println(itemLocation.X  + "" + itemLocation.Y);

            Circle circle = new Circle();
            circle.setRadius(4);
            circle.setStyle("black");
            circle.setLayoutX((itemLocation.X * cellSize) + cellSize / 2);
            circle.setLayoutY((itemLocation.Y * cellSize) + cellSize / 2);

            gridPane.getChildren().add(circle);
        }
    }

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
        Order selectedItem = (Order)this.allOrdersTableView.getSelectionModel().getSelectedItem();
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
}
