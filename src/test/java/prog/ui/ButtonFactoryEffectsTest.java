package prog.ui;

import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ButtonFactoryEffectsTest {
    @Test
    void hoverAndPressEffects() {
        JButton b = ButtonFactory.createStyledButton("Go", Color.BLUE, e -> {});
        // Swing adds its own listeners; pick the one ButtonFactory added
        MouseListener l = Arrays.stream(b.getMouseListeners())
                .filter(x -> x.getClass().getName().startsWith(ButtonFactory.class.getName()))
                .findFirst().orElseThrow();
        MouseEvent ev = new MouseEvent(b, MouseEvent.MOUSE_ENTERED, 0, 0, 0, 0, 0, false);

        l.mouseEntered(ev);
        assertEquals(Color.BLUE.brighter(), b.getBackground());
        l.mouseExited(ev);
        assertEquals(Color.BLUE, b.getBackground());

        Border before = b.getBorder();
        l.mousePressed(ev);
        assertNotSame(before, b.getBorder());
        Border pressed = b.getBorder();
        l.mouseReleased(ev);
        assertNotSame(pressed, b.getBorder());
    }
}
