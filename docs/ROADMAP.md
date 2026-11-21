# Roadmap de Desarrollo — Vertical Slice Action-RPG 2D (Java + Eclipse)

Plan de trabajo estructurado en 7 fases incrementales para construir, probar, empaquetar y documentar el proyecto en **Java 21** utilizando **Eclipse IDE**.

---

## Fase 0: Preparación, Diseño y Entorno en Eclipse

### 0.1. Especificación y alcance
* [x] Redactar `DESIGN.md` definiendo las 4 zonas, inventario, armas, enemigos y mini-jefe.
* [x] Establecer `AGENTS.md` con las reglas de arquitectura y estilo de código.
* [ ] Realizar un boceto detallado en papel de las 4 zonas y la ubicación de cada cofre e interruptor.

### 0.2. Configuración de Git y GitHub
* [ ] Crear repositorio público en GitHub (`zelda-vertical-slice-java`) con licencia MIT.
* [ ] Configurar `.gitignore` específico para Eclipse y Java (`.metadata/`, `bin/`, `*.class`, `.DS_Store`, `Thumbs.db`).
* [ ] Crear tablero Kanban en **GitHub Projects** (*To Do*, *In Progress*, *Done*).

### 0.3. Configuración del proyecto en Eclipse IDE
* [ ] Configurar el Workspace de Eclipse con codificación **UTF-8** (`Window > Preferences > General > Workspace`).
* [ ] Crear el `Java Project` apuntando al directorio del repositorio con **JavaSE-21**.
* [ ] Crear la estructura de paquetes en `src/` (`main`, `core`, `states`, `entities`, `inventory`, `world`, `ui`).
* [ ] Crear la carpeta `res/` en la raíz y marcarla como **Source Folder** (`Build Path > Use as Source Folder`) para cargar recursos desde el *classpath*.
* [ ] Compilar y ejecutar una ventana `JFrame` vacía de prueba y realizar el primer *commit* (`chore: initial eclipse project setup`).

---

## Fase 1: Motor Base Java2D y Pipeline de Renderizado

### 1.1. Game Loop y Doble/Triple Búfer
* [ ] Implementar `GamePanel` extendiendo `Canvas` e implementando `Runnable`.
* [ ] Programar el bucle principal a $60\text{ FPS}$ fijos ($\Delta t = 1/60\text{ s}$) separando `processInput()`, `update(dt)` y `render()`.
* [ ] Configurar `BufferStrategy` para evitar parpadeos (*flickering*) en pantalla.

### 1.2. Gestión de Entrada (`InputHandler`)
* [ ] Implementar `KeyListener` mapeando teclas físicas a acciones lógicas (`UP`, `DOWN`, `LEFT`, `RIGHT`, `SLOT_J`, `SLOT_K`, `INTERACT`, `INVENTORY`, `PAUSE`).
* [ ] Diferenciar estados de pulsación: *mantenida* (`isKeyDown`) frente a *un solo frame* (`isKeyJustPressed`).

### 1.3. Gestor de Recursos (`AssetManager`)
* [ ] Implementar carga centralizada de imágenes (`ImageIO.read(getClass().getResourceAsStream(...))`), fuentes `.ttf` y clips de audio en `HashMap` para evitar lecturas repetidas en disco.

### 1.4. Escalado *Pixel-Perfect*
* [ ] Renderizar todo el juego sobre un `BufferedImage` lógico de $256 \times 224\text{ px}$ y dibujarlo escalado $\times 3$ ($768 \times 672\text{ px}$) con `RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR`.

---

## Fase 2: Mundo Interconectado, Mapas y Movimiento

### 2.1. Diseño de Mapas en Tiled y Cargador
* [ ] Crear un mapa de prueba de $32 \times 28$ *tiles* en **Tiled** con capas: `Ground`, `Walls`, `Interactive` y `Entities`.
* [ ] Implementar `MapLoader` en Java para leer el archivo exportado y construir la matriz de colisiones y objetos.
* [ ] Optimizar el renderizado dibujando únicamente los *tiles* visibles dentro del rectángulo de la cámara.

