package Logic;

import Data.Database.DataServer;

import java.util.ArrayList;
import java.util.Date;

public class Order {
    private int id;
    private String buyer;
    private String address;
    private ArrayList<OrderItem> orderItems;
    private Date orderdate;
    private Date pickingCompleted;
    private ArrayList<StorageItem> route;
    private String orderItemsCount;

    public Order(int id, String buyer, String address, Date orderdate) {
        this.id = id;
        this.buyer = buyer;
        this.address = address;
        this.orderItems = new ArrayList<>();
        this.orderdate = orderdate;
        this.pickingCompleted = null;
        this.route = new ArrayList<>();
    }

    public void addOrderItems(OrderItem item) {
        orderItems.add(item);
        this.orderItemsCount = Integer.toString(this.orderItems.size());
    }

    public boolean isPickingCompleted() {
        return this.pickingCompleted != null;
    }

    private ArrayList<StorageItem> getRoute(ArrayList<OrderItem> orderItems){
        DataServer dataServer = new DataServer();
        TSP tsp = new TSP();
        ArrayList<StorageItem> storageItemsInOrder = new ArrayList<>();
        ArrayList<StorageItem> storageItems = dataServer.getStorageItems();
        System.out.println(orderItems.size());
        for (OrderItem oi: orderItems) {
            for (StorageItem si: storageItems) {
                if (oi.equals(si)){
                    storageItemsInOrder.add(si);
                }
            }
        }
        return tsp.calculateRoute(storageItemsInOrder);
    }
    
    public void setPickingCompleted(Date pickingCompleted) {
        this.pickingCompleted = pickingCompleted;
    }

    public void setRoute() {
        this.route = this.getRoute(this.orderItems);
    }

    public String getBuyer() {
        return buyer;
    }

    public int getId() {
        return id;
    }

    public String getAddr() {
        return this.address;
    }

    public Date getOrderData() {
        return this.orderdate;
    }

    public String getOrderItemsCount() {
        return orderItemsCount;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    @Override
    public String toString() {
        return "Logic.Order "+ id +" { \n" +
                 orderdate + "\n" +
                 buyer + "\n" +
                 address + "\n" +
                "orderItems=" + orderItems + "\n" +
                "route=" + route + "\n" +
                "}\n\n";
    }

    public ArrayList<StorageItem> getRoute() {
        return this.route;
    }
}
