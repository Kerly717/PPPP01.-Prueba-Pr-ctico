package towerdefense.modelo;

import towerdefense.mundo.Punto;
import towerdefense.mundo.Ruta;

/**
 * Enemigo que recorre la ruta hacia la base.
 *
 * Su posicion no es una casilla sino los pixeles que lleva recorridos sobre el
 * trazado; la ruta se encarga de traducir esa distancia a coordenadas. Gracias
 * a eso el enemigo se mueve en linea continua y toma bien las curvas.
 */
public class Enemigo {

    private final int id;
    private final TipoEnemigo tipo;
    private final Ruta ruta;
    private final int vidaMaxima;
    private final double velocidad;

    private int vida;
    private double distanciaRecorrida;
    private double destelloGolpe;

    public Enemigo(int id, TipoEnemigo tipo, Ruta ruta, double multiplicadorVida) {
        this.id = id;
        this.tipo = tipo;
        this.ruta = ruta;
        this.vidaMaxima = Math.max(1, (int) Math.round(tipo.getVidaBase() * multiplicadorVida));
        this.velocidad = tipo.getVelocidad();
        this.vida = vidaMaxima;
    }

    /**
     * Avanza por la ruta.
     *
     * @param dt segundos transcurridos desde el fotograma anterior.
     */
    public void avanzar(double dt) {
        distanciaRecorrida += velocidad * dt;
        destelloGolpe = Math.max(0, destelloGolpe - dt);
    }

    public void recibirDanio(int danio) {
        vida = Math.max(0, vida - danio);
        destelloGolpe = 0.12;
    }

    public boolean estaDestruido() {
        return vida == 0;
    }

    public boolean llegoALaBase() {
        return distanciaRecorrida >= ruta.getLongitudTotal();
    }

    public Punto getPosicion() {
        return ruta.puntoEn(distanciaRecorrida);
    }

    public double getPorcentajeVida() {
        return (double) vida / vidaMaxima;
    }

    /** Mayor que cero justo despues de recibir un impacto: la vista lo ilumina. */
    public double getDestelloGolpe() {
        return destelloGolpe;
    }

    public int getId() {
        return id;
    }

    public TipoEnemigo getTipo() {
        return tipo;
    }

    public int getVida() {
        return vida;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public double getDistanciaRecorrida() {
        return distanciaRecorrida;
    }

    /** Oro que entrega al ser destruido. */
    public int getRecompensa() {
        return tipo.getRecompensa();
    }

    @Override
    public String toString() {
        return "Enemigo #" + id + " " + tipo.getNombre() + " (vida " + vida + "/" + vidaMaxima + ")";
    }
}
