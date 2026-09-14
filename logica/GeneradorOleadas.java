package towerdefense.logica;

import towerdefense.estructuras.ListaCircular;
import towerdefense.modelo.Oleada;
import towerdefense.modelo.TipoEnemigo;

/**
 * Arma el ciclo de olas de la partida, con dificultad creciente.
 *
 * Las olas viven en una lista circular: cuando termina la ultima, el cursor
 * vuelve solo a la primera y se puede jugar otra vuelta mas dificil.
 */
public final class GeneradorOleadas {

    private GeneradorOleadas() {
        // Clase utilitaria.
    }

    public static ListaCircular<Oleada> crear() {
        ListaCircular<Oleada> olas = new ListaCircular<>();
        olas.insertar(new Oleada(1, TipoEnemigo.BASICO, 12, 1.0));
        olas.insertar(new Oleada(2, TipoEnemigo.BASICO, 15, 1.2));
        olas.insertar(new Oleada(3, TipoEnemigo.RAPIDO, 12, 1.0));
        olas.insertar(new Oleada(4, TipoEnemigo.BLINDADO, 8, 1.0));
        olas.insertar(new Oleada(5, TipoEnemigo.BASICO, 20, 1.6));
        olas.insertar(new Oleada(6, TipoEnemigo.RAPIDO, 18, 1.4));
        olas.insertar(new Oleada(7, TipoEnemigo.BLINDADO, 12, 1.4));
        olas.insertar(new Oleada(8, TipoEnemigo.JEFE, 3, 1.0));
        return olas;
    }

    public static int getTotalOlas() {
        return 8;
    }
}
