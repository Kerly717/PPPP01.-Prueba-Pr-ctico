package towerdefense.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import towerdefense.audio.GestorSonido;

/**
 * Control compacto de audio: una barra para la musica, otra para los efectos
 * y un boton para silenciarlo todo. Se dibuja a mano, como el resto de los
 * paneles, para no meter un JSlider con el aspecto del sistema operativo en
 * medio de la interfaz oscura del juego.
 */
public class PanelVolumen extends JPanel {

    private static final long serialVersionUID = 1L;

    private final BotonJuego botonSilencio = new BotonJuego(textoSilencio(), Paleta.BORDE);

    public PanelVolumen() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        GestorSonido gestor = GestorSonido.getInstancia();
        Deslizador musica = new Deslizador("MUSICA", gestor.getVolumenMusica(), gestor::setVolumenMusica);
        Deslizador efectos = new Deslizador("EFECTOS", gestor.getVolumenEfectos(), gestor::setVolumenEfectos);
        musica.setAlignmentX(CENTER_ALIGNMENT);
        efectos.setAlignmentX(CENTER_ALIGNMENT);

        botonSilencio.setAlignmentX(CENTER_ALIGNMENT);
        botonSilencio.setPreferredSize(new Dimension(160, 26));
        botonSilencio.setMaximumSize(new Dimension(220, 26));
        botonSilencio.addActionListener(e -> {
            gestor.setSilenciado(!gestor.isSilenciado());
            botonSilencio.setText(textoSilencio());
        });

        add(musica);
        add(Box.createVerticalStrut(2));
        add(efectos);
        add(Box.createVerticalStrut(6));
        add(botonSilencio);
    }

    private static String textoSilencio() {
        return GestorSonido.getInstancia().isSilenciado() ? "Sonido: apagado" : "Sonido: encendido";
    }

    /** Barra deslizable simple: se arrastra con el raton para fijar un valor entre 0 y 1. */
    private static class Deslizador extends JPanel {

        private static final long serialVersionUID = 1L;
        private static final int ALTO = 32;

        private final String etiqueta;
        private final Consumer<Float> alCambiar;
        private float valor;

        Deslizador(String etiqueta, float valorInicial, Consumer<Float> alCambiar) {
            this.etiqueta = etiqueta;
            this.valor = valorInicial;
            this.alCambiar = alCambiar;

            setOpaque(false);
            setPreferredSize(new Dimension(220, ALTO));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            MouseAdapter raton = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    actualizar(e.getX());
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    actualizar(e.getX());
                }
            };
            addMouseListener(raton);
            addMouseMotionListener(raton);
        }

        private void actualizar(int x) {
            int margen = 8;
            float nuevo = (x - margen) / (float) Math.max(1, getWidth() - margen * 2);
            valor = Math.max(0f, Math.min(1f, nuevo));
            alCambiar.accept(valor);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            Paleta.calidad(g2);

            g2.setFont(Paleta.PEQUENO);
            g2.setColor(Paleta.TEXTO_TENUE);
            g2.drawString(etiqueta, 2, 12);

            int margen = 8;
            int y = 19;
            int alto = 8;
            int ancho = Math.max(1, getWidth() - margen * 2);
            Paleta.barra(g2, margen, y, ancho, alto, valor, Paleta.ACENTO);

            int cx = margen + (int) (ancho * valor);
            g2.setColor(Paleta.TEXTO);
            g2.fillOval(cx - 5, y + alto / 2 - 5, 10, 10);

            g2.dispose();
        }
    }
}
