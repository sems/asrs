package Logic;

public class StorageItem {
    private int itemID;
    private String name;
    private String location;
    private int stock;
    private int prdocutHeight;

    private static final int maxProductHeight = 20;
    private static final int minProductHeight = 10;

    public StorageItem(int itemID, String name, String location, int stock) {
        this.itemID = itemID;
        this.name = name;
        this.location = location;
        this.stock = stock;
        this.prdocutHeight =  (int)(Math.random()*((maxProductHeight-minProductHeight)+1))+minProductHeight;
    }

    /**
     * Returns the position coordinates of this item in the storage rack
     * @return
     */
    public Location getLocation() {
        String[] parts = this.location.split(",");
        return new Location(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    /**
     * Returns the id of this storage item.
     * @return
     */
    public int getItemID() {
        return itemID;
    }


    @Override
    public String toString() {
        return location;
    }

    public int getPrdocutHeight() {
        return prdocutHeight;
    }
}
