package towerdefense.estructuras;

/**
 * Nodo de una lista simplemente enlazada: guarda el dato y una unica
 * referencia al siguiente nodo.
 *
 * @param <T> tipo de dato almacenado.
 */
public class NodoSimple<T> {

    private final T dato;
    private NodoSimple<T> siguiente;

    public NodoSimple(T dato) {
        this.dato = dato;
    }

    public T getDato() {
        return dato;
    }

    public NodoSimple<T> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoSimple<T> siguiente) {
        this.siguiente = siguiente;
    }
}
