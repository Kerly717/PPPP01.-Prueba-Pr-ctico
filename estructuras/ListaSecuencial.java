package towerdefense.estructuras;

/**
 * Lista secuencial (contigua) implementada sobre un arreglo, sin usar ninguna
 * coleccion de java.util.
 *
 * Se usa para las TORRES: son pocas, cambian con poca frecuencia y se accede a
 * ellas por indice en cada turno, que es justo donde un arreglo es mas rapido.
 *
 * Es generica para no escribir una lista distinta por cada tipo de dato (DRY).
 *
 * @param <T> tipo de elemento almacenado.
 */
public class ListaSecuencial<T> {

    private final Object[] elementos;
    private int contador;

    /**
     * @param capacidad numero maximo de elementos que admite la lista.
     */
    public ListaSecuencial(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
        }
        this.elementos = new Object[capacidad];
        this.contador = 0;
    }

    /**
     * Inserta al final del arreglo, en O(1).
     *
     * @return false si la lista ya esta llena.
     */
    public boolean insertar(T elemento) {
        if (estaLlena()) {
            return false;
        }
        elementos[contador] = elemento;
        contador++;
        return true;
    }

    /**
     * Elimina el elemento del indice indicado y desplaza a la izquierda los
     * posteriores, para que no queden huecos en el arreglo. Coste O(n).
     *
     * @return el elemento eliminado, o null si el indice no es valido.
     */
    public T eliminar(int indice) {
        if (!esIndiceValido(indice)) {
            return null;
        }
        T eliminado = obtener(indice);
        for (int i = indice; i < contador - 1; i++) {
            elementos[i] = elementos[i + 1];
        }
        elementos[contador - 1] = null; // se libera la referencia sobrante
        contador--;
        return eliminado;
    }

   
}
