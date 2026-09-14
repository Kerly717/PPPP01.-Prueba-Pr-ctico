package towerdefense.modelo;

/**
 * Catalogo de torres. Al ser un enum, el jugador elige de una lista cerrada y
 * el juego queda a salvo de valores imposibles.
 *
 * El rango se mide en pixeles y la cadencia en disparos por segundo, porque el
 * juego transcurre en tiempo real.
 */
public enum TipoTorre {

    ARQUERO("Arquero", "Barata y de disparo rapido. La base de la defensa.", 50, 12, 95, 1.6),
    CANON("Canon", "Golpe fuerte pero lento, ideal contra blindados.", 90, 34, 82, 0.7),
    MAGO("Mago", "Alcance largo para cubrir varias curvas del camino.", 120, 18, 135, 1.3);

    private final String nombre;
    private final String descripcion;
    private final int costo;
    private final int danio;
    private final int rango;
    private final double cadencia;

    TipoTorre(String nombre, String descripcion, int costo, int danio, int rango, double cadencia) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costo = costo;
        this.danio = danio;
        this.rango = rango;
        this.cadencia = cadencia;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCosto() {
        return costo;
    }

    public int getDanio() {
        return danio;
    }

    /** Alcance en pixeles. */
    public int getRango() {
        return rango;
    }

    /** Disparos por segundo. */
    public double getCadencia() {
        return cadencia;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
