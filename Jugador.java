package towerdefense.modelo;

/**
 * Estado del jugador: vidas de la base, oro disponible y puntuacion.
 *
 * El oro es lo que conecta las dos mitades del juego: matar enemigos da oro y
 * el oro es lo que permite construir mas torres.
 */
public class Jugador {

    private final int vidasIniciales;
    private int vidas;
    private int oro;
    private int puntuacion;

    public Jugador(int vidasIniciales, int oroInicial) {
        this.vidasIniciales = vidasIniciales;
        this.vidas = vidasIniciales;
        this.oro = oroInicial;
    }

    public void perderVida() {
        vidas = Math.max(0, vidas - 1);
    }

    public boolean estaDerrotado() {
        return vidas == 0;
    }

    public boolean puedePagar(int costo) {
        return oro >= costo;
    }

    public void gastar(int costo) {
        oro -= costo;
    }

    public void ganarOro(int cantidad) {
        oro += cantidad;
    }

    public void sumarPuntos(int puntos) {
        puntuacion += puntos;
    }

    public void restarPuntos(int puntos) {
        puntuacion = Math.max(0, puntuacion - puntos);
    }

    public int getVidas() {
        return vidas;
    }

    public int getVidasIniciales() {
        return vidasIniciales;
    }

    public int getOro() {
        return oro;
    }

    public int getPuntuacion() {
        return puntuacion;
    }
}
