package towerdefense.logica;

import towerdefense.estructuras.ListaDoble;
import towerdefense.estructuras.NodoDoble;

/**
 * Bitacora de la partida. Reutiliza la ListaDoble ya implementada en vez de
 * escribir otra estructura (DRY) y descarta los mensajes mas viejos cuando se
 * pasa del limite, para que la memoria no crezca sin control.
 */
public class RegistroEventos {

    private final ListaDoble<Evento> eventos = new ListaDoble<>();
    private final int maximo;

    public RegistroEventos(int maximo) {
        this.maximo = maximo;
    }

    public void agregar(Evento evento) {
        eventos.insertarAlFinal(evento);
        while (eventos.getContador() > maximo) {
            eventos.eliminarNodo(eventos.getPrimero());
        }
    }

    /** Ultimo evento registrado (el mas reciente). */
    public Evento getUltimo() {
        NodoDoble<Evento> ultimo = eventos.getUltimo();
        return ultimo == null ? null : ultimo.getDato();
    }

    /** Nodo final: la interfaz recorre hacia atras para mostrar lo mas nuevo. */
    public NodoDoble<Evento> getNodoUltimo() {
        return eventos.getUltimo();
    }

    public int getContador() {
        return eventos.getContador();
    }

    public void limpiar() {
        eventos.limpiar();
    }
}
