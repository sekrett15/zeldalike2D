package main;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GamePanel gamePanel = new GamePanel();
            new GameWindow(gamePanel);
            gamePanel.startGameThread();
        });
    }
}