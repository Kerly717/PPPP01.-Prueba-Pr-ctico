package towerdefense.mundo;

import towerdefense.logica.ConfigJuego;

/**
 * Mapa del bosque. Se construye a partir de un plano de texto donde cada
 * caracter es una casilla:
 *
 *   '.' cesped libre (aqui se puede construir)   '#' camino
 *   'E' entrada de los enemigos                  'B' base a proteger
 *   'T' arbol      'R' roca   (decoracion, no se puede construir)
 *
 * Guardar el mapa como texto hace que sea facil de leer y de modificar: para
 * cambiar el trazado basta con redibujar el plano, sin tocar una sola linea de
 * codigo.
 */
public class Mapa {

    /** Tipos de casilla del mapa. */
    public enum Casilla {
        CESPED, CAMINO, ENTRADA, BASE, ARBOL, ROCA;

        /** Los enemigos solo caminan por el trazado. */
        public boolean esTransitable() {
            return this == CAMINO || this == ENTRADA || this == BASE;
        }

        /** Solo se construye sobre cesped libre. */
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

    /** Una casilla admite torre si es cesped y no hay ya una encima. */
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

    /** Centro en pixeles de una casilla. */
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
