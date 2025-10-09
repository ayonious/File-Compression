package prog.lzw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class LzwCompressorTest {
    @TempDir
    Path tempDir;
    private File inputFile;
    private File compressedFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create temporary files for testing
        inputFile = tempDir.resolve("test_input.txt").toFile();
        compressedFile = new File(inputFile.getAbsolutePath() + ".LmZWp");
    }


    @Test
    void testFillBinaryString() {
        // Test with btsz = 8
        LzwCompressor.bitSize = 8;
        assertEquals("00000000", LzwCompressor.intToBinaryString(0));
        assertEquals("00000001", LzwCompressor.intToBinaryString(1));
        assertEquals("00001010", LzwCompressor.intToBinaryString(10));
        assertEquals("11111111", LzwCompressor.intToBinaryString(255));

        // Test with btsz = 4
        LzwCompressor.bitSize = 4;
        assertEquals("0000", LzwCompressor.intToBinaryString(0));
        assertEquals("0001", LzwCompressor.intToBinaryString(1));
        assertEquals("1111", LzwCompressor.intToBinaryString(15));
    }

    @Test
    void testStringToByte() {
        assertEquals((byte) 0, LzwCompressor.stringToByte("00000000"));
        assertEquals((byte) 1, LzwCompressor.stringToByte("00000001"));
        assertEquals((byte) -1, LzwCompressor.stringToByte("11111111"));
        assertEquals((byte) 65, LzwCompressor.stringToByte("01000001")); // 'A'
    }

    @Test
    void testEmptyFileCompression() throws IOException {
        // Create empty file
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("");
        }

        LzwCompressor.beginLzwCompression(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
        assertTrue(compressedFile.length() > 0); // Should at least contain the btsz value
    }

    @Test
    void testSingleCharacterCompression() throws IOException {
        // Create file with single repeated character
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("aaaa");
        }

        LzwCompressor.beginLzwCompression(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
        assertTrue(compressedFile.length() > 0);
    }

    @Test
    void testRepeatingPatternCompression() throws IOException {
        // Create file with repeating pattern
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("abcabcabc");
        }

        LzwCompressor.beginLzwCompression(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
        assertTrue(compressedFile.length() > 0);
        
        // Note: For very small files, the compressed file might be larger due to overhead
        // The compression ratio improves with larger files and more repetitive patterns
    }

    @Test
    void testLargeRepeatingContent() throws IOException {
        // Create a large file with repeating content
        StringBuilder content = new StringBuilder();
        String repeatingPattern = "This is a test string that will be repeated multiple times. ";
        for (int i = 0; i < 100; i++) {
            content.append(repeatingPattern);
        }

        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write(content.toString());
        }

        LzwCompressor.beginLzwCompression(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
        
        // Large repeating content should compress well
        assertTrue(compressedFile.length() < Files.size(inputFile.toPath()));
    }

    @Test
    void testRandomContent() throws IOException {
        // Create file with less compressible content
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            content.append((char) (32 + Math.random() * 95)); // Random printable ASCII
        }

        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write(content.toString());
        }

        LzwCompressor.beginLzwCompression(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
    }

    @Test
    void testSpecialCharacters() throws IOException {
        // Test with special characters
        String content = "!@#$%^&*()_+{}[]|\\:;\"'<>,.?/~`\n\t";
        
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write(content);
        }

        LzwCompressor.beginLzwCompression(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
    }

    @Test
    void testDictionaryLimitHandling() throws IOException {
        // Create a file that will test dictionary size limit (100000)
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 150000; i++) {
            content.append((char) (32 + (i % 95))); // Cycle through printable ASCII
        }

        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write(content.toString());
        }

        LzwCompressor.beginLzwCompression(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
    }

    @Test
    void testStateReset() {
        // Test that static variables are properly reset
        LzwCompressor.bitSize = 42;
        LzwCompressor.bitBuffer = "test";
        
        LzwCompressor.beginLzwCompression(inputFile.getAbsolutePath());
        
        assertEquals(0, LzwCompressor.bitSize);
        assertEquals("", LzwCompressor.bitBuffer);
    }
} 