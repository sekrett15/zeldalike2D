package core;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InputHandler implements KeyListener, FocusListener {

    private final Map<Integer, Action> keyBindings = new HashMap<>();

    // Teclas físicas actualmente pulsadas (modificado por el hilo de Swing EDT)
    private final Set<Integer> rawKeysPressed = ConcurrentHashMap.newKeySet();

    // Acciones nuevas capturadas entre ticks para no perder pulsaciones muy rápidas
    private final Set<Action> pendingJustPressed = ConcurrentHashMap.newKeySet();

    // Estados consultados por la lógica del juego durante el tick actual
    private final EnumSet<Action> currentActions = EnumSet.noneOf(Action.class);
    private final EnumSet<Action> justPressedActions = EnumSet.noneOf(Action.class);

    public InputHandler() {
        bindDefaultKeys();
    }

    private void bindDefaultKeys() {
        // Movimiento (WASD + Flechas)
        keyBindings.put(KeyEvent.VK_W, Action.MOVE_UP);
        keyBindings.put(KeyEvent.VK_UP, Action.MOVE_UP);

        keyBindings.put(KeyEvent.VK_S, Action.MOVE_DOWN);
        keyBindings.put(KeyEvent.VK_DOWN, Action.MOVE_DOWN);

        keyBindings.put(KeyEvent.VK_A, Action.MOVE_LEFT);
        keyBindings.put(KeyEvent.VK_LEFT, Action.MOVE_LEFT);

        keyBindings.put(KeyEvent.VK_D, Action.MOVE_RIGHT);
        keyBindings.put(KeyEvent.VK_RIGHT, Action.MOVE_RIGHT);

        // Combate e Interacción
        keyBindings.put(KeyEvent.VK_1, Action.USE_PRIMARY);
        keyBindings.put(KeyEvent.VK_SPACE, Action.USE_PRIMARY);
        keyBindings.put(KeyEvent.VK_2, Action.USE_SECONDARY);
        keyBindings.put(KeyEvent.VK_E, Action.INTERACT);

        // Interfaz y Sistema
        keyBindings.put(KeyEvent.VK_I, Action.INVENTORY);
        keyBindings.put(KeyEvent.VK_TAB, Action.INVENTORY);
        keyBindings.put(KeyEvent.VK_ESCAPE, Action.PAUSE);
        keyBindings.put(KeyEvent.VK_P, Action.PAUSE);
        keyBindings.put(KeyEvent.VK_ENTER, Action.CONFIRM);
    }

    /**
     * Debe llamarse una única vez al principio de GamePanel.update(dt).
     */
    public void update() {
        currentActions.clear();
        for (Integer keyCode : rawKeysPressed) {
            Action action = keyBindings.get(keyCode);
            if (action != null) {
                currentActions.add(action);
            }
        }

        justPressedActions.clear();
        justPressedActions.addAll(pendingJustPressed);
        pendingJustPressed.clear();
    }

    /**
     * Devuelve true mientras la tecla asociada permanezca pulsada.
     */
    public boolean isActionHeld(Action action) {
        return currentActions.contains(action);
    }

    /**
     * Devuelve true únicamente durante el frame en que se pulsa la tecla.
     */
    public boolean isActionJustPressed(Action action) {
        return justPressedActions.contains(action);
    }

    public void clear() {
        rawKeysPressed.clear();
        pendingJustPressed.clear();
        currentActions.clear();
        justPressedActions.clear();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        // add() devuelve true solo la primera vez que se pulsa (ignora auto-repeat del SO)
        if (rawKeysPressed.add(code)) {
            Action action = keyBindings.get(code);
            if (action != null) {
                pendingJustPressed.add(action);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        rawKeysPressed.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No utilizado
    }

    @Override
    public void focusGained(FocusEvent e) {
        // No utilizado
    }

    @Override
    public void focusLost(FocusEvent e) {
        clear();
    }
}