package Logic;

import Data.Database.DataServer;

import java.util.Date;

public class OrderItem {
    private int storageItemID;
    private int orderID;
    private String name;
    private int quantity;
    private Date pickingCompleted;

    public OrderItem(int itemID, int orderID, String name, int quantity) {
        this.storageItemID = itemID;
        this.orderID = orderID;
        this.name = name;
        this.quantity = quantity;
        this.pickingCompleted = null;
    }

    private boolean isPickingCompleted() {
        return this.pickingCompleted != null;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getStorageItemID() {
        return storageItemID;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setPickingCompleted() {
        DataServer tempDS = new DataServer();
        tempDS.completePicking(this.orderID, this.storageItemID);
        // If all the items are picked set order on picked.
        if (tempDS.areAllItemsPicked(this.orderID)) tempDS.completePicking(this.orderID);
        this.pickingCompleted = new Date();
    }

    public boolean equals(StorageItem si) {
        if (si == null || si.getClass() != si.getClass()) return false;
        return si.getItemID() == this.storageItemID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return this.orderID  == orderItem.orderID &&
                name.equals(orderItem.name);
    }


    @Override
    public String toString() {
        return "\n\tProduct name = " + name +", quantity = " + quantity;
    }
}
