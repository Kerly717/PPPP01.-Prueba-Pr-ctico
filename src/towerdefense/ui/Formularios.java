package towerdefense.ui;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Ventanas de dialogo del juego, reunidas en un solo sitio para no repetir
 * codigo de JOptionPane por toda la interfaz (DRY).
 */
public final class Formularios {

    private Formularios() {
        // Clase utilitaria.
    }

    public static boolean confirmar(Component padre, String titulo, String pregunta) {
        return JOptionPane.showConfirmDialog(padre, pregunta, titulo,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static void informar(Component padre, String titulo, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void advertir(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Accion no permitida", JOptionPane.WARNING_MESSAGE);
    }
}
