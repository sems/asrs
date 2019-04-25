package View.StorageRack;

import javax.swing.*;
import java.awt.*;

public class StorageRackPanel extends JPanel {
    private StorageRack _storageRack;
    private int _width;
    private int _height;
    private int _cellSize;

    public StorageRackPanel(StorageRack storageRack, int width, int height, int cellSize) {
        _width = width;
        _height = height;
        _cellSize = cellSize;
        _storageRack = storageRack;

        setPreferredSize(new Dimension(_width + 3, _height + 3));
        setBackground(Color.LIGHT_GRAY);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).setStroke(new BasicStroke(1));
        _storageRack.drawnRack(g, _width, _height, _cellSize);
    }
}
