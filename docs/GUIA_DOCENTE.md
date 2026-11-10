# Guía docente · RoboLab PSP UD2

## Alcance de la primera entrega

El **Reto 1** está preparado para realizarse después del apartado **4.1** de los apuntes de la unidad 2, dedicado a `synchronized` y los monitores.

La tarea del alumno consiste exclusivamente en editar:

`src/main/java/es/robolab/retos/reto1/MiControlPasillos.java`

El resto de archivos constituyen infraestructura de simulación y evaluación. El alumnado **no necesita entender** las clases de ejecución concurrente que utiliza internamente el evaluador.

## Criterios de comprobación

1. Los nueve robots completan su recorrido.
2. Nunca coinciden dos robots en un mismo pasillo.
3. Pueden cruzar simultáneamente robots que usan pasillos diferentes.
4. Todas las tareas terminan.

La versión inicial utiliza `synchronized` en el método completo: es segura, pero serializa los tres pasillos, por lo que no supera la comprobación de concurrencia.

## Seguridad de la solución

**No publicar la solución del profesor**, ni como código fuente ni compilada en un JAR. El bytecode puede descompilarse. La referencia del comportamiento debe ser audiovisual.

## Mejoras previstas

- Reto 2: contadores compartidos y `AtomicInteger`.
- Reto 3: comunicación mediante `BlockingQueue`.
- Reto 4: cancelación cooperativa mediante `interrupt()`.
- Reto 5: gestión de tareas mediante `ExecutorService`.

Los retos 2–5 son propuestas de ampliación; no hay todavía implementación funcional de esos escenarios.
