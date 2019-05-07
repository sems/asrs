package View.ASR;

import Logic.Order;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;

public class HMIController {
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;
    @FXML
    private ListView allOrdersToPickListView;
    @FXML
    private ListView allOrdersListView;

    private ArrayList<Order> orders;

    public void Initialize(ArrayList<Order> orders) {

        this.orders = orders;

        Platform.runLater(() -> {
            for (var order : orders) {
            }
        });
    }

    @FXML
    protected void handleAddOrderAction(ActionEvent event) {
    }

    @FXML
    protected void handleRemoveOrderAction(ActionEvent event) {
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
