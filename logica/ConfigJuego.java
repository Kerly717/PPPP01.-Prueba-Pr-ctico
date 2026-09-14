package towerdefense.logica;

/**
 * Constantes del juego reunidas en un solo sitio, para que ningun numero
 * magico quede suelto por el codigo (principio DRY).
 */
public final class ConfigJuego {

    /** Lado en pixeles de cada casilla del mapa. */
    public static final int TAM_CASILLA = 38;

    /** Fotogramas por segundo del bucle de juego. */
    public static final int FPS = 60;
    public static final int MILIS_POR_CUADRO = 1000 / FPS;

    /** Con que empieza el jugador. */
    public static final int VIDAS_INICIALES = 10;
    public static final int ORO_INICIAL = 200;

    /** Maximo de torres que caben en la lista secuencial. */
    public static final int MAXIMO_TORRES = 24;

    /** Segundos entre la salida de dos enemigos de la misma ola. */
    public static final double INTERVALO_APARICION = 0.85;

    /** Oro extra al limpiar una ola. */
    public static final int BONO_POR_OLA = 40;

    /** Oro que se recupera al vender (porcentaje de lo invertido). */
    public static final double REEMBOLSO_VENTA = 0.60;

    /** Segundos que permanece en pantalla el cartel de "ola completada". */
    public static final double DURACION_CARTEL = 4.0;

    /** Mensajes que conserva la bitacora antes de descartar los mas viejos. */
    public static final int MAXIMO_EVENTOS = 200;

    private ConfigJuego() {
        // Clase de constantes: no se instancia.
    }
}
