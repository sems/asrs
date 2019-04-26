import java.sql.*;
import View.StorageRack.*;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        Storage s = new Storage();

        try {
            String sql = "SELECT stockitems.StockItemID, StockItemName, BinLocation, QuantityOnHand " +
                    "FROM stockitems, stockitemholdings " +
                    "WHERE (stockitems.StockItemID = stockitemholdings.StockItemID) " +
                    "AND (stockitems.StockItemID BETWEEN  1 AND 25)";

            ResultSet rs = ConnectionManager.call(sql);
            while(rs.next()){
                int id  = rs.getInt(1);
                String name = rs.getString(2);
                String location = rs.getString(3);
                int stock = rs.getInt(4);
                s.addItemToStorage(new StorageItem(id, name, location, stock));
            }
            s.printStorage();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String orderSQL = "SELECT orders.OrderID, customers.CustomerName, customers.DeliveryAddressLine1, customers.DeliveryAddressLine2, customers.DeliveryPostalCode, cities.CityName, orders.OrderDate " +
                    "FROM orders, customers, cities  " +
                    "WHERE (orders.CustomerID = customers.CustomerID) " +
                    "AND (customers.DeliveryCityID = cities.CityID)" +
                    "AND orders.OrderDate > '2019-01-01' " +
                    "ORDER BY orders.OrderID ASC";
            ResultSet rsOrders = ConnectionManager.call(orderSQL);
            while(rsOrders.next()){
                // OrderId
                int id  = rsOrders.getInt(1);
                // The name of the buyer
                String buyer = rsOrders.getString(2);
                // Set the address
                String address = rsOrders.getString(3) + rsOrders.getString(4) + " \n"+ rsOrders.getString(5) + " " + rsOrders.getString(6);
                Date orderDate = rsOrders.getDate(7);

                Order o = new Order(id, buyer, address, orderDate);

                try {
                    String sqlOrderLines = "SELECT StockItemID, Description, Quantity, PickedQuantity, PickingCompletedWhen " +
                            "FROM orderlines " +
                            "WHERE OrderID = " + id;
                    ResultSet rsOrdersItems = ConnectionManager.call(sqlOrderLines);
                    while(rsOrdersItems.next()){

                        int itemID = rsOrdersItems.getInt(1);
                        String itemName = rsOrdersItems.getString(2);
                        int quantity = rsOrdersItems.getInt(3);

                        o.addOrderItems(new OrderItem(itemID, itemName, quantity));
                    }

                    rsOrdersItems.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(o.toString());
            }
            rsOrders.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static StorageRack getExampleStorage() {
        StorageRack storageRack = new StorageRack();

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                if ((x == 1 && y == 1) || (x == 2 && y == 3) || (x == 4 && y == 4)) {
                    storageRack.addRackSlot(new ProductStorageSlot(x, y));
                } else {
                    storageRack.addRackSlot(new EmptyStorageSlot(x, y));
                }
            }
        }
        return  storageRack;
    }
}
