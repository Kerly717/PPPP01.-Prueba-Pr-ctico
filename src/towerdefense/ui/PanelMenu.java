package towerdefense.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Menu principal: portada del juego con el titulo, las opciones y un resumen
 * de las estructuras de datos que usa el proyecto.
 *
 * La animacion del fondo se detiene al salir del menu para no gastar CPU
 * mientras se juega.
 */
public class PanelMenu extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int ESTRELLAS = 40;

    /** Opciones que ofrece el menu. */
    public interface OyenteMenu {
        void alJugar();

        void alSalir();
    }

    private final OyenteMenu oyente;
    private final Timer animacion;

    // Estrellas del fondo: posicion y velocidad de cada una.
    private final double[] estrellaX = new double[ESTRELLAS];
    private final double[] estrellaY = new double[ESTRELLAS];
    private final double[] estrellaVel = new double[ESTRELLAS];

    private double reloj;

    public PanelMenu(OyenteMenu oyente) {
        this.oyente = oyente;

        for (int i = 0; i < ESTRELLAS; i++) {
            estrellaX[i] = Math.random();
            estrellaY[i] = Math.random();
            estrellaVel[i] = 0.01 + Math.random() * 0.04;
        }

        setLayout(new GridBagLayout());
        setBackground(Paleta.FONDO);
        add(construirContenido(), new GridBagConstraints());

        animacion = new Timer(33, this::alCuadro);
    }

    private JPanel construirContenido() {
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(BorderFactory.createEmptyBorder(210, 40, 40, 40));

        BotonJuego jugar = new BotonJuego("JUGAR", Paleta.ACENTO);
        jugar.setPreferredSize(new Dimension(300, 46));
        jugar.setMaximumSize(new Dimension(300, 46));
        jugar.setFont(Paleta.SUBTITULO);
        jugar.setAlignmentX(CENTER_ALIGNMENT);
        jugar.addActionListener(e -> oyente.alJugar());
        contenido.add(jugar);

        contenido.add(Box.createVerticalStrut(10));

        BotonJuego salir = new BotonJuego("SALIR", Paleta.BORDE);
        salir.setPreferredSize(new Dimension(300, 38));
        salir.setMaximumSize(new Dimension(300, 38));
        salir.setAlignmentX(CENTER_ALIGNMENT);
        salir.addActionListener(e -> oyente.alSalir());
        contenido.add(salir);

        contenido.add(Box.createVerticalStrut(22));
        PanelVolumen volumen = new PanelVolumen();
        volumen.setAlignmentX(CENTER_ALIGNMENT);
        volumen.setPreferredSize(new Dimension(300, 108));
        volumen.setMaximumSize(new Dimension(300, 108));
        contenido.add(volumen);

        return contenido;
    }

    public void iniciarAnimacion() {
        animacion.start();
    }

    public void detenerAnimacion() {
        animacion.stop();
    }

    private void alCuadro(ActionEvent evento) {
        double dt = 0.033;
        reloj += dt;
        for (int i = 0; i < ESTRELLAS; i++) {
            estrellaY[i] -= estrellaVel[i] * dt;
            if (estrellaY[i] < 0) {
                estrellaY[i] = 1;
                estrellaX[i] = Math.random();
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        Paleta.calidad(g2);

        g2.setPaint(new GradientPaint(0, 0, Paleta.PANEL_CLARO, getWidth(), getHeight(), Paleta.FONDO));
        g2.fillRect(0, 0, getWidth(), getHeight());

        for (int i = 0; i < ESTRELLAS; i++) {
            int x = (int) (estrellaX[i] * getWidth());
            int y = (int) (estrellaY[i] * getHeight());
            int radio = 1 + (int) (estrellaVel[i] * 60);
            g2.setColor(Paleta.con(i % 3 == 0 ? Paleta.ORO : Paleta.ACENTO, 130));
            g2.fillOval(x, y, radio, radio);
        }

        int centroX = getWidth() / 2;
        double pulso = 0.5 + 0.5 * Math.sin(reloj * 1.8);
        Paleta.halo(g2, centroX, 120, 140, Paleta.ACENTO, (int) (60 + pulso * 40));

        g2.setFont(Paleta.TITULO);
        g2.setColor(Paleta.con(Color.BLACK, 160));
        Paleta.textoCentrado(g2, "TOWER DEFENSE", centroX + 2, 122);
        g2.setColor(Paleta.TEXTO);
        Paleta.textoCentrado(g2, "TOWER DEFENSE", centroX, 120);

        g2.setFont(Paleta.SUBTITULO);
        g2.setColor(Paleta.ACENTO);
        Paleta.textoCentrado(g2, "Defiende la base en el bosque", centroX, 150);

        g2.setFont(Paleta.PEQUENO);
        g2.setColor(Paleta.TEXTO_TENUE);
        Paleta.textoCentrado(g2, "Construye torres, resiste las olas y protege el nucleo", centroX, 174);

        g2.dispose();
    }
}
