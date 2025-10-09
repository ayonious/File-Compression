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

class HuffmanCompressorTest {
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

        HuffmanCompressor.beginHuffmanCompression(inputFile.getAbsolutePath());
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

        HuffmanCompressor.beginHuffmanCompression(inputFile.getAbsolutePath());
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

        HuffmanCompressor.beginHuffmanCompression(inputFile.getAbsolutePath());
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

        HuffmanCompressor.initHuffmanCompressor();
        HuffmanCompressor.calculateFrequency(inputFile.getAbsolutePath());

        // Check frequencies
        assertEquals(2, HuffmanCompressor.frequency['a']);
        assertEquals(2, HuffmanCompressor.frequency['b']);
        assertEquals(1, HuffmanCompressor.frequency['c']);
    }

    @Test
    void testByteConversion() {
        // Test positive byte
        assertEquals(65, HuffmanCompressor.to((byte) 65)); // 'A'
        // Test negative byte
        assertEquals(128, HuffmanCompressor.to((byte) -128));
    }

    @Test
    void testTreeConstruction() throws IOException {
        // Create test file
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("aabbc");
        }

        HuffmanCompressor.initHuffmanCompressor();
        HuffmanCompressor.calculateFrequency(inputFile.getAbsolutePath());
        HuffmanCompressor.buildHuffmanTree();

        // Verify tree construction
        assertNotNull(HuffmanCompressor.root);
        assertEquals(5, HuffmanCompressor.root.frequency); // Total frequency should be 5
    }

    @Test
    void testInitialization() {
        HuffmanCompressor.initHuffmanCompressor();
        
        // Check if frequencies are reset
        assertTrue(Arrays.stream(HuffmanCompressor.frequency).allMatch(freq -> freq == 0));
        // Check if string array is reset
        assertTrue(Arrays.stream(HuffmanCompressor.huffmanCodes).allMatch(s -> s.equals("")));
        // Check if priority queue is empty
        assertTrue(HuffmanCompressor.priorityQueue.isEmpty());
    }
} 