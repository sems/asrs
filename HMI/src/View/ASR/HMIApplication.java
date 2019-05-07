package View.ASR;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HMIApplication extends Application {
    FXMLLoader loader = null;

    @Override
    public void start(Stage stage) throws InterruptedException, IOException {
        loader = new FXMLLoader(
                getClass().getResource(
                        "hmi.fxml"
                )
        );

        Pane pane =  loader.load();

        HMIController controller =loader.getController();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(pane, screenBounds.getWidth(), screenBounds.getHeight());

        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();

        // example updating progress bar order items picked
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                return null;
            }

            @Override public void run() {
                for (int i = 0; i < 100; i++) {
                    controller.updateOrderItemsPickedStatus(i, 100);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        new Thread(task).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}