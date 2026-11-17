package es.robolab.retos.reto1;

import es.robolab.core.Observador;

/** Ejecuta desde IntelliJ como programa Java independiente. */
public class PruebasReto1 {
    public static void main(String[] args) {
        ResultadoPruebas r = EvaluadorPasillos.ejecutar(new MiControlPasillos(), new Observador(){}, false);
        System.out.println("=== RETO 1: PRUEBAS PÚBLICAS ===");
        System.out.println(r.detalles());
        System.out.println("Cruces simultáneos máximos: " + r.simultaneos());
        System.out.println("Colisiones/solapes: " + r.solapes());
        System.out.println("Entregas: " + r.completados() + "/9");
        System.out.println("Duración: " + r.duracionMs() + " ms (orientativa, no única condición)");
        if (!r.superadas()) System.exit(1);
    }
}
