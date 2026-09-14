package towerdefense.logica;

import towerdefense.audio.GestorSonido;
import towerdefense.estructuras.Cola;
import towerdefense.estructuras.ListaCircular;
import towerdefense.estructuras.ListaDoble;
import towerdefense.estructuras.ListaSecuencial;
import towerdefense.estructuras.NodoDoble;
import towerdefense.estructuras.Pila;
import towerdefense.modelo.Enemigo;
import towerdefense.modelo.Jugador;
import towerdefense.modelo.Oleada;
import towerdefense.modelo.TipoTorre;
import towerdefense.modelo.Torre;
import towerdefense.mundo.Mapa;
import towerdefense.mundo.Ruta;

/**
 * Motor del juego: contiene TODAS las reglas y no sabe nada de ventanas. Esa
 * separacion permite cambiar la interfaz sin tocar la logica y probar el juego
 * sin abrir una sola ventana.
 *
 * El juego va en tiempo real: la interfaz llama a {@link #actualizar(double)}
 * unas 60 veces por segundo con el tiempo transcurrido, y el motor mueve
 * enemigos, dispara torres y resuelve bajas.
 *
 * Estructura de datos que usa cada cosa, y por que:
 *
 *   ListaSecuencial<Torre>  torres        -> pocas y se recorren por indice
 *   ListaDoble<Enemigo>     enemigos      -> alta rotacion, se borra en O(1)
 *   ListaCircular<Oleada>   olas          -> al acabar la ultima vuelve la primera
 *   Cola<Enemigo>           colaAparicion -> los enemigos salen de uno en uno
 *   Pila<AccionTorre>       deshacer/rehacer -> la ultima accion es la primera en volver
 */
public class MotorJuego {

    private final Mapa mapa = new Mapa();
    private final Ruta ruta = new Ruta(mapa);

    private final ListaSecuencial<Torre> torres = new ListaSecuencial<>(ConfigJuego.MAXIMO_TORRES);
    private final ListaDoble<Enemigo> enemigos = new ListaDoble<>();
    private final ListaCircular<Oleada> olas = GeneradorOleadas.crear();
    private final Cola<Enemigo> colaAparicion = new Cola<>();
    private final Pila<AccionTorre> pilaDeshacer = new Pila<>();
    private final Pila<AccionTorre> pilaRehacer = new Pila<>();

    private final Jugador jugador = new Jugador(ConfigJuego.VIDAS_INICIALES, ConfigJuego.ORO_INICIAL);
    private final RegistroEventos registro = new RegistroEventos(ConfigJuego.MAXIMO_EVENTOS);

    private EstadoPartida estado = EstadoPartida.PREPARACION;
    private int siguienteIdTorre = 1;
    private int siguienteIdEnemigo = 1;
    private int olasSuperadas;
    private int numeroOlaActual;

    private double relojAparicion;
    private double tiempoJugado;

    private String cartel = "Construye tus torres y lanza la primera ola.";
    private double tiempoCartel = ConfigJuego.DURACION_CARTEL;

    public MotorJuego() {
        anotar("Partida iniciada. Tienes " + jugador.getOro() + " de oro.", Evento.Nivel.INFO);
    }

    // ===============================================================
    // BUCLE DE JUEGO
    // ===============================================================

    /**
     * Avanza la simulacion.
     *
     * @param dtReal segundos transcurridos desde el fotograma anterior.
     */
    public void actualizar(double dtReal) {
        // Se acota el salto de tiempo para que un tiron del sistema no
        // teletransporte a los enemigos al otro extremo del mapa.
        double dt = Math.min(dtReal, 0.05);
        tiempoJugado += dt;
        tiempoCartel = Math.max(0, tiempoCartel - dt);

        if (estado.haTerminado()) {
            return;
        }

        hacerAparecerEnemigos(dt);
        moverEnemigos(dt);
        dispararTorres(dt);
        retirarEnemigos();
        revisarFinDeOla();
    }

    /** Los enemigos salen de la cola de uno en uno, con un intervalo fijo. */
    private void hacerAparecerEnemigos(double dt) {
        if (colaAparicion.estaVacia()) {
            return;
        }
        relojAparicion -= dt;
        if (relojAparicion > 0) {
            return;
        }
        relojAparicion = ConfigJuego.INTERVALO_APARICION;
        enemigos.insertarAlFinal(colaAparicion.desencolar());
    }

    private void moverEnemigos(double dt) {
        for (NodoDoble<Enemigo> nodo = enemigos.getPrimero(); nodo != null; nodo = nodo.getSiguiente()) {
            nodo.getDato().avanzar(dt);
        }
    }

