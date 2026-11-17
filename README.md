# RoboLab — PSP · UD2 · Concurrencia en Java

Proyecto Maven con interfaz Swing y cinco retos independientes. El alumnado modifica **únicamente la clase `Mi*.java` del reto seleccionado**. La implementación inicial es deliberadamente incorrecta o incompleta.

## Puesta en marcha

1. Abrir la carpeta como proyecto Maven en IntelliJ IDEA con JDK 21 o posterior.
2. Ejecutar `es.robolab.app.Main`.
3. Seleccionar un reto, pulsar **Iniciar simulación** o **Ejecutar pruebas** y observar el panel y la consola.
4. Corregir la clase editable del reto y repetir las pruebas. Se dispone de pistas progresivas.

También se incluyen `scripts/compilar.sh`, `scripts/compilar.bat`, `scripts/ejecutar.sh` y `scripts/ejecutar.bat`.

## Retos

| Reto | Archivo editable | Concepto | Objetivo |
|---|---|---|---|
| 1. Pasillos | `reto1/MiControlPasillos.java` | `synchronized` | Evitar colisiones y permitir pasillos independientes |
| 2. Contadores | `reto2/MiContador.java` | `AtomicInteger` | Evitar pérdidas de incrementos |
| 3. Pedidos | `reto3/MiColaPedidos.java` | `BlockingQueue` | Procesar 100 pedidos únicos y completos |
| 4. Interrupciones | `reto4/MiTareaCancelable.java` | `interrupt()` | Cancelar cooperativamente una tarea |
| 5. Ejecutores | `reto5/MiGestorTareas.java` | `ExecutorService`, `Callable`, `Future` | Obtener ocho resultados con concurrencia real |

## Reto 2 — Contadores

- **Simulación:** ocho hilos con **10.000.000 incrementos por hilo** (80.000.000 en total), sin esperas artificiales.
- **Visualización:** barras de progreso basadas en operaciones efectivamente completadas por cada hilo, actualizadas aproximadamente cada 40 ms.
- **Pruebas automáticas:** cuatro rondas independientes con 8 y 16 hilos. Se comprueba el resultado esperado de cada ronda.
- El `int` compartido inicial presenta una condición de carrera. Que una ejecución dé el valor esperado **no demuestra** que el código sea correcto; hay que revisar el mecanismo de atomicidad.

## Reto 5 — Ejecutores

- **Simulación:** ocho tareas de cálculo real, sin `Thread.sleep()`; carga actual **100.000.000 iteraciones por tarea**.
- **Visualización:** estados **PENDIENTE**, **EJECUTANDO** y **TERMINADA** y solapamiento de tareas.
- **Pruebas automáticas:** carga reducida de **1.000.000 iteraciones por tarea**, con comprobación de resultados y concurrencia.
- La implementación inicial es secuencial intencionadamente. La duración varía según el equipo y el tamaño del pool; no se exige que las ocho tareas estén activas a la vez.
- **Ajustar velocidad:** modificar `OPERACIONES_EJECUTOR` en `src/main/java/es/robolab/core/EvaluadorRetos.java`. Valores orientativos: `50_000_000` (más rápido), `100_000_000` (actual), `200_000_000` (más lento). No cambia la carga de las pruebas automáticas.

## Pruebas

- Reto 1: `es.robolab.retos.reto1.PruebasReto1`.
- Retos 2–5: `es.robolab.PruebasRetos2a5`.
- Es normal que las pruebas fallen con las implementaciones iniciales. Las pruebas dinámicas no sustituyen una revisión docente del código.

## Vídeos de referencia

Los vídeos están en `referencia/`: `Reto1_Pasillos.mp4`, `Reto2_Contadores.mp4`, `Reto3_Pedidos.mp4`, `Reto4_Interrupciones.mp4` y `Reto5_Ejecutores.mp4`. Los vídeos 2–5 son **animaciones conceptuales**, no grabaciones de la ejecución; su duración puede diferir de la simulación.

## Para el profesorado

Las soluciones se distribuyen por separado: **no deben incluirse en el repositorio público**, tampoco como archivos compilados.

## Publicar estos dos archivos actualizados

```bash
git add README.md src/main/java/es/robolab/core/EvaluadorRetos.java
git commit -m "Actualiza documentacion y ajusta carga del reto 5"
git push
```
