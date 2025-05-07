package org.Lin_MidWork;


import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QwenGUI gui = new QwenGUI();
            gui.setVisible(true);
        });
    }
}