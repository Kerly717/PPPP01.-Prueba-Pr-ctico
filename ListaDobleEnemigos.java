
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
