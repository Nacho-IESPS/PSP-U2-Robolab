package es.robolab.retos.reto1;

/**
 * RETO 1 — ÚNICO ARCHIVO QUE DEBES MODIFICAR.
 *
 * Punto de partida: esta versión evita colisiones, pero obliga a
 * los nueve robots a cruzar de uno en uno.
 *
 * Objetivo: garantizar exclusión mutua por pasillo y permitir que
 * robots de pasillos DIFERENTES crucen a la vez.
 *
 * No debes modificar las pruebas ni omitir paso.avanzar().
 */
public final class MiControlPasillos implements ControlPasillos {
    @Override
    public synchronized void atravesar(int pasilloId, Paso paso)
            throws InterruptedException {
        // TODO: sustituye el bloqueo global por exclusión POR PASILLO.
        paso.avanzar();
    }
}
