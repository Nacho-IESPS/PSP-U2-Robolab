# RoboLab · PSP · Unidad 2

Simulador didáctico de concurrencia en **Java 21**, para el módulo de Programación de Servicios y Procesos de 2.º de DAM.

## Reto 1 — Control de pasillos

Nueve robots cruzan tres pasillos. En un mismo pasillo nunca deben cruzar dos robots simultáneamente, pero los tres pasillos deben poder funcionar a la vez.

**ÚNICO ARCHIVO QUE MODIFICA EL ALUMNADO:**
`src/main/java/es/robolab/retos/reto1/MiControlPasillos.java`

El reto trabaja los **monitores y `synchronized`**, explicados en el apartado **4.1** de los apuntes UD2. El código del simulador utiliza otras API de Java que **no es necesario comprender ni modificar** para resolver la tarea.

### Requisitos

- Java 21 o posterior.
- Ejecutar `es.robolab.app.Main` desde IntelliJ, o usar los scripts de `scripts/`.
- Elegir **Reto 1**, observar la simulación y usar **Ejecutar pruebas**.
- Consultar las pistas solo cuando sea necesario.
- No modificar las interfaces, el evaluador ni las pruebas.

### Qué comprueban las pruebas

- Entregas: 9/9.
- Seguridad: ninguna coincidencia de dos robots en el mismo pasillo.
- Concurrencia: al menos dos robots cruzan simultáneamente por pasillos diferentes.
- Finalización: todas las tareas concluyen.

**La solución inicial es segura pero demasiado global:** serializa todos los pasillos. Debes sustituir esa sincronización global por la protección específica de cada pasillo.

### Retos previstos

1. Control de pasillos — monitores y `synchronized`.
2. Contadores — `AtomicInteger`.
3. Pedidos — `BlockingQueue`.
4. Cancelación — `interrupt()`.
5. Gestor de trabajos — `ExecutorService`.

La arquitectura deja preparados los menús de los siguientes retos, pero **solo el Reto 1 está implementado**.

### Material de referencia

Los recursos audiovisuales se incluyen en el paquete ZIP de entrega de esta versión. No se publican implementaciones correctas ni archivos compilados con la solución.

### Licencia y autoría

Material docente experimental. Pendiente de asignación de licencia por el titular.