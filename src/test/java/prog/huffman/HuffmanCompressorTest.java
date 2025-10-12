package prog.huffman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import prog.util.ByteReader;
import prog.util.ByteWriter;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HuffmanCompressorTest {
    @TempDir
    Path tempDir;
    private File inputFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary input file for testing
        inputFile = tempDir.resolve("testInput.txt").toFile();
    }

    @Test
    void testEmptyFile() throws IOException {
        // Create empty file
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("");
        }

        // Should throw exception for empty file
        assertThrows(IllegalArgumentException.class, () ->
            new HuffmanCompressor(inputFile.getAbsolutePath()),
            "Should throw IllegalArgumentException for empty file"
        );
    }

    @Test
    void testSingleCharacterFile() throws IOException {
        // Create file with single character repeated
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("aaaa");
        }

        HuffmanCompressor compressor = new HuffmanCompressor(inputFile.getAbsolutePath());
        compressor.compress();
        File compressedFile = new File(inputFile.getAbsolutePath() + ".huffz");

        assertTrue(compressedFile.exists());
        assertTrue(compressedFile.length() > 0); // Verify file is created with content
        compressor.cleanup();
    }

    @Test
    void testMultipleCharacterFile() throws IOException {
        // Create file with multiple different characters
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("Hello, World! This is a test file with multiple characters.");
        }

        HuffmanCompressor compressor = new HuffmanCompressor(inputFile.getAbsolutePath());
        compressor.compress();
        File compressedFile = new File(inputFile.getAbsolutePath() + ".huffz");

        assertTrue(compressedFile.exists());
        // For small files with varied content, compressed size might be larger due to header overhead
        assertTrue(compressedFile.length() > 0);
        compressor.cleanup();
    }

    @Test
    void testTreeConstruction() throws IOException {
        // Create test file
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("aabbc");
        }

        // Test tree construction using HuffmanUtils directly
        int[] frequency = HuffmanUtils.calculateFrequencyOfBytesInFile(inputFile.getAbsolutePath());
        HuffmanNode root = HuffmanUtils.buildHuffmanTree(frequency);

        // Verify tree construction
        assertNotNull(root);
        assertEquals(5, root.getFrequency()); // Total frequency should be 5
    }

    @Test
    void testWriteTableSize() throws Exception {
        // Create a test file with known content: "ABC" has 3 unique bytes
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("ABC");
        }

        // Use reflection to access private method
        HuffmanCompressor compressor = new HuffmanCompressor(inputFile.getAbsolutePath());
        Method writeTableSize = HuffmanCompressor.class.getDeclaredMethod("writeTableSize");
        writeTableSize.setAccessible(true);

        // Invoke the method (it now uses instance fields calculated from "ABC")
        writeTableSize.invoke(compressor);

        // Read back the result from the output file
        String outputFile = inputFile.getAbsolutePath() + ".huffz";
        ByteReader reader = new ByteReader(outputFile);
        int tableSize = reader.readInt();
        reader.close();

        assertEquals(3, tableSize); // Three unique characters in "ABC"
        compressor.cleanup();
    }

    @Test
    void testWriteFrequencyTable() throws Exception {
        // Create a test file with "AB" - each appears once
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("AB");
        }

        // Use reflection to access private methods
        HuffmanCompressor compressor = new HuffmanCompressor(inputFile.getAbsolutePath());
        Method writeTableSize = HuffmanCompressor.class.getDeclaredMethod("writeTableSize");
        writeTableSize.setAccessible(true);
        Method writeFrequencyTable = HuffmanCompressor.class.getDeclaredMethod("writeFrequencyTable");
        writeFrequencyTable.setAccessible(true);

        // Invoke the methods (they now use instance fields calculated from "AB")
        writeTableSize.invoke(compressor);
        writeFrequencyTable.invoke(compressor);

        // Read back the result from the output file
        String outputFile = inputFile.getAbsolutePath() + ".huffz";
        ByteReader reader = new ByteReader(outputFile);
        reader.readInt(); // Skip table size

        // Read first entry - 'A' appears 1 time
        byte firstByte = reader.readNextByte();
        int firstFreq = reader.readInt();
        assertEquals(65, firstByte);
        assertEquals(1, firstFreq);

        // Read second entry - 'B' appears 1 time
        byte secondByte = reader.readNextByte();
        int secondFreq = reader.readInt();
        assertEquals(66, secondByte);
        assertEquals(1, secondFreq);

        reader.close();
        compressor.cleanup();
    }

    @Test
    void testWriteExtraBits() throws Exception {
        // Create a test file with "AB" - each appears once
        // With equal frequencies, Huffman will create codes like "0" and "1" (1 bit each)
        // Total: 1 bit * 1 + 1 bit * 1 = 2 bits, need 6 padding bits to reach 8
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("AB");
        }

        // Use reflection to access private methods
        HuffmanCompressor compressor = new HuffmanCompressor(inputFile.getAbsolutePath());
        Method writeTableSize = HuffmanCompressor.class.getDeclaredMethod("writeTableSize");
        writeTableSize.setAccessible(true);
        Method writeFrequencyTable = HuffmanCompressor.class.getDeclaredMethod("writeFrequencyTable");
        writeFrequencyTable.setAccessible(true);
        Method writeExtraBits = HuffmanCompressor.class.getDeclaredMethod("writeExtraBits");
        writeExtraBits.setAccessible(true);

        // Invoke the methods (they now use instance fields calculated from "AB")
        writeTableSize.invoke(compressor);
        writeFrequencyTable.invoke(compressor);
        writeExtraBits.invoke(compressor);

        // Read back the result from the output file
        String outputFile = inputFile.getAbsolutePath() + ".huffz";
        ByteReader reader = new ByteReader(outputFile);
        reader.readInt(); // Skip table size
        // Skip frequency table entries
        reader.readNextByte(); // byte 65
        reader.readInt(); // frequency
        reader.readNextByte(); // byte 66
        reader.readInt(); // frequency
        int extraBits = reader.readInt();
        reader.close();

        assertEquals(6, extraBits); // 2 bits data + 6 padding = 8
        compressor.cleanup();
    }

    @Test
    void testWriteExtraBitsWithPadding() throws Exception {
        // Create a test file with single character "A"
        // With only one unique character, Huffman code will be "0" (1 bit)
        // Total: 1 bit * 1 = 1 bit, need 7 padding bits to reach 8
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("A");
        }

        // Use reflection to access private methods
        HuffmanCompressor compressor = new HuffmanCompressor(inputFile.getAbsolutePath());
        Method writeTableSize = HuffmanCompressor.class.getDeclaredMethod("writeTableSize");
        writeTableSize.setAccessible(true);
        Method writeFrequencyTable = HuffmanCompressor.class.getDeclaredMethod("writeFrequencyTable");
        writeFrequencyTable.setAccessible(true);
        Method writeExtraBits = HuffmanCompressor.class.getDeclaredMethod("writeExtraBits");
        writeExtraBits.setAccessible(true);

        // Invoke the methods (they now use instance fields calculated from "A")
        writeTableSize.invoke(compressor);
        writeFrequencyTable.invoke(compressor);
        writeExtraBits.invoke(compressor);

        // Read back the result from the output file
        String outputFile = inputFile.getAbsolutePath() + ".huffz";
        ByteReader reader = new ByteReader(outputFile);
        reader.readInt(); // Skip table size
        // Skip frequency table entry
        reader.readNextByte(); // byte 65
        reader.readInt(); // frequency
        int extraBits = reader.readInt();
        reader.close();

        assertEquals(7, extraBits); // 1 bit data + 7 padding = 8
        compressor.cleanup();
    }
} 