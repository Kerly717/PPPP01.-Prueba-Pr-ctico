package towerdefense.logica;

/**
 * Situacion en la que se encuentra la partida.
 */
public enum EstadoPartida {

    /** Entre olas: se puede construir y mejorar con calma. */
    PREPARACION,
    /** Hay enemigos en el mapa o esperando su turno de salir. */
    EN_OLEADA,
    /** Se superaron todas las olas del ciclo. */
    VICTORIA,
    /** La base se quedo sin vidas. */
    DERROTA;

    public boolean haTerminado() {
        return this == VICTORIA || this == DERROTA;
    }
}
