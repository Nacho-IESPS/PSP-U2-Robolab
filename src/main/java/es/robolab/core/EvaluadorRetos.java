package es.robolab.core;
import es.robolab.retos.reto2.MiContador;
import es.robolab.retos.reto3.MiColaPedidos;
import es.robolab.retos.reto4.MiTareaCancelable;
import es.robolab.retos.reto5.MiGestorTareas;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.Consumer;

/** Evaluaciones independientes, con tiempos máximos y sin soluciones. */
public final class EvaluadorRetos {
    private EvaluadorRetos() {}
    public record Resultado(boolean correcto, String detalle) {}
    public static Resultado ejecutar(int reto, Consumer<String> log, Consumer<Integer> progreso) {
        try {
            return switch(reto) {
                case 2 -> contador(log,progreso);
                case 3 -> cola(log,progreso);
                case 4 -> interrupcion(log,progreso);
                case 5 -> ejecutor(log,progreso);
                default -> new Resultado(false,"Reto no implementado");
            };
        } catch (Exception e) {
            return new Resultado(false,"ERROR: "+e.getClass().getSimpleName()+": "+e.getMessage());
        }
    }
    private static Resultado contador(Consumer<String> log, Consumer<Integer> progreso) throws Exception {
        MiContador c = new MiContador();
        int hilos=8, porHilo=2000, esperado=hilos*porHilo;
        ExecutorService pool=Executors.newFixedThreadPool(hilos);
        CountDownLatch salida=new CountDownLatch(1);
        List<Future<?>> futuros=new ArrayList<>();
        try {
            for(int i=0;i<hilos;i++) futuros.add(pool.submit(()->{
                try { salida.await(); for(int n=0;n<porHilo;n++) c.incrementar(); }
                catch(InterruptedException ex){Thread.currentThread().interrupt();}
            }));
            salida.countDown();
            for(int i=0;i<futuros.size();i++){futuros.get(i).get(5,TimeUnit.SECONDS);progreso.accept((i+1)*100/hilos);}
        } finally {pool.shutdownNow();}
        int obtenido=c.consultar();
        log.accept("Contador: "+obtenido+" / "+esperado);
        return new Resultado(obtenido==esperado,"Resultado "+obtenido+"; esperado "+esperado);
    }
    private static Resultado cola(Consumer<String> log, Consumer<Integer> progreso) throws Exception {
        MiColaPedidos cola=new MiColaPedidos();
        ExecutorService pool=Executors.newFixedThreadPool(6);
        Set<Integer> recibidos=ConcurrentHashMap.newKeySet();
        AtomicInteger duplicados=new AtomicInteger(), invalidos=new AtomicInteger();
        List<Future<?>> fs=new ArrayList<>();
        try {
            for(int prod=0;prod<2;prod++){
                int base=prod*50;
                fs.add(pool.submit(()->{try{for(int i=1;i<=50;i++){cola.producir(base+i);Thread.sleep(2);}}catch(InterruptedException e){Thread.currentThread().interrupt();}}));
            }
            for(int cons=0;cons<4;cons++)fs.add(pool.submit(()->{
                try {for(int i=0;i<25;i++){
                    int id=cola.consumir();
                    if(id<1||id>100)invalidos.incrementAndGet();
                    else if(!recibidos.add(id))duplicados.incrementAndGet();
                    progreso.accept(Math.min(100,recibidos.size()));
                }}catch(InterruptedException e){Thread.currentThread().interrupt();}
            }));
            for(Future<?> f:fs)f.get(5,TimeUnit.SECONDS);
        } finally {pool.shutdownNow();}
        log.accept("Pedidos únicos: "+recibidos.size()+"/100; repetidos: "+duplicados+"; inválidos: "+invalidos);
        boolean ok=recibidos.size()==100&&duplicados.get()==0&&invalidos.get()==0;
        return new Resultado(ok,"Entregados "+recibidos.size()+"/100; duplicados "+duplicados+"; inválidos "+invalidos);
    }
    private static Resultado interrupcion(Consumer<String> log, Consumer<Integer> progreso) throws Exception {
        AtomicInteger ultimo=new AtomicInteger();
        Thread trabajador=new Thread(new MiTareaCancelable(i->{ultimo.set(i);progreso.accept(i);}),"robot-cancelable");
        trabajador.start();
        Thread.sleep(130);
        trabajador.interrupt();
        trabajador.join(650);
        boolean termino=!trabajador.isAlive();
        int valor=ultimo.get();
        if(!termino){trabajador.interrupt();log.accept("AVISO: tarea no se detuvo a tiempo");}
        log.accept("Cancelación solicitada; último avance: "+valor+"/100");
        return new Resultado(termino&&valor<100,"Finalizó tras interrupción: "+termino+"; progreso: "+valor+"/100");
    }
    private static Resultado ejecutor(Consumer<String> log, Consumer<Integer> progreso) throws Exception {
        List<Callable<Integer>> tareas=new ArrayList<>();
        AtomicInteger activos=new AtomicInteger(),max=new AtomicInteger();
        for(int i=1;i<=8;i++){
            final int id=i;
            tareas.add(()->{
                int n=activos.incrementAndGet();max.accumulateAndGet(n,Math::max);
                try {Thread.sleep(90);return id*id;}
                finally {activos.decrementAndGet();}
            });
        }
        long inicio=System.nanoTime();
        List<Future<Integer>> futuros=new MiGestorTareas().enviar(tareas);
        boolean integridad=futuros.size()==8;
        if(integridad)for(int i=0;i<8;i++){
            integridad &= futuros.get(i).get(4,TimeUnit.SECONDS)==(i+1)*(i+1);
            progreso.accept((i+1)*100/8);
        }
        long ms=TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-inicio);
        log.accept("Trabajos: "+futuros.size()+"/8; máximo simultáneo: "+max+"; tiempo: "+ms+" ms");
        return new Resultado(integridad&&max.get()>=2,"Resultados correctos: "+integridad+"; máximo simultáneo: "+max);
    }
}
