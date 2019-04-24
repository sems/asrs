import java.sql.*;
import View.StorageRack.*;

public class Main {
    public static void main(String[] args) {
        Storage s = new Storage();
        String sql = "SELECT stockitems.StockItemID, StockItemName, BinLocation, QuantityOnHand " +
                "FROM stockitems, stockitemholdings " +
                "WHERE (stockitems.StockItemID = stockitemholdings.StockItemID) AND (stockitems.StockItemID BETWEEN  1 AND 25)";
        try {
            ResultSet rs = ConnectionManager.call(sql);
            while(rs.next()){
                int id  = rs.getInt(1);
                String name = rs.getString(2);
                String location = rs.getString(3);
                int stock = rs.getInt(4);
                s.addItemToStorage(new StorageItem(id, name, location, stock));
            }
            s.printStorage();
            System.out.println("END!!!!");
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String orderSQL = "SELECT orders.OrderID, orders.CustomerID, orders.OrderDate " +
                "FROM orders, customers " +
                "WHERE (orders.CustomerID = customers.CustomerID) AND orders.OrderDate > '2019-01-01' ";

        try {
            ResultSet orders = ConnectionManager.call(orderSQL);
            while(orders.next()){
                //Retrieve by column name
                int id  = orders.getInt(1);
                int first = orders.getInt(2);
                String date = orders.getString(3);

                //Display values
                System.out.print("Order ID: " + id);
                System.out.print(", Customer ID: " + first);
                System.out.println(", Order date: " + date);
            }
            System.out.println("LAST");
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
