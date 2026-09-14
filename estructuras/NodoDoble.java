package towerdefense.estructuras;

/**
 * Nodo de una lista doblemente enlazada: guarda el dato y las referencias al
 * nodo anterior y al siguiente.
 *
 * @param <T> tipo de dato almacenado.
 */
public class NodoDoble<T> {

    private final T dato;
    private NodoDoble<T> anterior;
    private NodoDoble<T> siguiente;

    public NodoDoble(T dato) {
        this.dato = dato;
    }

    public T getDato() {
        return dato;
    }

    public NodoDoble<T> getAnterior() {
        return anterior;
    }

    public void setAnterior(NodoDoble<T> anterior) {
        this.anterior = anterior;
    }

    public NodoDoble<T> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoDoble<T> siguiente) {
        this.siguiente = siguiente;
    }
}
