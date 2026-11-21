package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class GamePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public static final int TILE_SIZE = 16;
    public static final int LOGICAL_WIDTH = 256;   // 16 tiles
    public static final int LOGICAL_HEIGHT = 224;  // 14 tiles
    public static final int SCALE = 3;

    public static final int WINDOW_WIDTH = LOGICAL_WIDTH * SCALE;   // 768 px
    public static final int WINDOW_HEIGHT = LOGICAL_HEIGHT * SCALE; // 672 px

    public GamePanel() {
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.scale(SCALE, SCALE);

        g2.setColor(new Color(46, 204, 113));
        g2.fillRect((LOGICAL_WIDTH - TILE_SIZE) / 2, (LOGICAL_HEIGHT - TILE_SIZE) / 2, TILE_SIZE, TILE_SIZE);

        g2.dispose();
    }
}