### 2.2. Entidad Jugador y Movimiento
* [ ] Crear la clase abstracta `Entity` (posición $x, y$, dimensiones, *hitbox*, *hurtbox*, estado activo) y heredar `Player`.
* [ ] Implementar movimiento en 4 direcciones normalizando el vector en diagonales ($\vec{v} / \|\vec{v}\|$).

### 2.3. Colisiones AABB y Cámara 2D
* [ ] Implementar colisiones *Axis-Aligned Bounding Box* resolviendo primero el eje $X$ y luego el eje $Y$ para permitir deslizarse suavemente junto a los muros.
* [ ] Implementar `Camera` centrada en el jugador con *clamping* a los límites del mundo ($512 \times 448\text{ px}$).

---

## Fase 3: Inventario, Armas (Patrón *Strategy*) y Combate

### 3.1. Máquina de Estados del Jugador (`State` Pattern)
* [ ] Implementar los estados `IDLE`, `WALK`, `ATTACK`, `HURT` y `DEAD`.
* [ ] Bloquear el desplazamiento durante las animaciones de ataque o retroceso por daño.

### 3.2. Arquitectura de Armas (`WeaponStrategy`)
* [ ] Definir la interfaz `WeaponStrategy` con el método `execute(Player player, World world)`.
* [ ] Implementar `SwordWeapon` (genera *Hitbox* frontal temporal durante $0.25\text{ s}$).
* [ ] Implementar `BowWeapon` (verifica munición de flechas e instancia `Projectile`).
* [ ] Implementar `BombWeapon` (verifica munición de bombas e instancia `BombEntity`).

### 3.3. Proyectiles y Entidades Dinámicas
* [ ] Crear la clase `Projectile` (trayectoria lineal, colisión contra muros y entidades enemigas).
* [ ] Crear `BombEntity` (temporizador de $2.0\text{ s}$, parpadeo acelerado y explosión en área de $32 \times 32\text{ px}$).
* [ ] Gestionar de forma segura la adición y eliminación de entidades dinámicas en el mundo durante el bucle `update()` (evitando `ConcurrentModificationException`).

### 3.4. Sistema de Inventario y Salud
* [ ] Implementar la clase `Inventory` con cuadrícula de $2 \times 4$ ranuras, almacenamiento de armas, consumibles (`RedPotion`) y contadores de munición/llaves.
* [ ] Implementar equipación dinámica en las ranuras rápidas **J** y **K**.
* [ ] Implementar sistema de vida (`maxHealth`, `currentHealth`), *knockback* e invulnerabilidad temporal (*i-frames*).

---

## Fase 4: Enemigos, Puzles e Inteligencia Artificial

### 4.1. Enemigo Cuerpo a Cuerpo (`Slime`)
* [ ] Implementar `Slime` heredando de `Enemy` con estados `PATROL` y `CHASE` basados en distancia euclidiana al jugador.
* [ ] Programar sistema de *drops* aleatorios al morir (corazones, flechas, bombas).

### 4.2. Enemigo a Distancia (`SkeletonArcher`)
* [ ] Implementar `SkeletonArcher` con lógica de alineación en ejes $X$/$Y$, pausa de telegrafiado y disparo de `Projectile`.

### 4.3. Objetos Interactivos y Puzles
* [ ] Implementar `Chest` (abre al interactuar con `E`, entrega el objeto al inventario y lanza cuadro de texto).
* [ ] Implementar `DestructibleBush` (se destruye con espada o bomba), `CrackedWall` (se destruye solo con bomba) y `CrystalSwitch` (se activa al recibir impacto de flecha o espada).
* [ ] Implementar `LockedDoor` (requiere Llave Pequeña o activación de interruptor).

