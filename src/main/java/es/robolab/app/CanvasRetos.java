package es.robolab.app;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Diagramas didácticos de los retos 2-5.
 * No modifica el estado del ejercicio: representa las mediciones del evaluador.
 */
public final class CanvasRetos extends JPanel {
    private int reto=2, progreso=0;
    private boolean finalizado=false, correcto=false;
    private String detalle="Pendiente de ejecutar";
    private final Color azul=new Color(70,156,255), verde=new Color(57,207,150);
    public CanvasRetos(){setBackground(new Color(17,33,52));}
    public void seleccionar(int n){reto=n;progreso=0;finalizado=false;detalle="Pendiente de ejecutar";repaint();}
    public void avanzar(int n){progreso=Math.max(0,Math.min(100,n));repaint();}
    public void terminar(boolean ok,String resultado){correcto=ok;detalle=resultado;finalizado=true;repaint();}
    private void texto(Graphics2D g,String s,int x,int y,int size,Color c){
        g.setColor(c);g.setFont(new Font("SansSerif",Font.BOLD,size));g.drawString(s,x,y);
    }
    private void barra(Graphics2D g,int x,int y,int w,int h,double f,Color color){
        g.setColor(new Color(45,66,91));g.fillRoundRect(x,y,w,h,10,10);
        g.setColor(color);g.fillRoundRect(x,y,(int)(w*Math.max(0,Math.min(1,f))),h,10,10);
    }
    @Override protected void paintComponent(Graphics gr){
        super.paintComponent(gr);
        Graphics2D g=(Graphics2D)gr.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.scale(getWidth()/640.0,getHeight()/420.0);
        Color blanco=new Color(223,235,248), gris=new Color(159,184,212);
        String[] titulos={"","","CONTADOR COMPARTIDO","FÁBRICA DE PEDIDOS","CANCELACIÓN COOPERATIVA","GESTOR DE TRABAJOS"};
        texto(g,titulos[reto],24,36,21,blanco);
        texto(g,"Representación de las comprobaciones del reto",24,58,12,gris);
        if(reto==2){
            for(int i=0;i<8;i++){
                int x=35+(i%4)*153,y=95+(i/4)*74;
                g.setColor(new Color(37,62,88));g.fillRoundRect(x,y,135,54,12,12);
                texto(g,"HILO "+(i+1),x+12,y+22,13,blanco);
                barra(g,x+12,y+33,110,8,progreso/100.0,azul);
            }
            texto(g,"8 hilos × 2.000 incrementos = 16.000",36,287,16,blanco);
            texto(g,finalizado?detalle:"Cada hilo actualiza el mismo contador",36,322,13,finalizado?(correcto?verde:new Color(255,140,130)):gris);
            texto(g,"Un incremento no atómico puede perder actualizaciones.",36,353,12,gris);
        }else if(reto==3){
            for(int i=0;i<2;i++){
                int x=25+i*180;
                g.setColor(new Color(34,78,103));g.fillRoundRect(x,110,148,70,12,12);
                texto(g,"PRODUCTOR "+(i+1),x+10,148,13,blanco);
            }
            g.setColor(new Color(47,69,97));g.fillRoundRect(160,215,320,48,14,14);
            for(int i=0;i<8;i++){
                g.setColor(i<Math.round(progreso/12.5)?verde:azul);
                g.fillRoundRect(178+i*36,226,25,25,4,4);
            }
            texto(g,"COLA COMPARTIDA",245,205,13,blanco);
            for(int i=0;i<4;i++){
                int x=20+i*154;
                g.setColor(new Color(48,80,92));g.fillRoundRect(x,290,137,52,10,10);
                texto(g,"CONSUMIDOR "+(i+1),x+6,320,12,blanco);
            }
            texto(g,finalizado?detalle:"Objetivo: 100 pedidos, sin pérdidas ni duplicados",25,384,13,finalizado?(correcto?verde:new Color(255,140,130)):gris);
        }else if(reto==4){
            texto(g,"TRABAJADOR",35,120,16,blanco);
            barra(g,35,145,570,35,progreso/100.0,azul);
            texto(g,progreso+" / 100",265,169,16,blanco);
            g.setColor(new Color(243,174,79));g.setStroke(new BasicStroke(4));
            g.drawLine(115,235,525,235);g.fillPolygon(new int[]{525,525,546},new int[]{226,244,235},3);
            texto(g,"interrupt()",245,218,18,new Color(243,174,79));
            texto(g,"La tarea debe atender la señal y finalizar antes del 100 %.",30,290,13,gris);
            texto(g,finalizado?detalle:"La interrupción se solicita durante la ejecución.",30,330,13,finalizado?(correcto?verde:new Color(255,140,130)):gris);
        }else if(reto==5){
            for(int i=0;i<8;i++){
                int x=38+(i%4)*152,y=92+(i/4)*92;
                g.setColor(new Color(40,65,91));g.fillRoundRect(x,y,130,70,12,12);
                texto(g,"TAREA "+(i+1),x+13,y+25,14,blanco);
                barra(g,x+13,y+42,102,11,progreso/100.0,azul);
            }
            texto(g,"8 tareas independientes: deben solaparse en el tiempo",30,314,14,blanco);
            texto(g,finalizado?detalle:"Comprobando resultados y simultaneidad",30,354,13,finalizado?(correcto?verde:new Color(255,140,130)):gris);
        }
        if(finalizado){
            g.setColor(correcto?verde:new Color(255,133,126));
            g.setStroke(new BasicStroke(3));g.drawRoundRect(13,13,614,393,15,15);
        }
        g.dispose();
    }
}
