package es.robolab.app;

import es.robolab.core.Observador;
import es.robolab.core.EvaluadorRetos;
import es.robolab.retos.reto1.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/** Interfaz y motor visual suministrados: NO modificar para resolver el reto. */
public final class Main extends JFrame {
    private final CanvasAlmacen canvas = new CanvasAlmacen();
    private final JPanel visual = new JPanel(new CardLayout());
    private final CanvasRetos canvasRetos = new CanvasRetos();
    private final JProgressBar barra = new JProgressBar(0,100);
    private final JLabel concepto = new JLabel("", SwingConstants.CENTER);
    private final JLabel descripcion = new JLabel("", SwingConstants.CENTER);
    private final JLabel subtitulo = new JLabel("Reto 1 — Seguridad y paralelismo en tres pasillos independientes");
    private final JTextArea instrucciones = new JTextArea();
    private final java.util.List<JButton> botonesReto = new java.util.ArrayList<>();
    private volatile int retoActual = 1;
    private final JTextPane consola = new JTextPane();
    private final JLabel resumen = new JLabel("Pendiente de probar", SwingConstants.CENTER);
    private final JTextArea informe = new JTextArea("Pulsa Ejecutar pruebas para ver el diagnóstico.");
    private final JLabel estado = new JLabel("9 robots · 3 pasillos · 3 robots/pasillo");
    private final JLabel tiempo = new JLabel("Tiempo: —");
    private final JLabel incidencias = new JLabel("Colisiones: —");
    private final JLabel simultaneos = new JLabel("Simultáneos: —");
    private final JButton ejecutar = boton("▶ Iniciar simulación", new Color(29,110,192));
    private final JButton probar = boton("✓ Ejecutar pruebas", new Color(16,135,100));
    private final JButton pista = boton("? Siguiente pista", new Color(112,75,177));
    private final AtomicBoolean ocupado = new AtomicBoolean(false);
    private int nivelPista = 0;
    private final boolean autoInicio;

