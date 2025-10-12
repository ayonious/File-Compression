package prog.compression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import prog.huffman.HuffmanCompressor;
import prog.huffman.HuffmanDecompressor;
import prog.lzw.LzwCompressor;
import prog.lzw.LzwDecompressor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Compressor and Decompressor interfaces.
 * Verifies that all implementations follow the interface contract.
 */
class CompressorDecompressorInterfaceTest {
    @TempDir
    Path tempDir;
    private File testFile;

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("test.txt").toFile();
        Files.writeString(testFile.toPath(), "Test content for compression");
    }

    @Test
    void testHuffmanCompressorImplementsInterface() {
        Compressor compressor = new HuffmanCompressor(testFile.getAbsolutePath());
        assertNotNull(compressor);
        assertTrue(compressor instanceof HuffmanCompressor);
    }

    @Test
    void testLzwCompressorImplementsInterface() {
        Compressor compressor = new LzwCompressor(testFile.getAbsolutePath());
        assertNotNull(compressor);
        assertTrue(compressor instanceof LzwCompressor);
    }

    @Test
    void testHuffmanDecompressorImplementsInterface() throws IOException {
        // First create a compressed file
        HuffmanCompressor compressor = new HuffmanCompressor(testFile.getAbsolutePath());
        compressor.compress();
        compressor.cleanup();

        File compressedFile = new File(testFile.getAbsolutePath() + ".huffz");
        assertTrue(compressedFile.exists());

        Decompressor decompressor = new HuffmanDecompressor(compressedFile.getAbsolutePath());
        assertNotNull(decompressor);
        assertTrue(decompressor instanceof HuffmanDecompressor);
    }

    @Test
    void testLzwDecompressorImplementsInterface() throws IOException {
        // First create a compressed file
        LzwCompressor compressor = new LzwCompressor(testFile.getAbsolutePath());
        compressor.compress();

        File compressedFile = new File(testFile.getAbsolutePath() + ".LmZWp");
        assertTrue(compressedFile.exists());

        Decompressor decompressor = new LzwDecompressor(compressedFile.getAbsolutePath());
        assertNotNull(decompressor);
        assertTrue(decompressor instanceof LzwDecompressor);
    }

    @Test
    void testCompressorPolymorphism() throws IOException {
        // Test that we can use Compressor interface polymorphically
        Compressor[] compressors = {
            new HuffmanCompressor(testFile.getAbsolutePath()),
            new LzwCompressor(testFile.getAbsolutePath())
        };

        for (Compressor compressor : compressors) {
            // Each compressor should work through the interface
            assertDoesNotThrow(() -> compressor.compress());
        }
    }


    @Test
    void testCleanupMethod() throws IOException {
        // HuffmanCompressor has cleanup, LzwCompressor uses default (no-op)
        Compressor huffmanCompressor = new HuffmanCompressor(testFile.getAbsolutePath());
        assertDoesNotThrow(() -> huffmanCompressor.cleanup());

        Compressor lzwCompressor = new LzwCompressor(testFile.getAbsolutePath());
        assertDoesNotThrow(() -> lzwCompressor.cleanup());
    }

    @Test
    void testCompressDecompressCycle() throws IOException {
        String originalContent = "Test content for full cycle";
        Files.writeString(testFile.toPath(), originalContent);

        // Test with Huffman
        Compressor huffmanCompressor = new HuffmanCompressor(testFile.getAbsolutePath());
        huffmanCompressor.compress();
        huffmanCompressor.cleanup();

        File huffmanCompressed = new File(testFile.getAbsolutePath() + ".huffz");
        Files.delete(testFile.toPath());

        Decompressor huffmanDecompressor = new HuffmanDecompressor(huffmanCompressed.getAbsolutePath());
        huffmanDecompressor.decompress();

        assertEquals(originalContent, Files.readString(testFile.toPath()));

        // Reset for LZW test
        Files.writeString(testFile.toPath(), originalContent);
        Files.delete(huffmanCompressed.toPath());

        Compressor lzwCompressor = new LzwCompressor(testFile.getAbsolutePath());
        lzwCompressor.compress();

        File lzwCompressed = new File(testFile.getAbsolutePath() + ".LmZWp");
        Files.delete(testFile.toPath());

        Decompressor lzwDecompressor = new LzwDecompressor(lzwCompressed.getAbsolutePath());
        lzwDecompressor.decompress();

        assertEquals(originalContent, Files.readString(testFile.toPath()));
    }
}
