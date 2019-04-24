package View.StorageRack;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class StorageRack {
    private ArrayList<StorageSlot> _storageRackElements =  new ArrayList<>();

    public void drawnRack(Graphics g, int width, int height, int cellSize) {
        int rows = height / cellSize;
        int cols = width / cellSize;
        g.setColor(Color.black);

        // draw the rows
        for (int i = 0; i < rows + 1; i++)
            g.drawLine(0, i * cellSize, width, i * cellSize);

        // draw the columns
        for (int i = 0; i < cols +1; i++) {
            g.drawLine(i * cellSize, 0, i * cellSize, height);
        }

        // draw the slots
        for (StorageSlot element : _storageRackElements) {
            element.drawSlot(g);
        }
    }

    public void addRackSlot(StorageSlot storageSlot) {
        _storageRackElements.add(storageSlot);
    }

    public void removeRackSlot(int x, int y) {
        var elements = _storageRackElements
                .stream()
                .filter(e -> e.getRelativeX() == x && y == e.getRelativeY())
                .collect(Collectors.toCollection(ArrayList::new));

        _storageRackElements.removeAll(elements);
    }
}
