package es.robolab.retos.reto3;
import java.util.ArrayDeque;
import java.util.Queue;
/** RETO 3. Modifica solo esta clase. */
public final class MiColaPedidos {
    private final Queue<Integer> pedidos = new ArrayDeque<>();
    public void producir(int id) throws InterruptedException { pedidos.add(id); }
    public int consumir() throws InterruptedException { Integer p = pedidos.poll(); return p == null ? -1 : p; }
}
