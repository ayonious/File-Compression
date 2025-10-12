package prog.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Panel for file selection with browse button.
 */
public class FileSelectionPanel extends JPanel {
    private final JTextField filePathField;
    private final JButton browseButton;

    public FileSelectionPanel(ActionListener browseListener) {
        super(new BorderLayout(10, 10));

        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Select File",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        // File path display
        filePathField = new JTextField("No file selected");
        filePathField.setEditable(false);
        filePathField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        filePathField.setBackground(Color.WHITE);

        // Browse button
        browseButton = new JButton("📁 Browse...");
        browseButton.setFont(new Font("Arial", Font.BOLD, 12));
        browseButton.setPreferredSize(new Dimension(120, 30));
        browseButton.addActionListener(browseListener);
        browseButton.setToolTipText("Click to select a file to compress or decompress");

        add(filePathField, BorderLayout.CENTER);
        add(browseButton, BorderLayout.EAST);
    }

    public JTextField getFilePathField() {
        return filePathField;
    }

    public JButton getBrowseButton() {
        return browseButton;
    }
}
