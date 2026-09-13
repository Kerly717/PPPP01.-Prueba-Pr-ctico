package towerdefense.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import towerdefense.estructuras.NodoDoble;
import towerdefense.logica.ConfigJuego;
import towerdefense.logica.MotorJuego;
import towerdefense.modelo.Enemigo;
import towerdefense.modelo.TipoTorre;
import towerdefense.modelo.Torre;
import towerdefense.mundo.Mapa;
import towerdefense.mundo.Punto;

/**
 * Dibuja el bosque, el camino en zigzag, las torres y los enemigos.
 *
 * El terreno (cesped, arboles, rocas y el trazado del camino) nunca cambia, asi
 * que se pinta UNA sola vez sobre una imagen en memoria y despues solo se copia.
 * Repetir ese dibujo sesenta veces por segundo seria un desperdicio.
 *
 * Es una vista pasiva: lee el estado del motor y lo muestra, nunca lo modifica.
 */
public class LienzoMapa extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int TAM = ConfigJuego.TAM_CASILLA;

    /** Aviso de que el jugador hizo clic en una casilla. */
    public interface OyenteMapa {
        void alPulsarCasilla(int columna, int fila);
    }

    private final MotorJuego motor;
    private final BufferedImage terreno;

    private OyenteMapa oyente;
    private TipoTorre tipoElegido;
    private Torre torreSeleccionada;
    private int columnaCursor = -1;
    private int filaCursor = -1;
    private double reloj;

    public LienzoMapa(MotorJuego motor) {
        this.motor = motor;
        this.terreno = dibujarTerreno();

        setPreferredSize(new Dimension(motor.getMapa().getAnchoPixeles(),
                motor.getMapa().getAltoPixeles()));
        setBackground(Paleta.FONDO);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        instalarRaton();
    }

    // ===============================================================
    // ENTRADA DEL RATON
    // ===============================================================

    private void instalarRaton() {
        MouseAdapter raton = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evento) {
                if (oyente != null && dentro(evento)) {
                    oyente.alPulsarCasilla(evento.getX() / TAM, evento.getY() / TAM);
                }
            }

            @Override
            public void mouseMoved(MouseEvent evento) {
                columnaCursor = dentro(evento) ? evento.getX() / TAM : -1;
                filaCursor = dentro(evento) ? evento.getY() / TAM : -1;
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                columnaCursor = -1;
                filaCursor = -1;
            }

            private boolean dentro(MouseEvent evento) {
                return motor.getMapa().dentro(evento.getX() / TAM, evento.getY() / TAM);
            }
        };
        addMouseListener(raton);
        addMouseMotionListener(raton);
    }

    public void setOyente(OyenteMapa oyente) {
        this.oyente = oyente;
    }

    public void setTipoElegido(TipoTorre tipo) {
        this.tipoElegido = tipo;
    }

    public void setTorreSeleccionada(Torre torre) {
        this.torreSeleccionada = torre;
    }

    /** Reloj propio de la vista para los brillos y pulsos. */
    public void avanzarReloj(double dt) {
        reloj += dt;
    }

    // ===============================================================
    // TERRENO (se dibuja una sola vez)
    // ===============================================================

    private BufferedImage dibujarTerreno() {
        Mapa mapa = motor.getMapa();
        BufferedImage imagen = new BufferedImage(mapa.getAnchoPixeles(), mapa.getAltoPixeles(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        Paleta.calidad(g);

        dibujarCesped(g, mapa);
        dibujarSendero(g);
        dibujarVegetacion(g, mapa);

        g.dispose();
        return imagen;
    }

    private void dibujarCesped(Graphics2D g, Mapa mapa) {
        for (int columna = 0; columna < mapa.getColumnas(); columna++) {
            for (int fila = 0; fila < mapa.getFilas(); fila++) {
                g.setColor((columna + fila) % 2 == 0 ? Paleta.CESPED_A : Paleta.CESPED_B);
                g.fillRect(columna * TAM, fila * TAM, TAM, TAM);
                dibujarMatas(g, columna, fila);
            }
        }
    }

    /** Briznas de hierba en posiciones fijas, para que el mapa no parpadee. */
    private void dibujarMatas(Graphics2D g, int columna, int fila) {
        int semilla = Math.abs(columna * 73856093 ^ fila * 19349663);
        g.setColor(Paleta.con(Paleta.HOJA, 70));
        for (int i = 0; i < 3; i++) {
            int x = columna * TAM + 4 + (semilla * (i + 3)) % (TAM - 8);
            int y = fila * TAM + 6 + (semilla * (i + 7)) % (TAM - 10);
            g.drawLine(x, y, x, y - 4);
        }
    }

    /**
     * El camino se pinta como una linea gruesa que pasa por todos los puntos de
     * la ruta. Al usar extremos y uniones redondeadas, las curvas del zigzag
     * salen suaves sin tener que dibujar casilla por casilla.
     */
    private void dibujarSendero(Graphics2D g) {
        var puntos = motor.getRuta().getPuntos();

        trazarRuta(g, puntos, TAM * 0.86f, Paleta.CAMINO_BORDE);
        trazarRuta(g, puntos, TAM * 0.70f, Paleta.CAMINO);
        trazarRuta(g, puntos, TAM * 0.30f, Paleta.con(Paleta.aclarar(Paleta.CAMINO, 18), 90));
    }

    private void trazarRuta(Graphics2D g, towerdefense.estructuras.ListaSecuencial<Punto> puntos,
                            float grosor, Color color) {
        g.setColor(color);
        g.setStroke(new BasicStroke(grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 1; i < puntos.getContador(); i++) {
            Punto desde = puntos.obtener(i - 1);
            Punto hasta = puntos.obtener(i);
            g.drawLine((int) desde.getX(), (int) desde.getY(), (int) hasta.getX(), (int) hasta.getY());
        }
    }

    private void dibujarVegetacion(Graphics2D g, Mapa mapa) {
        for (int columna = 0; columna < mapa.getColumnas(); columna++) {
            for (int fila = 0; fila < mapa.getFilas(); fila++) {
                Punto centro = mapa.centroDe(columna, fila);
                if (mapa.getCasilla(columna, fila) == Mapa.Casilla.ARBOL) {
                    dibujarArbol(g, centro);
                } else if (mapa.getCasilla(columna, fila) == Mapa.Casilla.ROCA) {
                    dibujarRoca(g, centro);
                }
            }
        }
    }

    private void dibujarArbol(Graphics2D g, Punto centro) {
        int x = (int) centro.getX();
        int y = (int) centro.getY();

        g.setColor(Paleta.con(Color.BLACK, 70));
        g.fillOval(x - 13, y + 6, 26, 9);
        g.setColor(Paleta.TRONCO);
        g.fillRect(x - 3, y - 2, 6, 12);
        g.setColor(Paleta.HOJA);
        g.fillOval(x - 14, y - 20, 28, 24);
        g.setColor(Paleta.HOJA_CLARA);
        g.fillOval(x - 10, y - 20, 15, 14);
    }

    private void dibujarRoca(Graphics2D g, Punto centro) {
        int x = (int) centro.getX();
        int y = (int) centro.getY();

        g.setColor(Paleta.con(Color.BLACK, 70));
        g.fillOval(x - 12, y + 3, 24, 8);
        g.setColor(Paleta.PIEDRA);
        g.fillOval(x - 12, y - 8, 24, 17);
        g.setColor(Paleta.aclarar(Paleta.PIEDRA, 30));
        g.fillOval(x - 8, y - 7, 11, 7);
    }

    // ===============================================================
    // DIBUJO POR FOTOGRAMA
    // ===============================================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        Paleta.calidad(g2);

        g2.drawImage(terreno, 0, 0, null);
        dibujarEntradaYBase(g2);
        dibujarCasillaBajoCursor(g2);
        dibujarRangoSeleccionado(g2);
        dibujarTorres(g2);
        dibujarDisparos(g2);
        dibujarEnemigos(g2);
        dibujarCartel(g2);
        dibujarAyuda(g2);

        g2.dispose();
    }

    private void dibujarEntradaYBase(Graphics2D g) {
        var puntos = motor.getRuta().getPuntos();
        Punto entrada = puntos.obtener(0);
        Punto base = puntos.obtener(puntos.getContador() - 1);
        double pulso = 0.5 + 0.5 * Math.sin(reloj * 3);

        Paleta.halo(g, entrada.getX(), entrada.getY(), 16 + pulso * 4, Paleta.PELIGRO, 110);
        g.setFont(Paleta.PEQUENO);
        g.setColor(Paleta.TEXTO_TENUE);
        g.drawString("ENTRADA", 6, (int) entrada.getY() - 22);

        double salud = (double) motor.getJugador().getVidas() / motor.getJugador().getVidasIniciales();
        Color color = salud > 0.5 ? Paleta.VIDA : salud > 0.25 ? Paleta.AVISO : Paleta.PELIGRO;
        int x = (int) base.getX();
        int y = (int) base.getY();

        Paleta.halo(g, x, y, 22 + pulso * 5, color, 120);
        g.setColor(color);
        int[] xs = {x - 14, x - 14, x - 7, x, x + 7, x + 14, x + 14};
        int[] ys = {y + 14, y - 5, y - 13, y - 5, y - 13, y - 5, y + 14};
        g.fillPolygon(xs, ys, 7);
        g.setColor(Paleta.oscurecer(color, 60));
        g.drawPolygon(xs, ys, 7);
        g.setColor(Paleta.TEXTO_TENUE);
        Paleta.textoCentrado(g, "BASE", x - 8, y - 20);
    }

    /** Resalta la casilla apuntada: verde si se puede construir, roja si no. */
    private void dibujarCasillaBajoCursor(Graphics2D g) {
        if (columnaCursor < 0 || tipoElegido == null) {
            return;
        }
        boolean valido = motor.getMapa().sePuedeConstruir(columnaCursor, filaCursor)
                && motor.getJugador().puedePagar(tipoElegido.getCosto());
        Color color = valido ? Paleta.VIDA : Paleta.PELIGRO;

        g.setColor(Paleta.con(color, 60));
        g.fillRect(columnaCursor * TAM, filaCursor * TAM, TAM, TAM);
        g.setColor(Paleta.con(color, 200));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(columnaCursor * TAM, filaCursor * TAM, TAM, TAM);

        if (valido) {
            dibujarCirculoRango(g, motor.getMapa().centroDe(columnaCursor, filaCursor),
                    tipoElegido.getRango(), color);
        }
    }

    /** Circulo semitransparente con el alcance de la torre seleccionada. */
    private void dibujarRangoSeleccionado(Graphics2D g) {
        if (torreSeleccionada == null) {
            return;
        }
        Color color = Paleta.colorDe(torreSeleccionada.getTipo());
        dibujarCirculoRango(g, torreSeleccionada.getPosicion(), torreSeleccionada.getRango(), color);

        g.setColor(Paleta.con(Paleta.TEXTO, 220));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(torreSeleccionada.getColumna() * TAM + 1, torreSeleccionada.getFila() * TAM + 1,
                TAM - 2, TAM - 2);
    }

    private void dibujarCirculoRango(Graphics2D g, Punto centro, int rango, Color color) {
        int x = (int) (centro.getX() - rango);
        int y = (int) (centro.getY() - rango);

        g.setColor(Paleta.con(color, 34));
        g.fillOval(x, y, rango * 2, rango * 2);
        // El desfase de la linea punteada debe ser positivo: BasicStroke rechaza
        // valores negativos. Se usa el resto para que gire en bucle.
        float desfase = (float) ((reloj * 16) % 14);
        g.setColor(Paleta.con(color, 170));
        g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, new float[]{7f, 7f}, desfase));
        g.drawOval(x, y, rango * 2, rango * 2);
    }

    private void dibujarTorres(Graphics2D g) {
        for (int i = 0; i < motor.getTorres().getContador(); i++) {
            dibujarTorre(g, motor.getTorres().obtener(i));
        }
    }

    private void dibujarTorre(Graphics2D g, Torre torre) {
        Color color = Paleta.colorDe(torre.getTipo());
        int x = (int) torre.getPosicion().getX();
        int y = (int) torre.getPosicion().getY();
        boolean disparando = torre.estaDisparando();

        g.setColor(Paleta.con(Color.BLACK, 80));
        g.fillOval(x - 14, y + 4, 28, 10);

        Paleta.halo(g, x, y, disparando ? 19 : 13, color, disparando ? 180 : 85);
        g.setColor(Paleta.oscurecer(color, 50));
        g.fillRoundRect(x - 12, y - 12, 24, 26, 8, 8);
        g.setColor(color);
        g.fillOval(x - 9, y - 13, 18, 18);
        g.setColor(Paleta.aclarar(color, 70));
        g.fillOval(x - 4, y - 10, 7, 7);

        dibujarEstrellasDeNivel(g, torre, x, y);
    }

    /** Una estrella por nivel extra, debajo de la torre. */
    private void dibujarEstrellasDeNivel(Graphics2D g, Torre torre, int x, int y) {
        if (torre.getNivel() <= 1) {
            return;
        }
        g.setColor(Paleta.ORO);
        int inicio = x - (torre.getNivel() - 1) * 4;
        for (int i = 0; i < torre.getNivel() - 1; i++) {
            g.fillOval(inicio + i * 8, y + 14, 5, 5);
        }
    }

    /** Rayo de la torre a su objetivo y destello en el punto de impacto. */
    private void dibujarDisparos(Graphics2D g) {
        for (int i = 0; i < motor.getTorres().getContador(); i++) {
            Torre torre = motor.getTorres().obtener(i);
            Enemigo objetivo = torre.getUltimoObjetivo();
            if (!torre.estaDisparando() || objetivo == null) {
                continue;
            }
            Punto destino = objetivo.getPosicion();
            Color color = Paleta.colorDe(torre.getTipo());

            g.setColor(Paleta.con(color, 210));
            g.setStroke(new BasicStroke(2.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine((int) torre.getPosicion().getX(), (int) torre.getPosicion().getY() - 6,
                    (int) destino.getX(), (int) destino.getY());

            Paleta.halo(g, destino.getX(), destino.getY(), 12, color, 170);
        }
    }

    private void dibujarEnemigos(Graphics2D g) {
        for (NodoDoble<Enemigo> nodo = motor.getEnemigos().getPrimero();
                nodo != null; nodo = nodo.getSiguiente()) {
            dibujarEnemigo(g, nodo.getDato());
        }
    }

    private void dibujarEnemigo(Graphics2D g, Enemigo enemigo) {
        Punto posicion = enemigo.getPosicion();
        int x = (int) posicion.getX();
        int y = (int) posicion.getY();
        int radio = enemigo.getTipo().getRadio();
        boolean golpeado = enemigo.getDestelloGolpe() > 0;
        Color color = golpeado ? Color.WHITE : Paleta.colorDe(enemigo.getTipo());

        g.setColor(Paleta.con(Color.BLACK, 90));
        g.fillOval(x - radio, y + radio - 4, radio * 2, radio);

        // Balanceo suave para que parezca que camina.
        int salto = (int) (Math.sin(reloj * 9 + enemigo.getId()) * 2);
        g.setColor(Paleta.oscurecer(color, 45));
        g.fillOval(x - radio, y - radio + salto, radio * 2, radio * 2);
        g.setColor(color);
        g.fillOval(x - radio + 2, y - radio + 1 + salto, radio * 2 - 4, radio * 2 - 4);
        g.setColor(new Color(0x14181F));
        g.fillOval(x - radio / 2 - 1, y - 2 + salto, 4, 4);
        g.fillOval(x + radio / 2 - 3, y - 2 + salto, 4, 4);

        dibujarBarraDeVida(g, enemigo, x, y - radio - 9);
    }

    /** Barra de vida sobre la cabeza del enemigo. */
    private void dibujarBarraDeVida(Graphics2D g, Enemigo enemigo, int centroX, int y) {
        int ancho = enemigo.getTipo().getRadio() * 2 + 6;
        double salud = enemigo.getPorcentajeVida();
        Color color = salud > 0.55 ? Paleta.VIDA : salud > 0.25 ? Paleta.AVISO : Paleta.PELIGRO;
        Paleta.barra(g, centroX - ancho / 2, y, ancho, 5, salud, color);
    }

    /** Cartel grande que anuncia el inicio o el final de una ola. */
    private void dibujarCartel(Graphics2D g) {
        String texto = motor.getCartel();
        if (texto == null) {
            return;
        }
        int alfa = (int) (235 * motor.getOpacidadCartel());
        g.setFont(Paleta.SUBTITULO);
        int ancho = g.getFontMetrics().stringWidth(texto) + 48;
        int x = (getWidth() - ancho) / 2;

        Paleta.tarjeta(g, x, 22, ancho, 46, 12,
                Paleta.con(Paleta.PANEL_SOLIDO, alfa), Paleta.con(Paleta.ACENTO, alfa));
        g.setColor(Paleta.con(Paleta.TEXTO, alfa));
        Paleta.textoCentrado(g, texto, getWidth() / 2, 51);
    }

    /** Ayuda contextual en la esquina inferior. */
    private void dibujarAyuda(Graphics2D g) {
        String linea = tipoElegido != null
                ? tipoElegido.getNombre() + " lista: haz clic en una casilla de cesped"
                : torreSeleccionada != null
                    ? "Torre seleccionada: puedes mejorarla o venderla en el panel"
                    : "Elige una torre en el panel derecho para construir";

        g.setFont(Paleta.PEQUENO);
        int ancho = g.getFontMetrics().stringWidth(linea) + 24;
        int y = getHeight() - 30;

        Paleta.tarjeta(g, 12, y, ancho, 22, 8, Paleta.con(Paleta.PANEL_SOLIDO, 225), Paleta.BORDE);
        g.setColor(tipoElegido != null ? Paleta.colorDe(tipoElegido) : Paleta.TEXTO_TENUE);
        g.drawString(linea, 24, y + 15);
    }
}
