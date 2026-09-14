package towerdefense.modelo;

import towerdefense.mundo.Punto;

/**
 * Torre colocada sobre una casilla de cesped.
 *
 * Dispara sola cada cierto tiempo segun su cadencia; el motor solo le dice
 * cuanto tiempo ha pasado y ella decide si le toca atacar.
 */
public class Torre {

    /** Nivel maximo que se alcanza con mejoras. */
    public static final int NIVEL_MAXIMO = 3;

    /** Cuanto sube el dano y el rango en cada mejora. */
    private static final double AUMENTO_DANIO = 0.45;
    private static final double AUMENTO_RANGO = 0.12;

    private final int id;
    private final TipoTorre tipo;
    private final int columna;
    private final int fila;
    private final Punto posicion;

    private int nivel = 1;
    private int inversionTotal;
    private double recargaRestante;

    // Datos del ultimo disparo, que la vista usa para dibujar el rayo.
    private double tiempoDesdeDisparo = 99;
    private Enemigo ultimoObjetivo;

    public Torre(int id, TipoTorre tipo, int columna, int fila, Punto posicion) {
        this.id = id;
        this.tipo = tipo;
        this.columna = columna;
        this.fila = fila;
        this.posicion = posicion;
        this.inversionTotal = tipo.getCosto();
    }

    /** Descuenta el tiempo de recarga y de la animacion de disparo. */
    public void actualizar(double dt) {
        recargaRestante = Math.max(0, recargaRestante - dt);
        tiempoDesdeDisparo += dt;
    }

    public boolean puedeDisparar() {
        return recargaRestante == 0;
    }

    /** Deja constancia del disparo y reinicia la recarga. */
    public void dispararA(Enemigo objetivo) {
        objetivo.recibirDanio(getDanio());
        ultimoObjetivo = objetivo;
        tiempoDesdeDisparo = 0;
        recargaRestante = 1.0 / tipo.getCadencia();
    }

    public boolean estaEnRango(Enemigo enemigo) {
        return posicion.distanciaA(enemigo.getPosicion()) <= getRango();
    }

    // ---------------------------------------------------------------
    // Mejoras
    // ---------------------------------------------------------------

    public boolean sePuedeMejorar() {
        return nivel < NIVEL_MAXIMO;
    }

    public int getCostoMejora() {
        return tipo.getCosto() * nivel;
    }

    public void mejorar() {
        if (sePuedeMejorar()) {
            inversionTotal += getCostoMejora();
            nivel++;
        }
    }

    // ---------------------------------------------------------------
    // Consultas
    // ---------------------------------------------------------------

    /** El dano crece un porcentaje fijo por cada nivel extra. */
    public int getDanio() {
        return (int) Math.round(tipo.getDanio() * (1 + AUMENTO_DANIO * (nivel - 1)));
    }

    public int getRango() {
        return (int) Math.round(tipo.getRango() * (1 + AUMENTO_RANGO * (nivel - 1)));
    }

    /** Oro gastado en total, incluidas las mejoras. */
    public int getInversionTotal() {
        return inversionTotal;
    }

    /** Se ve el rayo del disparo durante un instante despues de atacar. */
    public boolean estaDisparando() {
        return tiempoDesdeDisparo < 0.16;
    }

    public Enemigo getUltimoObjetivo() {
        return ultimoObjetivo;
    }

    public void olvidarObjetivo() {
        ultimoObjetivo = null;
    }

    public int getId() {
        return id;
    }

    public TipoTorre getTipo() {
        return tipo;
    }

    public int getColumna() {
        return columna;
    }

    public int getFila() {
        return fila;
    }

    public Punto getPosicion() {
        return posicion;
    }

    public int getNivel() {
        return nivel;
    }

    @Override
    public String toString() {
        return tipo.getNombre() + " #" + id + " (nivel " + nivel + ")";
    }
}
