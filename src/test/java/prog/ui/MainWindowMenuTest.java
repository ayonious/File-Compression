package prog.ui;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class MainWindowMenuTest {
    @BeforeAll
    static void headless() {
        System.setProperty("java.awt.headless", "true");
        // a real display would block on the Help dialogs
        assumeTrue(GraphicsEnvironment.isHeadless());
    }

    @Test
    void menuBarHasFileAndHelp() {
        AtomicBoolean opened = new AtomicBoolean();
        JMenuBar bar = MainWindow.createMenuBar(e -> opened.set(true));
        assertEquals(2, bar.getMenuCount());

        JMenu file = bar.getMenu(0);
        assertEquals("File", file.getText());
        assertEquals("Open", file.getItem(0).getText());
        assertEquals("Exit", file.getItem(1).getText());
        file.getItem(0).doClick();
        assertTrue(opened.get());

        JMenu help = bar.getMenu(1);
        assertEquals("How To", help.getItem(0).getText());
        assertEquals("About", help.getItem(1).getText());
        // dialogs throw in headless mode, after building their message
        assertThrows(HeadlessException.class, () -> help.getItem(0).doClick());
        assertThrows(HeadlessException.class, () -> help.getItem(1).doClick());
    }

    @Test
    void createAndShowGuiNeedsDisplay() {
        assertThrows(HeadlessException.class, () -> MainWindow.createAndShowGUI(new javax.swing.JPanel(), e -> {}));
    }
}
