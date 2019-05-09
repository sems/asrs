package Logic;

public class Location {
    private int X;
    private int Y;

    public Location(int x, int y) {
        X = x;
        Y = y;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    private int difference(int a, int b) {
        return Math.abs(a - b);
    }

    private double pitagoras(float a, float  b){
        return Math.sqrt((a*a) + (b*b));
    }

    public double getDistance(Location given){
        int difX = difference(this.X, given.getX());
        int difY = difference(this.Y, given.getY());
        return this.pitagoras(difX, difY);
    }

    @Override
    public String toString() {
        return "Location " + "X = " + X + ", Y = " + Y;
    }
}
