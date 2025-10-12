package prog.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Panel for a single compression/decompression algorithm with compress and decompress buttons.
 */
public class AlgorithmPanel extends JPanel {
    private final JButton compressButton;
    private final JButton decompressButton;

    /**
     * Creates an algorithm panel with compress and decompress buttons.
     *
     * @param algorithmName Name of the algorithm (e.g., "Huffman Coding")
     * @param description Brief description of the algorithm's use case
     * @param compressText Text for the compress button
     * @param compressColor Background color for the compress button
     * @param compressTooltip Tooltip for the compress button
     * @param decompressText Text for the decompress button
     * @param decompressColor Background color for the decompress button
     * @param decompressTooltip Tooltip for the decompress button
     * @param listener Action listener for button clicks
     */
    public AlgorithmPanel(String algorithmName, String description,
                          String compressText, Color compressColor, String compressTooltip,
                          String decompressText, Color decompressColor, String decompressTooltip,
                          ActionListener listener) {
        super(new BorderLayout(5, 5));

        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            algorithmName,
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 13)
        ));

        // Description label
        JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
        descLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        descLabel.setForeground(Color.DARK_GRAY);

        // Create buttons
        compressButton = ButtonFactory.createStyledButton(compressText, compressColor, listener);
        compressButton.setToolTipText(compressTooltip);

        decompressButton = ButtonFactory.createStyledButton(decompressText, decompressColor, listener);
        decompressButton.setToolTipText(decompressTooltip);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        buttonPanel.add(compressButton);
        buttonPanel.add(decompressButton);

        add(descLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    public JButton getCompressButton() {
        return compressButton;
    }

    public JButton getDecompressButton() {
        return decompressButton;
    }
}
