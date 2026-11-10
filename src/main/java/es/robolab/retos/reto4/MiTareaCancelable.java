package es.robolab.retos.reto4;
import java.util.function.IntConsumer;
public class MiTareaCancelable implements Runnable {
    private final IntConsumer progreso;
    public MiTareaCancelable(IntConsumer progreso) { this.progreso=progreso; }
    @Override public void run() {
        for (int i=1;i<=100;i++) {
            progreso.accept(i);
            try { Thread.sleep(15); }
            catch (InterruptedException e) { /* TODO: atender la cancelación */ }
        }
    }
}
