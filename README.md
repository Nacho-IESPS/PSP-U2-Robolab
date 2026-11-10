# RoboLab · PSP · Unidad 2 · Java 21

Aplicación didáctica Swing con **cinco retos independientes de concurrencia** para DAM. Cada reto parte de un código que debe corregir el alumnado.

## Cómo empezar

1. Abre el proyecto Maven en IntelliJ con **JDK 21 o posterior**.
2. Ejecuta **`es.robolab.app.Main`**.
3. Selecciona cualquier reto desde el menú de la izquierda.
4. Ejecuta la simulación y observa el diagrama y el diagnóstico.
5. Modifica **solo el archivo `Mi*.java` correspondiente** y vuelve a probar.

| Reto | Archivo editable | Concepto | Comprobación |
| --- | --- | --- | --- |
| 1. Pasillos | `reto1/MiControlPasillos.java` | `synchronized` | Sin colisiones y con pasillos paralelos |
| 2. Contadores | `reto2/MiContador.java` | `AtomicInteger` | 16.000 incrementos sin pérdidas |
| 3. Pedidos | `reto3/MiColaPedidos.java` | `BlockingQueue` | 100 pedidos únicos |
| 4. Interrupciones | `reto4/MiTareaCancelable.java` | `interrupt()` | Cancelación cooperativa |
| 5. Ejecutores | `reto5/MiGestorTareas.java` | `ExecutorService` | 8 trabajos correctos y concurrentes |

## Visualización

- **Reto 1:** simulación de robots que atraviesan tres pasillos.
- **Reto 2:** ocho hilos actualizando un contador compartido.
- **Reto 3:** productores, cola de pedidos y consumidores.
- **Reto 4:** progreso de una tarea y señal de interrupción.
- **Reto 5:** ocho tareas gestionadas por un ejecutor.

Los diagramas 2–5 **representan el progreso y el resultado medidos por el evaluador**; no son trazas individuales de cada operación.

## Pruebas

- Reto 1: `es.robolab.retos.reto1.PruebasReto1`.
- Retos 2–5: `es.robolab.PruebasRetos2a5`.
- Las pruebas fallan con el código inicial: **es lo esperado**.
- La salida con código 1 indica pruebas no superadas, no un fallo de Java.

## Recursos

El vídeo `referencia/Reto1_Pasillos.mp4` y la captura `referencia/Vista_Referencia.png` están en el ZIP de distribución. Los vídeos de los retos 2–5 aún no se han producido.

**Las soluciones del profesor se distribuyen por separado y no están en este repositorio público.**
