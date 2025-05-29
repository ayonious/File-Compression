package prog.lzw;

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

    @Test
    void testVeryLongFileCompression() throws IOException {
        // Create a very large file with diverse content types
        StringBuilder content = new StringBuilder();
        
        // Add Lorem Ipsum blocks
        for (int i = 0; i < 50; i++) {
            content.append("Lorem ipsum dolor sit amet, consectetur adipiscing elit. ")
                  .append("Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ")
                  .append("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris. ");
        }

        // Add repeating patterns
        for (int i = 0; i < 30; i++) {
            content.append("\nPATTERN_START\n")
                  .append("This is a repeating pattern that will help with compression.\n")
                  .append("We will repeat this block multiple times to create patterns.\n")
                  .append("The compression algorithm should handle this efficiently.\n")
                  .append("PATTERN_END\n");
        }

        // Add structured data
        for (int i = 0; i < 20; i++) {
            content.append("\n[DATA_BLOCK_").append(i).append("]\n")
                  .append("Name: Test User ").append(i).append("\n")
                  .append("Age: ").append(20 + i).append("\n")
                  .append("City: Test City ").append(i).append("\n")
                  .append("Occupation: Test Occupation ").append(i).append("\n");
        }

        // Add JSON-like content
        for (int i = 0; i < 15; i++) {
            content.append("\n{\n")
                  .append("    \"user\": {\n")
                  .append("        \"id\": ").append(i).append(",\n")
                  .append("        \"name\": \"Test User ").append(i).append("\",\n")
                  .append("        \"email\": \"test").append(i).append("@example.com\",\n")
                  .append("        \"preferences\": {\n")
                  .append("            \"theme\": \"dark\",\n")
                  .append("            \"notifications\": true,\n")
                  .append("            \"language\": \"en-US\"\n")
                  .append("        }\n")
                  .append("    }\n")
                  .append("}\n");
        }

        // Add HTML-like content
        for (int i = 0; i < 10; i++) {
            content.append("\n<section id=\"section-").append(i).append("\">\n")
                  .append("    <header>\n")
                  .append("        <h1>Sample Header ").append(i).append("</h1>\n")
                  .append("        <nav>\n")
                  .append("            <ul>\n")
                  .append("                <li>Menu Item 1</li>\n")
                  .append("                <li>Menu Item 2</li>\n")
                  .append("                <li>Menu Item 3</li>\n")
                  .append("            </ul>\n")
                  .append("        </nav>\n")
                  .append("    </header>\n")
                  .append("</section>\n");
        }

        // Add some pangrams and test patterns
        for (int i = 0; i < 25; i++) {
            content.append("\n[TEST_SECTION_").append(i).append("]\n")
                  .append("The quick brown fox jumps over the lazy dog.\n")
                  .append("Pack my box with five dozen liquor jugs.\n")
                  .append("How vexingly quick daft zebras jump!\n");
        }

        // Test the compression-decompression cycle with this large content
        testLzwCompressionCycle(content.toString());

        // Additional verification for large file
        assertTrue(compressedFile.length() > 0, "Compressed file should not be empty");
        assertTrue(compressedFile.length() < originalFile.length(), 
                "Compressed file should be smaller than original for this repeating content");
    }
} 