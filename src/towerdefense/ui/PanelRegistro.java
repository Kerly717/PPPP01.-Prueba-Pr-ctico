package towerdefense.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import towerdefense.estructuras.NodoDoble;
import towerdefense.logica.Evento;
import towerdefense.logica.MotorJuego;

/**
 * Bitacora de la partida, del mensaje mas reciente al mas antiguo.
 *
 * Recorre la lista doble del registro HACIA ATRAS empezando por el ultimo nodo:
 * es el uso practico del doble enlace, porque con una lista simple habria que
 * recorrerla entera para llegar al final.
 *
 * Ocupa todo el ancho inferior de la ventana y reparte los mensajes en varias
 * columnas para aprovechar el espacio.
 */
public class PanelRegistro extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int ALTO = 168;
    private static final int ANCHO_COLUMNA = 340;
    private static final int ALTO_LINEA = 16;

    private final MotorJuego motor;

    public PanelRegistro(MotorJuego motor) {
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
        g2.drawLine(0, 0, getWidth(), 0);

        String titulo = "BITACORA DE LA PARTIDA";
        g2.setFont(Paleta.ETIQUETA);
        g2.setColor(Paleta.ACENTO);
        g2.drawString(titulo, 16, 22);

        // El detalle se coloca despues del titulo midiendo su ancho real, para
        // que nunca se solapen aunque cambie la fuente.
        int inicioDetalle = 16 + g2.getFontMetrics().stringWidth(titulo) + 16;
        g2.setFont(Paleta.PEQUENO);
        g2.setColor(Paleta.TEXTO_TENUE);
        g2.drawString(motor.getRegistro().getContador()
                + " eventos  ·  del mas reciente al mas antiguo", inicioDetalle, 22);

        dibujarMensajes(g2);
        g2.dispose();
    }

    private void dibujarMensajes(Graphics2D g) {
        int lineasPorColumna = Math.max(1, (getHeight() - 44) / ALTO_LINEA);
        int columnas = Math.max(1, (getWidth() - 20) / ANCHO_COLUMNA);
        int anchoTexto = ANCHO_COLUMNA - 20;

        g.setFont(Paleta.PEQUENO);
        int indice = 0;

        for (NodoDoble<Evento> nodo = motor.getRegistro().getNodoUltimo();
                nodo != null && indice < lineasPorColumna * columnas; nodo = nodo.getAnterior()) {
            int columna = indice / lineasPorColumna;
            int linea = indice % lineasPorColumna;
            int x = 16 + columna * ANCHO_COLUMNA;
            int y = 44 + linea * ALTO_LINEA;

            Evento evento = nodo.getDato();
            g.setColor(Paleta.colorDe(evento.getNivel()));
            g.drawString(Paleta.recortar(g, evento.toString(), anchoTexto), x, y);
            indice++;
        }

        if (motor.getRegistro().getContador() == 0) {
            g.setColor(Paleta.TEXTO_TENUE);
            g.drawString("(sin eventos todavia)", 16, 44);
        }
    }
}
