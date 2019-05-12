package Logic;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class StorageItem {
    private int itemID;
    private String name;
    private String location;
    private int stock;

    public StorageItem(int itemID, String name, String location, int stock) {
        this.itemID = itemID;
        this.name = name;
        this.location = location;
        this.stock = stock;
    }

    public Location getLocation() {
        String[] parts = this.location.split(",");
        return new Location(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    public int getItemID() {
        return itemID;
    }

    @Override
    public String toString() {
        return location;
    }
}
