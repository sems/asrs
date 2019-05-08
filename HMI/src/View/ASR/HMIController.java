package View.ASR;

import Logic.Order;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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

    private ObservableList<Order> allOrdersObservableList;
    private ObservableList<Order> pickedOrdersObservableList;

    public void Initialize(ArrayList<Order> orders) {
        this.allOrdersObservableList = FXCollections.observableArrayList();
        this.pickedOrdersObservableList = FXCollections.observableArrayList();

        Platform.runLater(() -> {
            this.allOrdersObservableList.addAll(orders);
        });

        TableColumn orderIdCol = new TableColumn("Order ID");
        orderIdCol.setMinWidth(100);
        orderIdCol.setCellValueFactory(
                new PropertyValueFactory<Order, String>("id"));

        TableColumn productIDCol = new TableColumn("Product ID's");
        productIDCol.setMinWidth(100);
        productIDCol.setCellValueFactory(
                new PropertyValueFactory<Order, String>("buyer"));

        TableColumn buyerCol = new TableColumn("Koper");
        buyerCol.setMinWidth(200);
        buyerCol.setCellValueFactory(new PropertyValueFactory<Order, String>("buyer"));

        allOrdersTableView.setItems(allOrdersObservableList);
        allOrdersTableView.getColumns().addAll(orderIdCol, productIDCol, buyerCol);

        TableColumn orderIdCol1 = new TableColumn("Order ID");
        orderIdCol1.setMinWidth(100);
        orderIdCol1.setCellValueFactory(
                new PropertyValueFactory<Order, String>("id"));

        TableColumn productIDCol1 = new TableColumn("Product ID's");
        productIDCol1.setMinWidth(100);
        productIDCol1.setCellValueFactory(
                new PropertyValueFactory<Order, String>("buyer"));

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
            pickedOrdersTableView.getItems().add(new Order(selectedItem.getId(), selectedItem.getBuyer(), selectedItem.getAddr(), selectedItem.getOrderData()));
            this.allOrdersObservableList.removeIf(x -> x.getId() == selectedItem.getId());
        });
    }

    @FXML
    protected void handleRemoveOrderAction(ActionEvent event) {
        Order selectedItem = (Order)this.pickedOrdersTableView.getSelectionModel().getSelectedItem();
        Platform.runLater(() -> {
            allOrdersTableView.getItems().add(new Order(selectedItem.getId(), selectedItem.getBuyer(), selectedItem.getAddr(), selectedItem.getOrderData()));
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
