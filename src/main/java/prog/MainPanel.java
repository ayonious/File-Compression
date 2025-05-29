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
        Main.redLabel = createLabel("Selected File Size: ", 43, 0, 150, 30);
        Main.blueLabel = createLabel("After zip/unzip the file size: ", 10, 30, 170, 30);
        
        Main.titlePanel.add(Main.redLabel);
        Main.titlePanel.add(Main.blueLabel);
    }

    private static void createScorePanel(JPanel mainPanel) {
        Main.scorePanel = new JPanel();
        Main.scorePanel.setLayout(null);
        Main.scorePanel.setLocation(270, 20);
        Main.scorePanel.setSize(120, 60);
        mainPanel.add(Main.scorePanel);

        // Create and add score labels
        Main.redScore = createLabel("", 0, 0, 100, 30);
        Main.blueScore = createLabel("", 0, 30, 100, 30);
        
        Main.scorePanel.add(Main.redScore);
        Main.scorePanel.add(Main.blueScore);
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
        Main.ZH = createButton("ZIP HuffZ", 0, 0, 120, 30, listener);
        Main.UH = createButton("UNZIP HuffZ", 130, 0, 120, 30, listener);
        Main.ZL = createButton("ZIP LmZWp", 260, 0, 120, 30, listener);
        Main.UL = createButton("UNZIP LmZWp", 390, 0, 120, 30, listener);
        Main.EX = createButton("EXIT", 130, 70, 250, 30, listener);

        // Add buttons to panel
        Main.buttonPanel.add(Main.ZH);
        Main.buttonPanel.add(Main.UH);
        Main.buttonPanel.add(Main.ZL);
        Main.buttonPanel.add(Main.UL);
        Main.buttonPanel.add(Main.EX);
    }

    public static JPanel createContentPane(ActionListener listener) {
        PanelContainer container = createBasePanel();
        createTitlePanel(container.mainPanel);
        createScorePanel(container.mainPanel);
        createButtonPanel(container.mainPanel, listener);
        return container.totalGUI;
    }
} 