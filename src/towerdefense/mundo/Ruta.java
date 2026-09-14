package towerdefense.mundo;

import towerdefense.estructuras.ListaSecuencial;

public class Ruta {

    private static final int[][] DIRECCIONES = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

    private final ListaSecuencial<Punto> puntos;
    private double longitudTotal;

    public Ruta(Mapa mapa) {
        puntos = new ListaSecuencial<>(mapa.getColumnas() * mapa.getFilas());
        recorrerCamino(mapa);
        calcularLongitud();
    }

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
