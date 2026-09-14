package towerdefense.logica;

/**
 * Mensaje que el motor genera para contar lo que va pasando. La interfaz
 * decide como mostrarlo (color, icono, ventana), el motor solo lo describe.
 */
public class Evento {

    /** Importancia del mensaje; la interfaz la usa para elegir el color. */
    public enum Nivel { INFO, EXITO, AVISO, PELIGRO }

    private final String mensaje;
    private final Nivel nivel;
    private final int segundo;

    public Evento(String mensaje, Nivel nivel, int segundo) {
        this.mensaje = mensaje;
        this.nivel = nivel;
        this.segundo = segundo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Nivel getNivel() {
        return nivel;
    }

    /** Segundo de la partida en que ocurrio. */
    public int getSegundo() {
        return segundo;
    }

    @Override
    public String toString() {
        return "[" + segundo + "s] " + mensaje;
    }
}
