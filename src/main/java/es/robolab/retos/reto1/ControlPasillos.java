package es.robolab.retos.reto1;

/** Contrato del reto. No se debe modificar esta interfaz. */
public interface ControlPasillos {
    /**
     * Ejecuta exactamente una vez el desplazamiento recibido.
     * Para un mismo pasillo, dos llamadas NO pueden ejecutar paso.avanzar() a la vez.
     * Para pasillos distintos, SÍ debe ser posible hacerlo simultáneamente.
     *
     * @param pasilloId identificador 0, 1 o 2
     * @param paso animación/simulación suministrada, no modificable
     */
    void atravesar(int pasilloId, Paso paso) throws InterruptedException;

    @FunctionalInterface
    interface Paso { void avanzar() throws InterruptedException; }
}
