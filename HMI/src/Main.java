import Data.Database.DataServer;
import Logic.Location;
import Logic.Order;
import Logic.Storage;
import Logic.StorageItem;

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
        DataServer dataServer = new DataServer();
        Storage storage = new Storage();

        ArrayList<Order> orders = dataServer.getOrders();
        ArrayList<StorageItem> items = dataServer.getStorageItems();

        Location l1 = items.get(0).getLocation();
        Location l2 = items.get(1).getLocation();

        System.out.println(l1.getDistance(l2));

    }
}

