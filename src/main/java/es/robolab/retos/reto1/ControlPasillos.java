package es.robolab.retos.reto1;

/** Contrato del reto. No modificar esta interfaz. */
public interface ControlPasillos {
    /**
     * Ejecuta exactamente una vez el desplazamiento recibido.
     * En el MISMO pasillo no pueden avanzar dos robots a la vez.
     * En pasillos DISTINTOS sí debe ser posible el avance simultáneo.
     */
    void atravesar(int pasilloId, Paso paso) throws InterruptedException;

    @FunctionalInterface
    interface Paso {
        void avanzar() throws InterruptedException;
    }
}
