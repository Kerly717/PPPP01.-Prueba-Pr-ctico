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


    public NodoOleada avanzarSiguienteOleada() {
        if (estaVacia()) {
            return null;
        }
        oleadaActual = oleadaActual.getSiguiente();
        return oleadaActual;
    }

    public NodoOleada getOleadaActual() {
        return oleadaActual;
    }


    public void reiniciarCiclo() {
        if (!estaVacia()) {
            oleadaActual = ultimo.getSiguiente();
        }
    }
}
