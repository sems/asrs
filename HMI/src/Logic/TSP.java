package Logic;

import java.util.ArrayList;

/**
 * The type Tsp.
 */
public class TSP {

    /**
     * Calculate route array list.
     *
     * @param unsortedItems the unsorted items
     * @return the array list
     */
    public ArrayList<StorageItem> calculateRoute( ArrayList<StorageItem> unsortedItems){
        // Starting location of asr
        StorageItem defaultStartItem = new StorageItem(0,"start","0,0",1);
        ArrayList<StorageItem> route = new ArrayList<>();

        // Find the first nearest to get a point to calculated further.
        StorageItem first = this.getNearest(unsortedItems, defaultStartItem);
        route.add(first);
        unsortedItems.remove(first);

        // Finding the nearest neighbour as to the last in the route
        while (!unsortedItems.isEmpty()){
            StorageItem lastItem = route.get(route.size() - 1);
            StorageItem nearest = this.getNearest(unsortedItems, lastItem);
            unsortedItems.remove(nearest);
            route.add(nearest);
        }

        return route;
    }

    private StorageItem getNearest(ArrayList<StorageItem> items, StorageItem item) {
        Location startLocation = item.getLocation();

        StorageItem nearest = items.get(0);

        for (StorageItem si: items ) {
            double distanceToNearest = si.getLocation().getDistance(startLocation);
            if (distanceToNearest < nearest.getLocation().getDistance(startLocation)){
                nearest = si;
            }
        }
        return nearest;
    }
}
