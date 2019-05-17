package Logic.BINR;

import Logic.StorageItem;

import java.util.ArrayList;

/**
 * A box contains storage items that are packed by the BINRR.
 */
public class Box {
    // the items stored in this box
    private ArrayList<StorageItem> storedItems;
    // the type of this box
    private BoxType boxType;
    // specifies if this box is closed
    private boolean boxClosed = false;
    // the height of this box
    private int height;
    // the space left in height
    private int freeHeightSpace;

    public Box(BoxType boxType, int boxHeight) {
       this.storedItems = new ArrayList<>();
       this.boxType = boxType;
       this.height = boxHeight;
       this.storedItems = new ArrayList<>();
    }

    /**
     * Add a storage item to this box.
     * This function will also update the filledSpace of this box.
     *
     * When calling this function make sure that there is enough space left in this box.
     * @param storageItem
     */
    public void addStorageItem(StorageItem storageItem) {
       this.storedItems.add(storageItem);
        addStorageItem(storageItem);
        freeHeightSpace = (freeHeightSpace - height);
    }

    public void closeBox() {
        this.boxClosed = true;
    }

    /**
     * Return the stored items in this box.
     * @return
     */
    public ArrayList<StorageItem> getStoredItems() {
        return this.storedItems;
    }

    /**
     * Returns the height of this box.
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the free space in height.
     */
    public int getFreeHeightSpace() {
        return this.freeHeightSpace;
    }
}
