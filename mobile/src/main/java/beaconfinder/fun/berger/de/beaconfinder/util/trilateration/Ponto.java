package beaconfinder.fun.berger.de.beaconfinder.util.trilateration;

/**
 * Created by Berger on 22.05.2016.
 */
public class Ponto {


    private double x;
    private double y;
    private double z;
    private double r;


    public Ponto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Ponto(double x, double y, double z, double r) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
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

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }
}
