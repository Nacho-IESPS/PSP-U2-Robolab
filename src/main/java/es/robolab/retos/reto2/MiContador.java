package es.robolab.retos.reto2;
/** RETO 2: el alumnado modifica únicamente esta clase. */
public final class MiContador {
    private int valor = 0;
    // Implementación inicial NO atómica: pierde actualizaciones concurrentes.
    public void incrementar() { valor++; }
    public int consultar() { return valor; }
}
