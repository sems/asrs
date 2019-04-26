package Logic;

import java.util.ArrayList;

public class Storage {
    private ArrayList<StorageItem> storage;

    public Storage() {
        this.storage = new ArrayList<>();
    }

    public void addItemToStorage(StorageItem item) {
        storage.add(item);
    }

    public void printStorage(){
        for (StorageItem si: storage ) {
            System.out.println(si);
        }
    }
}

