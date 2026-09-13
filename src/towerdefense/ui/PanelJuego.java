package towerdefense.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.Timer;
import towerdefense.logica.ConfigJuego;
import towerdefense.logica.EstadoPartida;
import towerdefense.logica.MotorJuego;
import towerdefense.modelo.TipoTorre;
import towerdefense.modelo.Torre;

/**
 * Pantalla de partida: junta el encabezado, el mapa, el panel de controles y la
 * bitacora, y ejecuta el BUCLE DE JUEGO.
 *
 * Sobre el bucle y los hilos: se usa {@link javax.swing.Timer}, que dispara sus
 * avisos en el hilo de eventos de Swing (EDT). Como la simulacion y el dibujo
 * ocurren en ese mismo hilo, no hay dos hilos tocando las mismas estructuras y
 * no hace falta sincronizar nada. El tiempo entre fotogramas se mide con el
 * reloj del sistema, asi que el juego va a la misma velocidad aunque el
 * temporizador se retrase.
 */
public class PanelJuego extends JPanel
        implements PanelControles.Acciones, LienzoMapa.OyenteMapa {

    private static final long serialVersionUID = 1L;

    /** Aviso para que la ventana vuelva al menu principal. */
    public interface OyentePartida {
        void alSalirDeLaPartida();
    }

    private final MotorJuego motor = new MotorJuego();
    private final LienzoMapa lienzo = new LienzoMapa(motor);
    private final PanelEncabezado encabezado = new PanelEncabezado(motor);
    private final PanelControles controles = new PanelControles(motor, this);
    private final PanelRegistro registro = new PanelRegistro(motor);
    private final Timer bucle;
    private final OyentePartida oyente;

    private long instanteAnterior = System.nanoTime();
    private boolean finalAvisado;

    public PanelJuego(OyentePartida oyente) {
        this.oyente = oyente;

        setLayout(new BorderLayout());
        setBackground(Paleta.FONDO);

        JPanel centro = new JPanel(new java.awt.GridBagLayout());
        centro.setBackground(Paleta.FONDO);
        centro.add(lienzo);

        add(encabezado, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(controles, BorderLayout.EAST);
        add(registro, BorderLayout.SOUTH);

        lienzo.setOyente(this);
        bucle = new Timer(ConfigJuego.MILIS_POR_CUADRO, this::alFotograma);
        bucle.start();
    }

    /** Detiene el bucle al abandonar la partida. */
    public void detener() {
        bucle.stop();
    }

    // ===============================================================
    // BUCLE DE JUEGO
    // ===============================================================

    private void alFotograma(ActionEvent evento) {
        long ahora = System.nanoTime();
        double dt = (ahora - instanteAnterior) / 1_000_000_000.0;
        instanteAnterior = ahora;

        motor.actualizar(dt);
        lienzo.avanzarReloj(dt);

        soltarTorreDesaparecida();
        controles.sincronizar();
        encabezado.repaint();
        registro.repaint();
        lienzo.repaint();

        avisarFinDePartida();
    }

    /**
     * Si la torre seleccionada dejo de estar en el mapa (vendida o deshecha),
     * se suelta la referencia para no mostrar datos de algo que ya no existe.
     */
    private void soltarTorreDesaparecida() {
        Torre seleccionada = controles.getTorreSeleccionada();
        if (seleccionada == null) {
            return;
        }
        boolean sigueEnMapa = motor.getTorreEn(seleccionada.getColumna(), seleccionada.getFila())
                == seleccionada;
        if (!sigueEnMapa) {
            seleccionarTorre(null);
        }
    }

    // ===============================================================
    // CLIC SOBRE EL MAPA
    // ===============================================================

    @Override
    public void alPulsarCasilla(int columna, int fila) {
        if (motor.getEstado().haTerminado()) {
            return;
        }
        Torre existente = motor.getTorreEn(columna, fila);
        if (existente != null) {
            elegirTipo(null);
            seleccionarTorre(existente);
            return;
        }
        if (controles.getTipoElegido() == null) {
            seleccionarTorre(null);
            return;
        }
        avisarSiFalla(motor.construirTorre(controles.getTipoElegido(), columna, fila));
    }

    private void seleccionarTorre(Torre torre) {
        controles.setTorreSeleccionada(torre);
        lienzo.setTorreSeleccionada(torre);
    }

    private void elegirTipo(TipoTorre tipo) {
        controles.setTipoElegido(tipo);
        lienzo.setTipoElegido(tipo);
    }

    // ===============================================================
    // ACCIONES DEL PANEL DE CONTROLES
    // ===============================================================

    @Override
    public void alElegirTipoTorre(TipoTorre tipo) {
        // Pulsar dos veces la misma torre cancela la seleccion.
        boolean repetida = tipo == controles.getTipoElegido();
        elegirTipo(repetida ? null : tipo);
        if (!repetida) {
            seleccionarTorre(null);
        }
    }

    @Override
    public void alPedirSiguienteOla() {
        avisarSiFalla(motor.lanzarSiguienteOla());
    }

    @Override
    public void alPedirMejorar() {
        Torre torre = controles.getTorreSeleccionada();
        if (torre != null) {
            avisarSiFalla(motor.mejorarTorre(torre));
        }
    }

    @Override
    public void alPedirVender() {
        Torre torre = controles.getTorreSeleccionada();
        if (torre == null) {
            return;
        }
        boolean confirmado = Formularios.confirmar(this, "Vender torre",
                "Vender la " + torre.getTipo().getNombre() + " de nivel " + torre.getNivel() + "?");
        if (confirmado) {
            avisarSiFalla(motor.venderTorre(torre));
            seleccionarTorre(null);
        }
    }

    @Override
    public void alPedirDeshacer() {
        avisarSiFalla(motor.deshacer());
        seleccionarTorre(null);
    }

    @Override
    public void alPedirRehacer() {
        avisarSiFalla(motor.rehacer());
    }

    @Override
    public void alPedirVolverAlMenu() {
        oyente.alSalirDeLaPartida();
    }

    private void avisarSiFalla(String problema) {
        if (problema != null) {
            Formularios.advertir(this, problema);
        }
        controles.sincronizar();
    }

    /** Muestra el resultado una sola vez cuando la partida termina. */
    private void avisarFinDePartida() {
        if (!motor.getEstado().haTerminado() || finalAvisado) {
            return;
        }
        finalAvisado = true;

        String resumen = "Olas superadas: " + motor.getOlasSuperadas() + " de " + motor.getTotalOlas()
                + "\nPuntuacion: " + motor.getJugador().getPuntuacion()
                + "\nOro final: " + motor.getJugador().getOro();

        String titulo = motor.getEstado() == EstadoPartida.VICTORIA ? "Victoria" : "Derrota";
        String mensaje = motor.getEstado() == EstadoPartida.VICTORIA
                ? "Defendiste la base durante todas las olas.\n\n" + resumen
                : "Los enemigos arrasaron la base.\n\n" + resumen;
        Formularios.informar(this, titulo, mensaje);
    }
}
