package View;

import Data.Database.DataServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HMIApplication extends Application {
    FXMLLoader loader = null;
    private HMIController controller;

    @Override
    public void stop(){
        controller.asrCommunication.close();
    }

    @Override
    public void start(Stage stage) throws IOException {
        try {
            StackPane root = new StackPane();

            root.setAlignment(Pos.CENTER);

            loader = new FXMLLoader(
                    getClass().getResource(
                            "hmi.fxml"
                    )
            );

            Pane pane = loader.load();
            controller = loader.getController();
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(pane, screenBounds.getWidth(), screenBounds.getHeight());

            stage.setTitle("HMI Applicatie");
            stage.setScene(scene);
            stage.show();

            DataServer server = new DataServer();
            controller.Initialize(server.getOrders());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

