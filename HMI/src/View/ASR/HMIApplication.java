package View.ASR;

import Data.Database.DataServer;
import Logic.Order;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

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

        HMIController controller = loader.getController();

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
                DataServer server = new DataServer();

                var orders = new ArrayList<Order>();
//                orders.add(new Order(1, "timon", "hello", Date.from(Instant.now())));

                controller.Initialize(server.getOrders());

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