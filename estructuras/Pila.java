package towerdefense.estructuras;



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