    /** Cada torre recarga y, cuando puede, ataca al enemigo mas adelantado. */
    private void dispararTorres(double dt) {
        for (int i = 0; i < torres.getContador(); i++) {
            Torre torre = torres.obtener(i);
            torre.actualizar(dt);
            if (!torre.puedeDisparar()) {
                continue;
            }
            Enemigo objetivo = buscarObjetivo(torre);
            if (objetivo != null) {
                torre.dispararA(objetivo);
                GestorSonido.getInstancia().reproducirDisparo(torre.getTipo());
            }
        }
    }

    /**
     * Elige al enemigo vivo dentro del rango que mas ha avanzado: es el que
     * esta mas cerca de la base y por tanto el mas peligroso.
     */
    private Enemigo buscarObjetivo(Torre torre) {
        Enemigo mejor = null;
        for (NodoDoble<Enemigo> nodo = enemigos.getPrimero(); nodo != null; nodo = nodo.getSiguiente()) {
            Enemigo enemigo = nodo.getDato();
            if (enemigo.estaDestruido() || !torre.estaEnRango(enemigo)) {
                continue;
            }
            if (mejor == null || enemigo.getDistanciaRecorrida() > mejor.getDistanciaRecorrida()) {
                mejor = enemigo;
            }
        }
        return mejor;
    }

    /**
     * Retira a los destruidos y a los que llegaron a la base.
     * El siguiente nodo se guarda ANTES de eliminar, porque al desenlazar un
     * nodo se pierden sus punteros.
     */
    private void retirarEnemigos() {
        NodoDoble<Enemigo> nodo = enemigos.getPrimero();
        while (nodo != null) {
            NodoDoble<Enemigo> siguiente = nodo.getSiguiente();
            Enemigo enemigo = nodo.getDato();

            if (enemigo.estaDestruido()) {
                jugador.ganarOro(enemigo.getRecompensa());
                jugador.sumarPuntos(enemigo.getRecompensa());
                enemigos.eliminarNodo(nodo);
                anotar("Destruido el enemigo #" + enemigo.getId()
                        + " (+" + enemigo.getRecompensa() + " oro).", Evento.Nivel.EXITO);
                GestorSonido.getInstancia().reproducirEnemigoDestruido();
            } else if (enemigo.llegoALaBase()) {
                jugador.perderVida();
                enemigos.eliminarNodo(nodo);
                anotar("El enemigo #" + enemigo.getId() + " alcanzo la base. Quedan "
                        + jugador.getVidas() + " vidas.", Evento.Nivel.PELIGRO);
                GestorSonido.getInstancia().reproducirEnemigoEnBase();
                if (jugador.estaDerrotado()) {
                    terminar(EstadoPartida.DERROTA, "La base ha caido.");
                    return;
                }
            }
            nodo = siguiente;
        }
    }

    /** Al quedar el mapa limpio se pasa a la fase de preparacion. */
    private void revisarFinDeOla() {
        boolean olaLimpia = estado == EstadoPartida.EN_OLEADA
                && enemigos.estaVacia()
                && colaAparicion.estaVacia();
        if (!olaLimpia) {
            return;
        }

        olasSuperadas++;
        jugador.ganarOro(ConfigJuego.BONO_POR_OLA);
        anotar("Ola " + numeroOlaActual + " superada (+" + ConfigJuego.BONO_POR_OLA + " oro).",
                Evento.Nivel.EXITO);

        if (olasSuperadas >= GeneradorOleadas.getTotalOlas()) {
            terminar(EstadoPartida.VICTORIA, "Todas las olas superadas.");
            return;
        }
        GestorSonido.getInstancia().reproducirOlaSuperada();
        estado = EstadoPartida.PREPARACION;
        mostrarCartel("Ola completada. Refuerza tu defensa y lanza la siguiente.");
    }

    private void terminar(EstadoPartida resultado, String motivo) {
        estado = resultado;
        mostrarCartel(motivo);
        anotar(motivo, resultado == EstadoPartida.VICTORIA ? Evento.Nivel.EXITO : Evento.Nivel.PELIGRO);
        if (resultado == EstadoPartida.VICTORIA) {
            GestorSonido.getInstancia().reproducirVictoria();
        } else {
            GestorSonido.getInstancia().reproducirDerrota();
        }
    }

    // ===============================================================
    // OLAS
    // ===============================================================

