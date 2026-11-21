# Boceto Detallado de Niveles y Progresión (4 Zonas)

Este documento traduce el diseño conceptual del mundo en planos estructurados por rejilla de *tiles* ($16 \times 16\text{ px}$), definiendo la ubicación exacta de cofres, interruptores, obstáculos, enemigos y reglas de progresión para evitar bloqueos (*softlocks*).

---

## 1. Flujo de Progresión (*Item-Gating*) y Prevención de Bloqueos

El diseño sigue la estructura clásica de *The Legend of Zelda*: cada herramienta obtenida en un cofre desbloquea el acceso físico a la siguiente región.

```text
[Zona 1: Claro del Alba] ──(Espada corta arbustos B)──► [Zona 2: Sendero del Bosque]
                                                               │
                                              (Arco activa interruptor S1 sobre río)
                                                               ▼
[Zona 4: Cámara del Guardián] ◄──(Llave + Puente S2)── [Zona 3: Antesala del Templo]
```

### Reglas de Diseño y *Anti-Softlock*
1. **Zona 1 $\rightarrow$ Zona 2:** El jugador aparece desarmado en el claro. La salida norte está bloqueada por arbustos (`B`). Abrir el cofre `C1` otorga la **Espada del Bosque**, enseñando de forma natural su uso sin necesidad de tutoriales intrusivos.
2. **Zona 2 $\rightarrow$ Zona 3:** En `C2` se obtiene el **Arco de Cazador** ($+15$ flechas). Un río impide avanzar hacia el este, pero al otro lado se encuentra el interruptor de cristal `S1`. Disparar una flecha extiende el puente retráctil hacia la Zona 3.
3. **Zona 3 $\rightarrow$ Zona 4:** Para abrir la puerta del jefe (`DD`) se requieren dos pasos: activar el interruptor `S2` tras el abismo para desplegar la pasarela central y usar las **Bombas** (`C4`) para derribar el muro agrietado `W1`, accediendo al cofre `C5` con la **Llave del Guardián**.
4. **Regeneración de munición (*Anti-Softlock*):** Los arbustos (`B`) de la Zona 2 y las vasijas (`V`) de las Zonas 3 y 4 tienen probabilidad garantizada de soltar flechas o bombas si el jugador agota su inventario durante un puzle o combate.

---

## 2. Leyenda de Planos Esquemáticos

* **Entorno:** `#` Muro / Árbol / Roca sólida | `.` Suelo transitable | `~` Agua profunda / Abismo infranqueable
* **Puntos de interés:** `P` Inicio del jugador (*Spawn*) | `N` Cartel / NPC guía
* **Obstáculos y mecanismos:** `B` Arbusto cortable (Espada) | `W1` Muro agrietado (Bomba) | `S1..S2` Interruptores de cristal (Flecha) | `=` Puente retráctil | `DD` Puerta sellada / con cerradura
* **Cofres y suministros:** `C1..C5` Cofres de progresión | `C_FINAL` Cofre Reliquia (Victoria) | `V` Vasija rompible con consumibles
* **Enemigos:** `E1` Slime del Bosque | `E2` Esqueleto Arquero | `BOSS` Caballero Corrompido

---

## 3. Planos por Zona (Rejilla para Tiled Map Editor)

Cada pantalla base tiene una resolución lógica de $256 \times 224\text{ px}$ ($16 \times 14$ *tiles*). Las zonas de exploración (`Zona 2` y `Zona 3`) tienen doble anchura ($32 \times 14$ *tiles*, equivalentes a $512 \times 224\text{ px}$) para aprovechar el desplazamiento de la cámara 2D.

### Zona 1: Claro del Alba (Abajo-Izquierda — $16 \times 14$ *tiles*)
Zona segura de inicio. Presenta el entorno, un cartel con controles básicos y el primer cofre.

```text
######BB########   <-- Salida Norte hacia Zona 2 (bloqueada por arbustos B)
#.....BB.......#
#..####........#
#..#C1#....N...#   <-- C1: Cofre de madera (Espada del Bosque) | N: Cartel guía
#..#..#........#
#..............#
#......P.......#   <-- P: Punto de aparición inicial (Spawn)
#..............#
#..###....###..#
#..###....###..#
#..............#
################
```

---

### Zona 2: Sendero del Bosque (Arriba-Izquierda — $32 \times 14$ *tiles*)
Zona abierta dividida en dos pantallas horizontales conectadas por un sendero arbolado y un río en el extremo este.

```text
################################################################
#..C3..#.......................##..............................#
#..BB..#.....E1................##.........E2........####.......#
#..BB..#.......................##...................#C2#.......#
#......#...........####........##...................#..#.......#
#..................####........##........E1....................#
#..............................##....................~~~~......#
#........E1....................##....................~~~~..S1..#
#..............................##....................====......#
#..................####........##....................~~~~......#
#..................####........##........E1..........~~~~......#
#..............................##..............................#
######..########################################################
      ▲
   (Entrada desde Zona 1)                             (Salida Este hacia Zona 3 ►)
```

* **`C2` (Noreste):** Cofre con el **Arco de Cazador** ($+15$ flechas), custodiado por un Esqueleto Arquero (`E2`) y dos Slimes (`E1`).
* **`C3` (Noroeste):** Cofre opcional oculto tras cuatro arbustos densos (`B`) con una **Poción Roja**.
* **`S1` + `====` (Este):** Interruptor de cristal al otro lado del río (`~~~~`). Al impactar una flecha sobre `S1`, se despliega el puente `====` hacia la Zona 3.

---

