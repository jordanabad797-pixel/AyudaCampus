# Prompts utilizados

> Herramienta de IA: **Claude** (Anthropic), usada en lugar de Gemini en Android Studio. Cada mejora se pidió con un prompt independiente, se compiló, se probó en el emulador y se subió en su propio commit en la rama `mejora-ia`.

| # | Mejora | Commit |
|---|--------|--------|
| 1 | Tiempo promedio de resolución | `ia: agrega tiempo promedio de resolucion al panel` |
| 2 | Contador de SLA por prioridad | `ia: agrega contador de SLA por prioridad` |
| 3 | Búsqueda avanzada en la bandeja | `ia: agrega busqueda avanzada en la bandeja` |
| 4 | Registro de estudiante | `ia: agrega registro de estudiante con validaciones` |
| 5 | Comentarios internos | `ia: agrega comentarios internos de soporte` |
| 6 | Animaciones y estados vacíos | `ia: agrega animaciones y estados vacios` |

---

## Prompt 1 — Tiempo promedio de resolución

**Texto enviado a la IA:**
> Tengo una app Kotlin con Jetpack Compose, Material 3, MVVM, Room y un AyudaRepository (interface). Agrega en el panel de soporte el tiempo promedio de resolución de los tickets cerrados (fechaCierre - fecha). La consulta va en TicketDao, la función en AyudaRepository y su implementación, y el valor se expone como StateFlow en PanelViewModel. La View solo lo muestra. No uses XML. Muéstrame solo los archivos que cambian.

**Qué generó la IA:**
- `TicketDao.promedioResolucion()`: `AVG(fechaCierre - fecha)` solo de tickets `CERRADO`, devuelve `Flow<Double?>`.
- `AyudaRepository.observarPromedioResolucion(): Flow<Long?>`, declarada en la interface e implementada en `AyudaRepositoryRoom`.
- `PanelViewModel.promedioResolucion` (`StateFlow<Long?>`) y el conteo de cerrados.
- `Formatos.duracion()` (ej. "1 d 4 h", "35 min") y la tarjeta `TarjetaPromedio` en `PanelScreen`, con el texto "Sin datos" si no hay cerrados.

**Qué tuve que corregir yo:** Nada; compiló a la primera. Verifiqué en el emulador que el promedio se recalcula al cerrar un ticket.

**¿Respeta MVVM?:** Sí. La consulta está en el DAO, la función se declaró primero en la interface y la View solo lee el StateFlow.

---

## Prompt 2 — Contador de SLA por prioridad

**Texto enviado a la IA:**
> Siguiendo la misma arquitectura (MVVM + AyudaRepository como interface), agrega un contador de SLA: cada ticket debe mostrar el tiempo restante según su prioridad (Alta 4 h, Media 24 h, Baja 72 h) contado desde su fecha de creación. La regla de negocio debe estar fuera de la View y ser testeable; el ViewModel expone el estado como StateFlow y se actualiza cada minuto. Muéstralo en la lista del estudiante, en la bandeja, en el detalle y en atender ticket, con colores para "en tiempo", "por vencer" y "vencido". Los tickets resueltos o cerrados no muestran SLA. Agrega pruebas unitarias. No uses XML. Muéstrame solo los archivos que cambian.

**Qué generó la IA:**
- `model/Sla.kt`: `ReglasSla.calcular()` con plazos 4/24/72 h y niveles `EN_TIEMPO`, `POR_VENCER` (≤ 25 % del plazo) y `VENCIDO`; devuelve `null` si el ticket está resuelto o cerrado.
- `viewmodel/RelojSla.kt`: un reloj que emite cada minuto alineado al cambio de minuto, y `slaPorTicket`.
- StateFlow `sla` en `MisTicketsViewModel`, `BandejaViewModel`, `TicketDetalleViewModel` y `AtenderViewModel`.
- Componente `SlaChip` (verde, ámbar o rojo "Vencido") y parámetro opcional `sla` en `TicketCard`.
- `NuevoTicketScreen` reutiliza `ReglasSla.plazoHoras`, que antes tenía las horas duplicadas.
- `ReglasSlaTest` con 7 pruebas unitarias.

**Qué tuve que corregir yo:** Nada en la compilación. Revisé que las horas del formulario de nuevo ticket coincidieran con las del SLA, y la IA las unificó en una sola fuente (`ReglasSla.plazoHoras`). Las 7 pruebas pasaron.

**¿Respeta MVVM?:** Sí. La regla vive en `model` (Kotlin puro, sin Android), el ViewModel la combina con el reloj y la View solo pinta el chip. No fue necesario tocar el Repository ni la base de datos.

