package Data.Database;

import Logic.Order;
import Logic.OrderItem;
import Logic.StorageItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * The type Data server.
 */
public class DataServer {
    private ConnectionManager cm;

    /**
     * Instantiates a new Data server.
     */
    public DataServer() {
        this.cm = new ConnectionManager();
    }

    /**
     * Get storage items array list.
     *
     * @return array list of StorageItem
     */
    public ArrayList<StorageItem> getStorageItems(){
        ArrayList<StorageItem> storageItems = new ArrayList<>();
        try {
            String sql = "SELECT stockitems.StockItemID, StockItemName, BinLocation, QuantityOnHand " +
                    "FROM stockitems, stockitemholdings " +
                    "WHERE (stockitems.StockItemID = stockitemholdings.StockItemID) " +
                    "AND (stockitems.StockItemID BETWEEN  1 AND 25)";

            ResultSet rs = cm.call(sql);
            while(rs.next()){
                int id  = rs.getInt(1);
                String name = rs.getString(2);
                String location = rs.getString(3);
                int stock = rs.getInt(4);
                storageItems.add(new StorageItem(id, name, location, stock));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return storageItems;
    }

    /**
     * Get order order.
     *
     * @param orderID the orderID
     * @return the order
     */
    public Order getOrder(int orderID){
        Order order = null;
        try {
            String orderSQL = "SELECT orders.OrderID, customers.CustomerName, customers.DeliveryAddressLine1, customers.DeliveryAddressLine2, customers.DeliveryPostalCode, cities.CityName, orders.OrderDate " +
                    "FROM orders, customers, cities  " +
                    "WHERE (orders.CustomerID = customers.CustomerID) " +
                    "AND (customers.DeliveryCityID = cities.CityID)" +
                    "AND orders.OrderDate > '2019-01-01' " +
                    "AND orders.OrderID = " + orderID + " " +
                    "ORDER BY orders.OrderID ASC";
            ResultSet rsOrders = cm.call(orderSQL);
            while(rsOrders.next()){
                // OrderId
                int id  = rsOrders.getInt(1);
                // The name of the buyer
                String buyer = rsOrders.getString(2);
                // Set the address
                String address = rsOrders.getString(3) + rsOrders.getString(4) + " \n"+ rsOrders.getString(5) + " " + rsOrders.getString(6);
                Date orderDate = rsOrders.getDate(7);

                order = new Order(id, buyer, address, orderDate);

                getOrderItems(order, id);
            }
            rsOrders.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    /**
     * Get orders array list.
     *
     * @return the array list of orders
     */
    public ArrayList<Order> getOrders(){
        ArrayList<Order> orders = new ArrayList<>();
        try {
            String orderSQL = "SELECT orders.OrderID, customers.CustomerName, customers.DeliveryAddressLine1, customers.DeliveryAddressLine2, customers.DeliveryPostalCode, cities.CityName, orders.OrderDate " +
                    "FROM orders, customers, cities  " +
                    "WHERE (orders.CustomerID = customers.CustomerID) " +
                    "AND (customers.DeliveryCityID = cities.CityID)" +
                    "AND orders.OrderDate > '2019-01-01' " +
                    "ORDER BY orders.OrderID ASC";
            ResultSet rsOrders = cm.call(orderSQL);
            while(rsOrders.next()){
                // OrderId
                int id  = rsOrders.getInt(1);
                // The name of the buyer
                String buyer = rsOrders.getString(2);
                // Set the address
                String address = rsOrders.getString(3) + rsOrders.getString(4) + " \n"+ rsOrders.getString(5) + " " + rsOrders.getString(6);
                Date orderDate = rsOrders.getDate(7);

                Order o = new Order(id, buyer, address, orderDate);

                getOrderItems(o, id);
                orders.add(o);
            }
            rsOrders.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * Gets the items out of orderlines
     *
     * @param order create in method where is is called.
     * @param id from the order
     */
    private void getOrderItems(Order order, int id) {
        try {
            String sqlOrderLines = "SELECT StockItemID, Description, Quantity, PickedQuantity, PickingCompletedWhen " +
                    "FROM orderlines " +
                    "WHERE OrderID = " + id;
            ResultSet rsOrdersItems = cm.call(sqlOrderLines);
            while(rsOrdersItems.next()){

                int itemID = rsOrdersItems.getInt(1);
                String itemName = rsOrdersItems.getString(2);
                int quantity = rsOrdersItems.getInt(3);

                order.addOrderItems(new OrderItem(itemID, itemName, quantity));
            }

            rsOrdersItems.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
