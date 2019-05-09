package Logic;

public class StorageItem {
    private int id;
    private String name;
    private String location;
    private int stock;

    public StorageItem(int id, String name, String location, int stock) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.stock = stock;
    }

    public Location getLocation() {
        String[] parts = this.location.split(",");
        return new Location(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    @Override
    public String toString() {
        return "Logic.StorageItem{ name='" + name + '\'' + ", location='" + location + '\'' + ", stock=" + stock + '}';
    }
}