---

## Prompt 3 — Búsqueda avanzada en la bandeja

**Texto enviado a la IA:**
> En la bandeja de Soporte agrega búsqueda avanzada: debe poder filtrar por estado, prioridad, categoría y texto al mismo tiempo. El texto busca por título, número de ticket o nombre del cliente sin importar tildes ni mayúsculas. Toda la lógica de filtrado va en BandejaViewModel (combine de los tickets con los filtros); la View solo muestra chips y el buscador. Muestra cuántos tickets coinciden y un botón para limpiar filtros. Mantén el orden por prioridad que ya da el DAO. No uses XML. Muéstrame solo los archivos que cambian.

**Qué generó la IA:**
- `FiltrosBandeja` (estado, prioridad, categoría, texto y número de filtros activos) dentro de `BandejaViewModel`.
- `combine(todos, filtros)` con `filter()`, que conserva el orden del DAO.
- Búsqueda normalizada con `Normalizer`: sin tildes ni mayúsculas, por título, `#número` o cliente.
- Categorías tomadas de `repo.observarCategorias()`, `total`, `limpiarFiltros()` y chips de tipo toggle (tocar otra vez quita el filtro).
- En `BandejaScreen`: buscador, panel de filtros avanzados (botón Tune), "N de M tickets", "Limpiar filtros (n)" y un estado vacío con botón.
- Refactor: el componente `CampoBusqueda` se extrajo y ahora también lo usa *Mis tickets*.

**Qué tuve que corregir yo:** Nada; compiló a la primera. Probé combinaciones (Baja + Cuenta + En proceso) y la búsqueda con tilde ("contraseña" / "contrasena").

**¿Respeta MVVM?:** Sí. El filtrado está en el ViewModel y la View no accede al DAO ni al Repository; las categorías llegan por la interface `AyudaRepository`, que ya existía.

---

## Prompt 4 — Registro de estudiante

**Texto enviado a la IA:**
> Agrega una pantalla de registro para que un estudiante cree su cuenta: nombre, correo, contraseña y confirmación. Valida formato de correo, longitud mínima de la contraseña, que ambas coincidan y que el correo no exista (correo único, sin distinguir mayúsculas). La consulta y el insert van en UsuarioDao; las funciones se declaran primero en AyudaRepository y luego se implementan; RegistroViewModel muestra los errores por campo y, al registrar, guarda la sesión en DataStore y entra a la app. Las validaciones deben ser reutilizables por el Login y tener pruebas unitarias. El enlace "Regístrate" solo aparece en la pestaña Estudiante. No uses XML. Muéstrame solo los archivos que cambian.

**Qué generó la IA:**
- `UsuarioDao.contarPorCorreo()` (`COLLATE NOCASE`) e `insertar()` con `OnConflictStrategy.ABORT`, para que el índice único proteja el correo.
- En `AyudaRepository`: `existeCorreo()` y `registrarEstudiante()`, declaradas en la interface e implementadas en Room con el correo normalizado (trim + minúsculas).
- `model/Validaciones.kt`, compartido con `SesionViewModel` (Login), y `ValidacionesTest`.
- `RegistroViewModel`: errores por campo, verificación de correo único, guarda la sesión en DataStore y emite un evento de navegación.
- `RegistroScreen`, la ruta `Rutas.REGISTRO` y el enlace "¿No tienes cuenta? Regístrate" en `LoginScreen` (solo en la pestaña Estudiante).

**Qué tuve que corregir yo:** Nada; compiló a la primera. Probé un correo repetido (`juan@tecsup.edu.pe`) y un correo con mayúsculas, y los dos se rechazan. Las pruebas pasaron.

**¿Respeta MVVM?:** Sí. Las funciones nuevas se declararon primero en la interface. El rol `CLIENTE` lo asigna el Repository y no la pantalla, así que nadie puede registrarse como Soporte desde la View.

---

## Prompt 5 — Comentarios internos de Soporte

**Texto enviado a la IA:**
> Soporte necesita escribir notas internas en un ticket que el estudiante NO pueda ver. Agrega el campo en la entidad Comentario y sube la versión de la base de datos. AyudaRepository.agregarComentario debe recibir si es interno (declarado en la interface). La regla de visibilidad va en el ViewModel del estudiante, no en el Composable. En Atender ticket agrega un switch "Nota interna" sobre la barra de mensaje y muestra esas notas con un estilo distinto (solo Soporte). Agrega pruebas unitarias de la regla. No uses XML. Muéstrame solo los archivos que cambian.