    /**
     * Lanza la ola que marca el cursor circular: crea sus enemigos y los deja
     * en la cola de aparicion para que vayan saliendo uno tras otro.
     *
     * @return mensaje de error, o null si la ola arranco.
     */
    public String lanzarSiguienteOla() {
        if (estado != EstadoPartida.PREPARACION) {
            return "Espera a terminar la ola actual.";
        }
        Oleada ola = olas.avanzar();
        numeroOlaActual = ola.getNumero();

        for (int i = 0; i < ola.getCantidad(); i++) {
            colaAparicion.encolar(new Enemigo(siguienteIdEnemigo, ola.getTipo(), ruta,
                    ola.getMultiplicadorVida()));
            siguienteIdEnemigo++;
        }

        relojAparicion = 0;
        estado = EstadoPartida.EN_OLEADA;
        GestorSonido.getInstancia().reproducirInicioOla();
        mostrarCartel("Ola " + ola.getNumero() + ": " + ola.getCantidad()
                + " enemigos " + ola.getTipo().getNombre());
        anotar("Comienza la " + ola, Evento.Nivel.AVISO);
        return null;
    }

    // ===============================================================
    // TORRES
    // ===============================================================

    /**
     * Construye una torre en una casilla.
     *
     * @return mensaje de error, o null si se construyo.
     */
    public String construirTorre(TipoTorre tipo, int columna, int fila) {
        if (!mapa.sePuedeConstruir(columna, fila)) {
            return "Ahi no se puede construir: elige una casilla de cesped libre.";
        }
        if (torres.estaLlena()) {
            return "No caben mas torres (maximo " + torres.getCapacidad() + ").";
        }
        if (!jugador.puedePagar(tipo.getCosto())) {
            return "Oro insuficiente: " + tipo.getNombre() + " cuesta " + tipo.getCosto() + ".";
        }

        Torre torre = new Torre(siguienteIdTorre, tipo, columna, fila, mapa.centroDe(columna, fila));
        siguienteIdTorre++;

        jugador.gastar(tipo.getCosto());
        colocar(torre);
        GestorSonido.getInstancia().reproducirColocarTorre();
        pilaDeshacer.apilar(new AccionTorre(AccionTorre.Tipo.CONSTRUIR, torre));
        pilaRehacer.limpiar(); // una accion nueva invalida la linea de rehacer
        anotar("Construida una " + tipo.getNombre() + " por " + tipo.getCosto() + " de oro.",
                Evento.Nivel.INFO);
        return null;
    }

    /**
     * Sube una torre de nivel.
     *
     * @return mensaje de error, o null si se mejoro.
     */
    public String mejorarTorre(Torre torre) {
        if (!torre.sePuedeMejorar()) {
            return "La torre ya esta en su nivel maximo.";
        }
        int costo = torre.getCostoMejora();
        if (!jugador.puedePagar(costo)) {
            return "Oro insuficiente: la mejora cuesta " + costo + ".";
        }
        jugador.gastar(costo);
        torre.mejorar();
        GestorSonido.getInstancia().reproducirMejora();
        anotar("Mejorada la torre #" + torre.getId() + " a nivel " + torre.getNivel() + ".",
                Evento.Nivel.EXITO);
        return null;
    }

    /** Vende una torre y devuelve parte de lo invertido en ella. */
    public String venderTorre(Torre torre) {
        int reembolso = calcularReembolso(torre);
        retirar(torre);
        jugador.ganarOro(reembolso);
        pilaDeshacer.apilar(new AccionTorre(AccionTorre.Tipo.VENDER, torre));
        pilaRehacer.limpiar();
        GestorSonido.getInstancia().reproducirVenta();
        anotar("Vendida la torre #" + torre.getId() + " (+" + reembolso + " oro).", Evento.Nivel.INFO);
        return null;
    }

    private int calcularReembolso(Torre torre) {
        return (int) Math.round(torre.getInversionTotal() * ConfigJuego.REEMBOLSO_VENTA);
    }

    private void colocar(Torre torre) {
        torres.insertar(torre);
        mapa.ocupar(torre.getColumna(), torre.getFila(), true);
    }

    private void retirar(Torre torre) {
        torres.eliminar(buscarIndice(torre));
        torre.olvidarObjetivo();
        mapa.ocupar(torre.getColumna(), torre.getFila(), false);
    }

    // ===============================================================
    // DESHACER / REHACER (pilas)
    // ===============================================================

