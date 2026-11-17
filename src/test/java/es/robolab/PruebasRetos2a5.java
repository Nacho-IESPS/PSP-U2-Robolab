package es.robolab;
import es.robolab.core.EvaluadorRetos;
public final class PruebasRetos2a5 {
    public static void main(String[] args){
        int fallos=0;
        for(int reto=2;reto<=5;reto++){
            int id=reto;
            EvaluadorRetos.Resultado r=EvaluadorRetos.ejecutar(id,System.out::println,p->{});
            System.out.println("RETO "+id+": "+(r.correcto()?"OK":"FALLO")+" — "+r.detalle());
            if(!r.correcto())fallos++;
        }
        if(fallos>0)System.exit(1);
    }
}
