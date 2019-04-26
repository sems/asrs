package Logic;

import java.util.Date;

public class OrderItem {
    private int itemID;
    private String name;
    private int quantity;
    private int pickedQuantity;
    private Date pickingCompleted;

    public OrderItem(int itemID, String name, int quantity) {
        this.itemID = itemID;
        this.name = name;
        this.quantity = quantity;
        this.pickedQuantity = 0;
        this.pickingCompleted = null;
    }

    private boolean isPickingCompleted() {
        return this.pickingCompleted != null;
    }

    public void setPickingCompleted(Date pickingCompleted) {
        this.pickingCompleted = pickingCompleted;
    }

    @Override
    public String toString() {
        return "Product name = " + name +", quantity = " + quantity;
    }
}
