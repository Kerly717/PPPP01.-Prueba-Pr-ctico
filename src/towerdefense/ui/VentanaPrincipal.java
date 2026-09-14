package towerdefense.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import towerdefense.audio.GestorSonido;

/**
 * Ventana principal: alterna entre el menu y la partida con un CardLayout y se
 * encarga del ciclo de vida de cada pantalla, deteniendo los temporizadores de
 * la que se abandona para no dejar nada corriendo de fondo.
 */
public class VentanaPrincipal extends JFrame
        implements PanelMenu.OyenteMenu, PanelJuego.OyentePartida {

    private static final long serialVersionUID = 1L;
    private static final String TARJETA_MENU = "menu";
    private static final String TARJETA_JUEGO = "juego";

    private final CardLayout tarjetas = new CardLayout();
    private final PanelMenu panelMenu;
    private PanelJuego panelJuego;

    public VentanaPrincipal() {
        super("Tower Defense - Estructura de Datos");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(tarjetas);

        panelMenu = new PanelMenu(this);
        add(panelMenu, TARJETA_MENU);

        setMinimumSize(new Dimension(1100, 680));
        setPreferredSize(tamanoInicial());
        pack();
        setLocationRelativeTo(null);
        panelMenu.iniciarAnimacion();
        GestorSonido.getInstancia().iniciarMusica();
    }

    /** Se adapta a pantallas pequenas sin salirse del escritorio. */
    private Dimension tamanoInicial() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int ancho = Math.min(1240, pantalla.width - 60);
        int alto = Math.min(800, pantalla.height - 70);
        return new Dimension(Math.max(1100, ancho), Math.max(680, alto));
    }

    @Override
    public void alJugar() {
        cerrarPartida();
        panelJuego = new PanelJuego(this);
        add(panelJuego, TARJETA_JUEGO);

        panelMenu.detenerAnimacion();
        tarjetas.show(getContentPane(), TARJETA_JUEGO);
    }

    @Override
    public void alSalirDeLaPartida() {
        cerrarPartida();
        tarjetas.show(getContentPane(), TARJETA_MENU);
        panelMenu.iniciarAnimacion();
    }

    @Override
    public void alSalir() {
        dispose();
    }

    private void cerrarPartida() {
        if (panelJuego == null) {
            return;
        }
        panelJuego.detener();
        remove(panelJuego);
        panelJuego = null;
    }
}
