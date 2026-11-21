package main;

import javax.swing.JFrame;

public class GameWindow {

    private final JFrame frame;

    public GameWindow(GamePanel gamePanel) {
        frame = new JFrame("Zelda-Like 2D");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}