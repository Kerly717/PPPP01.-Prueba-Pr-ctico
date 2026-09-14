package towerdefense.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import towerdefense.audio.GestorSonido;

/**
 * Boton dibujado a mano para que todos los botones del juego compartan el
 * mismo estilo, con estados de reposo, cursor encima, pulsado y deshabilitado.
 */
public class BotonJuego extends JButton {

    private static final long serialVersionUID = 1L;

    private final Color acento;
    private boolean encima;
    private boolean pulsado;

    public BotonJuego(String texto, Color acento) {
        super(texto);
        this.acento = acento;

        setFont(Paleta.ETIQUETA);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(130, 34));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                encima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = false;
                pulsado = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pulsado = true;
                if (isEnabled()) {
                    GestorSonido.getInstancia().reproducirClicUi();
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pulsado = false;
                repaint();
            }
        });
    }

   
}
