package beaconfinder.fun.berger.de.beaconfinder.util.trilateration;

/**
 * Created by Berger on 22.05.2016.
 */
public class Ponto {


    private double x;
    private double y;

    public Ponto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Ponto() {

    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }
}
