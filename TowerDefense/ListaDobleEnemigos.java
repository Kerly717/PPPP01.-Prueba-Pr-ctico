/**
 * Lista doblemente enlazada con referencia a primero y ultimo,
 * usada para manejar los enemigos activos en el camino.
 */
public class ListaDobleEnemigos {

    private NodoEnemigo primero;
    private NodoEnemigo ultimo;
    private int contador;

    public ListaDobleEnemigos() {
        this.primero = null;
        this.ultimo = null;
        this.contador = 0;
    }

    public boolean estaVacia() {
        return primero == null;
    }

    public int getContador() {
        return contador;
    }

    /**
     * Inserta un nuevo enemigo al final de la lista.
     */
    public void insertarAlFinal(Enemigo enemigo) {
        NodoEnemigo nuevo = new NodoEnemigo(enemigo);
        if (estaVacia()) {
            primero = nuevo;
            ultimo = nuevo;
        } else {
            nuevo.setAnterior(ultimo);
            ultimo.setSiguiente(nuevo);
            ultimo = nuevo;
        }
        contador++;
    }

    /**
     * Busca un enemigo por id recorriendo la lista desde el primero.
     */
    public NodoEnemigo buscarPorId(int id) {
        NodoEnemigo actual = primero;
        while (actual != null) {
            if (actual.getEnemigo().getId() == id) {
                return actual;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    /**
     * Elimina un enemigo destruido (o cualquier enemigo) por su id,
     * reconectando los nodos anterior y siguiente.
     */
    public boolean eliminarPorId(int id) {
        NodoEnemigo nodo = buscarPorId(id);
        if (nodo == null) {
            return false;
        }

        NodoEnemigo nodoAnterior = nodo.getAnterior();
        NodoEnemigo nodoSiguiente = nodo.getSiguiente();

        if (nodoAnterior != null) {
            nodoAnterior.setSiguiente(nodoSiguiente);
        } else {
            primero = nodoSiguiente;
        }

        if (nodoSiguiente != null) {
            nodoSiguiente.setAnterior(nodoAnterior);
        } else {
            ultimo = nodoAnterior;
        }

        nodo.setAnterior(null);
        nodo.setSiguiente(null);
        contador--;
        return true;
    }

    /**
     * Recorre y muestra los enemigos desde el primero hacia el ultimo.
     */
    public void mostrarAdelante() {
        if (estaVacia()) {
            System.out.println("No hay enemigos activos.");
            return;
        }
        System.out.println("--- Enemigos activos (adelante) ---");
        NodoEnemigo actual = primero;
        while (actual != null) {
            System.out.println(actual);
            actual = actual.getSiguiente();
        }
    }

    /**
     * Recorre y muestra los enemigos desde el ultimo hacia el primero.
     */
    public void mostrarAtras() {
        if (estaVacia()) {
            System.out.println("No hay enemigos activos.");
            return;
        }
        System.out.println("--- Enemigos activos (atras) ---");
        NodoEnemigo actual = ultimo;
        while (actual != null) {
            System.out.println(actual);
            actual = actual.getAnterior();
        }
    }

    /**
     * Actualiza la posicion de cada enemigo segun su velocidad, en cada turno.
     */
    public void actualizarPosiciones() {
        NodoEnemigo actual = primero;
        while (actual != null) {
            Enemigo enemigo = actual.getEnemigo();
            enemigo.setPosicion(enemigo.getPosicion() + enemigo.getVelocidad());
            actual = actual.getSiguiente();
        }
    }

    public NodoEnemigo getPrimero() {
        return primero;
    }

    public NodoEnemigo getUltimo() {
        return ultimo;
    }
}
