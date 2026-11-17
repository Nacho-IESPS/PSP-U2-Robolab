package es.robolab.retos.reto5;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
/** RETO 5. Modifica solo esta clase. */
public final class MiGestorTareas {
    public List<Future<Integer>> enviar(List<Callable<Integer>> tareas) {
        List<Future<Integer>> resultados = new ArrayList<>();
        for (Callable<Integer> tarea : tareas) {
            FutureTask<Integer> futuro = new FutureTask<>(tarea);
            futuro.run(); // TODO: ejecutar mediante un ExecutorService, no secuencialmente
            resultados.add(futuro);
        }
        return resultados;
    }
}
