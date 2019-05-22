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
    private int currentStorageItemPickedIndex = 1;

    public LocationAdvancer(ObservableList<Order> orders, ASRCommunication asrCommunication) {
        this.orders = orders;
        this.asrCommunication = asrCommunication;
    }

    /**
     * Advances to the next storage item, it will advance to the next order if all storage items of an order are picked .
     *
     * Returns true in case there are more storage items left.
     * Returns false in case there are no more orders and storage items.
      */
    public LocationAdvanceStatus advanceToNextStorageItem() {
        if (currentRoute == null) {
            advanceToNextOrder();
        }

        if (currentStorageItemPickedIndex <= currentRoute.size()) {
            System.out.println(">>>>> Total storage items: " + currentRoute.size() +  " Current storage item: " + currentStorageItemPickedIndex);
            var nextLocation = currentRoute.get(currentStorageItemPickedIndex - 1);
            asrCommunication.gotoPos(nextLocation.getX(), nextLocation.getY());
            System.out.println(">>>>> Advancing to " + "x: " + nextLocation.getX() + " y: "+ nextLocation.getY());
            currentStorageItemPickedIndex += 1;
            return LocationAdvanceStatus.NewStorageItemPicked;
        }else {
            System.out.println(">>>>> No more elements in route. current route size: " + currentRoute.size() + " current index: " + currentStorageItemPickedIndex);

            var status = advanceToNextOrder();
            if (status == LocationAdvanceStatus.NoNewOrdersToPick) {
                System.out.println(">>>>> There are no new items to pick ");
                // if there are no more orders return
                return status;
            }
            if (status == LocationAdvanceStatus.NewOrderForPick) {
                System.out.println(">>>>> A new order is started advancing to the next item ");
                // advance to first storage item of new order
               return  advanceToNextStorageItem();
            }
        }
        return null;
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

    /**
     * Advance to the next order if all storage items are picked of the current order
     */
    private LocationAdvanceStatus advanceToNextOrder() {
        if (currentOrderPickedIndex < orders.size()) {
            currentRoute = mapLocation(orders.get(currentOrderPickedIndex).getRoute());
            currentOrderPickedIndex += 1;
            currentStorageItemPickedIndex = 1;
            System.out.println(">>>>> Total orders: " + orders.size() +  " Current order: " + currentOrderPickedIndex);
            return LocationAdvanceStatus.NewOrderForPick;
        } else {
            System.out.println(">>>>> There are no more orders");
            // no more orders
            return LocationAdvanceStatus.NoNewOrdersToPick;
        }
    }

    private ArrayList<Location> mapLocation(ArrayList<StorageItem> orders) {
        return orders.stream().map(StorageItem::getLocation).collect(Collectors.toCollection(ArrayList::new));
    }
}

