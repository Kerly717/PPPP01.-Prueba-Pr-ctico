package towerdefense.mundo;


public class Punto {

    private final double x;
    private final double y;

    public Punto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double distanciaA(Punto otro) {
        return Math.hypot(otro.x - x, otro.y - y);
    }
}
