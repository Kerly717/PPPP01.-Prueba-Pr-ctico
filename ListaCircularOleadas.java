/**
 * Lista simplemente enlazada circular, con referencia a ultimo,
 * usada para manejar las oleadas de enemigos.
 * El siguiente del ultimo nodo siempre apunta al primero.
 */
public class ListaCircularOleadas {

    private NodoOleada ultimo;
    private NodoOleada oleadaActual;
    private int contador;

    public ListaCircularOleadas() {
        this.ultimo = null;
        this.oleadaActual = null;
        this.contador = 0;
    }

    public boolean estaVacia() {
        return ultimo == null;
    }

    public int getContador() {
        return contador;
    }

    /**
     * Registra una nueva oleada al final de la lista circular.
     */
    public void registrarOleada(Oleada oleada) {
        NodoOleada nuevo = new NodoOleada(oleada);

        if (estaVacia()) {
            ultimo = nuevo;
            nuevo.setSiguiente(nuevo);
            oleadaActual = nuevo;
        } else {
            nuevo.setSiguiente(ultimo.getSiguiente());
            ultimo.setSiguiente(nuevo);
            ultimo = nuevo;
        }
        contador++;
    }

    /**
     * Muestra todas las oleadas registradas, recorriendo el circulo una sola vuelta.
     */
    public void mostrarOleadas() {
        if (estaVacia()) {
            System.out.println("No hay oleadas registradas.");
            return;
        }
        System.out.println("--- Oleadas registradas (" + contador + ") ---");
        NodoOleada primero = ultimo.getSiguiente();
        NodoOleada actual = primero;
        int i = 0;
        while (i < contador) {
            System.out.println(actual);
            actual = actual.getSiguiente();
            i++;
        }
    }

    /**
     * Avanza a la siguiente oleada del ciclo y la retorna.
     */
    public NodoOleada avanzarSiguienteOleada() {
        if (estaVacia()) {
            return null;
        }
        oleadaActual = oleadaActual.getSiguiente();
        return oleadaActual;
    }

    /**
     * Retorna la oleada actual sin avanzar el ciclo.
     */
    public NodoOleada getOleadaActual() {
        return oleadaActual;
    }

    /**
     * Reinicia el ciclo de oleadas, apuntando de nuevo a la primera registrada.
     */
    public void reiniciarCiclo() {
        if (!estaVacia()) {
            oleadaActual = ultimo.getSiguiente();
        }
    }
}
