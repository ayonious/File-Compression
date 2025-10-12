package prog.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import prog.util.Constants;

/**
 * Main window frame for the File Compressor application.
 * Handles window creation, menu bar, and file operations.
 */
public class MainWindow {

    /**
     * Creates the main application frame
     */
    private static JFrame createMainFrame() {
        JFrame frame = new JFrame("File Compressor - Huffman & LZW");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(200, 100, 700, 550);
        return frame;
    }

    /**
     * Creates a menu item with an action listener
     */
    private static JMenuItem createMenuItem(String text, ActionListener listener) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(listener);
        return item;
    }

    /**
     * Creates the File menu
     */
    public static JMenu createFileMenu(ActionListener openListener) {
        JMenu fileMenu = new JMenu("File");

        // Open item
        fileMenu.add(createMenuItem("Open", openListener));

        // Exit item
        fileMenu.add(createMenuItem("Exit", e -> System.exit(0)));

        return fileMenu;
    }

    /**
     * Creates the Help menu
     */
    public static JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");

        // How To item
        helpMenu.add(createMenuItem("How To", e -> showHowToDialog()));

        // About item
        helpMenu.add(createMenuItem("About", e -> showAboutDialog()));

        return helpMenu;
    }

    /**
     * Shows the "How To" dialog with usage instructions
     */
    private static void showHowToDialog() {
        String message = "<html><body style='width: 400px; padding: 10px;'>" +
                "<h2>How to Use File Compressor</h2>" +
                "<p>This application supports two compression algorithms:</p>" +
                "<ol>" +
                "<li><b>Huffman Coding</b> - Best for text files with uneven character distribution</li>" +
                "<li><b>LZW (Lempel-Ziv-Welch)</b> - Best for files with repetitive patterns</li>" +
                "</ol>" +
                "<h3>To Compress a File:</h3>" +
                "<ol>" +
                "<li>Click the <b>Browse</b> button to select a file</li>" +
                "<li>Choose an algorithm (Huffman or LZW)</li>" +
                "<li>Click the <b>Compress</b> button</li>" +
                "<li>The compressed file will be created in the same folder:<br>" +
                "   • Huffman: <code>" + Constants.HUFFMAN_FILE_EXTENSION + "</code> extension<br>" +
                "   • LZW: <code>" + Constants.LZW_FILE_EXTENSION + "</code> extension</li>" +
                "</ol>" +
                "<h3>To Decompress a File:</h3>" +
                "<ol>" +
                "<li>Click the <b>Browse</b> button to select the compressed file</li>" +
                "<li>Choose the <b>same algorithm</b> that was used for compression</li>" +
                "<li>Click the <b>Decompress</b> button</li>" +
                "<li>The original file will be restored</li>" +
                "</ol>" +
                "<p><b>Important:</b> Always use the same algorithm for compression and decompression!</p>" +
                "</body></html>";

        JOptionPane.showMessageDialog(null,
                new JLabel(message),
                "How To Use File Compressor",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows the "About" dialog with application information
     */
    private static void showAboutDialog() {
        String message = "<html><body style='width: 350px; padding: 10px;'>" +
                "<h2>File Compressor</h2>" +
                "<p><b>Version:</b> 2.0</p>" +
                "<p>A Java-based file compression application implementing two classic compression algorithms:</p>" +
                "<ul>" +
                "<li><b>Huffman Coding</b> - Frequency-based compression</li>" +
                "<li><b>LZW</b> - Dictionary-based compression</li>" +
                "</ul>" +
                "<p>This software was developed as a term project for Level 2, Term 1, " +
                "Department of CSE, BUET.</p>" +
                "<p><b>Developer:</b> Nahiyan Kamal (Student ID: 0805006)</p>" +
                "<p><b>Supervisor:</b> Jesun Shahariar, Department of CSE, BUET</p>" +
                "</body></html>";

        JOptionPane.showMessageDialog(null,
                new JLabel(message),
                "About File Compressor",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Creates the menu bar with File and Help menus
     */
    public static JMenuBar createMenuBar(ActionListener openListener) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu(openListener));
        menuBar.add(createHelpMenu());
        return menuBar;
    }

    /**
     * Creates and shows the main window
     */
    public static JFrame createAndShowGUI(JPanel contentPane, ActionListener openListener) {
        JFrame frame = createMainFrame();
        frame.setContentPane(contentPane);
        frame.setJMenuBar(createMenuBar(openListener));
        frame.setVisible(true);
        return frame;
    }

    /**
     * Initializes the window on the Event Dispatch Thread
     */
    public static void init(JPanel contentPane, ActionListener openListener) {
        SwingUtilities.invokeLater(() -> createAndShowGUI(contentPane, openListener));
    }
}
