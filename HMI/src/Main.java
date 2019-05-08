import static javafx.application.Application.launch;

import Data.Database.DataServer;
import Logic.Order;
import Logic.Storage;
import Logic.StorageItem;
import View.StorageRack.*;


import java.util.ArrayList;


/**
 * The type Main.
 */
public class Main {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        launch(args);
      
        DataServer dataServer = new DataServer();
        Storage storage = new Storage();

        ArrayList<Order> orders = dataServer.getOrders();
        ArrayList<StorageItem> items = dataServer.getStorageItems();

        for (StorageItem si: items) {
            storage.addItemToStorage(si);
        }
        orders.get(1).getOrderItems().get(0).setPickingCompleted();
    }
}