### 4.4. Mini-Jefe (`CorruptedKnight`)
* [ ] Programar la arena cerrada en la Zona 4 (se sella al entrar el jugador).
* [ ] Implementar la máquina de estados del jefe (`IDLE_GUARD`, `TELEGRAPH`, `CHARGE`, `SPIN_ATTACK`, `HURT`, `DEFEATED`) y su Fase 2 al bajar de $6\text{ HP}$.
* [ ] Hacer aparecer el cofre final tras derrotar al jefe.

---

## Fase 5: Producción de Arte Propio y Audio

### 5.1. Creación de *Tilesets* y Mapas Definitivos
* [ ] Dibujar en **Aseprite / LibreSprite** el *tileset* completo ($16 \times 16\text{ px}$) con paleta unificada.
* [ ] Construir las 4 zonas definitivas en **Tiled** siguiendo el plano de `DESIGN.md` y exportar.

### 5.2. Diseño y Animación de Personajes y Objetos
* [ ] Dibujar y animar el *spritesheet* del protagonista (caminar, espada y arco en 4 direcciones).
* [ ] Dibujar y animar `Slime`, `SkeletonArcher` y `CorruptedKnight` ($24 \times 24\text{ px}$).
* [ ] Dibujar armas, proyectiles, explosiones, cofres e iconos del inventario y HUD.

### 5.3. Motor de Animación y Sonido
* [ ] Implementar la clase `Animation` para gestionar *frames* y velocidad de reproducción según el estado de cada entidad.
* [ ] Generar efectos de sonido retro con **Bfxr / jsfxr** y música ambiental/jefe, integrándolos mediante `javax.sound.sampled.Clip`.

---

## Fase 6: Interfaz de Usuario (HUD), Estados y Diálogos

### 6.1. Gestor de Estados Globales (`GameStateManager`)
* [ ] Implementar pila o máquina de estados para gestionar: `TitleState`, `PlayState`, `InventoryState`, `DialogueState`, `PauseState`, `GameOverState` y `VictoryState`.

### 6.2. HUD y Pantalla de Inventario
* [ ] Renderizar HUD superior: corazones de vida, iconos de objetos equipados en **J** y **K**, y contadores numéricos de flechas, bombas y llaves.
* [ ] Programar la interfaz visual de `InventoryState` (navegación por cursor en la cuadrícula $2 \times 4$, descripción del objeto seleccionado y asignación a teclas `J`/`K`).

### 6.3. Sistema de Diálogos y Pantallas de Flujo
* [ ] Implementar `DialogueBox` con texto progresivo tipo máquina de escribir para carteles y obtención de objetos en cofres.
* [ ] Completar pantallas de Título, Pausa, *Game Over* y Victoria con reinicio limpio de partida.

---

## Fase 7: QA, Empaquetado JAR y Documentación de Portfolio

### 7.1. Control de Calidad y Perfilado en Java
* [ ] Probar casos límite: usar bombas junto a puertas, cambiar de arma durante un ataque, morir al mismo tiempo que el jefe.
* [ ] Analizar consumo de memoria y CPU con **VisualVM** o **JConsole** verificando estabilidad a $60\text{ FPS}$ y ausencia de fugas tras múltiples reinicios.

### 7.2. Empaquetado de Distribución
* [ ] Exportar el proyecto desde Eclipse como **Runnable JAR** (`File > Export > Java > Runnable JAR file`) empaquetando las librerías/recursos necesarios.
* [ ] Verificar que el `.jar` se ejecuta correctamente mediante doble clic o `java -jar zelda-vertical-slice.jar` fuera de Eclipse.

### 7.3. Documentación de Ingeniería en GitHub
* [ ] Redactar `README.md` profesional detallando arquitectura, patrones de diseño aplicados (*Strategy*, *State*, *Service Locator/AssetManager*), controles e instrucciones de ejecución.
* [ ] Añadir diagramas UML (jerarquía de entidades, armas y máquina de estados) en `/docs` y en el `README.md`.
* [ ] Grabar GIFs cortos mostrando inventario, combate con arco/bombas y batalla contra el mini-jefe.
* [ ] Publicar la versión **`v1.0.0`** en *GitHub Releases* adjuntando el archivo `.jar` listo para jugar.