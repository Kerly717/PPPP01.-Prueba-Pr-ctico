package towerdefense.mundo;

import towerdefense.estructuras.ListaSecuencial;

/**
 * Trayecto que siguen los enemigos, de la entrada a la base.
 *
 * Se calcula una sola vez al empezar la partida recorriendo el camino casilla
 * por casilla. Como el trazado es un pasillo sin bifurcaciones, basta con ir
 * saltando a la unica casilla vecina que aun no se ha visitado.
 *
 * Cada enemigo guarda cuantos pixeles lleva recorridos; {@link #puntoEn(double)}
 * traduce esa distancia a una coordenada de la pantalla. Asi el movimiento es
 * continuo y suave, en vez de ir a saltos de casilla en casilla.
 */
public class Ruta {

    /** Vecinos en cruz: arriba, derecha, abajo, izquierda. */
    private static final int[][] DIRECCIONES = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

    private final ListaSecuencial<Punto> puntos;
    private double longitudTotal;

    public Ruta(Mapa mapa) {
        puntos = new ListaSecuencial<>(mapa.getColumnas() * mapa.getFilas());
        recorrerCamino(mapa);
        calcularLongitud();
    }

    /** Sigue el pasillo desde la entrada hasta que no queda vecino nuevo. */
    private void recorrerCamino(Mapa mapa) {
        int[] entrada = buscarEntrada(mapa);
        boolean[][] visitada = new boolean[mapa.getColumnas()][mapa.getFilas()];

        int columna = entrada[0];
        int fila = entrada[1];
        while (true) {
            visitada[columna][fila] = true;
            puntos.insertar(mapa.centroDe(columna, fila));

            int[] siguiente = buscarVecinoNuevo(mapa, visitada, columna, fila);
            if (siguiente == null) {
                return;
            }
            columna = siguiente[0];
            fila = siguiente[1];
        }
    }

    private int[] buscarEntrada(Mapa mapa) {
        for (int fila = 0; fila < mapa.getFilas(); fila++) {
            for (int columna = 0; columna < mapa.getColumnas(); columna++) {
                if (mapa.getCasilla(columna, fila) == Mapa.Casilla.ENTRADA) {
                    return new int[]{columna, fila};
                }
            }
        }
        throw new IllegalStateException("El mapa no tiene casilla de entrada.");
    }

    private int[] buscarVecinoNuevo(Mapa mapa, boolean[][] visitada, int columna, int fila) {
        for (int[] direccion : DIRECCIONES) {
            int nuevaColumna = columna + direccion[0];
            int nuevaFila = fila + direccion[1];
            if (mapa.dentro(nuevaColumna, nuevaFila)
                    && !visitada[nuevaColumna][nuevaFila]
                    && mapa.getCasilla(nuevaColumna, nuevaFila).esTransitable()) {
                return new int[]{nuevaColumna, nuevaFila};
            }
        }
        return null;
    }

    private void calcularLongitud() {
        for (int i = 1; i < puntos.getContador(); i++) {
            longitudTotal += puntos.obtener(i - 1).distanciaA(puntos.obtener(i));
        }
    }

    /**
     * Convierte una distancia recorrida en la coordenada correspondiente,
     * interpolando entre los dos puntos del tramo en el que se encuentra.
     */
    public Punto puntoEn(double distancia) {
        if (distancia <= 0) {
            return puntos.obtener(0);
        }
        double restante = distancia;
        for (int i = 1; i < puntos.getContador(); i++) {
            Punto desde = puntos.obtener(i - 1);
            Punto hasta = puntos.obtener(i);
            double tramo = desde.distanciaA(hasta);

            if (restante <= tramo) {
                double avance = restante / tramo;
                return new Punto(desde.getX() + (hasta.getX() - desde.getX()) * avance,
                        desde.getY() + (hasta.getY() - desde.getY()) * avance);
            }
            restante -= tramo;
        }
        return puntos.obtener(puntos.getContador() - 1);
    }

    public double getLongitudTotal() {
        return longitudTotal;
    }

    public ListaSecuencial<Punto> getPuntos() {
        return puntos;
    }
}
