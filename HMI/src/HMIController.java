import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class HMIController {
    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

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

        progressLabel.setText("Product Items Opgehaald "+ item + " van de "+maxItems);
    }
}
