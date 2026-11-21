package core;

public enum Action {
    MOVE_UP,
    MOVE_DOWN,
    MOVE_LEFT,
    MOVE_RIGHT,
    USE_PRIMARY,    // 1 / Espacio (Arma principal)
    USE_SECONDARY,  // 2 (Arma secundaria)
    INTERACT,       // E (Abrir cofres, leer carteles)
    INVENTORY,      // I / TAB (Abrir/cerrar inventario)
    PAUSE,          // ESC / P (Pausar/reanudar)
    CONFIRM         // ENTER (Confirmar en menús)
}