**Qué generó la IA:**
- Columna `Comentario.interno` (`Boolean`, por defecto `false`) y `AyudaDatabase` en versión 3. La migración es destructiva y vuelve a sembrar los datos, ahora con una nota interna de ejemplo.
- `AyudaRepository.agregarComentario(..., interno = false)`, declarado en la interface y llevado a Room.
- `viewmodel/NotasInternas.kt` con `sinNotasInternas()`, que `TicketDetalleViewModel` aplica para que el estudiante nunca reciba las notas.
- `AtenderViewModel.notaInterna` y `cambiarNotaInterna()`; el switch vuelve a apagarse después de enviar.
- `BurbujaComentario` con estilo ámbar, candado y la etiqueta "Nota interna · solo Soporte".
- `BarraMensaje` con un slot `encabezado` para el switch.
- `NotasInternasTest` con 2 pruebas.

**Qué tuve que corregir yo:** Al cambiar la entidad, la app instalada tenía la base antigua, así que la versión debía subir. La IA la subió a 3 y yo verifiqué en Database Inspector la nueva columna `interno`. Comprobé que la nota aparece en Atender (Soporte) y no en el Detalle (Juan).

**¿Respeta MVVM?:** Sí. El filtro de privacidad está en el ViewModel y no en la View: aunque la pantalla cambie, el estudiante nunca recibe la nota en su estado.

---

## Prompt 6 — Animaciones y estados vacíos

**Texto enviado a la IA:**
> Mejora solo la capa View con animaciones y estados vacíos: transiciones entre pantallas en el NavHost; el StepperTicket debe animarse al cambiar de estado y resaltar el paso actual; los contadores del Panel y del Perfil deben animar el cambio de número; y los estados vacíos (mis tickets, bandeja, categorías, conversación, búsqueda sin resultados) deben mostrar una ilustración propia dibujada con Canvas que respete los colores del tema claro/oscuro. No modifiques ViewModels, Repository ni base de datos. No uses XML ni imágenes externas. Muéstrame solo los archivos que cambian.

**Qué generó la IA:**
- Transiciones en el `NavHost`: fundido más deslizamiento de 1/8 del ancho, en 280 ms.
- `StepperTicket`:
  - rebote (`spring`) al cambiar de estado;
  - halo que pulsa en el paso actual (`rememberInfiniteTransition`);
  - líneas que se rellenan en 600 ms.
- `NumeroAnimado` (`AnimatedContent`) en los contadores del Panel y del Perfil.
- `Ilustraciones.kt` con 5 ilustraciones dibujadas con Canvas (tickets, bandeja, categorías, conversación y búsqueda). Toman los colores del tema y flotan suavemente.
- `EstadoVacio` con entrada animada, ilustración y botón de acción, y `ConversacionVacia` en Detalle y Atender.

**Qué tuve que corregir yo:** Al limpiar imports sin uso se eliminaron por error `getValue`/`setValue`, que necesita la delegación `by` de los estados animados, y el proyecto dejó de compilar. Se restauraron y volvió a compilar. Para probar las animaciones usé "Escala de duración de animador" en 5x en las opciones de desarrollador.

**¿Respeta MVVM?:** Sí. Solo cambiaron archivos de `ui/`. Las animaciones reaccionan al estado que ya exponían los ViewModels, sin lógica nueva.

---

## Reflexión (5 líneas)

1. **Qué mejoró la IA:** aceleró mucho el código repetitivo (consultas, StateFlows, componentes) y propuso extras útiles, como el nivel "por vencer" del SLA, la búsqueda sin tildes y extraer `CampoBusqueda` para reutilizarlo.
2. **Qué no mejoró:** no conoce el estado real del emulador ni de Android Studio; los problemas de JDK, de caché del IDE y del emulador los tuve que diagnosticar probando yo mismo.
3. **Qué error cometió:** al limpiar imports borró `getValue`/`setValue` y rompió la compilación. Revisar cada cambio y compilar antes de cada commit fue indispensable.
4. **Sobre MVVM:** declarar primero cada función en la interface `AyudaRepository` permitió agregar 6 mejoras sin que ninguna pantalla tocara Room. Las reglas (SLA, validaciones, privacidad de notas) quedaron en `model`/ViewModel, donde se pueden probar con tests.
5. **Sobre Room y las reglas de negocio:** cambiar una entidad obliga a subir la versión de la base. Además, una regla como "el estudiante no ve notas internas" debe aplicarse antes de llegar a la View, no solo ocultarse en pantalla.
