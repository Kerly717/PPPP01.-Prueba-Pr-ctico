package towerdefense.estructuras;

/**
 * Cola FIFO (el primero en entrar es el primero en salir), enlazada.
 *
 * Se usa para la APARICION de enemigos: al lanzar una oleada, todos sus
 * enemigos entran a la cola y van saliendo de a uno por turno, en vez de
 * aparecer todos de golpe.
 *
 * @param <T> tipo de dato almacenado.
 */
public class Cola<T> {

    private NodoSimple<T> frente;
    private NodoSimple<T> fin;
    private int contador;

    public void encolar(T dato) {
        NodoSimple<T> nuevo = new NodoSimple<>(dato);
        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
        contador++;
    }

    /** Extrae el elemento del frente, o null si la cola esta vacia. */
    public T desencolar() {
        if (estaVacia()) {
            return null;
        }
        NodoSimple<T> salida = frente;
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;
        }
        salida.setSiguiente(null); // se corta el enlace para ayudar al recolector
        contador--;
        return salida.getDato();
    }

    public T verFrente() {
        return estaVacia() ? null : frente.getDato();
    }

    public NodoSimple<T> getFrente() {
        return frente;
    }

    public int getContador() {
        return contador;
    }

    public boolean estaVacia() {
        return frente == null;
    }

   
}
