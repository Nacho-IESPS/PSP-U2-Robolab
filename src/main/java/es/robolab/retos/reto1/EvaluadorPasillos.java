package es.robolab.retos.reto1;

import es.robolab.core.Observador;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/** Banco público de pruebas sobre el contrato. No revela ninguna implementación. */
public final class EvaluadorPasillos {
    private EvaluadorPasillos() {}

    public static ResultadoPruebas ejecutar(ControlPasillos control, Observador obs, boolean animar) {
        final int pasillos = 3, robotsPorPasillo = 3, total = pasillos * robotsPorPasillo;
        int[] ocupados = new int[pasillos];
        AtomicIntegerArray ocupacion = new AtomicIntegerArray(pasillos);
        AtomicInteger solapes = new AtomicInteger(), activos = new AtomicInteger(), maxActivos = new AtomicInteger();
        AtomicInteger completados = new AtomicInteger(), fallos = new AtomicInteger();
        CountDownLatch inicio = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(total);
        List<Future<?>> trabajos = new ArrayList<>();
        long comienzo = System.nanoTime();
        boolean finalizacion = true;
        try {
            for (int i = 0; i < total; i++) {
                final int robot = i, pasillo = i % pasillos;
                trabajos.add(pool.submit(() -> {
                    try {
                        obs.estado(robot, pasillo, 0, "ESPERANDO");
                        inicio.await();
                        control.atravesar(pasillo, () -> {
                            int presentes = ocupacion.incrementAndGet(pasillo);
                            if (presentes > 1) solapes.incrementAndGet();
                            int a = activos.incrementAndGet();
                            maxActivos.accumulateAndGet(a, Math::max);
                            obs.actividad(a, solapes.get(), completados.get());
                            try {
                                final int cuadros = animar ? 12 : 1;
                                for (int t = 1; t <= cuadros; t++) {
                                    obs.estado(robot, pasillo, (double)t / cuadros, "CRUZANDO");
                                    Thread.sleep(animar ? 55 : 220);
                                }
                            } finally {
                                ocupacion.decrementAndGet(pasillo);
                                activos.decrementAndGet();
                                obs.actividad(activos.get(), solapes.get(), completados.get());
                            }
                        });
                        completados.incrementAndGet();
                        obs.estado(robot, pasillo, 1, "ENTREGADO");
                        obs.actividad(activos.get(), solapes.get(), completados.get());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        obs.estado(robot, pasillo, 0, "CANCELADO");
                    } catch (RuntimeException e) {
                        fallos.incrementAndGet();
                        obs.mensaje("Error en robot " + robot + ": " + e.getMessage());
                    }
                }));
            }
            obs.mensaje("Comienzan " + total + " robots repartidos en tres pasillos.");
            inicio.countDown();
            for (Future<?> f : trabajos) f.get(animar ? 20 : 9, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            finalizacion = false;
            obs.mensaje("TIEMPO AGOTADO: quedan tareas sin terminar.");
            for (Future<?> f : trabajos) f.cancel(true);
        } catch (InterruptedException e) {
            finalizacion = false;
            for (Future<?> f : trabajos) f.cancel(true);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            fallos.incrementAndGet();
            obs.mensaje("Fallo inesperado: " + e.getCause());
        } finally {
            pool.shutdownNow();
            try { if (!pool.awaitTermination(2, TimeUnit.SECONDS)) finalizacion = false; }
            catch (InterruptedException e) { finalizacion = false; Thread.currentThread().interrupt(); }
        }
        long duracion = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - comienzo);
        boolean seguridad = solapes.get() == 0;
        boolean concurrencia = maxActivos.get() >= 2;
        boolean integridad = completados.get() == total && fallos.get() == 0;
        String detalles = "Seguridad: " + (seguridad ? "OK" : "FALLO")
                + " | Pasillos independientes: " + (concurrencia ? "OK" : "FALLO")
                + " | 9/9 entregas: " + (integridad ? "OK" : "FALLO")
                + " | Finalización: " + (finalizacion ? "OK" : "FALLO");
        return new ResultadoPruebas(seguridad, concurrencia, integridad, finalizacion,
                solapes.get(), maxActivos.get(), completados.get(), duracion, detalles);
    }
}
