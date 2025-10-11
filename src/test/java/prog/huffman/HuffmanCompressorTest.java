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
    void testTreeConstruction() throws IOException {
        // Create test file
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("aabbc");
        }

        HuffmanCompressor.initHuffmanCompressor(null);
        int[] frequency = HuffmanUtils.calculateFrequencyOfBytesInFile(inputFile.getAbsolutePath());
        String[] huffmanCodes = new String[300];
        HuffmanNode root = HuffmanUtils.buildHuffmanTree(frequency, huffmanCodes);

        // Verify tree construction
        assertNotNull(root);
        assertEquals(5, root.frequency); // Total frequency should be 5
    }

    @Test
    void testWriteTableSize() throws Exception {
        int[] frequency = new int[300];
        frequency[65] = 5; // 'A'
        frequency[66] = 3; // 'B'
        frequency[67] = 2; // 'C'

        // Use reflection to access private method
        Method writeTableSize = HuffmanCompressor.class.getDeclaredMethod("writeTableSize", ByteWriter.class, int[].class);
        writeTableSize.setAccessible(true);

        // Create a temporary file for ByteWriter
        String tempFile = "target/test-temp-tablesize.bin";
        ByteWriter writer = new ByteWriter(tempFile);
        writeTableSize.invoke(null, writer, frequency);
        writer.close();

        // Read back the result
        ByteReader reader = new ByteReader(tempFile);
        int tableSize = reader.readInt();
        reader.close();

        assertEquals(3, tableSize); // Three non-zero frequencies
    }

    @Test
    void testWriteFrequencyTable() throws Exception {
        int[] frequency = new int[300];
        frequency[65] = 5; // 'A'
        frequency[66] = 3; // 'B'

        // Use reflection to access private method
        Method writeFrequencyTable = HuffmanCompressor.class.getDeclaredMethod("writeFrequencyTable", ByteWriter.class, int[].class);
        writeFrequencyTable.setAccessible(true);

        // Create a temporary file for ByteWriter
        String tempFile = "target/test-temp-freqtable.bin";
        ByteWriter writer = new ByteWriter(tempFile);
        writeFrequencyTable.invoke(null, writer, frequency);
        writer.close();

        // Read back the result
        ByteReader reader = new ByteReader(tempFile);

        // Read first entry
        byte firstByte = reader.readNextByte();
        int firstFreq = reader.readInt();
        assertEquals(65, firstByte);
        assertEquals(5, firstFreq);

        // Read second entry
        byte secondByte = reader.readNextByte();
        int secondFreq = reader.readInt();
        assertEquals(66, secondByte);
        assertEquals(3, secondFreq);

        reader.close();
    }

    @Test
    void testWriteExtraBits() throws Exception {
        int[] frequency = new int[300];
        frequency[65] = 2; // 'A'
        frequency[66] = 3; // 'B'

        String[] huffmanCodes = new String[300];
        huffmanCodes[65] = "0";    // 1 bit * 2 = 2 bits
        huffmanCodes[66] = "11";   // 2 bits * 3 = 6 bits
        // Total = 8 bits, which is divisible by 8, so extraBits should be 0

        // Use reflection to access private method
        Method writeExtraBits = HuffmanCompressor.class.getDeclaredMethod("writeExtraBits", ByteWriter.class, int[].class, String[].class);
        writeExtraBits.setAccessible(true);

        // Create a temporary file for ByteWriter
        String tempFile = "target/test-temp-extrabits.bin";
        ByteWriter writer = new ByteWriter(tempFile);
        writeExtraBits.invoke(null, writer, frequency, huffmanCodes);
        writer.close();

        // Read back the result
        ByteReader reader = new ByteReader(tempFile);
        int extraBits = reader.readInt();
        reader.close();

        assertEquals(0, extraBits);
    }

    @Test
    void testWriteExtraBitsWithPadding() throws Exception {
        int[] frequency = new int[300];
        frequency[65] = 3; // 'A'

        String[] huffmanCodes = new String[300];
        huffmanCodes[65] = "0";    // 1 bit * 3 = 3 bits
        // Total = 3 bits, need 5 extra bits to make it 8

        // Use reflection to access private method
        Method writeExtraBits = HuffmanCompressor.class.getDeclaredMethod("writeExtraBits", ByteWriter.class, int[].class, String[].class);
        writeExtraBits.setAccessible(true);

        // Create a temporary file for ByteWriter
        String tempFile = "target/test-temp-extrabits-pad.bin";
        ByteWriter writer = new ByteWriter(tempFile);
        writeExtraBits.invoke(null, writer, frequency, huffmanCodes);
        writer.close();

        // Read back the result
        ByteReader reader = new ByteReader(tempFile);
        int extraBits = reader.readInt();
        reader.close();

        assertEquals(5, extraBits);
    }
} 