import java.util.Date;

public class Order {
    private int id;
    private Buyer buyer;
    private Address address;
    private Date orderdate;
    private Date pickingCompleted;
    private Date lastEditedWhen;

    public Order(int id, Buyer buyer, Address address, Date orderdate, Date lastEditedWhen) {
        this.id = id;
        this.buyer = buyer;
        this.address = address;
        this.orderdate = orderdate;
        this.pickingCompleted = null;
        this.lastEditedWhen = lastEditedWhen;
    }

    private boolean isPickingCompleted() {
        return this.pickingCompleted != null;
    }

    public void setPickingCompleted(Date pickingCompleted) {
        this.pickingCompleted = pickingCompleted;
    }
}