    public Main(boolean autoInicio) {
        super("RoboLab · Laboratorio de concurrencia · Reto 1");
        this.autoInicio = autoInicio;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1220,790);
        setMinimumSize(new Dimension(980,650));
        setLocationRelativeTo(null);
        setContentPane(construir());
        if (autoInicio) SwingUtilities.invokeLater(() -> lanzar(true));
    }

    private static JButton boton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setOpaque(true); b.setBackground(color); b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif",Font.BOLD,13));
        b.setBorder(new EmptyBorder(11,14,11,14)); b.setFocusPainted(false);
        return b;
    }
    private JComponent construir() {
        JPanel root = new JPanel(new BorderLayout(14,14));
        root.setBackground(new Color(242,246,252)); root.setBorder(new EmptyBorder(18,18,18,18));
        JPanel cabecera = new JPanel(new BorderLayout()); cabecera.setOpaque(false);
        JLabel titulo = new JLabel("ROBOLAB  /  ALMACÉN CONCURRENTE");
        titulo.setFont(new Font("SansSerif",Font.BOLD,22)); titulo.setForeground(new Color(20,50,84));
        JLabel subt = subtitulo;
        subt.setForeground(new Color(75,98,124));
        JPanel textos = new JPanel(new GridLayout(2,1,0,3)); textos.setOpaque(false);
        textos.add(titulo); textos.add(subt);
        cabecera.add(textos,BorderLayout.WEST);
        root.add(cabecera,BorderLayout.NORTH);

        JPanel menu = new JPanel(); menu.setLayout(new BoxLayout(menu,BoxLayout.Y_AXIS));
        menu.setBackground(Color.WHITE); menu.setBorder(new EmptyBorder(16,12,16,12));
        menu.setPreferredSize(new Dimension(195,400));
        JLabel m = new JLabel("ESCENARIOS"); m.setFont(new Font("SansSerif",Font.BOLD,14)); menu.add(m); menu.add(Box.createVerticalStrut(18));
        String[] nombres = {"01  Pasillos","02  Contadores","03  Pedidos","04  Interrupciones","05  Ejecutores"};
        for(int i=0;i<nombres.length;i++){
            final int num=i+1;
            JButton boton = new JButton(nombres[i]);
            boton.setHorizontalAlignment(SwingConstants.LEFT);
            boton.setMaximumSize(new Dimension(190,46));
            boton.setBorder(new EmptyBorder(12,9,12,6));
            boton.setFocusPainted(false);
            boton.addActionListener(e->seleccionar(num));
            botonesReto.add(boton);
            menu.add(boton);menu.add(Box.createVerticalStrut(5));
        }
        menu.add(Box.createVerticalGlue());
        JLabel pie = new JLabel("Selecciona cualquier reto");
        pie.setFont(new Font("SansSerif",Font.PLAIN,10));pie.setForeground(Color.GRAY);menu.add(pie);
        root.add(menu,BorderLayout.WEST);

        JPanel derecha = new JPanel(new BorderLayout(10,10));derecha.setOpaque(false);
        visual.add(canvas,"1");visual.add(canvasRetos,"otros");
        derecha.add(visual,BorderLayout.CENTER);
        JPanel abajo = new JPanel(new BorderLayout(9,9)); abajo.setOpaque(false);
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT,8,6)); controles.setOpaque(false);
        ejecutar.addActionListener(e -> lanzar(true));
        probar.addActionListener(e -> lanzar(false));
        pista.addActionListener(e -> revelarPista());
        controles.add(ejecutar);controles.add(probar);controles.add(pista);
        JButton referencia = boton("▶ Ver vídeo de referencia",new Color(69,83,106));
        referencia.addActionListener(e -> verReferencia());
        controles.add(referencia);
        abajo.add(controles, BorderLayout.NORTH);
        consola.setEditable(false);consola.setFont(new Font("Monospaced",Font.PLAIN,12));
        consola.setBackground(new Color(20,30,47));consola.setForeground(new Color(197,228,236));
        consola.setText("ROBOLAB LISTO\nInicia la simulación o pulsa 'Ejecutar pruebas'.\nModifica únicamente MiControlPasillos.java.\n");
        JScrollPane log = new JScrollPane(consola);log.setPreferredSize(new Dimension(0,150));
        abajo.add(log,BorderLayout.CENTER);
        derecha.add(abajo,BorderLayout.SOUTH);
        root.add(derecha,BorderLayout.CENTER);

        JPanel met = new JPanel();met.setLayout(new BoxLayout(met,BoxLayout.Y_AXIS));
        met.setBackground(Color.WHITE);met.setBorder(new EmptyBorder(14,13,14,13));
        met.setPreferredSize(new Dimension(225,400));
        JLabel l = new JLabel("DIAGNÓSTICO");l.setFont(new Font("SansSerif",Font.BOLD,14));
        met.add(l);met.add(Box.createVerticalStrut(16));
        for(JLabel x:new JLabel[]{estado,tiempo,incidencias,simultaneos}){
            x.setFont(new Font("SansSerif",Font.PLAIN,12));x.setAlignmentX(Component.LEFT_ALIGNMENT);
            met.add(x);met.add(Box.createVerticalStrut(13));
        }
        met.add(Box.createVerticalStrut(10));
        resumen.setOpaque(true); resumen.setBackground(new Color(239,243,248));
        resumen.setBorder(new EmptyBorder(12,9,12,9));resumen.setMaximumSize(new Dimension(220,70));
        resumen.setFont(new Font("SansSerif",Font.BOLD,11));
        resumen.setAlignmentX(Component.LEFT_ALIGNMENT);met.add(resumen);
        met.add(Box.createVerticalStrut(10));
        JLabel detalleTitulo = new JLabel("RESULTADO DE LAS PRUEBAS");
        detalleTitulo.setFont(new Font("SansSerif", Font.BOLD, 11));
        detalleTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        met.add(detalleTitulo); met.add(Box.createVerticalStrut(6));
        informe.setEditable(false); informe.setOpaque(false);
        informe.setLineWrap(true); informe.setWrapStyleWord(true);
        informe.setFont(new Font("SansSerif",Font.PLAIN,11));
        informe.setAlignmentX(Component.LEFT_ALIGNMENT);
        informe.setMaximumSize(new Dimension(215,125));
        met.add(informe); met.add(Box.createVerticalStrut(12));
        instrucciones.setText("ARCHIVO A MODIFICAR\nMiControlPasillos.java\n\nOBJETIVO\n• Sin colisiones por pasillo.\n• Pasillos distintos, simultáneos.\n• Nueve entregas correctas.\n• No modificar motor ni pruebas.");
        instrucciones.setFont(new Font("SansSerif",Font.PLAIN,12));
        instrucciones.setWrapStyleWord(true);instrucciones.setLineWrap(true);
        instrucciones.setEditable(false);instrucciones.setOpaque(false);
        met.add(instrucciones);
        root.add(met,BorderLayout.EAST);
        seleccionar(1);
        return root;
    }

    private void log(String texto){SwingUtilities.invokeLater(()->{
        try {
            StyledDocument doc = consola.getStyledDocument();
            SimpleAttributeSet estilo = new SimpleAttributeSet();
            boolean error = texto.contains("FALLO") || texto.contains("ERROR") || texto.contains("Error") || texto.contains("AGOTADO");
            StyleConstants.setForeground(estilo, error ? new Color(255,125,125) : new Color(197,228,236));
            StyleConstants.setBold(estilo, error);
            doc.insertString(doc.getLength(), texto + "\n", estilo);
            consola.setCaretPosition(doc.getLength());
        } catch (BadLocationException ex) { throw new IllegalStateException(ex); }
    });}
    private void seleccionar(int n) {
        if(ocupado.get())return;
        retoActual=n;nivelPista=0;
        String[] titulos={"","Pasillos independientes","Contador compartido","Fábrica de pedidos","Cancelación de una tarea","Gestor de trabajos"};
        String[] archivos={"","MiControlPasillos.java","MiContador.java","MiColaPedidos.java","MiTareaCancelable.java","MiGestorTareas.java"};
        String[] objetivos={"","Proteger cada pasillo sin bloquear los demás.",
            "Contabilizar 80.000.000 incrementos concurrentes sin pérdidas.",
            "Entregar 100 pedidos, sin pérdidas ni duplicados.",
            "Detener la tarea cuando se solicita una interrupción.",
            "Ejecutar ocho trabajos con al menos dos simultáneos."};
        for(int i=0;i<botonesReto.size();i++){
            JButton b=botonesReto.get(i);
            b.setBackground(i==n-1?new Color(222,239,255):Color.WHITE);
            b.setForeground(i==n-1?new Color(28,85,157):new Color(95,110,130));
        }
        ((CardLayout)visual.getLayout()).show(visual,n==1?"1":"otros");
        subtitulo.setText("Reto "+n+" — "+titulos[n]);
        concepto.setText(titulos[n]);descripcion.setText(objetivos[n]);
        canvasRetos.seleccionar(n);
        instrucciones.setText("ARCHIVO A MODIFICAR\n"+archivos[n]+"\n\nOBJETIVO\n"+objetivos[n]+"\n\nNo modificar las pruebas ni el motor.");
        resumen.setText("Pendiente de probar");informe.setText("Ejecuta las pruebas del reto seleccionado.");
        estado.setText("Reto "+n+" / 5");tiempo.setText("Tiempo: —");
        incidencias.setText("Progreso: 0 %");simultaneos.setText("Resultado: —");
        canvasRetos.avanzar(0);
        setTitle("RoboLab · Reto "+n+" · "+titulos[n]);
    }

    private void lanzar(boolean animar) {
        if(retoActual!=1){lanzarOtro(animar);return;}

        if(!ocupado.compareAndSet(false,true)) return;
        ejecutar.setEnabled(false);probar.setEnabled(false);
        canvas.reset();
        resumen.setText("Ejecutando...");resumen.setBackground(new Color(254,243,199));
        informe.setText("Se están realizando las comprobaciones...");
        log("\n=== "+(animar?"SIMULACIÓN VISUAL":"PRUEBAS AUTOMÁTICAS")+" ===");
        new Thread(()->{
            ResultadoPruebas r = EvaluadorPasillos.ejecutar(new MiControlPasillos(),new Observador(){
                @Override public void estado(int robot,int pasillo,double progreso,String desc){
                    canvas.setRobot(robot,pasillo,progreso,desc);
                }
                @Override public void mensaje(String texto){log(texto);}
                @Override public void actividad(int activos,int colisiones,int completados){
                    SwingUtilities.invokeLater(()->{
                        incidencias.setText("Colisiones: "+colisiones);
                        estado.setText("Entregas: "+completados+" / 9");
                        simultaneos.setText("Robots cruzando: "+activos);
                    });
                }
            },animar);
            SwingUtilities.invokeLater(()->{
                log(r.detalles());
                log("Simultáneos máximos: "+r.simultaneos()+" | Solapes: "+r.solapes()+" | Duración: "+r.duracionMs()+" ms");
                tiempo.setText("Tiempo: "+r.duracionMs()+" ms");
                simultaneos.setText("Máx. simultáneos: "+r.simultaneos());
                resumen.setText(r.superadas()?"✓ PRUEBAS SUPERADAS":"✗ PRUEBAS FALLIDAS");
                resumen.setBackground(r.superadas()?new Color(220,252,231):new Color(254,226,226));
                resumen.setForeground(r.superadas()?new Color(15,103,55):new Color(166,25,25));
                informe.setForeground(r.superadas()?new Color(15,103,55):new Color(166,25,25));
                informe.setText(
                    "Seguridad: " + (r.seguridad()?"OK":"FALLO") + "\n" +
                    "Pasillos independientes: " + (r.concurrencia()?"OK":"FALLO") + "\n" +
                    "Entregas (9): " + (r.integridad()?"OK":"FALLO") + "\n" +
                    "Finalización: " + (r.finalizacion()?"OK":"FALLO") + "\n" +
                    "Máximo simultáneo: " + r.simultaneos()
                );
                if (!r.superadas()) {
                    Toolkit.getDefaultToolkit().beep();
                    if (!r.concurrencia() && r.seguridad())
                        log("FALLO: los pasillos son independientes, pero los robots están cruzando de uno en uno. Revisa el monitor que utilizas.");
                    if (!r.seguridad()) log("FALLO: dos robots han entrado simultáneamente en el mismo pasillo.");
                    if (!r.integridad()) log("FALLO: no se han completado las nueve entregas.");
                    if (!r.finalizacion()) log("FALLO: alguna tarea no ha terminado a tiempo.");
                }
                ejecutar.setEnabled(true);probar.setEnabled(true);ocupado.set(false);
            });
        }, "evaluador-robolab").start();
    }
    private void lanzarOtro(boolean simulacion) {
        if(!ocupado.compareAndSet(false,true))return;
        final int n=retoActual;
        ejecutar.setEnabled(false);probar.setEnabled(false);
        canvasRetos.seleccionar(n);
        resumen.setText("Ejecutando...");informe.setText("Pruebas en curso...");
        log("\n=== RETO "+n+" · "+(simulacion?"SIMULACIÓN":"PRUEBAS AUTOMÁTICAS")+" ===");
        new Thread(()->{
            long inicio=System.nanoTime();
            // El temporizador solo refresca la pantalla; no ralentiza el cálculo.
            javax.swing.Timer refresco = new javax.swing.Timer(40, e -> {
                if (n == 2 && simulacion) {
                    int porcentaje = canvasRetos.porcentajeReal();
                    canvasRetos.avanzar(porcentaje);
                    incidencias.setText("Progreso real: " + porcentaje + " %");
                } else if(n==5 && simulacion) {
                    canvasRetos.repaint();
                    incidencias.setText("Tareas terminadas: "+canvasRetos.terminadasTarea()+"/8");
                    simultaneos.setText("Tareas activas: "+canvasRetos.activasTarea());
                }
            });
            if((n==2 || n==5) && simulacion) SwingUtilities.invokeLater(refresco::start);
            EvaluadorRetos.Resultado r;
            try {
                if (n == 2 && simulacion) {
                    r = EvaluadorRetos.contador(this::log,p->{},canvasRetos::avanzarHilo);
                } else if (n == 2) {
                    r = EvaluadorRetos.contadorPruebas(this::log,p ->
                        SwingUtilities.invokeLater(() -> canvasRetos.avanzar(p)));
                } else if (n == 5) {
                    r = EvaluadorRetos.ejecutor(this::log,p ->
                        SwingUtilities.invokeLater(() -> canvasRetos.avanzar(p)),
                        canvasRetos::estadoTarea,simulacion);
                } else {
                    r = EvaluadorRetos.ejecutar(n,this::log,p->
                        SwingUtilities.invokeLater(()->{
                            canvasRetos.avanzar(p);
                            incidencias.setText("Progreso: "+p+" %");
                        }));
                }
            } catch (Exception ex) {
                r = new EvaluadorRetos.Resultado(false, "ERROR: " + ex);
            }
            if((n==2 || n==5) && simulacion) SwingUtilities.invokeLater(refresco::stop);
            long ms=java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-inicio);
            final EvaluadorRetos.Resultado resultado = r;
            SwingUtilities.invokeLater(()->{
                if(n==2 && simulacion) {
                    canvasRetos.avanzar(100);
                    incidencias.setText("Progreso real: " + canvasRetos.porcentajeReal() + " %");
                } else if(n==5 && simulacion) {
                    incidencias.setText("Tareas terminadas: "+canvasRetos.terminadasTarea()+"/8");
                    simultaneos.setText("Máximo simultáneo: ver informe");
                }
                canvasRetos.terminar(resultado.correcto(),resultado.detalle());
                resumen.setText(resultado.correcto()?"✓ PRUEBAS SUPERADAS":"✗ PRUEBAS FALLIDAS");
                resumen.setBackground(resultado.correcto()?new Color(220,252,231):new Color(254,226,226));
                informe.setText(resultado.detalle());
                tiempo.setText("Tiempo: "+ms+" ms");
                simultaneos.setText("Resultado: "+(resultado.correcto()?"OK":"FALLO"));
                log(resultado.detalle());
                ejecutar.setEnabled(true);probar.setEnabled(true);ocupado.set(false);
            });
        },"evaluador-reto-"+n).start();
    }
    private void revelarPista(){
        String[][] pistas={
            {},
            {"Observa si un robot bloquea un pasillo diferente.","¿Qué recurso compartido necesitas proteger?","Cada pasillo puede tener un monitor propio."},
            {"¿Qué ocurre si dos hilos leen el mismo valor antes de escribirlo?","Incrementar no es una única operación indivisible.","Consulta AtomicInteger e incrementAndGet()."},
            {"¿Qué sucede si el consumidor llega antes que el productor?","Necesitas una estructura segura para varios hilos.","Consulta BlockingQueue, put() y take()."},
            {"¿Qué indica el estado de interrupción de un hilo?","Thread.sleep() puede lanzar InterruptedException.","Al recibir la interrupción, termina el trabajo de forma cooperativa."},
            {"¿Estás ejecutando las tareas en el hilo que llama a enviar()?","FutureTask.run() es una llamada síncrona.","Utiliza ExecutorService para enviar las tareas a un pool."}
        };
        String[] ps=pistas[retoActual];
        if(nivelPista<ps.length)log("PISTA "+(++nivelPista)+": "+ps[nivelPista-1]);
        else log("Ya has consultado las tres pistas disponibles.");
    }
    private void verReferencia(){
        String[] nombres = {"", "Reto1_Pasillos.mp4", "Reto2_Contadores.mp4",
                "Reto3_Pedidos.mp4", "Reto4_Interrupciones.mp4", "Reto5_Ejecutores.mp4"};
        File f = new File("referencia", nombres[retoActual]);
        if (!f.isFile()) {
            log("No se encuentra el vídeo: " + f.getAbsolutePath());
            return;
        }
        try {
            Desktop.getDesktop().open(f);
        } catch (Exception e) {
            log("No se pudo abrir automáticamente. Abre el archivo: " + f.getAbsolutePath());
        }
    }

    public static void main(String[] args){
        System.setProperty("sun.java2d.uiScale.enabled","true");
        SwingUtilities.invokeLater(()->new Main(args.length>0 && "--autoplay".equals(args[0])).setVisible(true));
    }

    private static final class CanvasAlmacen extends JPanel {
        private final double[] progreso=new double[9];
        private final String[] estados=new String[9];
        private final Color[] colores={new Color(59,130,246),new Color(245,158,11),new Color(16,185,129)};
        CanvasAlmacen(){setBackground(new Color(17,33,52));setBorder(new EmptyBorder(8,8,8,8));setPreferredSize(new Dimension(610,400));reset();}
        synchronized void reset(){for(int i=0;i<9;i++){progreso[i]=0;estados[i]="EN ESPERA";}repaint();}
        synchronized void setRobot(int r,int p,double v,String d){progreso[r]=v;estados[r]=d;repaint();}
        @Override protected synchronized void paintComponent(Graphics g){
            super.paintComponent(g); Graphics2D x=(Graphics2D)g.create();
            x.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            double sx=getWidth()/640.0,sy=getHeight()/420.0;
            x.scale(sx,sy);
            x.setColor(new Color(194,215,238));x.setFont(new Font("SansSerif",Font.BOLD,15));
            x.drawString("ALMACÉN  /  ZONAS DE PASO",22,28);
            x.setFont(new Font("SansSerif",Font.PLAIN,11));x.drawString("Los colores representan pasillos independientes",22,45);
            for(int p=0;p<3;p++){
                int y=90+p*100;
                x.setColor(new Color(29,50,76));x.fillRoundRect(15,y-15,610,82,12,12);
                x.setColor(new Color(104,125,152));x.drawRoundRect(15,y-15,610,82,12,12);
                x.setColor(new Color(178,195,216));x.setFont(new Font("SansSerif",Font.BOLD,12));
                x.drawString("PASILLO "+(p+1),25,y+10);
                x.setColor(new Color(46,67,91));x.fillRoundRect(158,y-4,330,45,7,7);
                x.setColor(colores[p]); x.setStroke(new BasicStroke(2)); x.drawRoundRect(158,y-4,330,45,7,7);
                x.setColor(new Color(230,239,251));x.setFont(new Font("SansSerif",Font.PLAIN,10));
                x.drawString("ENTRADA",110,y+23);x.drawString("SALIDA",507,y+23);
                for(int n=0;n<3;n++){
                    int robot=n*3+p;
                    boolean terminado="ENTREGADO".equals(estados[robot]);
                    double pos="CRUZANDO".equals(estados[robot])? 158+progreso[robot]*330 : terminado?552+n*20:112-n*27;
                    int yy=y+9+(n-1)*10;
                    x.setColor(colores[p]);x.fill(new Ellipse2D.Double(pos-11,yy-11,22,22));
                    x.setColor(Color.WHITE);x.setStroke(new BasicStroke(1.4f));x.draw(new Ellipse2D.Double(pos-11,yy-11,22,22));
                    x.setFont(new Font("SansSerif",Font.BOLD,10));
                    String num=""+(robot+1);x.drawString(num,(float)pos-3,yy+4);
                }
            }
            x.setColor(new Color(152,176,203));x.setFont(new Font("SansSerif",Font.PLAIN,11));
            x.drawString("Un pasillo admite 1 robot; pasillos distintos pueden trabajar en paralelo.",20,403);
            x.dispose();
        }
    }
}
