package towerdefense.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import towerdefense.logica.EstadoPartida;
import towerdefense.logica.MotorJuego;
import towerdefense.modelo.Oleada;
import towerdefense.modelo.TipoTorre;
import towerdefense.modelo.Torre;

/**
 * Panel derecho con todo lo que el jugador puede hacer: elegir torre, lanzar la
 * siguiente ola, mejorar o vender la torre seleccionada y deshacer o rehacer.
 *
 * No aplica ninguna regla: avisa de lo que se pidio a traves de
 * {@link Acciones} y deja que el motor decida si es posible.
 */
public class PanelControles extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int ANCHO = 272;
    private static final int ALTO_TARJETA = 54;
    private static final int SEPARACION = 6;

    /** Peticiones que este panel puede lanzar. */
    public interface Acciones {
        void alElegirTipoTorre(TipoTorre tipo);

        void alPedirSiguienteOla();

        void alPedirMejorar();

        void alPedirVender();

        void alPedirDeshacer();

        void alPedirRehacer();

        void alPedirVolverAlMenu();
    }

    private final MotorJuego motor;
    private final Acciones acciones;

    private final BotonJuego botonOla = new BotonJuego("SIGUIENTE OLA", Paleta.ACENTO);
    private final BotonJuego botonMejorar = new BotonJuego("Mejorar", Paleta.VIDA);
    private final BotonJuego botonVender = new BotonJuego("Vender", Paleta.PELIGRO);
    private final BotonJuego botonDeshacer = new BotonJuego("Deshacer", Paleta.ACENTO);
    private final BotonJuego botonRehacer = new BotonJuego("Rehacer", Paleta.AVISO);

    private TipoTorre tipoElegido;
    private Torre torreSeleccionada;

    public PanelControles(MotorJuego motor, Acciones acciones) {
        this.motor = motor;
        this.acciones = acciones;

        setLayout(new BorderLayout(0, 8));
        setBackground(Paleta.PANEL_SOLIDO);
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        setPreferredSize(new Dimension(ANCHO, 100));

        add(new Catalogo(), BorderLayout.NORTH);
        add(new Ficha(), BorderLayout.CENTER);
        add(construirBotones(), BorderLayout.SOUTH);

        conectarBotones();
        sincronizar();
    }

    private void conectarBotones() {
        botonOla.addActionListener(e -> acciones.alPedirSiguienteOla());
        botonMejorar.addActionListener(e -> acciones.alPedirMejorar());
        botonVender.addActionListener(e -> acciones.alPedirVender());
        botonDeshacer.addActionListener(e -> acciones.alPedirDeshacer());
        botonRehacer.addActionListener(e -> acciones.alPedirRehacer());
    }

    private JPanel construirBotones() {
        JPanel contenedor = new JPanel(new BorderLayout(0, 6));
        contenedor.setOpaque(false);

        botonOla.setPreferredSize(new Dimension(ANCHO, 40));
        contenedor.add(botonOla, BorderLayout.NORTH);

        JPanel rejilla = new JPanel(new GridLayout(2, 2, 6, 6));
        rejilla.setOpaque(false);
        rejilla.add(botonMejorar);
        rejilla.add(botonVender);
        rejilla.add(botonDeshacer);
        rejilla.add(botonRehacer);
        contenedor.add(rejilla, BorderLayout.CENTER);

        JPanel pie = new JPanel();
        pie.setOpaque(false);
        pie.setLayout(new BoxLayout(pie, BoxLayout.Y_AXIS));

        PanelVolumen volumen = new PanelVolumen();
        volumen.setAlignmentX(CENTER_ALIGNMENT);
        pie.add(volumen);
        pie.add(Box.createVerticalStrut(8));

        BotonJuego botonMenu = new BotonJuego("Menu principal", Paleta.BORDE);
        botonMenu.setPreferredSize(new Dimension(ANCHO, 32));
        botonMenu.setMaximumSize(new Dimension(ANCHO, 32));
        botonMenu.setAlignmentX(CENTER_ALIGNMENT);
        botonMenu.addActionListener(e -> acciones.alPedirVolverAlMenu());
        pie.add(botonMenu);

        contenedor.add(pie, BorderLayout.SOUTH);

        return contenedor;
    }

    // ---------------------------------------------------------------
    // Estado de la seleccion
    // ---------------------------------------------------------------

    public TipoTorre getTipoElegido() {
        return tipoElegido;
    }

    public void setTipoElegido(TipoTorre tipo) {
        this.tipoElegido = tipo;
        repaint();
    }

    public Torre getTorreSeleccionada() {
        return torreSeleccionada;
    }

    public void setTorreSeleccionada(Torre torre) {
        this.torreSeleccionada = torre;
        repaint();
    }

    /** Habilita cada boton segun lo que tenga sentido en este momento. */
    public void sincronizar() {
        boolean enJuego = !motor.getEstado().haTerminado();
        boolean preparando = motor.getEstado() == EstadoPartida.PREPARACION;

        botonOla.setEnabled(enJuego && preparando);
        botonOla.setText(preparando ? "SIGUIENTE OLA" : "OLA EN CURSO");
        botonMejorar.setEnabled(enJuego && torreSeleccionada != null && torreSeleccionada.sePuedeMejorar());
        botonVender.setEnabled(enJuego && torreSeleccionada != null);
        botonDeshacer.setEnabled(enJuego && !motor.getPilaDeshacer().estaVacia());
        botonRehacer.setEnabled(enJuego && !motor.getPilaRehacer().estaVacia());
        repaint();
    }

    // ===============================================================
    // Catalogo de torres
    // ===============================================================

    private class Catalogo extends JPanel {

        private static final long serialVersionUID = 1L;
        private int resaltado = -1;

        Catalogo() {
            setOpaque(false);
            setPreferredSize(new Dimension(ANCHO,
                    22 + TipoTorre.values().length * (ALTO_TARJETA + SEPARACION)));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            MouseAdapter raton = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int nuevo = indiceEn(e.getY());
                    if (nuevo != resaltado) {
                        resaltado = nuevo;
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    resaltado = -1;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    int indice = indiceEn(e.getY());
                    if (indice >= 0) {
                        acciones.alElegirTipoTorre(TipoTorre.values()[indice]);
                    }
                }
            };
            addMouseListener(raton);
            addMouseMotionListener(raton);
        }

        private int indiceEn(int y) {
            int indice = (y - 22) / (ALTO_TARJETA + SEPARACION);
            return y >= 22 && indice >= 0 && indice < TipoTorre.values().length ? indice : -1;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            Paleta.calidad(g2);

            g2.setFont(Paleta.ETIQUETA);
            g2.setColor(Paleta.TEXTO_TENUE);
            g2.drawString("CONSTRUIR TORRE", 2, 14);

            TipoTorre[] tipos = TipoTorre.values();
            for (int i = 0; i < tipos.length; i++) {
                dibujarTarjeta(g2, tipos[i], i);
            }
            g2.dispose();
        }

        private void dibujarTarjeta(Graphics2D g, TipoTorre tipo, int indice) {
            int y = 22 + indice * (ALTO_TARJETA + SEPARACION);
            boolean elegida = tipo == tipoElegido;
            boolean alcanzable = motor.getJugador().puedePagar(tipo.getCosto());
            Color color = Paleta.colorDe(tipo);

            Color fondo = elegida ? Paleta.oscurecer(color, 72)
                    : indice == resaltado ? Paleta.PANEL_CLARO : Paleta.oscurecer(Paleta.PANEL_CLARO, 6);
            Paleta.tarjeta(g, 0, y, getWidth(), ALTO_TARJETA, 9, fondo, elegida ? color : Paleta.BORDE);

            Paleta.halo(g, 24, y + ALTO_TARJETA / 2.0, 13, color, 90);
            g.setColor(color);
            g.fillOval(14, y + ALTO_TARJETA / 2 - 10, 20, 20);

            g.setFont(Paleta.ETIQUETA);
            g.setColor(Paleta.TEXTO);
            g.drawString(tipo.getNombre(), 46, y + 21);

            g.setFont(Paleta.PEQUENO);
            g.setColor(Paleta.TEXTO_TENUE);
            g.drawString("dano " + tipo.getDanio() + "   rango " + tipo.getRango()
                    + "   " + String.format("%.1f/s", tipo.getCadencia()), 46, y + 38);

            g.setFont(Paleta.ETIQUETA);
            g.setColor(alcanzable ? Paleta.ORO : Paleta.PELIGRO);
            String costo = tipo.getCosto() + " oro";
            g.drawString(costo, getWidth() - g.getFontMetrics().stringWidth(costo) - 10, y + 21);
        }
    }

    // ===============================================================
    // Ficha: torre seleccionada o proxima ola
    // ===============================================================

    private class Ficha extends JPanel {

        private static final long serialVersionUID = 1L;

        Ficha() {
            setOpaque(false);
            setPreferredSize(new Dimension(ANCHO, 128));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            Paleta.calidad(g2);
            g2.clipRect(0, 0, getWidth(), getHeight());
            Paleta.tarjeta(g2, 0, 0, getWidth(), getHeight(), 9,
                    Paleta.oscurecer(Paleta.PANEL_CLARO, 6), Paleta.BORDE);

            if (torreSeleccionada != null) {
                dibujarTorre(g2);
            } else {
                dibujarProximaOla(g2);
            }
            g2.dispose();
        }

        private void dibujarTorre(Graphics2D g) {
            Torre torre = torreSeleccionada;

            g.setFont(Paleta.ETIQUETA);
            g.setColor(Paleta.colorDe(torre.getTipo()));
            g.drawString(torre.getTipo().getNombre() + "  ·  nivel "
                    + torre.getNivel() + "/" + Torre.NIVEL_MAXIMO, 12, 20);

            fila(g, "Dano por disparo", String.valueOf(torre.getDanio()), 42, Paleta.TEXTO);
            fila(g, "Rango", torre.getRango() + " px", 60, Paleta.TEXTO);
            fila(g, "Cadencia", String.format("%.1f disparos/s", torre.getTipo().getCadencia()),
                    78, Paleta.TEXTO);
            fila(g, "Invertido", torre.getInversionTotal() + " oro", 96, Paleta.ORO);

            g.setFont(Paleta.PEQUENO);
            if (torre.sePuedeMejorar()) {
                g.setColor(Paleta.VIDA);
                g.drawString("Mejora: " + torre.getCostoMejora() + " oro", 12, 118);
            } else {
                g.setColor(Paleta.AVISO);
                g.drawString("Nivel maximo alcanzado", 12, 118);
            }
        }

        private void dibujarProximaOla(Graphics2D g) {
            g.setFont(Paleta.ETIQUETA);
            g.setColor(Paleta.TEXTO_TENUE);
            g.drawString("PROXIMA OLA", 12, 20);

            Oleada siguiente = motor.verSiguienteOla();
            g.setFont(Paleta.PEQUENO);
            if (siguiente == null) {
                g.setColor(Paleta.VIDA);
                g.drawString("No quedan olas por lanzar.", 12, 42);
                return;
            }

            g.setColor(Paleta.TEXTO);
            g.drawString("Ola " + siguiente.getNumero() + ": " + siguiente.getCantidad()
                    + " x " + siguiente.getTipo().getNombre(), 12, 42);

            g.setColor(Paleta.colorDe(siguiente.getTipo()));
            g.fillOval(12, 54, 10, 10);
            g.setColor(Paleta.TEXTO_TENUE);
            g.drawString("vida " + Math.round(siguiente.getTipo().getVidaBase()
                    * siguiente.getMultiplicadorVida())
                    + "   velocidad " + siguiente.getTipo().getVelocidad(), 28, 63);

            g.setColor(Paleta.TEXTO_TENUE);
            g.drawString("Haz clic sobre una torre del mapa", 12, 90);
            g.drawString("para mejorarla o venderla.", 12, 106);
        }

        /** Fila con la etiqueta a la izquierda y el valor pegado a la derecha. */
        private void fila(Graphics2D g, String etiqueta, String valor, int y, Color colorValor) {
            g.setFont(Paleta.PEQUENO);
            g.setColor(Paleta.TEXTO_TENUE);
            g.drawString(etiqueta, 12, y);

            g.setFont(Paleta.ETIQUETA);
            g.setColor(colorValor);
            g.drawString(valor, getWidth() - g.getFontMetrics().stringWidth(valor) - 12, y);
        }
    }
}
