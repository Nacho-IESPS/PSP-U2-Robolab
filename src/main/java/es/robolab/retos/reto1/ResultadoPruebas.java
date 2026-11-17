package es.robolab.retos.reto1;

public record ResultadoPruebas(boolean seguridad, boolean concurrencia, boolean integridad,
                              boolean finalizacion, int solapes, int simultaneos,
                              int completados, long duracionMs, String detalles) {
    public boolean superadas() { return seguridad && concurrencia && integridad && finalizacion; }
}
