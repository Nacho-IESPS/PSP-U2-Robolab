package es.robolab.app;

import es.robolab.core.Observador;
import es.robolab.retos.reto1.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/** Interfaz gráfica suministrada. El alumnado solo modifica MiControlPasillos.java. */
public final class Main extends JFrame {
    private final CanvasAlmacen canvas = new CanvasAlmacen();
    private final JTextPane consola = new JTextPane();
    private final JLabel resumen = new JLabel("Pendiente de probar", SwingConstants.CENTER);
    private final JTextArea informe = new JTextArea("Pulsa Ejecutar pruebas para ver el diagnóstico.");
    private final JLabel estado = new JLabel("9 robots · 3 pasillos · 3 robots/pasillo");
    private final JLabel tiempo = new JLabel("Tiempo: —");
    private final JLabel incidencias = new JLabel("Colisiones: —");
    private final JLabel simultaneos = new JLabel("Simultáneos: —");
    private final JButton ejecutar = boton("▶ Iniciar simulación", new Color(29, 110, 192));
    private final JButton probar = boton("✓ Ejecutar pruebas", new Color(16, 135, 100));
    private final JButton pista = boton("? Siguiente pista", new Color(112, 75, 177));
    private final AtomicBoolean ocupado = new AtomicBoolean(false);
    private int nivelPista = 0;

