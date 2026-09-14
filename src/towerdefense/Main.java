package towerdefense;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import towerdefense.ui.VentanaPrincipal;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        aplicarAspectoDelSistema();
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }

    private static void aplicarAspectoDelSistema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedOperationException
                 | javax.swing.UnsupportedLookAndFeelException excepcion) {
        }
    }
}
