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

class HuffmanDecompressorTest {
    @TempDir
    Path tempDir;
    private File inputFile;
    private File compressedFile;
    private File decompressedFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create temporary files for testing
        inputFile = tempDir.resolve("test_input.txt").toFile();
        compressedFile = new File(inputFile.getAbsolutePath() + ".huffz");
        decompressedFile = new File(inputFile.getAbsolutePath());
    }

    @Test
    void testEmptyFileUnzipping() throws IOException {
        // Create and compress empty file
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("");
        }
        
        HuffmanCompressor.beginHuffmanCompression(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
        
        // Test unzipping
        HuffmanDecompressor.beginHuffmanDecompression(compressedFile.getAbsolutePath());
        assertTrue(decompressedFile.exists());
        assertEquals(0, decompressedFile.length());
    }

    @Test
    void testSingleCharacterUnzipping() throws IOException {
        // Create and compress file with single repeated character
        String content = "aaaa";
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write(content);
        }
        
        HuffmanCompressor.beginHuffmanCompression(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
        
        // Test unzipping
        HuffmanDecompressor.beginHuffmanDecompression(compressedFile.getAbsolutePath());
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
        
        HuffmanCompressor.beginHuffmanCompression(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
        
        // Test unzipping
        HuffmanDecompressor.beginHuffmanDecompression(compressedFile.getAbsolutePath());
        assertTrue(decompressedFile.exists());
        
        String decompressedContent = Files.readString(decompressedFile.toPath());
        assertEquals(content, decompressedContent);
    }

    @Test
    void testByteConversion() {
        // Test positive byte
        assertEquals(65, HuffmanDecompressor.to((byte) 65)); // 'A'
        // Test negative byte
        assertEquals(128, HuffmanDecompressor.to((byte) -128));
    }

    @Test
    void testMakeEight() {
        // Test padding of binary strings to 8 bits
        assertEquals("00000000", HuffmanDecompressor.makeeight(""));
        assertEquals("00000001", HuffmanDecompressor.makeeight("1"));
        assertEquals("00001111", HuffmanDecompressor.makeeight("1111"));
        assertEquals("11111111", HuffmanDecompressor.makeeight("11111111"));
    }

    @Test
    void testInitialization() {
        // Test initialization of static variables
        HuffmanDecompressor.initHuffmanDecompressor();

        // Check if frequencies are reset
        assertTrue(Arrays.stream(HuffmanDecompressor.frequency).allMatch(freq -> freq == 0));
        // Check if string arrays are reset
        assertTrue(Arrays.stream(HuffmanDecompressor.huffmanCodes).allMatch(s -> s == null || s.equals("")));
        // Check if priority queue is empty
        assertTrue(HuffmanDecompressor.priorityQueue.isEmpty());
        // Check if other variables are reset
        assertEquals("", HuffmanDecompressor.bitBuffer);
        assertEquals("", HuffmanDecompressor.tempCode);
        assertEquals(0, HuffmanDecompressor.extraBits);
        assertEquals(0, HuffmanDecompressor.decodedByte);
        assertEquals(0, HuffmanDecompressor.uniqueCharCount);
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
        
        HuffmanCompressor.beginHuffmanCompression(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
        
        // Test unzipping
        HuffmanDecompressor.beginHuffmanDecompression(compressedFile.getAbsolutePath());
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
        
        HuffmanCompressor.beginHuffmanCompression(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
        
        // Test unzipping
        HuffmanDecompressor.beginHuffmanDecompression(compressedFile.getAbsolutePath());
        assertTrue(decompressedFile.exists());
        
        String decompressedContent = Files.readString(decompressedFile.toPath());
        assertEquals(content, decompressedContent);
    }
} 