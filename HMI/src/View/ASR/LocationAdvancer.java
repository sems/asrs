package View.ASR;

import Logic.Communication.ASRCommunication;
import Logic.Location;
import Logic.Order;
import Logic.OrderItem;
import Logic.StorageItem;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class LocationAdvancer {
    private ObservableList<Order> orders;
    private ASRCommunication asrCommunication;
    private ArrayList<Location> currentRoute;

    private int currentOrderPickedIndex = 0;
    private int currentStorageItemPickedIndex = 0;

    public LocationAdvancer(ObservableList<Order> orders, ASRCommunication asrCommunication) {
        this.orders = orders;
        this.asrCommunication = asrCommunication;

        if (orders.size() > 0) {
            currentRoute = mapLocation(orders.get(0).getRoute());
        }else {
            System.out.println("There are no orders for picking.");
        }
    }

    /**
     * Advances to the next storage item, it will advance to the next order if all storage items of an order are picked .
     *
     * Returns true in case there are more storage items left.
     * Returns false in case there are no more orders and storage items.
      */
    public boolean advanceToNextStorageItem() {
        if (currentStorageItemPickedIndex < currentRoute.size()) {
            var nextLocation = currentRoute.get(currentStorageItemPickedIndex);
            asrCommunication.gotoPos(nextLocation.getX(), nextLocation.getY());
            System.out.println("Advancing to " + "x: " + nextLocation.getX() + " y: "+ nextLocation.getY());
            currentStorageItemPickedIndex += 1;
            return true;
        }else {
            System.out.println("No more elements in route. current route size: " + currentRoute.size() + " current index: " + currentStorageItemPickedIndex);

            if (!advanceToNextOrder()) {
                // if there are no more orders return
                return false;
            } else {
                // advance to first storage item of new order
               return  advanceToNextStorageItem();
            }
        }
    }

    /**
     * Get the count of items in the current route
     * @return
     */
    public int getCurrentRouteItemsNumber() {
        return currentRoute.size();
    }

    public int getCurrentRoutePickedItem() {
        return currentStorageItemPickedIndex;
    }

    /**
     * Returns the current route locations
     * @return
     */
    public ArrayList<Location> getCurrentRouteLocations() {
        return currentRoute;
    }

    /**
     * Returns the current route order items.
     * @return
     */
    public ArrayList<OrderItem> getCurrentRouteOrderItems() {
        return orders.get(currentOrderPickedIndex).getOrderItems();
    }

    public int getCurrentRouteOrderItemPickedIndex() {
        return currentOrderPickedIndex;
    }
    /**
     * Advance to the next order if all storage items are picked of the current order
     */
    private boolean advanceToNextOrder() {
        if (orders.size() < currentOrderPickedIndex + 1) {
            currentOrderPickedIndex += 1;
            currentRoute = mapLocation(orders.get(currentOrderPickedIndex).getRoute());
            return true;
        } else {
            // no more orders
            return false;
        }
    }

    private ArrayList<Location> mapLocation(ArrayList<StorageItem> orders) {
        return orders.stream().map(StorageItem::getLocation).collect(Collectors.toCollection(ArrayList::new));
    }
}
