package prog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;

class MainPanelTest {
    private JPanel panel;
    private ActionListener mockListener;

    @BeforeEach
    void setUp() {
        mockListener = e -> {};  // Empty action listener for testing
        panel = MainPanel.createContentPane(mockListener);
    }

    @Test
    void testPanelCreation() {
        assertNotNull(panel, "Panel should be created");
        assertTrue(panel.isOpaque(), "Panel should be opaque");
        assertNotNull(panel.getLayout(), "Panel should have a layout");
        assertTrue(panel.getLayout() instanceof BorderLayout, "Panel should have BorderLayout");
    }

    @Test
    void testPanelHierarchy() {
        assertEquals(1, panel.getComponentCount(), "Main panel should have one child");
        Component mainPanel = panel.getComponent(0);
        assertTrue(mainPanel instanceof JPanel, "Child should be a JPanel");
        assertNull(((JPanel) mainPanel).getLayout(), "Inner panel should have null layout for absolute positioning");
    }

    @Test
    void testTitlePanelProperties() {
        assertNotNull(Main.titlePanel, "Title panel should be created");
        assertEquals(new Point(90, 20), Main.titlePanel.getLocation(), "Title panel location should match");
        assertEquals(new Dimension(170, 70), Main.titlePanel.getSize(), "Title panel size should match");
        
        // Test labels in title panel
        assertEquals("Selected File Size: ", Main.redLabel.getText(), "Red label text should match");
        assertEquals("After zip/unzip the file size: ", Main.blueLabel.getText(), "Blue label text should match");
        
        // Test label alignment
        assertEquals(SwingConstants.CENTER, Main.redLabel.getHorizontalAlignment(), "Red label should be center-aligned");
        assertEquals(SwingConstants.CENTER, Main.blueLabel.getHorizontalAlignment(), "Blue label should be center-aligned");
    }

    @Test
    void testScorePanelProperties() {
        assertNotNull(Main.scorePanel, "Score panel should be created");
        assertEquals(new Point(270, 20), Main.scorePanel.getLocation(), "Score panel location should match");
        assertEquals(new Dimension(120, 60), Main.scorePanel.getSize(), "Score panel size should match");
        
        // Test score labels
        assertEquals("", Main.redScore.getText(), "Red score should be empty initially");
        assertEquals("", Main.blueScore.getText(), "Blue score should be empty initially");
        
        // Test label alignment
        assertEquals(SwingConstants.CENTER, Main.redScore.getHorizontalAlignment(), "Red score should be center-aligned");
        assertEquals(SwingConstants.CENTER, Main.blueScore.getHorizontalAlignment(), "Blue score should be center-aligned");
    }

    @Test
    void testButtonPanelProperties() {
        assertNotNull(Main.buttonPanel, "Button panel should be created");
        assertEquals(new Point(10, 130), Main.buttonPanel.getLocation(), "Button panel location should match");
        assertEquals(new Dimension(5200, 150), Main.buttonPanel.getSize(), "Button panel size should match");
        assertNull(Main.buttonPanel.getLayout(), "Button panel should have null layout for absolute positioning");
    }

    @Test
    void testButtonProperties() {
        // Test ZIP HuffZ button
        assertEquals("ZIP HuffZ", Main.ZH.getText(), "ZIP HuffZ button text should match");
        assertEquals(new Point(0, 0), Main.ZH.getLocation(), "ZIP HuffZ button location should match");
        assertEquals(new Dimension(120, 30), Main.ZH.getSize(), "ZIP HuffZ button size should match");

        // Test UNZIP HuffZ button
        assertEquals("UNZIP HuffZ", Main.UH.getText(), "UNZIP HuffZ button text should match");
        assertEquals(new Point(130, 0), Main.UH.getLocation(), "UNZIP HuffZ button location should match");
        assertEquals(new Dimension(120, 30), Main.UH.getSize(), "UNZIP HuffZ button size should match");

        // Test ZIP LmZWp button
        assertEquals("ZIP LmZWp", Main.ZL.getText(), "ZIP LmZWp button text should match");
        assertEquals(new Point(260, 0), Main.ZL.getLocation(), "ZIP LmZWp button location should match");
        assertEquals(new Dimension(120, 30), Main.ZL.getSize(), "ZIP LmZWp button size should match");

        // Test UNZIP LmZWp button
        assertEquals("UNZIP LmZWp", Main.UL.getText(), "UNZIP LmZWp button text should match");
        assertEquals(new Point(390, 0), Main.UL.getLocation(), "UNZIP LmZWp button location should match");
        assertEquals(new Dimension(120, 30), Main.UL.getSize(), "UNZIP LmZWp button size should match");

        // Test EXIT button
        assertEquals("EXIT", Main.EX.getText(), "EXIT button text should match");
        assertEquals(new Point(130, 70), Main.EX.getLocation(), "EXIT button location should match");
        assertEquals(new Dimension(250, 30), Main.EX.getSize(), "EXIT button size should match");
    }

    @Test
    void testButtonActionListeners() {
        // Verify that all buttons have action listeners attached
        assertNotNull(Main.ZH.getActionListeners(), "ZIP HuffZ button should have action listener");
        assertNotNull(Main.UH.getActionListeners(), "UNZIP HuffZ button should have action listener");
        assertNotNull(Main.ZL.getActionListeners(), "ZIP LmZWp button should have action listener");
        assertNotNull(Main.UL.getActionListeners(), "UNZIP LmZWp button should have action listener");
        assertNotNull(Main.EX.getActionListeners(), "EXIT button should have action listener");
        
        assertEquals(1, Main.ZH.getActionListeners().length, "ZIP HuffZ button should have exactly one listener");
        assertEquals(1, Main.UH.getActionListeners().length, "UNZIP HuffZ button should have exactly one listener");
        assertEquals(1, Main.ZL.getActionListeners().length, "ZIP LmZWp button should have exactly one listener");
        assertEquals(1, Main.UL.getActionListeners().length, "UNZIP LmZWp button should have exactly one listener");
        assertEquals(1, Main.EX.getActionListeners().length, "EXIT button should have exactly one listener");
        
        // Verify that all buttons have the same listener instance
        ActionListener zhListener = Main.ZH.getActionListeners()[0];
        assertSame(zhListener, Main.UH.getActionListeners()[0], "All buttons should share the same listener");
        assertSame(zhListener, Main.ZL.getActionListeners()[0], "All buttons should share the same listener");
        assertSame(zhListener, Main.UL.getActionListeners()[0], "All buttons should share the same listener");
        assertSame(zhListener, Main.EX.getActionListeners()[0], "All buttons should share the same listener");
    }

    @Test
    void testComponentParentage() {
        assertSame(Main.titlePanel.getParent(), panel.getComponent(0), "Title panel should be child of main panel");
        assertSame(Main.scorePanel.getParent(), panel.getComponent(0), "Score panel should be child of main panel");
        assertSame(Main.buttonPanel.getParent(), panel.getComponent(0), "Button panel should be child of main panel");
    }
} 