### Zona 3: Antesala del Templo (Arriba-Derecha — $32 \times 14$ *tiles*)
Ruinas de piedra con combate a distancia, un foso central y una cámara secreta tras un muro agrietado.

```text
################W1##############################################
#..............#C5#............##........V....................#
#.....E2.......####............##........~~~~~~~......S2.......#
#..............................##........~~~~~~~...............#
#..............................##........~~~~~~~...............#
#..............................##........=======...............#
► (Entrada desde Zona 2).......##........~~~~~~~...............#
#..............................##........~~~~~~~...............#
#..............................##........~~~~~~~......E2.......#
#.....E2.......................##..............................#
#..####........................##..............................#
#..#C4#........V...............##....................DD........#
#####################################################DD#########
                                                     ▼
                                           (Salida Sur hacia Zona 4)
```

* **`C4` (Suroeste):** Cofre custodiado por dos Arqueros (`E2`). Contiene **Bombas ($\times 5$)**.
* **`W1` $\rightarrow$ `C5` (Norte):** Muro agrietado destruible mediante explosión de bomba. Revela el cofre dorado `C5` con la **Llave del Guardián**.
* **`S2` + `=======` (Centro-Este):** Interruptor de cristal al otro lado del foso. Al recibir un flechazo desde el borde oeste, extiende la pasarela `=======`.
* **`DD` (Sureste):** Puerta sellada del Jefe. Requiere cruzar el puente y consumir la **Llave del Guardián**.

---

### Zona 4: Cámara del Guardián (Abajo-Derecha — $16 \times 14$ *tiles*)
Arena cerrada de una sola pantalla ($256 \times 224\text{ px}$). Al entrar el jugador, la cámara queda bloqueada en la sala y la puerta norte se cierra con rejas hasta derrotar al jefe.

```text
######DD########   <-- Puerta de entrada (se sella con rejas al entrar)
#V............V#
#..............#
#...##....##...#   <-- 4 pilares de piedra para cubrirse de embestidas
#...##....##...#
#..............#
#.....BOSS.....#   <-- Caballero Corrompido (12 HP, 2 fases)
#..............#
#...##....##...#
#...##....##...#
#V............V#   <-- Vasijas (V) con corazones/flechas si se rompen
######==########   <-- Barrera mágica (cae al derrotar al BOSS)
#...C_FINAL....#   <-- Cofre Reliquia (activa pantalla VictoryState)
################
```

---

## 4. Tabla Maestra de Entidades Interactivas (Mapeo a Java)

Esta tabla vincula cada identificador del boceto con su clase correspondiente en los paquetes `world` y `entities` y sus propiedades en **Tiled**:

| ID en Mapa | Zona | Clase Java | Condición de Activación | Efecto / Recompensa |
| :--- | :--- | :--- | :--- | :--- |
| **`C1`** | Zona 1 | `Chest` | Interacción frontal (`E`) | Añade `SwordWeapon` al inventario y la equipa en ranura `J`. |
| **`B`** | Zona 1, 2 | `DestructibleBush` | Colisión con *Hitbox* de Espada o Explosión | Se destruye ($25\%$ prob. de soltar corazón o flecha). |
| **`C2`** | Zona 2 | `Chest` | Interacción frontal (`E`) | Añade `BowWeapon` al inventario $+ 15$ flechas. |
| **`C3`** | Zona 2 | `Chest` | Interacción frontal (`E`) | Añade `RedPotion` al inventario ($+4\text{ HP}$ al usar). |
| **`S1`** | Zona 2 | `CrystalSwitch` | Impacto de `Projectile` (Flecha) | Cambia estado a `ACTIVATED` y habilita *tiles* del puente Este. |
| **`C4`** | Zona 3 | `Chest` | Interacción frontal (`E`) | Añade `BombWeapon` al inventario $+ 5$ bombas. |
| **`W1`** | Zona 3 | `CrackedWall` | Explosión de `BombEntity` en radio $24\text{ px}$ | Reemplaza *tiles* sólidos por suelo transitable hacia `C5`. |
| **`C5`** | Zona 3 | `Chest` | Interacción frontal (`E`) | Añade `BossKey` (Llave del Guardián) al inventario. |
| **`S2`** | Zona 3 | `CrystalSwitch` | Impacto de `Projectile` (Flecha) | Extiende pasarela central sobre el abismo de la Zona 3. |
| **`DD`** | Zona 3 | `LockedDoor` | Contacto teniendo `BossKey` | Consume `BossKey` y abre transición hacia Zona 4. |
| **`V`** | Zona 3, 4 | `DestructiblePot` | Impacto de Espada o Proyectil | Se rompe y suelta corazón ($+1\text{ HP}$) o munición ($+3$ flechas). |
| **`C_FINAL`** | Zona 4 | `RelicChest` | Barrera abierta tras morir `BOSS` | Dispara transición global a `VictoryState`. |

---

## 5. Estructura de Capas en Tiled Map Editor

Al trasladar este boceto a **Tiled** en la **Fase 2.1** y **Fase 5.1**, se utilizarán exactamente 4 capas ordenadas de abajo hacia arriba:

1. **`Ground` (Tile Layer):** Suelos transitables, caminos de tierra, hierba y suelo de ruinas (sin colisión).
2. **`Walls` (Tile Layer):** Muros, árboles, pilares y agua/abismos (genera la matriz booleana de colisiones AABB).
3. **`Interactive` (Object Layer):** Rectángulos con propiedades personalizadas (`id`, `type`, `targetId`) para instanciar cofres, interruptores, muros agrietados y puertas.
4. **`Entities` (Object Layer):** Puntos de aparición (`PlayerSpawn`, `Slime`, `SkeletonArcher`, `CorruptedKnight`).