    public Main(boolean autoInicio) {
        super("RoboLab · Laboratorio de concurrencia · Reto 1");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1220, 790);
        setMinimumSize(new Dimension(980, 650));
        setLocationRelativeTo(null);
        setContentPane(construir());
        if (autoInicio) SwingUtilities.invokeLater(() -> lanzar(true));
    }

    private static JButton boton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setOpaque(true);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBorder(new EmptyBorder(11, 14, 11, 14));
        b.setFocusPainted(false);
        return b;
    }

    private JComponent construir() {
        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBackground(new Color(242, 246, 252));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        JLabel titulo = new JLabel("ROBOLAB  /  ALMACÉN CONCURRENTE");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(new Color(20, 50, 84));
        JLabel subt = new JLabel("Reto 1 — Seguridad y paralelismo en tres pasillos independientes");
        subt.setForeground(new Color(75, 98, 124));
        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 3));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subt);
        cabecera.add(textos, BorderLayout.WEST);
        root.add(cabecera, BorderLayout.NORTH);

        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(Color.WHITE);
        menu.setBorder(new EmptyBorder(16, 12, 16, 12));
        menu.setPreferredSize(new Dimension(195, 400));
        JLabel m = new JLabel("ESCENARIOS");
        m.setFont(new Font("SansSerif", Font.BOLD, 14));
        menu.add(m);
        menu.add(Box.createVerticalStrut(18));
        String[] nombres = {"01  Pasillos ✓", "02  Contadores", "03  Pedidos",
                "04  Interrupciones", "05  Ejecutores"};
        for (int i = 0; i < nombres.length; i++) {
            JLabel l = new JLabel(nombres[i]);
            l.setOpaque(true);
            l.setBackground(i == 0 ? new Color(222, 239, 255) : Color.WHITE);
            l.setForeground(i == 0 ? new Color(28, 85, 157) : new Color(151, 160, 173));
            l.setMaximumSize(new Dimension(190, 45));
            l.setBorder(new EmptyBorder(12, 9, 12, 6));
            menu.add(l);
            menu.add(Box.createVerticalStrut(5));
        }
        menu.add(Box.createVerticalGlue());
        JLabel pie = new JLabel("Próximos retos: ampliables");
        pie.setFont(new Font("SansSerif", Font.PLAIN, 10));
        pie.setForeground(Color.GRAY);
        menu.add(pie);
        root.add(menu, BorderLayout.WEST);

        JPanel derecha = new JPanel(new BorderLayout(10, 10));
        derecha.setOpaque(false);
        derecha.add(canvas, BorderLayout.CENTER);
        JPanel abajo = new JPanel(new BorderLayout(9, 9));
        abajo.setOpaque(false);
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        controles.setOpaque(false);
        ejecutar.addActionListener(e -> lanzar(true));
        probar.addActionListener(e -> lanzar(false));
        pista.addActionListener(e -> revelarPista());
        controles.add(ejecutar);
        controles.add(probar);
        controles.add(pista);
        JButton referencia = boton("▶ Ver vídeo de referencia", new Color(69, 83, 106));
        referencia.addActionListener(e -> verReferencia());
        controles.add(referencia);
        abajo.add(controles, BorderLayout.NORTH);

        consola.setEditable(false);
        consola.setFont(new Font("Monospaced", Font.PLAIN, 12));
        consola.setBackground(new Color(20, 30, 47));
        consola.setForeground(new Color(197, 228, 236));
        consola.setText("ROBOLAB LISTO\nInicia la simulación o pulsa 'Ejecutar pruebas'.\nModifica únicamente MiControlPasillos.java.\n");
        JScrollPane log = new JScrollPane(consola);
        log.setPreferredSize(new Dimension(0, 150));
        abajo.add(log, BorderLayout.CENTER);
        derecha.add(abajo, BorderLayout.SOUTH);
        root.add(derecha, BorderLayout.CENTER);

        JPanel met = new JPanel();
        met.setLayout(new BoxLayout(met, BoxLayout.Y_AXIS));
        met.setBackground(Color.WHITE);
        met.setBorder(new EmptyBorder(14, 13, 14, 13));
        met.setPreferredSize(new Dimension(225, 400));
        JLabel l = new JLabel("DIAGNÓSTICO");
        l.setFont(new Font("SansSerif", Font.BOLD, 14));
        met.add(l);
        met.add(Box.createVerticalStrut(16));
        for (JLabel x : new JLabel[]{estado, tiempo, incidencias, simultaneos}) {
            x.setFont(new Font("SansSerif", Font.PLAIN, 12));
            x.setAlignmentX(Component.LEFT_ALIGNMENT);
            met.add(x);
            met.add(Box.createVerticalStrut(13));
        }
        met.add(Box.createVerticalStrut(10));
        resumen.setOpaque(true);
        resumen.setBackground(new Color(239, 243, 248));
        resumen.setBorder(new EmptyBorder(12, 9, 12, 9));
        resumen.setMaximumSize(new Dimension(220, 70));
        resumen.setFont(new Font("SansSerif", Font.BOLD, 11));
        resumen.setAlignmentX(Component.LEFT_ALIGNMENT);
        met.add(resumen);
        met.add(Box.createVerticalStrut(10));

        JLabel detalleTitulo = new JLabel("RESULTADO DE LAS PRUEBAS");
        detalleTitulo.setFont(new Font("SansSerif", Font.BOLD, 11));
        detalleTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        met.add(detalleTitulo);
        met.add(Box.createVerticalStrut(6));
        informe.setEditable(false);
        informe.setOpaque(false);
        informe.setLineWrap(true);
        informe.setWrapStyleWord(true);
        informe.setFont(new Font("SansSerif", Font.PLAIN, 11));
        informe.setAlignmentX(Component.LEFT_ALIGNMENT);
        informe.setMaximumSize(new Dimension(215, 125));
        met.add(informe);
        met.add(Box.createVerticalStrut(12));

        JTextArea instrucciones = new JTextArea(
            "ARCHIVO A MODIFICAR\nMiControlPasillos.java\n\nOBJETIVO\n" +
            "• Sin colisiones por pasillo.\n• Pasillos distintos, simultáneos.\n" +
            "• Nueve entregas correctas.\n• No modificar motor ni pruebas.");
        instrucciones.setFont(new Font("SansSerif", Font.PLAIN, 12));
        instrucciones.setWrapStyleWord(true);
        instrucciones.setLineWrap(true);
        instrucciones.setEditable(false);
        instrucciones.setOpaque(false);
        met.add(instrucciones);
        root.add(met, BorderLayout.EAST);
        return root;
    }

    private void log(String texto) {
        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = consola.getStyledDocument();
                SimpleAttributeSet estilo = new SimpleAttributeSet();
                boolean error = texto.contains("FALLO") || texto.contains("ERROR")
                    || texto.contains("Error") || texto.contains("AGOTADO");
                StyleConstants.setForeground(estilo, error ?
                    new Color(255, 125, 125) : new Color(197, 228, 236));
                StyleConstants.setBold(estilo, error);
                doc.insertString(doc.getLength(), texto + "\n", estilo);
                consola.setCaretPosition(doc.getLength());
            } catch (BadLocationException ex) {
                throw new IllegalStateException(ex);
            }
        });
    }

    private void lanzar(boolean animar) {
        if (!ocupado.compareAndSet(false, true)) return;
        ejecutar.setEnabled(false);
        probar.setEnabled(false);
        canvas.reset();
        resumen.setText("Ejecutando...");
        resumen.setBackground(new Color(254, 243, 199));
        informe.setText("Se están realizando las comprobaciones...");
        log("\n=== " + (animar ? "SIMULACIÓN VISUAL" : "PRUEBAS AUTOMÁTICAS") + " ===");
        new Thread(() -> {
            ResultadoPruebas r = EvaluadorPasillos.ejecutar(
                new MiControlPasillos(), new Observador() {
                    @Override
                    public void estado(int robot, int pasillo, double progreso, String desc) {
                        canvas.setRobot(robot, pasillo, progreso, desc);
                    }
                    @Override
                    public void mensaje(String texto) { log(texto); }
                    @Override
                    public void actividad(int activos, int colisiones, int completados) {
                        SwingUtilities.invokeLater(() -> {
                            incidencias.setText("Colisiones: " + colisiones);
                            estado.setText("Entregas: " + completados + " / 9");
                            simultaneos.setText("Robots cruzando: " + activos);
                        });
                    }
                }, animar);
            SwingUtilities.invokeLater(() -> {
                log(r.detalles());
                log("Simultáneos máximos: " + r.simultaneos()
                    + " | Solapes: " + r.solapes()
                    + " | Duración: " + r.duracionMs() + " ms");
                tiempo.setText("Tiempo: " + r.duracionMs() + " ms");
                simultaneos.setText("Máx. simultáneos: " + r.simultaneos());
                resumen.setText(r.superadas() ? "✓ PRUEBAS SUPERADAS" : "✗ PRUEBAS FALLIDAS");
                resumen.setBackground(r.superadas() ?
                    new Color(220, 252, 231) : new Color(254, 226, 226));
                resumen.setForeground(r.superadas() ?
                    new Color(15, 103, 55) : new Color(166, 25, 25));
                informe.setForeground(r.superadas() ?
                    new Color(15, 103, 55) : new Color(166, 25, 25));
                informe.setText(
                    "Seguridad: " + (r.seguridad() ? "OK" : "FALLO") + "\n" +
                    "Pasillos independientes: " + (r.concurrencia() ? "OK" : "FALLO") + "\n" +
                    "Entregas (9): " + (r.integridad() ? "OK" : "FALLO") + "\n" +
                    "Finalización: " + (r.finalizacion() ? "OK" : "FALLO") + "\n" +
                    "Máximo simultáneo: " + r.simultaneos());
                if (!r.superadas()) {
                    Toolkit.getDefaultToolkit().beep();
                    if (!r.concurrencia() && r.seguridad()) {
                        log("FALLO: los pasillos son independientes, pero los robots " +
                            "están cruzando de uno en uno. Revisa el monitor que utilizas.");
                    }
                    if (!r.seguridad()) log("FALLO: dos robots han entrado simultáneamente en el mismo pasillo.");
                    if (!r.integridad()) log("FALLO: no se han completado las nueve entregas.");
                    if (!r.finalizacion()) log("FALLO: alguna tarea no ha terminado a tiempo.");
                }
                ejecutar.setEnabled(true);
                probar.setEnabled(true);
                ocupado.set(false);
            });
        }, "evaluador-robolab").start();
    }

    private void revelarPista() {
        String[] pistas = {
            "PISTA 1: Observa si un robot en el pasillo 0 impide avanzar a otro del pasillo 1.",
            "PISTA 2: ¿Qué recurso debe protegerse? El almacén completo no es un recurso indivisible.",
            "PISTA 3: Revisa el objeto monitor de synchronized. Cada pasillo independiente necesita exclusión propia."
        };
        if (nivelPista < pistas.length) log(pistas[nivelPista++]);
        else log("Ya has consultado las tres pistas disponibles.");
    }

    private void verReferencia() {
        File f = new File("referencia/Reto1_Pasillos.mp4");
        if (!f.exists()) {
            log("El vídeo no está en este repositorio. Utiliza el ZIP de materiales del curso.");
            return;
        }
        try {
            Desktop.getDesktop().open(f);
        } catch (Exception e) {
            log("Abre manualmente: " + f.getAbsolutePath());
        }
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        SwingUtilities.invokeLater(() -> new Main(
            args.length > 0 && "--autoplay".equals(args[0])).setVisible(true));
    }

    private static final class CanvasAlmacen extends JPanel {
        private final double[] progreso = new double[9];
        private final String[] estados = new String[9];
        private final Color[] colores = {
            new Color(59, 130, 246), new Color(245, 158, 11), new Color(16, 185, 129)
        };

        CanvasAlmacen() {
            setBackground(new Color(17, 33, 52));
            setBorder(new EmptyBorder(8, 8, 8, 8));
            setPreferredSize(new Dimension(610, 400));
            reset();
        }

        synchronized void reset() {
            for (int i = 0; i < 9; i++) {
                progreso[i] = 0;
                estados[i] = "EN ESPERA";
            }
            repaint();
        }

        synchronized void setRobot(int robot, int pasillo, double valor, String descripcion) {
            progreso[robot] = valor;
            estados[robot] = descripcion;
            repaint();
        }

        @Override
        protected synchronized void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D x = (Graphics2D) g.create();
            x.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            x.scale(getWidth() / 640.0, getHeight() / 420.0);
            x.setColor(new Color(194, 215, 238));
            x.setFont(new Font("SansSerif", Font.BOLD, 15));
            x.drawString("ALMACÉN  /  ZONAS DE PASO", 22, 28);
            x.setFont(new Font("SansSerif", Font.PLAIN, 11));
            x.drawString("Los colores representan pasillos independientes", 22, 45);

            for (int p = 0; p < 3; p++) {
                int y = 90 + p * 100;
                x.setColor(new Color(29, 50, 76));
                x.fillRoundRect(15, y - 15, 610, 82, 12, 12);
                x.setColor(new Color(104, 125, 152));
                x.drawRoundRect(15, y - 15, 610, 82, 12, 12);
                x.setColor(new Color(178, 195, 216));
                x.setFont(new Font("SansSerif", Font.BOLD, 12));
                x.drawString("PASILLO " + (p + 1), 25, y + 10);
                x.setColor(new Color(46, 67, 91));
                x.fillRoundRect(158, y - 4, 330, 45, 7, 7);
                x.setColor(colores[p]);
                x.setStroke(new BasicStroke(2));
                x.drawRoundRect(158, y - 4, 330, 45, 7, 7);
                x.setColor(new Color(230, 239, 251));
                x.setFont(new Font("SansSerif", Font.PLAIN, 10));
                x.drawString("ENTRADA", 110, y + 23);
                x.drawString("SALIDA", 507, y + 23);

                for (int n = 0; n < 3; n++) {
                    int robot = n * 3 + p;
                    boolean terminado = "ENTREGADO".equals(estados[robot]);
                    double pos = "CRUZANDO".equals(estados[robot]) ?
                        158 + progreso[robot] * 330 : terminado ? 552 + n * 20 : 112 - n * 27;
                    int yy = y + 9 + (n - 1) * 10;
                    x.setColor(colores[p]);
                    x.fill(new Ellipse2D.Double(pos - 11, yy - 11, 22, 22));
                    x.setColor(Color.WHITE);
                    x.setStroke(new BasicStroke(1.4f));
                    x.draw(new Ellipse2D.Double(pos - 11, yy - 11, 22, 22));
                    x.setFont(new Font("SansSerif", Font.BOLD, 10));
                    String num = "" + (robot + 1);
                    x.drawString(num, (float) pos - 3, yy + 4);
                }
            }
            x.setColor(new Color(152, 176, 203));
            x.setFont(new Font("SansSerif", Font.PLAIN, 11));
            x.drawString("Un pasillo admite 1 robot; pasillos distintos pueden trabajar en paralelo.", 20, 403);
            x.dispose();
        }
    }
}
