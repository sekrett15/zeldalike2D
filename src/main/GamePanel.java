package main;

import core.Action;
import core.InputHandler;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    private static final long serialVersionUID = 1L;

    // Constantes de resolución (DESIGN.md)
    public static final int TILE_SIZE = 16;
    public static final int LOGICAL_WIDTH = 256;   // 16 tiles
    public static final int LOGICAL_HEIGHT = 224;  // 14 tiles
    public static final int SCALE = 3;

    public static final int WINDOW_WIDTH = LOGICAL_WIDTH * SCALE;   // 768 px
    public static final int WINDOW_HEIGHT = LOGICAL_HEIGHT * SCALE; // 672 px

    // Constantes del Game Loop (60 FPS fijos)
    public static final int TARGET_FPS = 60;
    public static final double FIXED_DT = 1.0 / TARGET_FPS;
    private static final double MAX_FRAME_TIME = 0.25;

    private Thread gameThread;
    private volatile boolean running = false;

    private final InputHandler inputHandler = new InputHandler();

    // Variables temporales de prueba para movimiento (80 px/s) y ataque (0.25 s)
    private double testX = (LOGICAL_WIDTH - TILE_SIZE) / 2.0;
    private double testY = (LOGICAL_HEIGHT - TILE_SIZE) / 2.0;
    private final double testSpeed = 80.0;
    private double attackFlashTimer = 0.0;

    public GamePanel() {
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false); // Permite capturar la tecla TAB

        this.addKeyListener(inputHandler);
        this.addFocusListener(inputHandler);
    }

    public synchronized void startGameThread() {
        if (running) return;
        running = true;
        gameThread = new Thread(this, "GameLoop-Thread");
        gameThread.start();
    }

    public synchronized void stopGameThread() {
        running = false;
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void run() {
        long previousTime = System.nanoTime();
        double accumulator = 0.0;

        long timer = System.currentTimeMillis();
        int updates = 0;
        int frames = 0;

        while (running) {
            long currentTime = System.nanoTime();
            double frameTime = (currentTime - previousTime) / 1_000_000_000.0;
            previousTime = currentTime;

            if (frameTime > MAX_FRAME_TIME) {
                frameTime = MAX_FRAME_TIME;
            }

            accumulator += frameTime;

            while (accumulator >= FIXED_DT) {
                update(FIXED_DT);
                accumulator -= FIXED_DT;
                updates++;
            }

            repaint();
            frames++;

            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.printf("UPS: %d | FPS: %d%n", updates, frames);
                updates = 0;
                frames = 0;
                timer += 1000;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void update(double dt) {
        inputHandler.update();

        // 1. Prueba de movimiento continuo (isActionHeld)
        double dx = 0.0;
        double dy = 0.0;

        if (inputHandler.isActionHeld(Action.MOVE_UP)) dy -= 1.0;
        if (inputHandler.isActionHeld(Action.MOVE_DOWN)) dy += 1.0;
        if (inputHandler.isActionHeld(Action.MOVE_LEFT)) dx -= 1.0;
        if (inputHandler.isActionHeld(Action.MOVE_RIGHT)) dx += 1.0;

        // Normalización diagonal para evitar velocidad extra
        if (dx != 0.0 && dy != 0.0) {
            double length = Math.hypot(dx, dy);
            dx /= length;
            dy /= length;
        }

        testX += dx * testSpeed * dt;
        testY += dy * testSpeed * dt;

        // Mantener dentro de los límites de pantalla
        testX = Math.max(0, Math.min(LOGICAL_WIDTH - TILE_SIZE, testX));
        testY = Math.max(0, Math.min(LOGICAL_HEIGHT - TILE_SIZE, testY));

        // 2. Prueba de pulsación única (isActionJustPressed)
        if (inputHandler.isActionJustPressed(Action.USE_PRIMARY)) {
            attackFlashTimer = 0.25; // Duración de ataque en DESIGN.md
        }

        if (attackFlashTimer > 0.0) {
            attackFlashTimer -= dt;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        g2.scale(SCALE, SCALE);

        // Cambia a dorado durante 0.25 s al pulsar J o Espacio
        if (attackFlashTimer > 0.0) {
            g2.setColor(new Color(241, 196, 15));
        } else {
            g2.setColor(new Color(46, 204, 113));
        }

        g2.fillRect((int) Math.round(testX), (int) Math.round(testY), TILE_SIZE, TILE_SIZE);

        Toolkit.getDefaultToolkit().sync();
        g2.dispose();
    }
}