    /**
     * Deshace la ultima accion aplicando la contraria, y la guarda en la pila
     * de rehacer.
     *
     * @return mensaje de error, o null si se deshizo.
     */
    public String deshacer() {
        AccionTorre accion = pilaDeshacer.desapilar();
        if (accion == null) {
            return "No hay nada que deshacer.";
        }
        boolean fueConstruccion = accion.getTipo() == AccionTorre.Tipo.CONSTRUIR;
        String problema = fueConstruccion
                ? quitarTorreYDevolver(accion.getTorre())
                : reponerTorreYCobrar(accion.getTorre());

        if (problema != null) {
            pilaDeshacer.apilar(accion); // se devuelve: el estado no cambio
            return problema;
        }
        pilaRehacer.apilar(accion);
        anotar("Deshecho: " + accion.describir(), Evento.Nivel.INFO);
        return null;
    }

    /**
     * Vuelve a aplicar la ultima accion deshecha.
     *
     * @return mensaje de error, o null si se rehizo.
     */
    public String rehacer() {
        AccionTorre accion = pilaRehacer.desapilar();
        if (accion == null) {
            return "No hay nada que rehacer.";
        }
        boolean fueConstruccion = accion.getTipo() == AccionTorre.Tipo.CONSTRUIR;
        String problema = fueConstruccion
                ? reponerTorreYCobrar(accion.getTorre())
                : quitarTorreYDevolver(accion.getTorre());

        if (problema != null) {
            pilaRehacer.apilar(accion);
            return problema;
        }
        pilaDeshacer.apilar(accion);
        anotar("Rehecho: " + accion.describir(), Evento.Nivel.INFO);
        return null;
    }

    /** Retira la torre del mapa y reintegra el oro invertido en ella. */
    private String quitarTorreYDevolver(Torre torre) {
        if (buscarIndice(torre) < 0) {
            return "Esa torre ya no esta en el mapa.";
        }
        retirar(torre);
        jugador.ganarOro(torre.getInversionTotal());
        return null;
    }

    /** Vuelve a poner la torre en su casilla cobrando lo que costo. */
    private String reponerTorreYCobrar(Torre torre) {
        if (!mapa.sePuedeConstruir(torre.getColumna(), torre.getFila())) {
            return "La casilla de esa torre esta ocupada.";
        }
        if (!jugador.puedePagar(torre.getInversionTotal())) {
            return "Oro insuficiente para reponer esa torre.";
        }
        jugador.gastar(torre.getInversionTotal());
        colocar(torre);
        return null;
    }

    private int buscarIndice(Torre torre) {
        for (int i = 0; i < torres.getContador(); i++) {
            if (torres.obtener(i) == torre) {
                return i;
            }
        }
        return -1;
    }

    // ===============================================================
    // CONSULTAS
    // ===============================================================

    /** Torre situada en una casilla, o null si esta libre. */
    public Torre getTorreEn(int columna, int fila) {
        for (int i = 0; i < torres.getContador(); i++) {
            Torre torre = torres.obtener(i);
            if (torre.getColumna() == columna && torre.getFila() == fila) {
                return torre;
            }
        }
        return null;
    }

    private void mostrarCartel(String texto) {
        cartel = texto;
        tiempoCartel = ConfigJuego.DURACION_CARTEL;
    }

    private void anotar(String mensaje, Evento.Nivel nivel) {
        registro.agregar(new Evento(mensaje, nivel, (int) tiempoJugado));
    }

    /** Texto del cartel grande, o null si ya se desvanecio. */
    public String getCartel() {
        return tiempoCartel > 0 ? cartel : null;
    }

    /** De 1 a 0 segun se va desvaneciendo el cartel. */
    public double getOpacidadCartel() {
        return Math.min(1, tiempoCartel / 1.0);
    }

    public Mapa getMapa() {
        return mapa;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public ListaSecuencial<Torre> getTorres() {
        return torres;
    }

    public ListaDoble<Enemigo> getEnemigos() {
        return enemigos;
    }

    public ListaCircular<Oleada> getOlas() {
        return olas;
    }

    public Cola<Enemigo> getColaAparicion() {
        return colaAparicion;
    }

    public Pila<AccionTorre> getPilaDeshacer() {
        return pilaDeshacer;
    }

    public Pila<AccionTorre> getPilaRehacer() {
        return pilaRehacer;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public RegistroEventos getRegistro() {
        return registro;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public int getNumeroOlaActual() {
        return numeroOlaActual;
    }

    public int getOlasSuperadas() {
        return olasSuperadas;
    }

    public int getTotalOlas() {
        return GeneradorOleadas.getTotalOlas();
    }

    /** Siguiente ola de la cola circular, sin lanzarla. */
    public Oleada verSiguienteOla() {
        return olas.verActual();
    }
}
