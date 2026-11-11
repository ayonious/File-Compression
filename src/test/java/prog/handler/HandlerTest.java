package prog.handler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import prog.util.Constants;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/** Handlers end with a JOptionPane, which throws HeadlessException in headless mode (as in CI). */
class HandlerTest {
    private static final String CONTENT = "hello hello hello compression world";

    @TempDir
    Path tempDir;

    @BeforeAll
    static void headless() {
        System.setProperty("java.awt.headless", "true");
        // a real display would block on the dialogs
        assumeTrue(GraphicsEnvironment.isHeadless());
    }

    private File input() throws Exception {
        Path p = tempDir.resolve("in.txt");
        Files.writeString(p, CONTENT);
        return p.toFile();
    }

    @Test
    void huffmanRoundTripRunsBeforeDialog() throws Exception {
        File in = input();
        assertThrows(HeadlessException.class, () -> new CompressionHandler().compressWithHuffman(in));
        File compressed = new File(in.getPath() + Constants.HUFFMAN_FILE_EXTENSION);
        assertTrue(compressed.exists());

        Files.delete(in.toPath());
        assertThrows(HeadlessException.class, () -> new DecompressionHandler().decompressHuffman(compressed));
        assertEquals(CONTENT, Files.readString(in.toPath()));
    }

    @Test
    void lzwRoundTripRunsBeforeDialog() throws Exception {
        File in = input();
        assertThrows(HeadlessException.class, () -> new CompressionHandler().compressWithLZW(in));
        File compressed = new File(in.getPath() + Constants.LZW_FILE_EXTENSION);
        assertTrue(compressed.exists());

        Files.delete(in.toPath());
        assertThrows(HeadlessException.class, () -> new DecompressionHandler().decompressLZW(compressed));
        assertEquals(CONTENT, Files.readString(in.toPath()));
    }

    @Test
    void missingFileHitsErrorPath() {
        File missing = tempDir.resolve("missing.txt").toFile();
        File missingHuff = tempDir.resolve("missing.txt" + Constants.HUFFMAN_FILE_EXTENSION).toFile();
        File missingLzw = tempDir.resolve("missing.txt" + Constants.LZW_FILE_EXTENSION).toFile();
        assertThrows(HeadlessException.class, () -> new CompressionHandler().compressWithHuffman(missing));
        assertThrows(HeadlessException.class, () -> new CompressionHandler().compressWithLZW(missing));
        assertThrows(HeadlessException.class, () -> new DecompressionHandler().decompressHuffman(missingHuff));
        assertThrows(HeadlessException.class, () -> new DecompressionHandler().decompressLZW(missingLzw));
    }

    @Test
    void compressionRatio() {
        assertEquals(75.0, new CompressionHandler().calculateCompressionRatio(100, 25), 1e-9);
    }

    @Test
    void fileOperationHandler() {
        FileOperationHandler h = new FileOperationHandler();
        assertFalse(h.isFileSelected());
        File f = new File("x.txt");
        h.setSelectedFile(f);
        h.setOutputFile(f);
        assertTrue(h.isFileSelected());
        assertSame(f, h.getSelectedFile());
        assertSame(f, h.getOutputFile());

        assertEquals("512 B", h.formatFileSize(512));
        assertEquals(String.format("%.2f KB", 1.5), h.formatFileSize(1536));
        assertEquals(String.format("%.2f MB", 2.0), h.formatFileSize(2L * 1024 * 1024));
        assertEquals(String.format("%.2f GB", 3.0), h.formatFileSize(3L * 1024 * 1024 * 1024));

        assertThrows(HeadlessException.class, h::browseForFile);
        assertThrows(HeadlessException.class, h::showNoFileSelectedWarning);
    }
}
