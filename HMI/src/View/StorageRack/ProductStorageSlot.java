package View.StorageRack;
import java.awt.*;

public class ProductStorageSlot extends StorageSlot {

    public ProductStorageSlot(int x, int y) {
        super(x, y);
    }

    @Override
    public void drawSlot(Graphics g) {
        g.setColor(Color.black);
        g.fillOval(this.getX(), this.getY(), 40, 40);
    }
}
