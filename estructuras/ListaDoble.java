package towerdefense.estructuras;

/**
 * Lista doblemente enlazada con referencias a primero y ultimo.
 *
 * Se usa para los ENEMIGOS ACTIVOS: se insertan y eliminan constantemente
 * (cada turno pueden morir varios), y el doble enlace permite recorrerlos en
 * ambos sentidos sin volver a empezar desde el principio.
 *
 * La lista NO imprime nada: solo guarda y enlaza datos. Quien los muestra es la
 * interfaz. Asi la estructura sirve igual en consola, en ventana o en pruebas.
 *
 * @param <T> tipo de dato almacenado.
 */
public class ListaDoble<T> {

    private NodoDoble<T> primero;
    private NodoDoble<T> ultimo;
    private int contador;

    /** Inserta al final de la lista en O(1). */
    public void insertarAlFinal(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        if (estaVacia()) {
            primero = nuevo;
        } else {
            nuevo.setAnterior(ultimo);
            ultimo.setSiguiente(nuevo);
        }
        ultimo = nuevo;
        contador++;
    }

    /**
     * Desenlaza un nodo reconectando a sus vecinos. Recibe el nodo y no el id
     * para poder eliminar en O(1) mientras se recorre la lista.
     *
     * @return true si el nodo se desenlazo.
     */
    public boolean eliminarNodo(NodoDoble<T> nodo) {
        if (nodo == null) {
            return false;
        }
        NodoDoble<T> anterior = nodo.getAnterior();
        NodoDoble<T> siguiente = nodo.getSiguiente();

        if (anterior != null) {
            anterior.setSiguiente(siguiente);
        } else {
            primero = siguiente; // el nodo era el primero
        }
        if (siguiente != null) {
            siguiente.setAnterior(anterior);
        } else {
            ultimo = anterior; // el nodo era el ultimo
        }

        nodo.setAnterior(null);
        nodo.setSiguiente(null);
        contador--;
        return true;
    }

    public NodoDoble<T> getPrimero() {
        return primero;
    }

    public NodoDoble<T> getUltimo() {
        return ultimo;
    }

    public int getContador() {
        return contador;
    }

    public boolean estaVacia() {
        return primero == null;
    }

    public void limpiar() {
        primero = null;
        ultimo = null;
        contador = 0;
    }
}
