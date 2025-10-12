package prog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import prog.huffman.HuffmanCompressor;
import prog.huffman.HuffmanDecompressor;
import prog.lzw.LzwCompressor;
import prog.lzw.LzwDecompressor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for edge cases in compression algorithms.
 * Tests scenarios like single character files, very small files, and special character patterns.
 * Test files are stored in src/test/resources/ with prefix "edge_case_"
 */
class EdgeCaseCompressionTest {
    @TempDir
    Path tempDir;
    private File originalFile;
    private File huffmanCompressedFile;
    private File lzwCompressedFile;
    private File huffmanDecompressedFile;
    private File lzwDecompressedFile;

    private static final String RESOURCES_PATH = "src/test/resources/";

    @BeforeEach
    void setUp() throws IOException {
        originalFile = tempDir.resolve("test.txt").toFile();
        huffmanCompressedFile = new File(originalFile.getAbsolutePath() + ".huffz");
        lzwCompressedFile = new File(originalFile.getAbsolutePath() + ".LmZWp");
        huffmanDecompressedFile = new File(originalFile.getAbsolutePath());
        lzwDecompressedFile = new File(originalFile.getAbsolutePath());
    }

    /**
     * Helper method to copy a resource file to the temp directory
     */
    private void copyResourceFile(String resourceFileName) throws IOException {
        Path sourcePath = Path.of(RESOURCES_PATH + resourceFileName);
        Files.copy(sourcePath, originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void testSingleByteFile() throws IOException {
        copyResourceFile("edge_case_single_byte.txt");
        testHuffmanCompressionCycle();

        copyResourceFile("edge_case_single_byte.txt");
        testLzwCompressionCycle();
    }

    @Test
    void testSingleCharacter1KB() throws IOException {
        copyResourceFile("edge_case_1kb_single_char.txt");
        long originalSize = Files.size(originalFile.toPath());

        testHuffmanCompressionCycle();

        copyResourceFile("edge_case_1kb_single_char.txt");
        testLzwCompressionCycle();

        // Verify LZW achieves excellent compression for this case
        long compressedSize = lzwCompressedFile.length();
        assertTrue(compressedSize < originalSize / 10,
            "LZW should achieve >90% compression on single repeating character");
    }

    @Test
    void testOnlyDigit2Character() throws IOException {
        copyResourceFile("edge_case_digit_2.txt");
        testHuffmanCompressionCycle();

        copyResourceFile("edge_case_digit_2.txt");
        testLzwCompressionCycle();
    }

    @Test
    void testTwoCharactersAlternating() throws IOException {
        copyResourceFile("edge_case_alternating_ab.txt");
        testHuffmanCompressionCycle();

        copyResourceFile("edge_case_alternating_ab.txt");
        testLzwCompressionCycle();
    }

    @Test
    void testThreeBytes() throws IOException {
        copyResourceFile("edge_case_three_bytes.txt");
        testHuffmanCompressionCycle();

        copyResourceFile("edge_case_three_bytes.txt");
        testLzwCompressionCycle();
    }

    @Test
    void testSingleNewline() throws IOException {
        copyResourceFile("edge_case_single_newline.txt");
        testHuffmanCompressionCycle();

        copyResourceFile("edge_case_single_newline.txt");
        testLzwCompressionCycle();
    }

    @Test
    void testSingleSpace() throws IOException {
        copyResourceFile("edge_case_single_space.txt");
        testHuffmanCompressionCycle();

        copyResourceFile("edge_case_single_space.txt");
        testLzwCompressionCycle();
    }

    @Test
    void testOnlyNullBytes() throws IOException {
        // Copy binary file with null bytes
        Path sourcePath = Path.of(RESOURCES_PATH + "edge_case_null_bytes.bin");
        Files.copy(sourcePath, originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        byte[] originalBytes = Files.readAllBytes(originalFile.toPath());
        long originalSize = Files.size(originalFile.toPath());

        // Huffman compression
        HuffmanCompressor huffmanCompressor = new HuffmanCompressor(originalFile.getAbsolutePath());
        huffmanCompressor.compress();
        huffmanCompressor.cleanup();
        assertTrue(huffmanCompressedFile.exists());

        Files.delete(originalFile.toPath());

        HuffmanDecompressor huffmanDecompressor = new HuffmanDecompressor(huffmanCompressedFile.getAbsolutePath());
        huffmanDecompressor.decompress();
        assertTrue(huffmanDecompressedFile.exists());

        byte[] decompressed = Files.readAllBytes(huffmanDecompressedFile.toPath());
        assertArrayEquals(originalBytes, decompressed);

        // Clean up and test LZW
        Files.delete(huffmanDecompressedFile.toPath());
        Files.delete(huffmanCompressedFile.toPath());
        Files.copy(sourcePath, originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        LzwCompressor lzwCompressor = new LzwCompressor(originalFile.getAbsolutePath());
        lzwCompressor.compress();
        assertTrue(lzwCompressedFile.exists());

        Files.delete(originalFile.toPath());

        LzwDecompressor lzwDecompressor = new LzwDecompressor(lzwCompressedFile.getAbsolutePath());
        lzwDecompressor.decompress();
        assertTrue(lzwDecompressedFile.exists());

        decompressed = Files.readAllBytes(lzwDecompressedFile.toPath());
        assertArrayEquals(originalBytes, decompressed);
    }

    @Test
    void testAllSameSpecialChar() throws IOException {
        copyResourceFile("edge_case_special_char.txt");
        testHuffmanCompressionCycle();

        copyResourceFile("edge_case_special_char.txt");
        testLzwCompressionCycle();
    }

    @Test
    void testUnicodeEmoji1KB() throws IOException {
        copyResourceFile("edge_case_unicode_emoji.txt");
        testHuffmanCompressionCycle();

        copyResourceFile("edge_case_unicode_emoji.txt");
        testLzwCompressionCycle();
    }

    @Test
    void testRepeatingTwoByteSequence() throws IOException {
        copyResourceFile("edge_case_xy_repeated.txt");
        long originalSize = Files.size(originalFile.toPath());

        testHuffmanCompressionCycle();

        copyResourceFile("edge_case_xy_repeated.txt");
        testLzwCompressionCycle();

        // LZW should compress this very well
        long compressedSize = lzwCompressedFile.length();
        assertTrue(compressedSize < originalSize / 2,
            "LZW should achieve >50% compression on repeating two-byte sequence");
    }

    @Test
    void testOnlyDigits() throws IOException {
        copyResourceFile("edge_case_only_digits.txt");
        testHuffmanCompressionCycle();

        copyResourceFile("edge_case_only_digits.txt");
        testLzwCompressionCycle();
    }

    @Test
    void testSingleCharacterWithVariation() throws IOException {
        copyResourceFile("edge_case_variation.txt");
        testHuffmanCompressionCycle();

        copyResourceFile("edge_case_variation.txt");
        testLzwCompressionCycle();
    }

    private void testHuffmanCompressionCycle() throws IOException {
        String originalContent = Files.readString(originalFile.toPath());
        long originalSize = Files.size(originalFile.toPath());

        // Compress
        HuffmanCompressor compressor = new HuffmanCompressor(originalFile.getAbsolutePath());
        compressor.compress();
        compressor.cleanup();
        assertTrue(huffmanCompressedFile.exists(), "Huffman compressed file should exist");

        // Delete original
        Files.delete(originalFile.toPath());

        // Decompress
        HuffmanDecompressor decompressor = new HuffmanDecompressor(huffmanCompressedFile.getAbsolutePath());
        decompressor.decompress();
        assertTrue(huffmanDecompressedFile.exists(), "Huffman decompressed file should exist");

        // Verify
        String decompressedContent = Files.readString(huffmanDecompressedFile.toPath());
        assertEquals(originalContent, decompressedContent, "Huffman: Decompressed content should match original");
        assertEquals(originalSize, Files.size(huffmanDecompressedFile.toPath()), "Huffman: File sizes should match");

        // Clean up for next test
        Files.delete(huffmanDecompressedFile.toPath());
        Files.delete(huffmanCompressedFile.toPath());
    }

    private void testLzwCompressionCycle() throws IOException {
        String originalContent = Files.readString(originalFile.toPath());
        long originalSize = Files.size(originalFile.toPath());

        // Compress
        LzwCompressor compressor = new LzwCompressor(originalFile.getAbsolutePath());
        compressor.compress();
        assertTrue(lzwCompressedFile.exists(), "LZW compressed file should exist");

        // Delete original
        Files.delete(originalFile.toPath());

        // Decompress
        LzwDecompressor decompressor = new LzwDecompressor(lzwCompressedFile.getAbsolutePath());
        decompressor.decompress();
        assertTrue(lzwDecompressedFile.exists(), "LZW decompressed file should exist");

        // Verify
        String decompressedContent = Files.readString(lzwDecompressedFile.toPath());
        assertEquals(originalContent, decompressedContent, "LZW: Decompressed content should match original");
        assertEquals(originalSize, Files.size(lzwDecompressedFile.toPath()), "LZW: File sizes should match");

        // Clean up for next test
        Files.delete(lzwDecompressedFile.toPath());
        Files.delete(lzwCompressedFile.toPath());
    }
}
