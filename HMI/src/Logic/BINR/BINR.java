package Logic.BINR;

import Logic.OrderItem;

import java.util.ArrayList;

/**
 * BINR packs products in the most efficient box.
 */
public class BINR {
    private int leftBoxSize;
    private int rightBoxSize;
    private Box leftBox;
    private Box rightBox;
    private ArrayList<Box> closedBoxes;

    public BINR(int leftBoxSize, int rightBoxSize) {
        this.leftBoxSize = leftBoxSize;
        this.rightBoxSize = rightBoxSize;
        leftBox = new Box(BoxType.Left, leftBoxSize);
        rightBox = new Box(BoxType.Right, rightBoxSize);
        closedBoxes = new ArrayList<>();
    }

    /**
     * Packs the given items into the most efficient box.
     *
     * @param items
     */
    public void packItems(ArrayList<OrderItem> items) {
        for (var item : items) {
            packItem(item);
        }
    }

    /**
     *  Packs the given item into the most efficient box.
     * @param orderItem
     * @return
     */
    public PackStatus packItem(OrderItem orderItem) {
        var productHeight = orderItem.getPrdocutHeight();

        if (leftBox.getFreeHeightSpace() >= productHeight) {
            // item fits in left box
            leftBox.addOrderItem(orderItem);
            return PackStatus.PackedIntoLeftBox;
        } else if (rightBox.getFreeHeightSpace() >= productHeight) {
            // item fits in right box
            rightBox.addOrderItem(orderItem);
            return PackStatus.PackedIntoRightBox;
        } else {
            // check the most filled box and close that one.
            if (rightBox.getFreeHeightSpace() > leftBox.getFreeHeightSpace()) {
                closeLeftBox();
                leftBox.addOrderItem(orderItem);
                return PackStatus.ClosedLeftBox;
            } else if (rightBox.getFreeHeightSpace() < leftBox.getFreeHeightSpace()) {
                closeRightBox();
                rightBox.addOrderItem(orderItem);
                return PackStatus.ClosedRightBox;
            } else {
                // free space is the same in each box, close the right one.
                closeRightBox();
                rightBox.addOrderItem(orderItem);
                return PackStatus.ClosedRightBox;
            }
        }
    }

    /**
     * Returns the box that matches the given box type.
     * @param boxType
     * @return
     */
    public Box getBoxByType(BoxType boxType) {
        if (boxType == BoxType.Right) {
            return rightBox;
        }
        if (boxType == BoxType.Left) {
            return leftBox;
        }else {
            return null;
        }
    }

    /**
     * Returns the closed boxes with packet items.
     * @return
     */
    public ArrayList<Box> getClosedBoxes() {
        return this.closedBoxes;
    }

    private void closeRightBox() {
        rightBox.closeBox();
        closedBoxes.add(rightBox);
        rightBox = new Box(BoxType.Right, rightBoxSize);
    }

    private void closeLeftBox() {
        leftBox.closeBox();
        closedBoxes.add(leftBox);
        leftBox = new Box(BoxType.Left, leftBoxSize);
    }
}
