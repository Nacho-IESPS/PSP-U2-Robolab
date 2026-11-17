package es.robolab.retos.reto1;

/**
 * RETO 1 — EDITA SOLO ESTA CLASE.
 *
 * Versión de partida: es SEGURA, pero demasiado lenta: hace pasar a los robots
 * de uno en uno incluso cuando utilizan pasillos DIFERENTES.
 *
 * Objetivo: que el PASILLO 0 no interfiera con el 1 ni el 2, pero que dentro de
 * cada pasillo nunca haya dos robots ejecutando paso.avanzar() simultáneamente.
 *
 * Restricciones: no modificar las pruebas, no omitir ni duplicar paso.avanzar(),
 * no cambiar el ID del pasillo y no bloquear todos los pasillos con un único monitor.
 */
public final class MiControlPasillos implements ControlPasillos {

    @Override
    public synchronized void atravesar(int pasilloId, Paso paso) throws InterruptedException {
        // TODO R1: reemplaza la sincronización global por exclusión mutua POR PASILLO.
        // 'synchronized' impide que dos hilos entren simultáneamente en una sección.
        // Para este ejercicio debe protegerse solo el recurso realmente compartido.
        paso.avanzar();
    }
}
