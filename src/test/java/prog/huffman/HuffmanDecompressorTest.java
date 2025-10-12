package prog.huffman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HuffmanDecompressorTest {
    @TempDir
    Path tempDir;
    private File inputFile;
    private File compressedFile;
    private File decompressedFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create temporary files for testing
        inputFile = tempDir.resolve("testInput.txt").toFile();
        compressedFile = new File(inputFile.getAbsolutePath() + ".huffz");
        decompressedFile = new File(inputFile.getAbsolutePath());
    }

    @Test
    void testEmptyFileUnzipping() throws IOException {
        // Create empty file
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("");
        }

        // Should throw exception for empty file compression
        assertThrows(IllegalArgumentException.class, () ->
            new HuffmanCompressor(inputFile.getAbsolutePath()),
            "Should throw IllegalArgumentException for empty file"
        );

        // Compressed file should not exist
        assertFalse(compressedFile.exists());
    }

    @Test
    void testSingleCharacterUnzipping() throws IOException {
        // Create and compress file with single repeated character
        String content = "aaaa";
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write(content);
        }
        
        HuffmanCompressor compressor = new HuffmanCompressor(inputFile.getAbsolutePath());
        compressor.compress();
        compressor.cleanup();
        assertTrue(compressedFile.exists());
        
        // Test unzipping
        HuffmanDecompressor decompressor = new HuffmanDecompressor(compressedFile.getAbsolutePath());
        decompressor.decompress();
        assertTrue(decompressedFile.exists());
        
        String decompressedContent = Files.readString(decompressedFile.toPath());
        assertEquals(content, decompressedContent);
    }

    @Test
    void testMultipleCharacterUnzipping() throws IOException {
        // Create and compress file with multiple different characters
        String content = "Hello, World! This is a test file.";
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write(content);
        }
        
        HuffmanCompressor compressor = new HuffmanCompressor(inputFile.getAbsolutePath());
        compressor.compress();
        compressor.cleanup();
        assertTrue(compressedFile.exists());
        
        // Test unzipping
        HuffmanDecompressor decompressor = new HuffmanDecompressor(compressedFile.getAbsolutePath());
        decompressor.decompress();
        assertTrue(decompressedFile.exists());
        
        String decompressedContent = Files.readString(decompressedFile.toPath());
        assertEquals(content, decompressedContent);
    }

    @Test
    void testLargeFileUnzipping() throws IOException {
        // Create and compress a larger file with repeated patterns
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            content.append("This is a test line ").append(i).append("\n");
        }
        
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write(content.toString());
        }
        
        HuffmanCompressor compressor = new HuffmanCompressor(inputFile.getAbsolutePath());
        compressor.compress();
        compressor.cleanup();
        assertTrue(compressedFile.exists());
        
        // Test unzipping
        HuffmanDecompressor decompressor = new HuffmanDecompressor(compressedFile.getAbsolutePath());
        decompressor.decompress();
        assertTrue(decompressedFile.exists());
        
        String decompressedContent = Files.readString(decompressedFile.toPath());
        assertEquals(content.toString(), decompressedContent);
    }

    @Test
    void testSpecialCharactersUnzipping() throws IOException {
        // Create and compress file with special characters
        String content = "!@#$%^&*()_+{}[]|\\:;\"'<>,.?/~`";
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write(content);
        }
        
        HuffmanCompressor compressor = new HuffmanCompressor(inputFile.getAbsolutePath());
        compressor.compress();
        compressor.cleanup();
        assertTrue(compressedFile.exists());
        
        // Test unzipping
        HuffmanDecompressor decompressor = new HuffmanDecompressor(compressedFile.getAbsolutePath());
        decompressor.decompress();
        assertTrue(decompressedFile.exists());
        
        String decompressedContent = Files.readString(decompressedFile.toPath());
        assertEquals(content, decompressedContent);
    }
} 