package towerdefense.estructuras;

/**
 * Lista simplemente enlazada CIRCULAR: el siguiente del ultimo nodo apunta
 * siempre al primero, de modo que el recorrido nunca termina.
 *
 * Se usa para las OLEADAS: al acabar la ultima se vuelve sola a la primera, que
 * es exactamente el comportamiento que queremos en un juego por rondas.
 *
 * Se guarda solo la referencia al ultimo nodo, porque desde el se llega al
 * primero en un paso ({@code ultimo.siguiente}) y eso permite insertar al final
 * en O(1).
 *
 * Un cursor independiente marca la oleada que toca enviar.
 *
 * @param <T> tipo de dato almacenado.
 */
public class ListaCircular<T> {

    private NodoSimple<T> ultimo;
    private NodoSimple<T> cursor;
    private int contador;

    /** Inserta al final del circulo manteniendo la circularidad. */
    public void insertar(T dato) {
        NodoSimple<T> nuevo = new NodoSimple<>(dato);
        if (estaVacia()) {
            nuevo.setSiguiente(nuevo); // se apunta a si mismo: circulo de uno
            cursor = nuevo;
        } else {
            nuevo.setSiguiente(ultimo.getSiguiente());
            ultimo.setSiguiente(nuevo);
        }
        ultimo = nuevo;
        contador++;
    }

    /** Primer nodo del circulo (el siguiente al ultimo). */
    public NodoSimple<T> getPrimero() {
        return estaVacia() ? null : ultimo.getSiguiente();
    }

    /** Dato que marca el cursor, sin avanzarlo. */
    public T verActual() {
        return cursor == null ? null : cursor.getDato();
    }

    /**
     * Devuelve el dato actual y mueve el cursor al siguiente. Al llegar al
     * final el cursor vuelve solo al principio: eso es la circularidad.
     */
    public T avanzar() {
        if (estaVacia()) {
            return null;
        }
        T actual = cursor.getDato();
        cursor = cursor.getSiguiente();
        return actual;
    }

    /** Devuelve el cursor al primer elemento del circulo. */
    public void reiniciarCursor() {
        cursor = getPrimero();
    }

    /**
     * Posicion del cursor dentro del circulo (0 = primero). Sirve para mostrar
     * en pantalla por que oleada va el ciclo.
     */
    public int getPosicionCursor() {
        if (estaVacia()) {
            return -1;
        }
        NodoSimple<T> actual = getPrimero();
        for (int i = 0; i < contador; i++) {
            if (actual == cursor) {
                return i;
            }
            actual = actual.getSiguiente();
        }
        return -1;
    }

    public int getContador() {
        return contador;
    }

    public boolean estaVacia() {
        return ultimo == null;
    }

    public void limpiar() {
        ultimo = null;
        cursor = null;
        contador = 0;
    }
}
