import java.util.ArrayList;

public class Buyer {
    private int id;
    private String fullname;
    private ArrayList<Address> addresses;

    public Buyer(int id, String fullname) {
        this.id = id;
        this.fullname = fullname;
        this.addresses = new ArrayList<>();
    }
}
