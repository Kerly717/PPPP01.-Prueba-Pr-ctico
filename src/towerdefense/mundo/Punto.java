package towerdefense.mundo;

/**
 * Punto inmutable en pixeles dentro del mapa.
 */
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

    /** Distancia en linea recta hasta otro punto. */
    public double distanciaA(Punto otro) {
        return Math.hypot(otro.x - x, otro.y - y);
    }
}
