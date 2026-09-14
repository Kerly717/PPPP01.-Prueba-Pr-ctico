package towerdefense.estructuras;
/**
 * Pila LIFO (el ultimo en entrar es el primero en salir), enlazada.
 *
 * Se usa para el HISTORIAL DE ACCIONES: una pila guarda lo que el jugador hizo
 * (deshacer) y otra lo que deshizo (rehacer).
 *
 * @param <T> tipo de dato almacenado.
 */
public class Pila<T> {

    private NodoSimple<T> tope;
    private int contador;

    public void apilar(T dato) {
        NodoSimple<T> nuevo = new NodoSimple<>(dato);
        nuevo.setSiguiente(tope);
        tope = nuevo;
        contador++;
    }

    /** Extrae el tope, o null si la pila esta vacia. */
    public T desapilar() {
        if (estaVacia()) {
            return null;
        }
        NodoSimple<T> salida = tope;
        tope = tope.getSiguiente();
        salida.setSiguiente(null);
        contador--;
        return salida.getDato();
    }

    public T verTope() {
        return estaVacia() ? null : tope.getDato();
    }


    public NodoSimple<T> getTope() {
        return tope;
    }

    public int getContador() {
        return contador;
    }

    public boolean estaVacia() {
        return tope == null;
    }

    public void limpiar() {
        tope = null;
        contador = 0;
    }
}
