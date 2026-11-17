# RoboLab — PSP · Unidad 2 · Java 21

Aplicación Swing con **cinco retos independientes** sobre concurrencia. Cada reto contiene una implementación inicial incorrecta o incompleta que debe corregir el alumnado.

## Uso

1. Abrir como proyecto Maven en IntelliJ IDEA, con JDK 21 o posterior.
2. Ejecutar `es.robolab.app.Main` (no la clase de pruebas si quieres la interfaz gráfica).
3. Seleccionar un reto en el menú izquierdo.
4. Pulsar **Iniciar simulación** o **Ejecutar pruebas** y observar el resultado.
5. Modificar **solo el archivo `Mi*.java` correspondiente**; volver a ejecutar.
6. Usar las pistas de forma progresiva.

| Reto | Archivo editable | Concepto | Comprobación principal |
|---|---|---|---|
| 1. Pasillos | `reto1/MiControlPasillos.java` | Monitores `synchronized` | Sin colisiones y con cruces en paralelo |
| 2. Contadores | `reto2/MiContador.java` | `AtomicInteger` | 16 000 incrementos correctos |
| 3. Pedidos | `reto3/MiColaPedidos.java` | `BlockingQueue` | 100 pedidos únicos y completos |
| 4. Interrupciones | `reto4/MiTareaCancelable.java` | `interrupt()` | Detención anticipada al interrumpir |
| 5. Ejecutores | `reto5/MiGestorTareas.java` | `ExecutorService` | 8 resultados correctos y ejecución concurrente |

## Pruebas

- Reto 1: `es.robolab.retos.reto1.PruebasReto1`.
- Retos 2–5: `es.robolab.PruebasRetos2a5` (fallará inicialmente: **es lo esperado**).
- Para compilar: `scripts/compilar.sh` o `scripts/compilar.bat`.
- Para ejecutar la interfaz: `scripts/ejecutar.sh` o `scripts/ejecutar.bat`.

Las pruebas no sustituyen una revisión docente: pueden existir soluciones incorrectas que superen algún caso de prueba.

## Referencia visual

`referencia/Reto1_Pasillos.mp4` y `referencia/Vista_Referencia.png` muestran el comportamiento del primer reto. **Los vídeos de los retos 2–5 están pendientes de producir**. La interfaz muestra diagramas diferenciados: ocho hilos actualizando un contador, productores/cola/consumidores, señal de interrupción y ocho tareas del ejecutor. Son representaciones de las mediciones de las pruebas, no una traza detallada de cada operación.

## Para el profesorado

Las soluciones se distribuyen **en un ZIP separado**, no incluido en este repositorio público. No publiques archivos compilados que permitan recuperar las soluciones mediante descompilación.

## Visualización de los retos 2–5

Los cuatro paneles muestran el avance y el resultado de las comprobaciones, con una representación conceptual de los recursos compartidos. No representan eventos internos individuales en tiempo real; para depurar el código sigue siendo necesario consultar las pruebas y la consola.

## Reto 2 (actualización)

8 hilos × 10.000.000 incrementos = **80.000.000** operaciones reales, sin `sleep()` ni `yield()` en el cálculo. Las barras muestran el número de operaciones completadas por cada hilo, muestreadas por Swing cada 40 ms. La duración depende del equipo. La implementación inicial contiene una condición de carrera.


## Actualización del 17/11/2026 — retos 2 y 5

**Reto 2.** `Iniciar simulación` ejecuta ocho hilos con 10.000.000 de incrementos por hilo, sin esperas, y representa el progreso real. `Ejecutar pruebas` realiza cuatro rondas independientes (8 y 16 hilos). La implementación inicial con `int` es deliberadamente incorrecta. Superar las pruebas dinámicas no demuestra formalmente que el código esté libre de condiciones de carrera: el profesor debe revisar el mecanismo de atomicidad utilizado.

**Reto 5.** `Iniciar simulación` ejecuta ocho tareas con cálculo computacional real (700.000.000 iteraciones por tarea), sin `sleep()`. El panel muestra los estados PENDIENTE, EJECUTANDO y TERMINADA. `Ejecutar pruebas` usa una carga reducida para comprobar resultados y solapamiento de tareas. La duración depende del ordenador y del número de hilos del pool; no se fuerza una duración exacta. La implementación inicial es secuencial deliberadamente.

Los archivos `MiContador.java` y `MiGestorTareas.java` siguen siendo los únicos que modifica el alumnado en sus respectivos retos. Las soluciones del profesor no se incluyen en este paquete.

Los vídeos de referencia se encuentran en `referencia/`. Para publicar el proyecto desde su directorio raíz:

```bash
git add README.md src referencia
git commit -m "Mejora retos 2 y 5: pruebas de concurrencia y visualizacion real"
git push origin main
```

No subir el ZIP privado de soluciones del profesor.
