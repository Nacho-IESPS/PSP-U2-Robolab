package es.robolab.retos.reto5;
import java.util.*;
import java.util.concurrent.*;
public class MiGestorTareas {
    public List<Future<Integer>> enviar(List<Callable<Integer>> tareas) throws Exception {
        List<Future<Integer>> resultados=new ArrayList<>();
        for (Callable<Integer> tarea:tareas) {
            FutureTask<Integer> futuro=new FutureTask<>(tarea);
            futuro.run(); // TODO: no ejecutar secuencialmente
            resultados.add(futuro);
        }
        return resultados;
    }
}
