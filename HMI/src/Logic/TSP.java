package Logic;

import java.util.ArrayList;

public class TSP {
    public ArrayList<StorageItem> calculateRoute( ArrayList<StorageItem> unsortedItems){
        Location start = new Location(0,0);
        ArrayList<StorageItem> route = new ArrayList<>();



        return new ArrayList<>();
    }

    public StorageItem getNearest(ArrayList<StorageItem> items, StorageItem item) {
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
