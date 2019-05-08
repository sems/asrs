package Logic;

import java.util.ArrayList;
import java.util.Date;

public class Order {
    private int id;
    private String buyer;
    private String address;
    private ArrayList<OrderItem> orderItems;
    private Date orderdate;
    private Date pickingCompleted;
    private String orderItemsCount = "3";

    public Order(int id, String buyer, String address, Date orderdate) {
        this.id = id;
        this.buyer = buyer;
        this.address = address;
        this.orderItems = new ArrayList<>();
        this.orderdate = orderdate;
        this.pickingCompleted = null;
        orderItemsCount = "2";
    }

    public void addOrderItems(OrderItem item) {
        orderItems.add(item);
        this.orderItemsCount = Integer.toString(this.orderItems.size());
    }

    public void printOrderItems(){
        System.out.println("\nOrderItems:");
        for (OrderItem oi: orderItems ) {
            System.out.println(oi);
        }
    }

    public boolean isPickingCompleted() {
        return this.pickingCompleted != null;
    }

    public void setPickingCompleted(Date pickingCompleted) {
        this.pickingCompleted = pickingCompleted;
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

    @Override
    public String toString() {
        return "Logic.Order "+ id +" { \n" +
                 orderdate + "\n" +
                 buyer + "\n" +
                 address +
                "\norderItems=" + orderItems +
                "\n}\n";
    }

    public String getOrderItemsCount() {
        return orderItemsCount;
    }
}
