package prog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import prog.ui.FileCompressorUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;

class MainPanelTest {
    private Main mainApp;
    private JPanel contentPane;
    private ActionListener mockListener;

    @BeforeEach
    void setUp() {
        mockListener = e -> {};  // Empty action listener for testing
        mainApp = new Main();
        contentPane = mainApp.createContentPane();
    }

    @Test
    void testPanelCreation() {
        assertNotNull(contentPane, "Content pane should be created");
        assertTrue(contentPane.isOpaque(), "Panel should be opaque");
        assertNotNull(contentPane.getLayout(), "Panel should have a layout");
        assertTrue(contentPane.getLayout() instanceof BorderLayout, "Panel should have BorderLayout");
    }

    @Test
    void testPanelHierarchy() {
        assertTrue(contentPane.getComponentCount() > 0, "Content pane should have components");

        // Check that major sections are present
        boolean hasComponents = false;
        for (Component comp : contentPane.getComponents()) {
            if (comp instanceof JPanel) {
                hasComponents = true;
                break;
            }
        }
        assertTrue(hasComponents, "Content pane should contain JPanels");
    }

    @Test
    void testFileSelectionComponents() {
        assertNotNull(mainApp.getFilePathField(), "File path field should be created");
        assertFalse(mainApp.getFilePathField().isEditable(), "File path field should not be editable");
        assertEquals("No file selected", mainApp.getFilePathField().getText(), "Initial file path text should be 'No file selected'");
    }

    @Test
    void testFileSizeLabels() {
        assertNotNull(mainApp.getOriginalSizeValue(), "Original size value label should be created");
        assertNotNull(mainApp.getCompressedSizeValue(), "Compressed size value label should be created");

        assertEquals("—", mainApp.getOriginalSizeValue().getText(), "Original size should show '—' initially");
        assertEquals("—", mainApp.getCompressedSizeValue().getText(), "Compressed size should show '—' initially");
    }

    @Test
    void testCompressionButtons() {
        // Huffman buttons
        assertNotNull(mainApp.getHuffmanCompressButton(), "Huffman compress button should be created");
        assertNotNull(mainApp.getHuffmanDecompressButton(), "Huffman decompress button should be created");
        assertTrue(mainApp.getHuffmanCompressButton().getText().contains("Compress"), "Huffman compress button should have Compress in text");
        assertTrue(mainApp.getHuffmanDecompressButton().getText().contains("Decompress"), "Huffman decompress button should have Decompress in text");

        // LZW buttons
        assertNotNull(mainApp.getLzwCompressButton(), "LZW compress button should be created");
        assertNotNull(mainApp.getLzwDecompressButton(), "LZW decompress button should be created");
        assertTrue(mainApp.getLzwCompressButton().getText().contains("Compress"), "LZW compress button should have Compress in text");
        assertTrue(mainApp.getLzwDecompressButton().getText().contains("Decompress"), "LZW decompress button should have Decompress in text");
    }

    @Test
    void testExitButton() {
        assertNotNull(mainApp.getExitButton(), "Exit button should be created");
        assertTrue(mainApp.getExitButton().getText().contains("Exit"), "Exit button should have Exit in text");
    }

    @Test
    void testStatusLabel() {
        assertNotNull(mainApp.getStatusLabel(), "Status label should be created");
        assertEquals("Ready", mainApp.getStatusLabel().getText(), "Status label should show 'Ready' initially");
        assertEquals(SwingConstants.CENTER, mainApp.getStatusLabel().getHorizontalAlignment(), "Status label should be center-aligned");
    }

    @Test
    void testButtonActionListeners() {
        // Test that all buttons have action listeners
        assertTrue(mainApp.getHuffmanCompressButton().getActionListeners().length > 0,
                  "Huffman compress button should have action listener");
        assertTrue(mainApp.getHuffmanDecompressButton().getActionListeners().length > 0,
                  "Huffman decompress button should have action listener");
        assertTrue(mainApp.getLzwCompressButton().getActionListeners().length > 0,
                  "LZW compress button should have action listener");
        assertTrue(mainApp.getLzwDecompressButton().getActionListeners().length > 0,
                  "LZW decompress button should have action listener");
        assertTrue(mainApp.getExitButton().getActionListeners().length > 0,
                  "Exit button should have action listener");
    }

    @Test
    void testButtonToolTips() {
        assertNotNull(mainApp.getHuffmanCompressButton().getToolTipText(), "Huffman compress button should have tooltip");
        assertNotNull(mainApp.getHuffmanDecompressButton().getToolTipText(), "Huffman decompress button should have tooltip");
        assertNotNull(mainApp.getLzwCompressButton().getToolTipText(), "LZW compress button should have tooltip");
        assertNotNull(mainApp.getLzwDecompressButton().getToolTipText(), "LZW decompress button should have tooltip");
        assertNotNull(mainApp.getExitButton().getToolTipText(), "Exit button should have tooltip");
    }
}