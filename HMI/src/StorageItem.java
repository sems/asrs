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

    @Override
    public String toString() {
        return "StorageItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", stock=" + stock +
                '}';
    }
}
