package beaconfinder.fun.berger.de.beaconfinder.util.trilateration;

/**
 * Created by Berger on 24.05.2016.
 */
public class Tril {
    double sqr(double a) {
        return a * a;
    }

    public double norm(Ponto a) {
        return Math.sqrt(sqr(a.getX()) + sqr(a.getY()) + sqr(a.getZ()));
    }

    public double dot(Ponto a, Ponto b) {
        return a.getX() * b.getX() + a.getY() * b.getY() + a.getZ() * b.getZ();
    }


    Ponto vector_subtract(Ponto a, Ponto b) {


        double x = a.getX() - b.getX();
        double y = a.getY() - b.getY();
        double z = a.getZ() - b.getZ();
        Ponto p = new Ponto();
        p.setX(x);
        p.setY(y);
        p.setZ(z);
        return p;
    }

    Ponto vector_add(Ponto a, Ponto b) {


        double x = a.getX() + b.getX();
        double y = a.getY() + b.getY();
        double z = a.getZ() + b.getZ();
        Ponto p = new Ponto();
        p.setX(x);
        p.setY(y);
        p.setZ(z);
        return p;
    }

    Ponto vector_divide(Ponto a, double b) {


        double x = a.getX() / b;
        double y = a.getY() / b;
        double z = a.getZ() / b;
        Ponto p = new Ponto();
        p.setX(x);
        p.setY(y);
        p.setZ(z);
        return p;
    }


    Ponto vector_multiply(Ponto a, double b) {
        double x = a.getX() * b;
        double y = a.getY() * b;
        double z = a.getZ() * b;
        Ponto p = new Ponto();
        p.setX(x);
        p.setY(y);
        p.setZ(z);
        return p;
    }

    Ponto vector_cross(Ponto a, Ponto b) {
        double x = a.getY() * b.getZ() - a.getZ() * b.getY();
        double y = a.getZ() * b.getX() - a.getX() * b.getZ();
        double z = a.getX() * b.getY() - a.getY() * b.getX();
        Ponto p = new Ponto();
        p.setX(x);
        p.setY(y);
        p.setZ(z);
        return p;

    }

    public Ponto trilaterate(Ponto p1, Ponto p2, Ponto p3, boolean return_middle) {
        // based on: https://en.wikipedia.org/wiki/Trilateration

        // some additional local functions declared here for
        // scalar and vector operations


        double i, j, d, x, y, z;
        Ponto a;
        Ponto ey;
        Ponto ez;
        Ponto p4a;
        Ponto p4b;
        Ponto ex;
        //ex
        ex = vector_divide(vector_subtract(p2, p1), norm(vector_subtract(p2, p1)));

        i = dot(ex, vector_subtract(p3, p1));
        a = vector_subtract(vector_subtract(p3, p1), vector_multiply(ex, i));
        ey = vector_divide(a, norm(a));
        ez = vector_cross(ex, ey);
        d = norm(vector_subtract(p2, p1));
        j = dot(ey, vector_subtract(p3, p1));

        x = (sqr(p1.getR()) - sqr(p2.getR()) + sqr(d)) / (2 * d);
        y = (sqr(p1.getR()) - sqr(p3.getR()) + sqr(i) + sqr(j)) / (2 * j) - (i / j) * x;
        z = Math.sqrt(sqr(p1.getR()) - sqr(x) - sqr(y));

        // no solution found
//        if (new Double(z).isNaN()) {
//            return null;
//        }

        a = vector_add(p1, vector_add(vector_multiply(ex, x), vector_multiply(ey, y)));
        p4a = vector_add(a, vector_multiply(ez, z));
        p4b = vector_subtract(a, vector_multiply(ez, z));

        if (z == 0 || return_middle) {
            return a;
        } else {
            return p4a;
        }
    }

}
