package View.StorageRack;
import java.awt.*;

public class RobotPositionStorageSlot extends StorageSlot {

    public RobotPositionStorageSlot(int x, int y) {
        super(x, y);
    }

    @Override
    public void drawSlot(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(getX(), getY(), 40,40);
    }
}
