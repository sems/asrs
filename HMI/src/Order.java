import java.util.ArrayList;
import java.util.Date;

public class Order {
    private int id;
    private String buyer;
    private String address;
    private ArrayList<OrderItem> orderItems;
    private Date orderdate;
    private Date pickingCompleted;

    public Order(int id, String buyer, String address, Date orderdate) {
        this.id = id;
        this.buyer = buyer;
        this.address = address;
        this.orderItems = new ArrayList<>();
        this.orderdate = orderdate;
        this.pickingCompleted = null;
    }

    public void addOrderItems(OrderItem item) {
        orderItems.add(item);
    }

    public void printOrderItems(){
        System.out.println("\nOrderItems:");
        for (OrderItem oi: orderItems ) {
            System.out.println(oi);
        }
    }

    private boolean isPickingCompleted() {
        return this.pickingCompleted != null;
    }

    public void setPickingCompleted(Date pickingCompleted) {
        this.pickingCompleted = pickingCompleted;
    }

    @Override
    public String toString() {
        return "Order "+ id +" { \n" +
                 orderdate + "\n" +
                 buyer + "\n" +
                 address +
                "\norderItems=" + orderItems +
                "\n}\n";
    }
}
