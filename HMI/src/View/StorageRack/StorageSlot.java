package View.StorageRack;

import java.awt.*;

public abstract class StorageSlot {
    private int x;
    private  int y;

    public StorageSlot(int x, int y) {
        this.x = x * 40;
        this.y =  y * 40;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getRelativeX() {
        return this.x / 40;
    }

    public int getRelativeY() {
        return this.y / 40;
    }

    public abstract void drawSlot(Graphics g);
}
