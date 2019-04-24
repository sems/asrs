package com.company;

import com.company.View.StorageRack.EmptyStorageSlot;
import com.company.View.StorageRack.ProductStorageSlot;
import com.company.View.StorageRack.StorageRack;

public class Main {
    public static void main(String[] args) {
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
