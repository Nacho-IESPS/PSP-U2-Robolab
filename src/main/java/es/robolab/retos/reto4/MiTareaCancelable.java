package es.robolab.retos.reto4;
import java.util.function.IntConsumer;
/** RETO 4. Modifica solo esta clase. */
public final class MiTareaCancelable implements Runnable {
    private final IntConsumer progreso;
    public MiTareaCancelable(IntConsumer progreso) { this.progreso = progreso; }
    @Override public void run() {
        for (int i=1; i<=100; i++) {
            try { Thread.sleep(20); } catch (InterruptedException e) { /* TODO: detener cooperativamente */ }
            progreso.accept(i);
        }
    }
}
