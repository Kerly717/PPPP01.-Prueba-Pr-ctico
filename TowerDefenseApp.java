import java.util.Scanner;

/**
 * Clase principal del juego Tower Defense.
 * Contiene el menu y la logica de turnos. Usa unicamente
 * Scanner y String para la entrada de datos, sin colecciones de java.util.
 */
public class TowerDefenseApp {

    private static final int LONGITUD_RUTA = 20;

    private static ListaSecuencialTorres listaTorres = new ListaSecuencialTorres();
    private static ListaDobleEnemigos listaEnemigos = new ListaDobleEnemigos();
    private static ListaCircularOleadas listaOleadas = new ListaCircularOleadas();
    private static Jugador jugador = new Jugador(3);

    private static int contadorIdTorre = 1;
    private static int contadorIdOleada = 1;
    private static int contadorIdEnemigo = 1;

    private static boolean primeraOleadaIniciada = false;
    private static int oleadasEnviadas = 0;
    private static boolean todasLasOleadasEnviadas = false;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean salir = false;

        System.out.println("=== TOWER DEFENSE - Prueba practica Estructura de Datos ===");

        while (!salir) {
            mostrarMenu();
            int opcion = leerEntero(sc, "Seleccione una opcion: ");

            switch (opcion) {
                case 1:
                    registrarTorre(sc);
                    break;
                case 2:
                    listaTorres.mostrarTorres();
                    break;
                case 3:
                    eliminarTorre(sc);
                    break;
                case 4:
                    registrarOleada(sc);
                    break;
                case 5:
                    listaOleadas.mostrarOleadas();
                    break;
                case 6:
                    iniciarSiguienteOleada();
                    break;
                case 7:
                    avanzarTurno();
                    break;
                case 8:
                    mostrarEnemigos(sc);
                    break;
                case 9:
                    mostrarEstadoGeneral();
                    break;
                case 10:
                    salir = true;
                    System.out.println("Gracias por jugar. Hasta la proxima.");
                    break;
                default:
                    System.out.println("Opcion invalida, intente de nuevo.");
            }

            if (jugador.estaDerrotado()) {
                System.out.println("\n*** El jugador ha perdido todas sus vidas. Fin del juego. ***");
                salir = true;
            } else if (todasLasOleadasEnviadas && listaEnemigos.estaVacia() && listaOleadas.getContador() > 0) {
                System.out.println("\n*** Se completaron todas las oleadas definidas y no quedan enemigos. ***");
                boolean continuaJugando = preguntarReinicioCiclo(sc);
                if (!continuaJugando) {
                    System.out.println("El jugador gana la partida.");
                    salir = true;
                }
            }
        }

