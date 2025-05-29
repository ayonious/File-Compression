package prog.huffman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HuffmanCompressionIntegrationTest {
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

    @Test
    void testVeryLargeFileCompression() throws IOException {
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

        // Add binary-like patterns
        for (int i = 0; i < 100; i++) {
            content.append(String.format("%8s", Integer.toBinaryString(i)).replace(' ', '0'))
                  .append(" ");
            if (i % 8 == 7) content.append("\n");
        }

        // Add number sequences
        for (int i = 0; i < 100; i++) {
            content.append(String.format("%d,%d,%d\n", i, i*i, fibonacci(i)));
        }

        // Test the compression-decompression cycle with this large content
        testCompressionCycle(content.toString());

        // Additional verification
        assertTrue(compressedFile.length() > 0, "Compressed file should not be empty");
        assertTrue(compressedFile.length() < originalFile.length(), 
                "Compressed file should be smaller than original for this repeating content");
    }

    @Test
    void testExtremeCompressionCases() throws IOException {
        StringBuilder content = new StringBuilder();

        // Add highly repetitive content (should compress very well)
        for (int i = 0; i < 1000; i++) {
            content.append("AAAAA");
        }
        content.append("\n\n");

        // Add random-looking but patterned content
        String[] words = {"compression", "algorithm", "huffman", "encoding", "binary", "tree"};
        for (int i = 0; i < 500; i++) {
            content.append(words[i % words.length]).append(" ");
            if (i % 10 == 9) content.append("\n");
        }
        content.append("\n\n");

        // Add nested repetitive structures
        for (int i = 0; i < 20; i++) {
            content.append("Level 1 Start\n");
            for (int j = 0; j < 10; j++) {
                content.append("  Level 2 Start\n");
                for (int k = 0; k < 5; k++) {
                    content.append("    Level 3: Data ").append(i).append("-").append(j).append("-").append(k).append("\n");
                }
                content.append("  Level 2 End\n");
            }
            content.append("Level 1 End\n\n");
        }

        // Add XML-like structured data with repeated elements
        for (int i = 0; i < 50; i++) {
            content.append("<record>\n")
                  .append("  <id>").append(i).append("</id>\n")
                  .append("  <data>\n")
                  .append("    <type>Type").append(i % 5).append("</type>\n")
                  .append("    <value>Value").append(i).append("</value>\n")
                  .append("    <timestamp>").append(System.currentTimeMillis()).append("</timestamp>\n")
                  .append("    <metadata>\n")
                  .append("      <author>Author").append(i % 10).append("</author>\n")
                  .append("      <version>1.").append(i % 5).append("</version>\n")
                  .append("    </metadata>\n")
                  .append("  </data>\n")
                  .append("</record>\n");
        }

        // Add some Unicode text to test multi-byte character handling
        String[] emojis = {"😀", "😎", "🚀", "💻", "📚", "🔥"};
        for (int i = 0; i < 100; i++) {
            content.append(emojis[i % emojis.length]);
            if (i % 10 == 9) content.append("\n");
        }
        content.append("\n\n");

        // Add some mathematical sequences
        for (int i = 1; i <= 100; i++) {
            // Square numbers
            content.append(i * i).append(",");
            // Triangular numbers
            content.append((i * (i + 1)) / 2).append(",");
            // Prime check
            if (isPrime(i)) content.append("PRIME");
            content.append("\n");
        }

        // Add some CSV-like data with repeating patterns
        content.append("\nCSV Data:\n");
        for (int i = 0; i < 100; i++) {
            content.append(String.format("%d,%s,%s,%d,%s\n",
                i,
                "Category" + (i % 5),
                "Product" + (i % 10),
                (i * 100),
                "Status" + (i % 3)));
        }

        // Test the compression-decompression cycle
        testCompressionCycle(content.toString());

        // Verify compression ratio
        double compressionRatio = (double) compressedFile.length() / originalFile.length();
        assertTrue(compressionRatio < 1.0, "File should be compressed (ratio: " + compressionRatio + ")");
        System.out.println("Compression Ratio: " + compressionRatio);
        System.out.println("Original Size: " + originalFile.length() + " bytes");
        System.out.println("Compressed Size: " + compressedFile.length() + " bytes");
    }

    private long fibonacci(int n) {
        if (n <= 1) return n;
        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            long temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }

    private boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }
} 