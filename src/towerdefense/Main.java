package towerdefense;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import towerdefense.ui.VentanaPrincipal;

/**
 * Punto de entrada de TOWER DEFENSE.
 *
 * Juego por turnos en el que el jugador construye torres a lo largo de un
 * camino para impedir que los enemigos lleguen a su base.
 *
 * Estructuras de datos implementadas a mano (sin colecciones de java.util):
 *
 *   ListaSecuencial - torres colocadas (arreglo con contador)
 *   ListaDoble      - enemigos activos, recorribles en ambos sentidos
 *   ListaCircular   - ciclo de oleadas: tras la ultima vuelve la primera
 *   Cola (FIFO)     - orden de aparicion de los enemigos de la oleada
 *   Pila (LIFO)     - historial de acciones para deshacer y rehacer
 *
 * Organizacion en paquetes:
 *   estructuras - estructuras de datos genericas y reutilizables
 *   modelo      - datos del juego (torre, enemigo, oleada, jugador)
 *   logica      - reglas de la partida, sin nada de interfaz
 *   ui          - ventanas y paneles, sin ninguna regla del juego
 *
 * Compilar y ejecutar:
 *   javac -d build $(find src -name "*.java")
 *   java -cp build towerdefense.Main
 */
public final class Main {

    private Main() {
        // Clase de arranque: no se instancia.
    }

    public static void main(String[] args) {
        aplicarAspectoDelSistema();
        // Toda la interfaz Swing debe construirse en el hilo de eventos (EDT).
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }

    private static void aplicarAspectoDelSistema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedOperationException
                 | javax.swing.UnsupportedLookAndFeelException excepcion) {
            // Si el sistema no ofrece ese aspecto se usa el de Swing por
            // defecto: los paneles dibujan su propio estilo de todas formas.
        }
    }
}
