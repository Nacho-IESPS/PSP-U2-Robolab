package es.robolab;
import es.robolab.core.EvaluadorRetos;

/** Ejecuta las pruebas de los retos 2-5 sin abrir la interfaz. */
public class PruebasRetos2a5 {
    public static void main(String[] args) {
        boolean todoCorrecto=true;
        for(int n=2;n<=5;n++){
            final int reto=n;
            var resultado=EvaluadorRetos.ejecutar(reto,System.out::println,p->{});
            System.out.println("Reto "+reto+": "+(resultado.correcto()?"OK":"FALLO")+" — "+resultado.detalle());
            todoCorrecto &= resultado.correcto();
        }
        if(!todoCorrecto)System.exit(1);
    }
}
