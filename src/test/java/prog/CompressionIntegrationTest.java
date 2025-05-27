package prog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CompressionIntegrationTest {
    @TempDir
    Path tempDir;
    private File originalFile;
    private File compressedFile;
    private File decompressedFile;

    @BeforeEach
    void setUp() throws IOException {
        originalFile = tempDir.resolve("original.txt").toFile();
        compressedFile = new File(originalFile.getAbsolutePath() + ".huffz");
        decompressedFile = new File(originalFile.getAbsolutePath());
    }

    private void testCompressionCycle(String content) throws IOException {
        // Write original content
        try (FileWriter writer = new FileWriter(originalFile)) {
            writer.write(content);
        }
        long originalSize = Files.size(originalFile.toPath());

        // Compress
        Hzipping.beginHzipping(originalFile.getAbsolutePath());
        assertTrue(compressedFile.exists(), "Compressed file should exist");

        // Store original file content
        String originalContent = Files.readString(originalFile.toPath());

        // Delete original file to ensure we're reading from decompressed file
        Files.delete(originalFile.toPath());
        assertFalse(originalFile.exists(), "Original file should be deleted");

        // Decompress
        Hunzipping.beginHunzipping(compressedFile.getAbsolutePath());
        assertTrue(decompressedFile.exists(), "Decompressed file should exist");

        // Compare contents
        String decompressedContent = Files.readString(decompressedFile.toPath());
        assertEquals(originalContent, decompressedContent, "Decompressed content should match original");
        assertEquals(originalSize, Files.size(decompressedFile.toPath()), "File sizes should match");
    }

    @Test
    void testEmptyFile() throws IOException {
        testCompressionCycle("");
    }

    @Test
    void testSingleCharacter() throws IOException {
        testCompressionCycle("aaaa");
    }

    @Test
    void testSimpleText() throws IOException {
        testCompressionCycle("Hello, World!");
    }

    @Test
    void testLongText() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            content.append("Line ").append(i).append(": This is a test of the compression system.\n");
        }
        testCompressionCycle(content.toString());
    }

    @Test
    void testSpecialCharacters() throws IOException {
        testCompressionCycle("!@#$%^&*()_+{}[]|\\:;\"'<>,.?/~`\n\t");
    }

    @Test
    void testRepeatingPatterns() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            content.append("This is a repeating pattern. ");
        }
        testCompressionCycle(content.toString());
    }

    @Test
    void testBinaryContent() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            content.append((char) i);
        }
        testCompressionCycle(content.toString());
    }

    @Test
    void testMixedContent() throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("Normal text\n");
        content.append("Special chars: !@#$%\n");
        content.append("Numbers: 12345\n");
        content.append("Repeated: aaaaaaa\n");
        content.append("Unicode: ♠♣♥♦\n");
        testCompressionCycle(content.toString());
    }
} 