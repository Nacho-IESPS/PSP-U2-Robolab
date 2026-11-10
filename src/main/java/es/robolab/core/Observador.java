package es.robolab.core;

public interface Observador {
    void estado(int robot, int pasillo, double progreso, String descripcion);
    void mensaje(String texto);
    void actividad(int activos, int colisiones, int completados);
}
