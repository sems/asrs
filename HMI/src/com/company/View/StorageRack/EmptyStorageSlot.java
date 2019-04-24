package com.company.View.StorageRack;

import java.awt.*;

public class EmptyStorageSlot extends StorageSlot {
    public EmptyStorageSlot(int x, int y) {
        super(x, y);
    }

    @Override
    public void drawSlot(Graphics g) { }
}
