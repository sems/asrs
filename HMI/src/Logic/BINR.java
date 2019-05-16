package Logic;

import java.util.ArrayList;

enum PackingBox {
    Left,
    Right
}

class PackingLocation {
    private PackingBox box;
    private int StorageItemId;
}

class Box {
    private ArrayList<StorageItem> packetItems;
    private PackingBox boxType;
    private boolean boxClosed = false;

    public Box(PackingBox boxType) {
       this.packetItems = new ArrayList<>();
       this.boxType = boxType;
    }

    public void addStorageItem(StorageItem storageItem) {
        packetItems = new ArrayList<>();
    }

    public void closeBox() {
        this.boxClosed = true;
    }
}

public class BINR {
    private Box leftBox;
    private Box rightBox;

    public BINR() {
        leftBox = new Box(PackingBox.Left);
        rightBox = new Box(PackingBox.Right);
    }

    public void packItems(ArrayList<StorageItem> itemsToPack) {
        // verdeel items over dozen
    }
}
