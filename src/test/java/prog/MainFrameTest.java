package prog;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import prog.ui.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class MainFrameTest {
    @TempDir
    Path tempDir;
    private File testFile;
    private JFrame mainFrame;
    private CountDownLatch latch;
    private Main mainApp;

    @BeforeAll
    static void setUpClass() {
        // Set headless mode if not already set
        System.setProperty("java.awt.headless", "true");
        // Initialize toolkit in headless mode
        try {
            Toolkit.getDefaultToolkit();
        } catch (AWTError e) {
            // Ignore AWTError in headless mode
        }
    }

    @Test
    @DisabledIfSystemProperty(named = "java.awt.headless", matches = "true")
    void testFrameInitialization() throws InterruptedException {
        assumeFalse(GraphicsEnvironment.isHeadless());

        latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            mainApp = new Main();
            JPanel contentPane = mainApp.createContentPane();
            mainFrame = MainWindow.createAndShowGUI(contentPane, e -> {});
            latch.countDown();
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Frame initialization timed out");
        assertNotNull(mainFrame, "Main frame should be created");
        assertTrue(mainFrame.isVisible(), "Frame should be visible");
        assertEquals(new Rectangle(200, 100, 700, 550), mainFrame.getBounds(), "Frame bounds should match");

        SwingUtilities.invokeLater(() -> mainFrame.dispose());
    }

    @Test
    @DisabledIfSystemProperty(named = "java.awt.headless", matches = "true")
    void testMenuBarStructure() throws InterruptedException {
        assumeFalse(GraphicsEnvironment.isHeadless());

        latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            mainApp = new Main();
            JPanel contentPane = mainApp.createContentPane();
            mainFrame = MainWindow.createAndShowGUI(contentPane, e -> {});
            latch.countDown();
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Frame initialization timed out");

        JMenuBar menuBar = mainFrame.getJMenuBar();
        assertNotNull(menuBar, "Menu bar should exist");
        assertEquals(2, menuBar.getMenuCount(), "Should have 2 menus");

        // Test File menu
        JMenu fileMenu = menuBar.getMenu(0);
        assertEquals("File", fileMenu.getText(), "First menu should be File");
        assertEquals(2, fileMenu.getItemCount(), "File menu should have 2 items");
        assertEquals("Open", fileMenu.getItem(0).getText(), "First item should be Open");
        assertEquals("Exit", fileMenu.getItem(1).getText(), "Second item should be Exit");

        // Test Help menu
        JMenu helpMenu = menuBar.getMenu(1);
        assertEquals("Help", helpMenu.getText(), "Second menu should be Help");
        assertEquals(2, helpMenu.getItemCount(), "Help menu should have 2 items");
        assertEquals("How To", helpMenu.getItem(0).getText(), "First item should be How To");
        assertEquals("About", helpMenu.getItem(1).getText(), "Second item should be About");

        SwingUtilities.invokeLater(() -> mainFrame.dispose());
    }

    @Test
    @DisabledIfSystemProperty(named = "java.awt.headless", matches = "true")
    void testFileOpenOperation() throws InterruptedException, IOException {
        assumeFalse(GraphicsEnvironment.isHeadless());

        // Create a test file
        testFile = tempDir.resolve("test.txt").toFile();
        Files.writeString(testFile.toPath(), "Test content");

        latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            mainApp = new Main();
            JPanel contentPane = mainApp.createContentPane();
            mainFrame = MainWindow.createAndShowGUI(contentPane, e -> {});
            latch.countDown();
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Frame initialization timed out");

        // Verify file selection components are initialized
        assertNotNull(mainApp.getFilePathField(), "File path field should be initialized");
        assertNotNull(mainApp.getOriginalSizeValue(), "Original size label should be initialized");
        assertNotNull(mainApp.getCompressedSizeValue(), "Compressed size label should be initialized");

        SwingUtilities.invokeLater(() -> mainFrame.dispose());
    }

    @Test
    @DisabledIfSystemProperty(named = "java.awt.headless", matches = "true")
    void testContentPaneSetup() throws InterruptedException {
        assumeFalse(GraphicsEnvironment.isHeadless());

        latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            mainApp = new Main();
            JPanel contentPane = mainApp.createContentPane();
            mainFrame = MainWindow.createAndShowGUI(contentPane, e -> {});
            latch.countDown();
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Frame initialization timed out");

        Container contentPane = mainFrame.getContentPane();
        assertNotNull(contentPane, "Content pane should exist");
        assertTrue(contentPane instanceof JPanel, "Content pane should be a JPanel");
        assertNotNull(contentPane.getLayout(), "Content pane should have a layout");

        SwingUtilities.invokeLater(() -> mainFrame.dispose());
    }

    @Test
    @DisabledIfSystemProperty(named = "java.awt.headless", matches = "true")
    void testFrameDefaultCloseOperation() throws InterruptedException {
        assumeFalse(GraphicsEnvironment.isHeadless());

        latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            mainApp = new Main();
            JPanel contentPane = mainApp.createContentPane();
            mainFrame = MainWindow.createAndShowGUI(contentPane, e -> {});
            latch.countDown();
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Frame initialization timed out");

        assertEquals(JFrame.EXIT_ON_CLOSE, mainFrame.getDefaultCloseOperation(),
            "Frame should exit on close");

        SwingUtilities.invokeLater(() -> mainFrame.dispose());
    }
} 