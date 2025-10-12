package prog.huffman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;

class HuffmanCompressionIntegrationTest {
    @TempDir
    Path tempDir;
    private File originalFile;
    private File compressedFile;
    private File decompressedFile;
    private static final String TEST_RESOURCE_PATH = "src/test/resources/large_test_file.txt";

    @BeforeEach
    void setUp() throws IOException {
        // Create a copy of the test file in the temp directory
        originalFile = tempDir.resolve("large_test_file.txt").toFile();
        Files.copy(Path.of(TEST_RESOURCE_PATH), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        compressedFile = new File(originalFile.getAbsolutePath() + ".huffz");
        decompressedFile = new File(originalFile.getAbsolutePath());
    }

    @Test
    void testLargeFileCompression() throws IOException {
        // Store original file size and content
        long originalSize = Files.size(originalFile.toPath());
        String originalContent = Files.readString(originalFile.toPath());

        // Compress the file
        HuffmanCompressor compressor = new HuffmanCompressor(originalFile.getAbsolutePath());
        compressor.compress();
        compressor.cleanup();
        assertTrue(compressedFile.exists(), "Compressed file should exist");

        // Delete original file to ensure we're reading from decompressed file
        Files.delete(originalFile.toPath());
        assertFalse(originalFile.exists(), "Original file should be deleted");

        // Decompress
        HuffmanDecompressor decompressor = new HuffmanDecompressor(compressedFile.getAbsolutePath());
        decompressor.decompress();
        assertTrue(decompressedFile.exists(), "Decompressed file should exist");

        // Compare contents
        String decompressedContent = Files.readString(decompressedFile.toPath());
        assertEquals(originalContent, decompressedContent, "Decompressed content should match original");
        
        // Compare file sizes
        long decompressedSize = Files.size(decompressedFile.toPath());
        assertEquals(originalSize, decompressedSize, "Decompressed file size should match original");
    }

    @Test
    void testSimpleText() throws IOException {
        // Write simple content
        Files.writeString(originalFile.toPath(), "Hello, World!");
        testCompressionCycle(originalFile);
    }

    @Test
    void testEmptyFile() throws IOException {
        // Write empty content
        Files.writeString(originalFile.toPath(), "");

        // Should throw exception for empty file
        assertThrows(IllegalArgumentException.class, () ->
            new HuffmanCompressor(originalFile.getAbsolutePath()),
            "Should throw IllegalArgumentException for empty file"
        );
    }

    @Test
    void testSpecialCharacters() throws IOException {
        // Write content with special characters
        Files.writeString(originalFile.toPath(), "!@#$%^&*()_+{}[]|\\:;\"'<>,.?/~`\n\t");
        testCompressionCycle(originalFile);
    }

    private void testCompressionCycle(File testFile) throws IOException {
        long originalSize = Files.size(testFile.toPath());
        String originalContent = Files.readString(testFile.toPath());

        // Compress
        HuffmanCompressor compressor = new HuffmanCompressor(testFile.getAbsolutePath());
        compressor.compress();
        compressor.cleanup();
        assertTrue(compressedFile.exists(), "Compressed file should exist");

        // Delete original file
        Files.delete(testFile.toPath());
        assertFalse(testFile.exists(), "Original file should be deleted");

        // Decompress
        HuffmanDecompressor decompressor = new HuffmanDecompressor(compressedFile.getAbsolutePath());
        decompressor.decompress();
        assertTrue(decompressedFile.exists(), "Decompressed file should exist");

        // Compare contents
        String decompressedContent = Files.readString(decompressedFile.toPath());
        assertEquals(originalContent, decompressedContent, "Decompressed content should match original");
        assertEquals(originalSize, Files.size(decompressedFile.toPath()), "File sizes should match");
    }
} 