package es.robolab.retos.reto1;

import es.robolab.core.Observador;

/** Pruebas ejecutables sin depender de bibliotecas externas. */
public class PruebasReto1 {
    public static void main(String[] args) {
        Observador silencio = new Observador() {
            public void estado(int robot, int pasillo, double avance, String estado) {}
            public void mensaje(String texto) { System.out.println(texto); }
            public void actividad(int activos, int colisiones, int completados) {}
        };
        ResultadoPruebas resultado =
                EvaluadorPasillos.ejecutar(new MiControlPasillos(), silencio, false);
        System.out.println(resultado.detalles());
        System.out.println("Máximo simultáneo: " + resultado.simultaneos());
        System.out.println("Duración: " + resultado.duracionMs() + " ms");
        if (!resultado.superadas()) System.exit(1);
    }
}
