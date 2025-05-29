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

class HunzippingTest {
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
        
        Hzipping.beginHzipping(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
        
        // Test unzipping
        Hunzipping.beginHunzipping(compressedFile.getAbsolutePath());
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
        
        Hzipping.beginHzipping(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
        
        // Test unzipping
        Hunzipping.beginHunzipping(compressedFile.getAbsolutePath());
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
        
        Hzipping.beginHzipping(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
        
        // Test unzipping
        Hunzipping.beginHunzipping(compressedFile.getAbsolutePath());
        assertTrue(decompressedFile.exists());
        
        String decompressedContent = Files.readString(decompressedFile.toPath());
        assertEquals(content, decompressedContent);
    }

    @Test
    void testByteConversion() {
        // Test positive byte
        assertEquals(65, Hunzipping.to((byte) 65)); // 'A'
        // Test negative byte
        assertEquals(128, Hunzipping.to((byte) -128));
    }

    @Test
    void testMakeEight() {
        // Test padding of binary strings to 8 bits
        assertEquals("00000000", Hunzipping.makeeight(""));
        assertEquals("00000001", Hunzipping.makeeight("1"));
        assertEquals("00001111", Hunzipping.makeeight("1111"));
        assertEquals("11111111", Hunzipping.makeeight("11111111"));
    }

    @Test
    void testInitialization() {
        // Test initialization of static variables
        Hunzipping.initHunzipping();
        
        // Check if frequencies are reset
        assertTrue(Arrays.stream(Hunzipping.freq1).allMatch(freq -> freq == 0));
        // Check if string arrays are reset
        assertTrue(Arrays.stream(Hunzipping.ss1).allMatch(s -> s == null || s.equals("")));
        // Check if priority queue is empty
        assertTrue(Hunzipping.pq1.isEmpty());
        // Check if other variables are reset
        assertEquals("", Hunzipping.bigone);
        assertEquals("", Hunzipping.temp);
        assertEquals(0, Hunzipping.exbits1);
        assertEquals(0, Hunzipping.putit);
        assertEquals(0, Hunzipping.cntu);
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
        
        Hzipping.beginHzipping(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
        
        // Test unzipping
        Hunzipping.beginHunzipping(compressedFile.getAbsolutePath());
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
        
        Hzipping.beginHzipping(inputFile.getAbsolutePath());
        assertTrue(compressedFile.exists());
        
        // Test unzipping
        Hunzipping.beginHunzipping(compressedFile.getAbsolutePath());
        assertTrue(decompressedFile.exists());
        
        String decompressedContent = Files.readString(decompressedFile.toPath());
        assertEquals(content, decompressedContent);
    }
} 