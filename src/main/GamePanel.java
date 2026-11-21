package main;

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
    public static final double FIXED_DT = 1.0 / TARGET_FPS; // ~0.01667 s
    private static final double MAX_FRAME_TIME = 0.25;      // Evita espiral de la muerte

    private Thread gameThread;
    private volatile boolean running = false;

    // Variables temporales para validar el movimiento a 80 px/s (velocidad del jugador)
    private double testX = 0.0;
    private final double testY = (LOGICAL_HEIGHT - TILE_SIZE) / 2.0;
    private double testSpeed = 80.0;

    public GamePanel() {
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
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

        // Métricas de depuración en consola (cada segundo)
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

            // 1. Actualización lógica determinista (60 veces por segundo)
            while (accumulator >= FIXED_DT) {
                update(FIXED_DT);
                accumulator -= FIXED_DT;
                updates++;
            }

            // 2. Renderizado
            repaint();
            frames++;

            // 3. Log de rendimiento cada 1000 ms
            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.printf("UPS: %d | FPS: %d%n", updates, frames);
                updates = 0;
                frames = 0;
                timer += 1000;
            }

            // Cede brevemente CPU para evitar consumo del 100% de un núcleo
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Actualiza toda la lógica del juego en pasos de tiempo constantes.
     * @param dt Delta time fijo en segundos (1/60 s).
     */
    private void update(double dt) {
        // Prueba de movimiento horizontal a 80 px/s con rebote en los bordes
        testX += testSpeed * dt;

        if (testX <= 0) {
            testX = 0;
            testSpeed = Math.abs(testSpeed);
        } else if (testX + TILE_SIZE >= LOGICAL_WIDTH) {
            testX = LOGICAL_WIDTH - TILE_SIZE;
            testSpeed = -Math.abs(testSpeed);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Configurar interpolación Nearest-Neighbor para mantener el pixel art nítido
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Escalado lógico x3 (256x224 -> 768x672)
        g2.scale(SCALE, SCALE);

        // Dibujo de prueba redondeado a píxel entero
        g2.setColor(new Color(46, 204, 113));
        g2.fillRect((int) Math.round(testX), (int) Math.round(testY), TILE_SIZE, TILE_SIZE);

        Toolkit.getDefaultToolkit().sync(); // Evita tirones de sincronización en algunos sistemas
        g2.dispose();
    }
}