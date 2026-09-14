package towerdefense.modelo;

/**
 * Catalogo de enemigos. La velocidad se expresa en pixeles por segundo.
 */
public enum TipoEnemigo {

    BASICO("Basico", 60, 70, 12, 11),
    RAPIDO("Rapido", 45, 125, 16, 9),
    BLINDADO("Blindado", 190, 45, 30, 14),
    JEFE("Jefe", 700, 38, 90, 18);

    private final String nombre;
    private final int vidaBase;
    private final int velocidad;
    private final int recompensa;
    private final int radio;
        }

    public String getNombre() {
        return nombre;
    }

    public int getVidaBase() {
        return vidaBase;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public int getRecompensa() {
        return recompensa;
    }

    /** Radio en pixeles con el que se dibuja. */
    public int getRadio() {
        return radio;
    }


    TipoEnemigo(String nombre, int vidaBase, int velocidad, int recompensa, int radio) {
        this.nombre = nombre;
        this.vidaBase = vidaBase;
        this.velocidad = velocidad;
        this.recompensa = recompensa;
        this.radio = radio;

    @Override
    public String toString() {
        return nombre;
    }
}
