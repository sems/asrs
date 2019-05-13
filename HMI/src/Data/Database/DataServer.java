package Data.Database;

import Logic.Order;
import Logic.OrderItem;
import Logic.StorageItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
                order = getOrder(rsOrders);
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
        Order order;
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
                order = getOrder(rsOrders);
                orders.add(order);
            }
            rsOrders.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    private Order getOrder(ResultSet rsOrders) throws SQLException {
        Order order;
        int id = rsOrders.getInt(1);
        String buyer = rsOrders.getString(2);
        String address = rsOrders.getString(3) + rsOrders.getString(4) + " \n" + rsOrders.getString(5) + " " + rsOrders.getString(6);
        Date orderDate = rsOrders.getDate(7);

        order = new Order(id, buyer, address, orderDate);

        ResultSet rs = getOrderItems(id);
        while (rs.next()) {
            int itemID = rs.getInt(1);
            int OrderID = rs.getInt(2);
            String itemName = rs.getString(3);
            int quantity = rs.getInt(4);

            order.addOrderItems(new OrderItem(itemID, OrderID, itemName, quantity));
        }
        rs.close();
        order.initializeFastestRoute();
        return order;
    }

    /**
     * Gets the items out of orderlines
     *
     * @param id from the order
     */
    private ResultSet getOrderItems(int id) {
        ResultSet rsOrdersItems = null;
        try {
            String sqlOrderLines = "SELECT StockItemID, OrderID, Description, Quantity, PickedQuantity, PickingCompletedWhen " +
                    "FROM orderlines " +
                    "WHERE OrderID = " + id;
            rsOrdersItems = cm.call(sqlOrderLines);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsOrdersItems;
    }

    /**
     * Complete picking.
     *
     * @param idOrder the order id
     */
    public void completePicking(int idOrder){
        try {
            String sqlUpdateOrderLines = "UPDATE orders SET PickingCompletedWhen = NOW() " +
                    "WHERE OrderID = " + idOrder;
            cm.update(sqlUpdateOrderLines);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Complete picking.
     *
     * @param orderId     the order id
     * @param orderItemId the order item id
     */
    public void completePicking(int orderId, int orderItemId){
        try {
            String sqlUpdateOrderLines = "UPDATE orderlines SET PickingCompletedWhen = NOW() " +
                    "WHERE OrderID = " + orderId + " " +
                    "AND StockItemID = " + orderItemId;
            cm.update(sqlUpdateOrderLines);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Are all items picked boolean.
     *
     * @param orderId the order id
     * @return the boolean
     */
    public boolean areAllItemsPicked(int orderId){
        boolean status = false;
        int size = 0;
        int count = 0;
        try {
            ResultSet rs = this.getOrderItems(orderId);

            // Get the last on and set it as size
            rs.last();
            size = rs.getRow();

            // Go back to the first for looping
            rs.beforeFirst();
            while(rs.next()){
                Timestamp datePicked = rs.getTimestamp("PickingCompletedWhen");
                if (datePicked != null) count++;
            }

            // Check if all are picked
            if (size == count) status = true;
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }
}
