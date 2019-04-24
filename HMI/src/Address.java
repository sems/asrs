public class Address {
    private int id;
    private String customerName;
    private String street;
    private int number;
    private String zipcode;
    private String city;

    public Address(String customerName, String street, int number, String zipcode, String city) {
        this.customerName = customerName;
        this.street = street;
        this.number = number;
        this.zipcode = zipcode;
        this.city = city;
    }
}
