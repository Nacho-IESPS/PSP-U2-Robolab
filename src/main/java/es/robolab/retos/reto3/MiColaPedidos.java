package es.robolab.retos.reto3;
import java.util.*;
public class MiColaPedidos {
    private final Queue<Integer> pedidos = new LinkedList<>();
    public void producir(int id) throws InterruptedException { pedidos.add(id); }
    public int consumir() throws InterruptedException { return pedidos.remove(); }
}
