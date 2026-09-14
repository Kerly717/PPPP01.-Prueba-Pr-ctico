package towerdefense.modelo;

/**
 * Receta de una oleada: cuantos enemigos salen, de que tipo y con cuanta vida
 * respecto a la normal.
 */
public class Oleada {

    private final int numero;
    private final TipoEnemigo tipo;
    private final int cantidad;
    private final double multiplicadorVida;

    public Oleada(int numero, TipoEnemigo tipo, int cantidad, double multiplicadorVida) {
        this.numero = numero;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.multiplicadorVida = multiplicadorVida;
    }

    public int getNumero() {
        return numero;
    }

    public TipoEnemigo getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getMultiplicadorVida() {
        return multiplicadorVida;
    }

    @Override
    public String toString() {
        return "Ola " + numero + ": " + cantidad + " x " + tipo.getNombre();
    }
}
