package towerdefense.mundo;

import towerdefense.logica.ConfigJuego;

public class Mapa {

    public enum Casilla {
        CESPED, CAMINO, ENTRADA, BASE, ARBOL, ROCA;
        
        public boolean esTransitable() {
            return this == CAMINO || this == ENTRADA || this == BASE;
        }

        public boolean esConstruible() {
            return this == CESPED;
        }
    }

    private static final String[] PLANO = {
        ".T....T......T.....T..",
        "...R.............R....",
        "E######...............",
        "......#...............",
        "..T...#.........T.....",
        "......#########.......",
        "..R........T..#.......",
        "..............#.......",
        "...############.......",
        "...#.....T.......T....",
        "...##################B",
        ".T.......R........T..."
    };

    private final Casilla[][] casillas;
    private final boolean[][] ocupada;
    private final int columnas;
    private final int filas;

    public Mapa() {
        filas = PLANO.length;
        columnas = PLANO[0].length();
        casillas = new Casilla[columnas][filas];
        ocupada = new boolean[columnas][filas];

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                casillas[columna][fila] = interpretar(PLANO[fila].charAt(columna));
            }
        }
    }

    private Casilla interpretar(char simbolo) {
        switch (simbolo) {
            case '#': return Casilla.CAMINO;
            case 'E': return Casilla.ENTRADA;
            case 'B': return Casilla.BASE;
            case 'T': return Casilla.ARBOL;
            case 'R': return Casilla.ROCA;
            default:  return Casilla.CESPED;
        }
    }

    public boolean dentro(int columna, int fila) {
        return columna >= 0 && columna < columnas && fila >= 0 && fila < filas;
    }

    public Casilla getCasilla(int columna, int fila) {
        return dentro(columna, fila) ? casillas[columna][fila] : Casilla.ARBOL;
    }

    public boolean sePuedeConstruir(int columna, int fila) {
        return dentro(columna, fila)
                && casillas[columna][fila].esConstruible()
                && !ocupada[columna][fila];
    }

    public void ocupar(int columna, int fila, boolean valor) {
        if (dentro(columna, fila)) {
            ocupada[columna][fila] = valor;
        }
    }

    public Punto centroDe(int columna, int fila) {
        double mitad = ConfigJuego.TAM_CASILLA / 2.0;
        return new Punto(columna * ConfigJuego.TAM_CASILLA + mitad,
                fila * ConfigJuego.TAM_CASILLA + mitad);
    }

    public int getColumnas() {
        return columnas;
    }

    public int getFilas() {
        return filas;
    }

    public int getAnchoPixeles() {
        return columnas * ConfigJuego.TAM_CASILLA;
    }

    public int getAltoPixeles() {
        return filas * ConfigJuego.TAM_CASILLA;
    }
}
