package prog.huffman;

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

class HzippingTest {
    @TempDir
    Path tempDir;
    private File inputFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary input file for testing
        inputFile = tempDir.resolve("test_input.txt").toFile();
    }

    @Test
    void testEmptyFile() throws IOException {
        // Create empty file
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("");
        }

        Hzipping.beginHzipping(inputFile.getAbsolutePath());
        File compressedFile = new File(inputFile.getAbsolutePath() + ".huffz");

        assertTrue(compressedFile.exists());
        assertTrue(compressedFile.length() > 0); // Even empty files will have header information
    }

    @Test
    void testSingleCharacterFile() throws IOException {
        // Create file with single character repeated
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("aaaa");
        }

        Hzipping.beginHzipping(inputFile.getAbsolutePath());
        File compressedFile = new File(inputFile.getAbsolutePath() + ".huffz");

        assertTrue(compressedFile.exists());
        assertTrue(compressedFile.length() > 0); // Verify file is created with content
    }

    @Test
    void testMultipleCharacterFile() throws IOException {
        // Create file with multiple different characters
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("Hello, World! This is a test file with multiple characters.");
        }

        Hzipping.beginHzipping(inputFile.getAbsolutePath());
        File compressedFile = new File(inputFile.getAbsolutePath() + ".huffz");

        assertTrue(compressedFile.exists());
        // For small files with varied content, compressed size might be larger due to header overhead
        assertTrue(compressedFile.length() > 0);
    }

    @Test
    void testFrequencyCalculation() throws IOException {
        // Create test file
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("aabbc");
        }

        Hzipping.initHzipping();
        Hzipping.CalFreq(inputFile.getAbsolutePath());

        // Check frequencies
        assertEquals(2, Hzipping.freq['a']);
        assertEquals(2, Hzipping.freq['b']);
        assertEquals(1, Hzipping.freq['c']);
    }

    @Test
    void testByteConversion() {
        // Test positive byte
        assertEquals(65, Hzipping.to((byte) 65)); // 'A'
        // Test negative byte
        assertEquals(128, Hzipping.to((byte) -128));
    }

    @Test
    void testTreeConstruction() throws IOException {
        // Create test file
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("aabbc");
        }

        Hzipping.initHzipping();
        Hzipping.CalFreq(inputFile.getAbsolutePath());
        Hzipping.MakeNode();

        // Verify tree construction
        assertNotNull(Hzipping.Root);
        assertEquals(5, Hzipping.Root.Freqnc); // Total frequency should be 5
    }

    @Test
    void testInitialization() {
        Hzipping.initHzipping();
        
        // Check if frequencies are reset
        assertTrue(Arrays.stream(Hzipping.freq).allMatch(freq -> freq == 0));
        // Check if string array is reset
        assertTrue(Arrays.stream(Hzipping.ss).allMatch(s -> s.equals("")));
        // Check if priority queue is empty
        assertTrue(Hzipping.pq.isEmpty());
    }
} 