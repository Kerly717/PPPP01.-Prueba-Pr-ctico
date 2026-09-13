package towerdefense.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import towerdefense.logica.EstadoPartida;
import towerdefense.logica.MotorJuego;

/**
 * Barra superior con los indicadores de la partida: vidas de la base, oro,
 * puntuacion, ola en curso, enemigos vivos y estado.
 */
public class PanelEncabezado extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int ALTO = 66;

    private final MotorJuego motor;

    public PanelEncabezado(MotorJuego motor) {
        this.motor = motor;
        setPreferredSize(new Dimension(100, ALTO));
        setBackground(Paleta.PANEL_SOLIDO);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        Paleta.calidad(g2);

        g2.setColor(Paleta.BORDE);
        g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

        int x = 14;
        x = indicador(g2, x, "VIDAS", motor.getJugador().getVidas()
                + " / " + motor.getJugador().getVidasIniciales(), colorVida());
        x = indicador(g2, x, "ORO", String.valueOf(motor.getJugador().getOro()), Paleta.ORO);
        x = indicador(g2, x, "PUNTOS", String.valueOf(motor.getJugador().getPuntuacion()), Paleta.TEXTO);
        // Durante una ola se muestra la que se esta jugando; entre olas, las superadas.
        int olaMostrada = Math.max(motor.getNumeroOlaActual(), motor.getOlasSuperadas());
        x = indicador(g2, x, "OLA", olaMostrada + " / " + motor.getTotalOlas(), Paleta.ACENTO);
        x = indicador(g2, x, "ENEMIGOS", String.valueOf(motor.getEnemigos().getContador()), Paleta.VIDA);
        indicador(g2, x, "EN COLA", String.valueOf(motor.getColaAparicion().getContador()), Paleta.TEXTO_TENUE);

        dibujarEstado(g2);
        g2.dispose();
    }

    private Color colorVida() {
        double salud = (double) motor.getJugador().getVidas() / motor.getJugador().getVidasIniciales();
        return salud > 0.5 ? Paleta.VIDA : salud > 0.25 ? Paleta.AVISO : Paleta.PELIGRO;
    }

    private int indicador(Graphics2D g, int x, String titulo, String valor, Color color) {
        int ancho = Math.max(96, titulo.length() * 7 + 24);
        Paleta.tarjeta(g, x, 9, ancho, ALTO - 20, 9, Paleta.PANEL_CLARO, Paleta.BORDE);

        g.setFont(Paleta.PEQUENO);
        g.setColor(Paleta.TEXTO_TENUE);
        g.drawString(titulo, x + 11, 26);

        g.setFont(Paleta.NUMERO);
        g.setColor(color);
        g.drawString(valor, x + 11, 49);
        return x + ancho + 9;
    }

    private void dibujarEstado(Graphics2D g) {
        EstadoPartida estado = motor.getEstado();
        String texto;
        Color color;
        switch (estado) {
            case EN_OLEADA:
                texto = "OLA " + motor.getNumeroOlaActual() + " EN CURSO";
                color = Paleta.PELIGRO;
                break;
            case VICTORIA:
                texto = "VICTORIA";
                color = Paleta.VIDA;
                break;
            case DERROTA:
                texto = "DERROTA";
                color = Paleta.PELIGRO;
                break;
            default:
                texto = "PREPARACION";
                color = Paleta.ACENTO;
                break;
        }

        g.setFont(Paleta.ETIQUETA);
        int ancho = g.getFontMetrics().stringWidth(texto) + 26;
        int x = getWidth() - ancho - 14;
        Paleta.tarjeta(g, x, 19, ancho, 28, 14, Paleta.oscurecer(color, 92), Paleta.con(color, 210));
        g.setColor(color);
        Paleta.textoCentrado(g, texto, x + ancho / 2, 38);
    }
}
