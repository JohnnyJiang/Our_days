package hk.edu.cuhk.ie.iems5722.group4_our_days;

/**
 * Created by jiang on 2016/4/17.
*/
public class Coordinate {
    public int x;
    public int y;

    public Coordinate(int newX, int newY) {
        x = newX;
        y = newY;
    }

    // public boolean equals(Coordinate other) {
    // if (x == other.x && y == other.y) {
    // return true;
    // }
    // return false;
    // }

    @Override
    public String toString() {
        return "Coordinate: [" + x + "," + y + "]";
    }
}

