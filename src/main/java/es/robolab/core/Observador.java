package es.robolab.core;

public interface Observador {
    default void estado(int robot, int pasillo, double progreso, String descripcion) {}
    default void mensaje(String texto) {}
    default void actividad(int activos, int colisiones, int completados) {}
}