        sc.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n----- MENU PRINCIPAL -----");
        System.out.println("1. Registrar torre defensiva");
        System.out.println("2. Mostrar torres registradas");
        System.out.println("3. Eliminar torre");
        System.out.println("4. Registrar oleada");
        System.out.println("5. Mostrar oleadas");
        System.out.println("6. Iniciar siguiente oleada");
        System.out.println("7. Avanzar turno");
        System.out.println("8. Mostrar enemigos activos");
        System.out.println("9. Mostrar estado general del juego");
        System.out.println("10. Salir");
    }

    private static void registrarTorre(Scanner sc) {
        System.out.println("--- Registrar torre ---");
        String nombre = leerTexto(sc, "Nombre: ");
        String tipo = leerTexto(sc, "Tipo (ej. Arquero, Canon): ");
        int posicion = leerEntero(sc, "Posicion en la ruta (0 a " + LONGITUD_RUTA + "): ");
        int danio = leerEntero(sc, "Danio: ");
        int rango = leerEntero(sc, "Rango: ");
        int costo = leerEntero(sc, "Costo: ");

        Torre nuevaTorre = new Torre(contadorIdTorre, nombre, tipo, posicion, danio, rango, costo);
        boolean insertada = listaTorres.insertarTorre(nuevaTorre);
        if (insertada) {
            System.out.println("Torre registrada con id " + contadorIdTorre);
            contadorIdTorre++;
        }
    }

    private static void eliminarTorre(Scanner sc) {
        if (listaTorres.getContador() == 0) {
            System.out.println("No hay torres para eliminar.");
            return;
        }
        int id = leerEntero(sc, "Ingrese el id de la torre a eliminar: ");

        Torre encontrada = listaTorres.buscarTorrePorId(id);
        if (encontrada == null) {
            System.out.println("No se encontro una torre con ese id.");
            return;
        }

        System.out.println("Torre encontrada: " + encontrada);
        listaTorres.eliminarTorrePorId(id);
        System.out.println("Torre eliminada correctamente.");
    }

    private static void registrarOleada(Scanner sc) {
        System.out.println("--- Registrar oleada ---");
        int cantidadEnemigos = leerEntero(sc, "Cantidad de enemigos: ");
        String tipoEnemigo = leerTexto(sc, "Tipo de enemigo: ");
        int vidaBase = leerEntero(sc, "Vida base: ");
        int velocidadBase = leerEntero(sc, "Velocidad base: ");

        Oleada nuevaOleada = new Oleada(contadorIdOleada, cantidadEnemigos, tipoEnemigo, vidaBase, velocidadBase);
        listaOleadas.registrarOleada(nuevaOleada);
        System.out.println("Oleada registrada con id " + contadorIdOleada);
        contadorIdOleada++;
        todasLasOleadasEnviadas = false;
    }

    /**
     * Toma la oleada actual del ciclo circular y crea sus enemigos
     * en la lista doblemente enlazada de enemigos activos.
     * El circulo avanza una vuelta por partida (regla 7: la partida puede
     * terminar cuando se completan todas las oleadas definidas); si el
     * jugador elige continuar, preguntarReinicioCiclo() llama a
     * reiniciarCiclo() para volver a recorrer las oleadas desde el inicio.
     */
    private static void iniciarSiguienteOleada() {
        if (listaOleadas.estaVacia()) {
            System.out.println("No hay oleadas registradas.");
            return;
        }
        if (todasLasOleadasEnviadas) {
            System.out.println("Ya se enviaron todas las oleadas definidas.");
            return;
        }

        NodoOleada nodoOleada;
        if (!primeraOleadaIniciada) {
            nodoOleada = listaOleadas.getOleadaActual();
            primeraOleadaIniciada = true;
        } else {
            nodoOleada = listaOleadas.avanzarSiguienteOleada();
        }
        oleadasEnviadas++;

        Oleada oleada = nodoOleada.getOleada();
        System.out.println("Iniciando " + oleada);
        for (int i = 0; i < oleada.getCantidadEnemigos(); i++) {
            Enemigo nuevoEnemigo = new Enemigo(
                    contadorIdEnemigo,
                    oleada.getTipoEnemigo(),
                    oleada.getVidaBase(),
                    oleada.getVelocidadBase(),
                    0,
                    oleada.getVidaBase() / 2
            );
            listaEnemigos.insertarAlFinal(nuevoEnemigo);
            contadorIdEnemigo++;
        }
        System.out.println(oleada.getCantidadEnemigos() + " enemigos de la oleada han aparecido en la posicion 0.");

        if (oleadasEnviadas >= listaOleadas.getContador()) {
            todasLasOleadasEnviadas = true;
            System.out.println("Esa era la ultima oleada registrada.");
        }
    }

    /**
     * Ejecuta un turno completo: mover enemigos, atacar con torres,
     * eliminar enemigos destruidos y descontar vidas si alguno llega al final.
     */
    private static void avanzarTurno() {
        if (listaEnemigos.estaVacia()) {
            System.out.println("No hay enemigos activos. Inicie una oleada primero.");
            return;
        }

        System.out.println("--- Avanzando turno ---");

        // 1. Mover enemigos segun su velocidad.
        listaEnemigos.actualizarPosiciones();

        // 2 y 3. Verificar rango de cada torre y aplicar danio.
        int totalTorres = listaTorres.getContador();
        for (int i = 0; i < totalTorres; i++) {
            Torre torre = listaTorres.obtenerTorreEnIndice(i);
            NodoEnemigo actual = listaEnemigos.getPrimero();
            while (actual != null) {
                Enemigo enemigo = actual.getEnemigo();
                if (torre.estaEnRango(enemigo.getPosicion()) && !enemigo.estaDestruido()) {
                    enemigo.recibirDanio(torre.getDanio());
                    System.out.println("Torre " + torre.getNombre() + " ataco a enemigo #"
                            + enemigo.getId() + " (vida restante: " + enemigo.getVida() + ")");
                }
                actual = actual.getSiguiente();
            }
        }

        // 4. Eliminar enemigos con vida 0 y 5. descontar vidas si llegaron al final.
        NodoEnemigo actual = listaEnemigos.getPrimero();
        while (actual != null) {
            NodoEnemigo siguienteNodo = actual.getSiguiente();
            Enemigo enemigo = actual.getEnemigo();

            if (enemigo.estaDestruido()) {
                System.out.println("Enemigo #" + enemigo.getId() + " fue destruido.");
                listaEnemigos.eliminarPorId(enemigo.getId());
            } else if (enemigo.getPosicion() >= LONGITUD_RUTA) {
                System.out.println("Enemigo #" + enemigo.getId() + " llego a la base. El jugador pierde una vida.");
                jugador.perderVida();
                listaEnemigos.eliminarPorId(enemigo.getId());
            }

            actual = siguienteNodo;
        }

        // 6. Resumen del turno.
        System.out.println("Resumen del turno: vidas del jugador = " + jugador.getVidas()
                + ", enemigos activos = " + listaEnemigos.getContador());
    }

    /**
     * Muestra los enemigos activos. Deja elegir el sentido del recorrido
     * para ejercitar tanto mostrarAdelante() como mostrarAtras(), y tambien
     * permite buscar un enemigo puntual por id (operacion 4.2 del documento).
     */
    private static void mostrarEnemigos(Scanner sc) {
        if (listaEnemigos.estaVacia()) {
            System.out.println("No hay enemigos activos.");
            return;
        }
        System.out.println("1. Ver recorrido de primero a ultimo (adelante)");
        System.out.println("2. Ver recorrido de ultimo a primero (atras)");
        System.out.println("3. Buscar un enemigo por id");
        int opcion = leerEntero(sc, "Elija una opcion: ");

        if (opcion == 2) {
            listaEnemigos.mostrarAtras();
        } else if (opcion == 3) {
            int id = leerEntero(sc, "Ingrese el id del enemigo a buscar: ");
            NodoEnemigo encontrado = listaEnemigos.buscarPorId(id);
            if (encontrado != null) {
                System.out.println("Enemigo encontrado: " + encontrado);
            } else {
                System.out.println("No se encontro un enemigo con ese id.");
            }
        } else {
            listaEnemigos.mostrarAdelante();
        }
    }

    /**
     * Pregunta al jugador si desea reiniciar el ciclo de oleadas (operacion
     * obligatoria de ListaCircularOleadas) para seguir jugando una ronda mas,
     * o terminar la partida ahora que se completaron todas las oleadas definidas.
     */
    private static boolean preguntarReinicioCiclo(Scanner sc) {
        System.out.println("1. Reiniciar el ciclo de oleadas y seguir jugando");
        System.out.println("2. Terminar la partida aqui");
        int opcion = leerEntero(sc, "Elija una opcion: ");

        if (opcion == 1) {
            listaOleadas.reiniciarCiclo();
            oleadasEnviadas = 0;
            todasLasOleadasEnviadas = false;
            primeraOleadaIniciada = false;
            System.out.println("El ciclo de oleadas se reinicio. Use la opcion 6 para iniciar la siguiente oleada.");
            return true;
        }
        return false;
    }

    private static void mostrarEstadoGeneral() {
        System.out.println("\n--- Estado general del juego ---");
        System.out.println("Vidas del jugador: " + jugador.getVidas());
        System.out.println("Torres activas: " + listaTorres.contarActivas());
        System.out.println("Enemigos activos: " + listaEnemigos.getContador());
        System.out.println("Oleadas registradas: " + listaOleadas.getContador());
        System.out.println("Oleadas enviadas: " + oleadasEnviadas);
        if (listaOleadas.getOleadaActual() != null) {
            System.out.println("Oleada actual: " + listaOleadas.getOleadaActual());
        }
    }

    private static int leerEntero(Scanner sc, String mensaje) {
        System.out.print(mensaje);
        while (!sc.hasNextInt()) {
            System.out.print("Ingrese un numero valido: ");
            sc.next();
        }
        int valor = sc.nextInt();
        sc.nextLine();
        return valor;
    }

    private static String leerTexto(Scanner sc, String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine();
    }
}
