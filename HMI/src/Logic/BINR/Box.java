package Logic.BINR;

import Logic.Order;
import Logic.OrderItem;
import Logic.StorageItem;

import java.util.ArrayList;

/**
 * A box contains storage items that are packed by the BINRR.
 */
public class Box {
    // the items stored in this box
    private ArrayList<OrderItem> storedItems;
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
       this.freeHeightSpace = boxHeight;
       this.storedItems = new ArrayList<>();
    }

    /**
     * Add an order item to this box.
     * This function will also update the filledSpace of this box.
     *
     * When calling this function make sure that there is enough space left in this box.
     * @param orderItem
     */
    public void addOrderItem(OrderItem orderItem) {
       this.storedItems.add(orderItem);
        freeHeightSpace = (freeHeightSpace - orderItem.getPrdocutHeight());
    }

    public void closeBox() {
        this.boxClosed = true;
    }

    /**
     * Return the stored items in this box.
     * @return
     */
    public ArrayList<OrderItem> getStoredItems() {
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

    public BoxType getBoxType() {
        return boxType;
    }
}
