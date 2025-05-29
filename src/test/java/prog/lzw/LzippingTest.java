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

class LzippingTest {
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
    void testByteToInteger() {
        // Test positive byte values
        assertEquals(65, Lzipping.btoi((byte) 65));  // 'A'
        assertEquals(90, Lzipping.btoi((byte) 90));  // 'Z'
        assertEquals(48, Lzipping.btoi((byte) 48));  // '0'
        
        // Test negative byte values (should convert to positive values 128-255)
        assertEquals(128, Lzipping.btoi((byte) -128));
        assertEquals(255, Lzipping.btoi((byte) -1));
    }

    @Test
    void testFillBinaryString() {
        // Test with btsz = 8
        Lzipping.btsz = 8;
        assertEquals("00000000", Lzipping.fil(0));
        assertEquals("00000001", Lzipping.fil(1));
        assertEquals("00001010", Lzipping.fil(10));
        assertEquals("11111111", Lzipping.fil(255));

        // Test with btsz = 4
        Lzipping.btsz = 4;
        assertEquals("0000", Lzipping.fil(0));
        assertEquals("0001", Lzipping.fil(1));
        assertEquals("1111", Lzipping.fil(15));
    }

    @Test
    void testStringToByte() {
        assertEquals((byte) 0, Lzipping.strtobt("00000000"));
        assertEquals((byte) 1, Lzipping.strtobt("00000001"));
        assertEquals((byte) -1, Lzipping.strtobt("11111111"));
        assertEquals((byte) 65, Lzipping.strtobt("01000001")); // 'A'
    }

    @Test
    void testEmptyFileCompression() throws IOException {
        // Create empty file
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("");
        }

        Lzipping.beginLzipping(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
        assertTrue(compressedFile.length() > 0); // Should at least contain the btsz value
    }

    @Test
    void testSingleCharacterCompression() throws IOException {
        // Create file with single repeated character
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("aaaa");
        }

        Lzipping.beginLzipping(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
        assertTrue(compressedFile.length() > 0);
    }

    @Test
    void testRepeatingPatternCompression() throws IOException {
        // Create file with repeating pattern
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("abcabcabc");
        }

        Lzipping.beginLzipping(inputFile.getAbsolutePath());
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

        Lzipping.beginLzipping(inputFile.getAbsolutePath());
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

        Lzipping.beginLzipping(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
    }

    @Test
    void testSpecialCharacters() throws IOException {
        // Test with special characters
        String content = "!@#$%^&*()_+{}[]|\\:;\"'<>,.?/~`\n\t";
        
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write(content);
        }

        Lzipping.beginLzipping(inputFile.getAbsolutePath());
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

        Lzipping.beginLzipping(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
    }

    @Test
    void testStateReset() {
        // Test that static variables are properly reset
        Lzipping.btsz = 42;
        Lzipping.big = "test";
        
        Lzipping.beginLzipping(inputFile.getAbsolutePath());
        
        assertEquals(0, Lzipping.btsz);
        assertEquals("", Lzipping.big);
    }
} 