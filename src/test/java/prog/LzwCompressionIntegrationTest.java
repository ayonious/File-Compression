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

class LzwCompressionIntegrationTest {
    @TempDir
    Path tempDir;
    private File originalFile;
    private File compressedFile;
    private File decompressedFile;

    @BeforeEach
    void setUp() throws IOException {
        originalFile = tempDir.resolve("original.txt").toFile();
        compressedFile = new File(originalFile.getAbsolutePath() + ".LmZWp");
        decompressedFile = new File(originalFile.getAbsolutePath());
    }

    private void testLzwCompressionCycle(String content) throws IOException {
        // Write original content
        try (FileWriter writer = new FileWriter(originalFile)) {
            writer.write(content);
        }
        long originalSize = Files.size(originalFile.toPath());

        // Compress using LZW
        Lzipping.beginLzipping(originalFile.getAbsolutePath());
        assertTrue(compressedFile.exists(), "Compressed file should exist");

        // Store original file content
        String originalContent = Files.readString(originalFile.toPath());

        // Delete original file to ensure we're reading from decompressed file
        Files.delete(originalFile.toPath());
        assertFalse(originalFile.exists(), "Original file should be deleted");

        // Decompress using LZW
        Lunzipping.beginLunzipping(compressedFile.getAbsolutePath());
        assertTrue(decompressedFile.exists(), "Decompressed file should exist");

        // Compare contents
        String decompressedContent = Files.readString(decompressedFile.toPath());
        assertEquals(originalContent, decompressedContent, "Decompressed content should match original");
        assertEquals(originalSize, Files.size(decompressedFile.toPath()), "File sizes should match");
    }

    @Test
    void testEmptyFile() throws IOException {
        testLzwCompressionCycle("");
    }

    @Test
    void testSingleCharacter() throws IOException {
        testLzwCompressionCycle("aaaa");
    }

    @Test
    void testSimpleText() throws IOException {
        testLzwCompressionCycle("Hello, World!");
    }

    @Test
    void testLongText() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            content.append("Line ").append(i).append(": This is a test of the LZW compression system.\n");
        }
        testLzwCompressionCycle(content.toString());
    }

    @Test
    void testSpecialCharacters() throws IOException {
        testLzwCompressionCycle("!@#$%^&*()_+{}[]|\\:;\"'<>,.?/~`\n\t");
    }

    @Test
    void testRepeatingPatterns() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            content.append("This is a repeating pattern that should compress well with LZW. ");
        }
        testLzwCompressionCycle(content.toString());
    }

    @Test
    void testBinaryContent() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            content.append((char) i);
        }
        testLzwCompressionCycle(content.toString());
    }

    @Test
    void testMixedContent() throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("Normal text\n");
        content.append("Special chars: !@#$%\n");
        content.append("Numbers: 12345\n");
        content.append("Repeated: aaaaaaa\n");
        content.append("Unicode: ♠♣♥♦\n");
        testLzwCompressionCycle(content.toString());
    }

    @Test
    void testDictionaryLimitBehavior() throws IOException {
        // Create content that will test dictionary size limit (100000)
        StringBuilder content = new StringBuilder();
        String repeatingBase = "This is a test string with some variation ";
        for (int i = 0; i < 5000; i++) {
            content.append(repeatingBase).append(i).append(" ");
        }
        testLzwCompressionCycle(content.toString());
    }

    @Test
    void testIncrementalPatterns() throws IOException {
        // Test how LZW handles incrementally growing patterns
        StringBuilder content = new StringBuilder();
        String base = "a";
        for (int i = 0; i < 100; i++) {
            content.append(base);
            base += "a";
        }
        testLzwCompressionCycle(content.toString());
    }

    @Test
    void testAlternatingPatterns() throws IOException {
        // Test how LZW handles alternating patterns
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            content.append(i % 2 == 0 ? "pattern1 " : "pattern2 ");
        }
        testLzwCompressionCycle(content.toString());
    }

    @Test
    void testLongWords() throws IOException {
        // Test compression of long words without spaces
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            content.append("supercalifragilisticexpialidocious");
        }
        testLzwCompressionCycle(content.toString());
    }
} 