package towerdefense.logica;

import towerdefense.modelo.Torre;

/**
 * Accion del jugador sobre una torre, guardada en las pilas de deshacer y
 * rehacer.
 *
 * Solo hay dos acciones y una es la inversa exacta de la otra, asi que basta
 * con el tipo y la torre implicada. Montar un patron Command completo para dos
 * casos seria complicar de mas (KISS y YAGNI).
 */
public class AccionTorre {

    public enum Tipo { CONSTRUIR, VENDER }

    private final Tipo tipo;
    private final Torre torre;

    public AccionTorre(Tipo tipo, Torre torre) {
        this.tipo = tipo;
        this.torre = torre;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public Torre getTorre() {
        return torre;
    }

    public String describir() {
        String verbo = tipo == Tipo.CONSTRUIR ? "Construir" : "Vender";
        return verbo + " " + torre.getTipo().getNombre();
    }
}
