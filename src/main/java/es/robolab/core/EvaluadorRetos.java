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
    public static final int HILOS_CONTADOR = 8;
    public static final int INCREMENTOS_POR_HILO = 10_000_000;
    public static final int TOTAL_CONTADOR = HILOS_CONTADOR * INCREMENTOS_POR_HILO;

    /** Progreso real por hilo: operaciones realizadas, NO valor del contador compartido. */
    public static Resultado contador(Consumer<String> log, Consumer<Integer> progreso,
                                     java.util.function.BiConsumer<Integer,Integer> avanceHilo) throws Exception {
        MiContador contador = new MiContador();
        ExecutorService pool = Executors.newFixedThreadPool(HILOS_CONTADOR);
        CountDownLatch salida = new CountDownLatch(1);
        java.util.concurrent.atomic.AtomicIntegerArray avances =
                new java.util.concurrent.atomic.AtomicIntegerArray(HILOS_CONTADOR);
        java.util.List<Future<?>> futuros = new ArrayList<>();
        long inicio = System.nanoTime();
        try {
            for (int hilo = 0; hilo < HILOS_CONTADOR; hilo++) {
                final int id = hilo;
                futuros.add(pool.submit(() -> {
                    salida.await();
                    // Un aviso cada 100.000 operaciones; no hay esperas artificiales.
                    final int lote = 100_000;
                    for (int i = 1; i <= INCREMENTOS_POR_HILO; i++) {
                        contador.incrementar();
                        if (i % lote == 0) {
                            avances.set(id, i);
                            avanceHilo.accept(id, i);
                        }
                    }
                    return null;
                }));
            }
            salida.countDown();
            // Las actualizaciones gráficas se consultan desde Swing cada 40 ms.
            for (Future<?> futuro : futuros) futuro.get(120, TimeUnit.SECONDS);
        } finally {
            pool.shutdownNow();
        }
        long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - inicio);
        int obtenido = contador.consultar();
        int realizados = 0;
        for (int i = 0; i < HILOS_CONTADOR; i++) realizados += avances.get(i);
        progreso.accept(100);
        boolean ok = obtenido == TOTAL_CONTADOR && realizados == TOTAL_CONTADOR;
        log.accept("Reto 2: " + obtenido + " / " + TOTAL_CONTADOR +
                   " incrementos; operaciones ejecutadas: " + realizados +
                   "; duración: " + ms + " ms");
        return new Resultado(ok, "Contador: " + obtenido + " / " + TOTAL_CONTADOR +
                              "; perdidos: " + (TOTAL_CONTADOR - obtenido) +
                              "; tiempo: " + ms + " ms");
    }

    private static Resultado contador(Consumer<String> log, Consumer<Integer> progreso) throws Exception {
        return contadorPruebas(log, progreso);
    }

    /** Varias rondas independientes. La ausencia de fallos NO prueba formalmente la atomicidad. */
    public static Resultado contadorPruebas(Consumer<String> log, Consumer<Integer> progreso) throws Exception {
        int[][] escenarios = {{8, 1_000_000}, {16, 500_000}, {8, 2_000_000}, {16, 1_000_000}};
        int fallos = 0;
        StringBuilder resumen = new StringBuilder();
        for (int ronda=0; ronda<escenarios.length; ronda++) {
            final int hilos=escenarios[ronda][0], vueltas=escenarios[ronda][1];
            MiContador contador=new MiContador();
            ExecutorService pool=Executors.newFixedThreadPool(hilos);
            CountDownLatch salida=new CountDownLatch(1);
            List<Future<?>> fs=new ArrayList<>();
            try {
                for(int j=0;j<hilos;j++) fs.add(pool.submit(()->{
                    salida.await();
                    for(int k=0;k<vueltas;k++) contador.incrementar();
                    return null;
                }));
                salida.countDown();
                for(Future<?> f:fs) f.get(90,TimeUnit.SECONDS);
            } finally { pool.shutdownNow(); }
            int esperado=hilos*vueltas, obtenido=contador.consultar();
            boolean ok=esperado==obtenido;
            if(!ok) fallos++;
            String linea="Ronda "+(ronda+1)+": "+hilos+" hilos × "+vueltas+
                         " = "+obtenido+" / "+esperado+" — "+(ok?"OK":"FALLO");
            log.accept(linea);
            resumen.append(linea).append("\n");
            progreso.accept((ronda+1)*100/escenarios.length);
        }
        String aviso="Las pruebas dinámicas no demuestran por sí solas ausencia de carreras; revisar el mecanismo atómico.";
        log.accept(aviso);
        return new Resultado(fallos==0, "Rondas superadas: "+(escenarios.length-fallos)+"/"+
            escenarios.length+". "+aviso);
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
    // Se escribe el resultado de cada cálculo en un campo volátil para impedir su eliminación por JIT.
    private static volatile long testigoCalculo;
    public static final int TAREAS_EJECUTOR=8;
    public static final int OPERACIONES_EJECUTOR=100_000_000;

    /** Estados: 0 pendiente, 1 ejecutando, 2 terminada. */
    public static Resultado ejecutor(Consumer<String> log, Consumer<Integer> progreso,
                                     java.util.function.BiConsumer<Integer,Integer> estadoTarea,
                                     boolean simulacion) throws Exception {
        final int vueltas=simulacion?OPERACIONES_EJECUTOR:1_000_000;
        List<Callable<Integer>> tareas=new ArrayList<>();
        AtomicInteger activos=new AtomicInteger(), max=new AtomicInteger(), terminadas=new AtomicInteger();
        for(int i=1;i<=TAREAS_EJECUTOR;i++){
            final int id=i;
            estadoTarea.accept(id-1,0);
            tareas.add(()->{
                int n=activos.incrementAndGet();
                max.accumulateAndGet(n,Math::max);
                estadoTarea.accept(id-1,1);
                try {
                    long acumulado=id;
                    // Trabajo computacional real, sin esperas. Cada resultado depende del cálculo.
                    for(int k=0;k<vueltas;k++) {
                        acumulado = (acumulado * 1664525L + 1013904223L + k) & 0x7fffffffL;
                    }
                    testigoCalculo=acumulado;
                    return id*id;
                } finally {
                    estadoTarea.accept(id-1,2);
                    progreso.accept(terminadas.incrementAndGet()*100/TAREAS_EJECUTOR);
                    activos.decrementAndGet();
                }
            });
        }
        long inicio=System.nanoTime();
        List<Future<Integer>> futuros=new MiGestorTareas().enviar(tareas);
        boolean integridad=futuros!=null && futuros.size()==TAREAS_EJECUTOR;
        if(integridad)for(int i=0;i<TAREAS_EJECUTOR;i++){
            integridad &= futuros.get(i).get(90,TimeUnit.SECONDS)==(i+1)*(i+1);
        }
        long ms=TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-inicio);
        log.accept("Reto 5: "+(futuros==null?0:futuros.size())+"/8 tareas; máximo simultáneo: "+max.get()+
                   "; tiempo: "+ms+" ms; cálculo por tarea: "+vueltas+" iteraciones");
        return new Resultado(integridad&&max.get()>=2,
            "Resultados correctos: "+integridad+"; máximo simultáneo: "+max.get()+
            "; tiempo: "+ms+" ms");
    }

    private static Resultado ejecutor(Consumer<String> log, Consumer<Integer> progreso) throws Exception {
        return ejecutor(log,progreso,(id,estado)->{},false);
    }
}
