package prog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame {
    private static JFrame createMainFrame() {
        JFrame frame = new JFrame("File Compresser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(350, 170, 550, 300);
        return frame;
    }

    private static void setupContentPane(JFrame frame) {
        Main demo = new Main();
        frame.setContentPane(demo.createContentPane());
    }

    private static JMenuItem createMenuItem(String text, ActionListener listener) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(listener);
        return item;
    }

    private static JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");

        // Open item
        fileMenu.add(createMenuItem("Open", new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                handleFileOpen();
            }
        }));

        // Exit item
        fileMenu.add(createMenuItem("Exit", e -> System.exit(0)));

        return fileMenu;
    }

    private static void handleFileOpen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            Main.opened_file = fileChooser.getSelectedFile();
            Main.past = Main.opened_file.length();
            Main.redScore.setText(Main.past + "Bytes");
            Main.blueScore.setText("NotYetCalculated");
        }
    }

    private static JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");

        // How To item
        helpMenu.add(createMenuItem("How To", e -> showHowToDialog()));

        // About item
        helpMenu.add(createMenuItem("About", e -> showAboutDialog()));

        return helpMenu;
    }

    private static void showHowToDialog() {
        String message = "Two algorithms are used for file compression\n" +
                "1. Huffmans Codes\n" +
                "2. Lampel-Ziv-Welch\n" +
                "To compress a file first open the file then click\n" +
                "ZIP HuffZ to zip the file using Huffmans algo.A zipped\n" +
                "file with extension .huffz will be created in the same\n" +
                "folder. This is the zipped file. During unzipping just open\n" +
                "this file and click UNZIP HuffZ button.\n\n" +
                "Same will happen for LZW. Zipped file's extension will be .LmZWp\n" +
                "for LZW. Always make sure that during unzipping you must use\n" +
                "the same algorithm that you used for zipping ";
        JOptionPane.showMessageDialog(null, message, "How To...", JOptionPane.PLAIN_MESSAGE);
    }

    private static void showAboutDialog() {
        String message = "File Compresser is a software to zip and unzip files\n" +
                "It is developed on JAVA, as a term project of\n" +
                " Level - 2, Term - 1,Dept. of CSE,BUET.\n" +
                "It is completed by Nahiyan Kamal (St. ID 0805006) under\n" +
                "the supervision of Jesun Shahariar of Dept. of CSE, BUET.";
        JOptionPane.showMessageDialog(null, message, "About", JOptionPane.PLAIN_MESSAGE);
    }

    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createHelpMenu());
        return menuBar;
    }

    public static JFrame createAndShowGUI() {
        JFrame frame = createMainFrame();
        setupContentPane(frame);
        frame.setJMenuBar(createMenuBar());
        frame.setVisible(true);
        return frame;
    }

    public static void init() {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
} 