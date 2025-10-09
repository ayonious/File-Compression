package prog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainPanel {
    private static class PanelContainer {
        final JPanel totalGUI;
        final JPanel mainPanel;

        PanelContainer() {
            totalGUI = new JPanel();
            totalGUI.setLayout(new BorderLayout());
            mainPanel = new JPanel();
            mainPanel.setLayout(null);
            totalGUI.add(mainPanel, BorderLayout.CENTER);
            totalGUI.setOpaque(true);
        }
    }

    private static PanelContainer createBasePanel() {
        return new PanelContainer();
    }

    private static void createTitlePanel(JPanel mainPanel) {
        Main.titlePanel = new JPanel();
        Main.titlePanel.setLayout(null);
        Main.titlePanel.setLocation(90, 20);
        Main.titlePanel.setSize(170, 70);
        mainPanel.add(Main.titlePanel);

        // Create and add labels
        Main.originalSizeLabel = createLabel("Selected File Size: ", 43, 0, 150, 30);
        Main.compressedSizeLabel = createLabel("After zip/unzip the file size: ", 10, 30, 170, 30);

        Main.titlePanel.add(Main.originalSizeLabel);
        Main.titlePanel.add(Main.compressedSizeLabel);
    }

    private static void createScorePanel(JPanel mainPanel) {
        Main.scorePanel = new JPanel();
        Main.scorePanel.setLayout(null);
        Main.scorePanel.setLocation(270, 20);
        Main.scorePanel.setSize(120, 60);
        mainPanel.add(Main.scorePanel);

        // Create and add score labels
        Main.originalSizeValue = createLabel("", 0, 0, 100, 30);
        Main.compressedSizeValue = createLabel("", 0, 30, 100, 30);

        Main.scorePanel.add(Main.originalSizeValue);
        Main.scorePanel.add(Main.compressedSizeValue);
    }

    private static JLabel createLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setLocation(x, y);
        label.setSize(width, height);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private static JButton createButton(String text, int x, int y, int width, int height, ActionListener listener) {
        JButton button = new JButton(text);
        button.setLocation(x, y);
        button.setSize(width, height);
        button.addActionListener(listener);
        return button;
    }

    private static void createButtonPanel(JPanel mainPanel, ActionListener listener) {
        Main.buttonPanel = new JPanel();
        Main.buttonPanel.setLayout(null);
        Main.buttonPanel.setLocation(10, 130);
        Main.buttonPanel.setSize(5200, 150);
        mainPanel.add(Main.buttonPanel);

        // Create compression buttons
        Main.huffmanCompressButton = createButton("ZIP HuffZ", 0, 0, 120, 30, listener);
        Main.huffmanDecompressButton = createButton("UNZIP HuffZ", 130, 0, 120, 30, listener);
        Main.lzwCompressButton = createButton("ZIP LmZWp", 260, 0, 120, 30, listener);
        Main.lzwDecompressButton = createButton("UNZIP LmZWp", 390, 0, 120, 30, listener);
        Main.exitButton = createButton("EXIT", 130, 70, 250, 30, listener);

        // Add buttons to panel
        Main.buttonPanel.add(Main.huffmanCompressButton);
        Main.buttonPanel.add(Main.huffmanDecompressButton);
        Main.buttonPanel.add(Main.lzwCompressButton);
        Main.buttonPanel.add(Main.lzwDecompressButton);
        Main.buttonPanel.add(Main.exitButton);
    }

    public static JPanel createContentPane(ActionListener listener) {
        PanelContainer container = createBasePanel();
        createTitlePanel(container.mainPanel);
        createScorePanel(container.mainPanel);
        createButtonPanel(container.mainPanel, listener);
        return container.totalGUI;
    }
} 