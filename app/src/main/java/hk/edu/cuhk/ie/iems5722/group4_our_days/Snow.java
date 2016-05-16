package hk.edu.cuhk.ie.iems5722.group4_our_days;

/**
 * Created by jiang on 2016/4/17.
 */

public class Snow {
    Coordinate coordinate;
    int speed;

    public Snow(int x, int y, int speed){
        coordinate = new Coordinate(x, y);
        System.out.println("Speed:"+speed);
        this.speed = speed;
        if(this.speed == 0) {
            this.speed = 200;
        }
    }

}