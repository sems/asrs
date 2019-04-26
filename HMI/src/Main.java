import Data.Database.DataServer;
import View.StorageRack.*;

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
        DataServer ds = new DataServer();

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
