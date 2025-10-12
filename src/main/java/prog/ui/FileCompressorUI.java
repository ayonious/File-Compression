package prog.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Main UI panel for the File Compressor application.
 * Provides a modern, intuitive interface for file compression and decompression.
 */
public class FileCompressorUI {

    // UI Components that need to be accessed by the application
    private JTextField filePathField;
    private JLabel originalSizeValue;
    private JLabel compressedSizeValue;
    private JLabel statusLabel;
    private JButton huffmanCompressButton;
    private JButton huffmanDecompressButton;
    private JButton lzwCompressButton;
    private JButton lzwDecompressButton;
    private JButton exitButton;
    private JButton browseButton;

    /**
     * Creates file information display panel
     */
    private JPanel createFileInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Original file size
        JLabel originalLabel = new JLabel("Original Size:");
        originalLabel.setFont(new Font("Arial", Font.BOLD, 12));
        originalSizeValue = new JLabel("—");
        originalSizeValue.setFont(new Font("Arial", Font.PLAIN, 12));

        // Output file size
        JLabel outputLabel = new JLabel("Output Size:");
        outputLabel.setFont(new Font("Arial", Font.BOLD, 12));
        compressedSizeValue = new JLabel("—");
        compressedSizeValue.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(originalLabel);
        panel.add(originalSizeValue);
        panel.add(outputLabel);
        panel.add(compressedSizeValue);

        return panel;
    }

    /**
     * Creates the compression/decompression action buttons panel
     */
    private JPanel createActionButtonsPanel(ActionListener listener) {
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Choose Compression Algorithm",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        // Huffman algorithm panel
        AlgorithmPanel huffmanPanel = new AlgorithmPanel(
            "Huffman Coding",
            "Best for: Text files, source code, logs",
            "🗜️ Compress", new Color(34, 139, 34),
            "Compress file using Huffman algorithm - Best for text files",
            "📂 Decompress", new Color(30, 144, 255),
            "Decompress .huffz file",
            listener
        );
        huffmanCompressButton = huffmanPanel.getCompressButton();
        huffmanDecompressButton = huffmanPanel.getDecompressButton();

        // LZW algorithm panel
        AlgorithmPanel lzwPanel = new AlgorithmPanel(
            "LZW (Lempel-Ziv-Welch)",
            "Best for: Structured data, XML, JSON, logs",
            "🗜️ Compress", new Color(34, 139, 34),
            "Compress file using LZW algorithm - Best for repetitive data",
            "📂 Decompress", new Color(30, 144, 255),
            "Decompress .LmZWp file",
            listener
        );
        lzwCompressButton = lzwPanel.getCompressButton();
        lzwDecompressButton = lzwPanel.getDecompressButton();

        mainPanel.add(huffmanPanel);
        mainPanel.add(lzwPanel);

        return mainPanel;
    }

    /**
     * Main content pane creation with modern layout
     */
    public JPanel createContentPane(ActionListener buttonListener, ActionListener browseListener) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Top section: File selection
        JPanel topSection = new JPanel(new BorderLayout(10, 10));
        topSection.setOpaque(false);

        FileSelectionPanel fileSelectionPanel = new FileSelectionPanel(browseListener);
        filePathField = fileSelectionPanel.getFilePathField();
        browseButton = fileSelectionPanel.getBrowseButton();

        topSection.add(fileSelectionPanel, BorderLayout.CENTER);
        topSection.add(createFileInfoPanel(), BorderLayout.SOUTH);

        // Center section: Action buttons
        JPanel centerSection = createActionButtonsPanel(buttonListener);
        centerSection.setOpaque(false);

        // Bottom section: Status and exit
        JPanel bottomSection = new JPanel(new BorderLayout());
        bottomSection.setOpaque(false);

        statusLabel = new JLabel("Ready", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        statusLabel.setForeground(Color.DARK_GRAY);

        exitButton = new JButton("❌ Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 11));
        exitButton.setPreferredSize(new Dimension(100, 30));
        exitButton.addActionListener(buttonListener);
        exitButton.setToolTipText("Close the application");

        bottomSection.add(statusLabel, BorderLayout.CENTER);
        bottomSection.add(exitButton, BorderLayout.EAST);

        // Add all sections to main panel
        mainPanel.add(topSection, BorderLayout.NORTH);
        mainPanel.add(centerSection, BorderLayout.CENTER);
        mainPanel.add(bottomSection, BorderLayout.SOUTH);

        return mainPanel;
    }

    // Getters for UI components
    public JTextField getFilePathField() { return filePathField; }
    public JLabel getOriginalSizeValue() { return originalSizeValue; }
    public JLabel getCompressedSizeValue() { return compressedSizeValue; }
    public JLabel getStatusLabel() { return statusLabel; }
    public JButton getHuffmanCompressButton() { return huffmanCompressButton; }
    public JButton getHuffmanDecompressButton() { return huffmanDecompressButton; }
    public JButton getLzwCompressButton() { return lzwCompressButton; }
    public JButton getLzwDecompressButton() { return lzwDecompressButton; }
    public JButton getExitButton() { return exitButton; }
    public JButton getBrowseButton() { return browseButton; }
}
