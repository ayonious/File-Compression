package prog;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class MainActionTest {
    @BeforeAll
    static void headless() {
        System.setProperty("java.awt.headless", "true");
        // a real display would block on the warning dialog
        assumeTrue(GraphicsEnvironment.isHeadless());
    }

    @Test
    void everyActionWithoutFileShowsWarning() {
        Main main = new Main();
        main.createContentPane();
        assertNull(main.getOpenedFile());
        for (JButton b : new JButton[]{main.getHuffmanCompressButton(), main.getHuffmanDecompressButton(),
                main.getLzwCompressButton(), main.getLzwDecompressButton()}) {
            ActionEvent ev = new ActionEvent(b, ActionEvent.ACTION_PERFORMED, "");
            assertThrows(HeadlessException.class, () -> main.actionPerformed(ev));
        }
        // unknown source is ignored
        main.actionPerformed(new ActionEvent(new Object(), ActionEvent.ACTION_PERFORMED, ""));
    }
}
