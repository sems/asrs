package Logic;

import java.util.ArrayList;

/**
 * Enum specifying the different boxes.
 */
enum BoxType {
    Left,
    Right
}

/**
 * A box contains storage items that are packed by the BINRR.
 */
class Box {
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

/**
 * BINR packs products in the most efficient box.
 */
public class BINR {
    private Box leftBox;
    private Box rightBox;
    private ArrayList<Box> closedBoxes;

    public BINR() {
        leftBox = new Box(BoxType.Left, 100);
        rightBox = new Box(BoxType.Right, 60);
        closedBoxes = new ArrayList<>();
    }

    /**
     * Packs the given items into the most efficient box.
     * @param itemsToPack
     */
    public void packItems(ArrayList<StorageItem> itemsToPack) {
        for (var item: itemsToPack) {
            var productHeight = item.getPrdocutHeight();

            var leftBoxFreeHeightSpace = leftBox.getFreeHeightSpace();

            if (leftBoxFreeHeightSpace <= productHeight) {
                // item fits in left box
                leftBox.addStorageItem(item);
            } else if (rightBox.getFreeHeightSpace() <= productHeight) {
                // item fits in right box
                rightBox.addStorageItem(item);
            } else {
                // item doesn't fit in either right or left box, create new box.

            }
        }
    }

    /**
     * Returns the box that matches the given box type.
     * @param boxType
     * @return
     */
    public Box getBoxByType(BoxType boxType) {
        if (boxType == BoxType.Right) {
            return rightBox;
        }
        if (boxType == BoxType.Left) {
            return leftBox;
        }else {
            return null;
        }
    }
}
