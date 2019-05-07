import Data.Database.DataServer;
import Logic.Order;
import Logic.Storage;
import Logic.StorageItem;
import View.StorageRack.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;

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

        System.out.println(orders);

        for (StorageItem si: items) {
            storage.addItemToStorage(si);
        }

        orders.get(2).setPickingCompleted();
    }

    private static StorageRack getExampleStorage() {
        StorageRack storageRack = new StorageRack();

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                if ((x == 1 && y == 1) || (x == 2 && y == 3) || (x == 4 && y == 4)) {
                    storageRack.addRackSlot(new ProductStorageSlot(x, y));
                } else {
                    storageRack.addRackSlot(new EmptyStorageSlot(x, y));
                }
            }
        }
        return  storageRack;
    }
}
