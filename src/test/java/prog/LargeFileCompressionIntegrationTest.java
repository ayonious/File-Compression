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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Large file integration tests for both LZW and Huffman compression algorithms.
 * Tests compression and decompression round-trips with 50KB files containing
 * different content patterns to verify correctness and performance.
 *
 * Test files are pre-generated in src/test/resources/ (10 files, 50KB each).
 */
class LargeFileCompressionIntegrationTest {
    @TempDir
    Path tempDir;

    private File originalFile;
    private File lzwCompressedFile;
    private File huffmanCompressedFile;
    private File lzwDecompressedFile;
    private File huffmanDecompressedFile;

    @BeforeEach
    void setUp() {
        originalFile = tempDir.resolve("original.txt").toFile();
        lzwCompressedFile = new File(originalFile.getAbsolutePath() + ".LmZWp");
        huffmanCompressedFile = new File(originalFile.getAbsolutePath() + ".huffz");
        lzwDecompressedFile = new File(originalFile.getAbsolutePath());
        huffmanDecompressedFile = new File(originalFile.getAbsolutePath());
    }

    /**
     * Copy a pre-generated test file from resources to the temp directory
     */
    private void copyTestFile(String resourceFileName) throws IOException {
        Path sourcePath = Path.of("src/test/resources/" + resourceFileName);
        Files.copy(sourcePath, originalFile.toPath());
    }

    /**
     * Calculate SHA-256 hash of a byte array
     */
    private String calculateHash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Helper method to test both compression algorithms on a file
     */
    private void testBothCompressionAlgorithms(String testName, String expectedOriginalHash,
                                               String expectedLzwHash, String expectedHuffmanHash) throws IOException {
        long originalSize = Files.size(originalFile.toPath());
        byte[] originalBytes = Files.readAllBytes(originalFile.toPath());
        String originalHash = calculateHash(originalBytes);

        System.out.println("\n=== Testing: " + testName + " ===");
        System.out.println("Original file size: " + formatBytes(originalSize));
        System.out.println("Original file hash: " + originalHash);

        // Verify original file hash matches expected
        assertEquals(expectedOriginalHash, originalHash,
            "Original file hash should match expected constant value");

        // Test LZW compression
        testLzwCompression(originalBytes, originalSize, originalHash, expectedLzwHash, testName);

        // Restore original file for Huffman test
        Files.write(originalFile.toPath(), originalBytes);

        // Test Huffman compression
        testHuffmanCompression(originalBytes, originalSize, originalHash, expectedHuffmanHash, testName);
    }

    private void testLzwCompression(byte[] originalBytes, long originalSize, String originalHash,
                                    String expectedCompressedHash, String testName) throws IOException {
        System.out.println("\n--- LZW Compression ---");

        // Compress first time
        LzwCompressor compressor = new LzwCompressor(originalFile.getAbsolutePath());
        compressor.compress();
        assertTrue(lzwCompressedFile.exists(), "LZW compressed file should exist");

        byte[] compressedBytes1 = Files.readAllBytes(lzwCompressedFile.toPath());
        String compressedHash1 = calculateHash(compressedBytes1);
        long compressedSize = compressedBytes1.length;
        double compressionRatio = (1.0 - (double) compressedSize / originalSize) * 100;

        System.out.println("LZW compressed size: " + formatBytes(compressedSize));
        System.out.println("LZW compression ratio: " + String.format("%.2f%%", compressionRatio));
        System.out.println("LZW compressed hash: " + compressedHash1);

        // Verify compressed hash matches expected constant value
        assertEquals(expectedCompressedHash, compressedHash1,
            "LZW compressed file hash should match expected constant value");
        System.out.println("LZW compressed hash matches expected: VERIFIED ✓");

        // Compress second time to verify deterministic compression
        Files.delete(lzwCompressedFile.toPath());
        LzwCompressor compressor2 = new LzwCompressor(originalFile.getAbsolutePath());
        compressor2.compress();
        byte[] compressedBytes2 = Files.readAllBytes(lzwCompressedFile.toPath());
        String compressedHash2 = calculateHash(compressedBytes2);

        assertEquals(compressedHash1, compressedHash2,
            "LZW compression should be deterministic - same input should produce same compressed output");
        System.out.println("LZW deterministic compression: VERIFIED ✓");

        // Delete original
        Files.delete(originalFile.toPath());
        assertFalse(originalFile.exists(), "Original file should be deleted");

        // Decompress
        LzwDecompressor decompressor = new LzwDecompressor(lzwCompressedFile.getAbsolutePath());
        decompressor.decompress();
        assertTrue(lzwDecompressedFile.exists(), "LZW decompressed file should exist");

        // Verify decompression
        byte[] decompressedBytes = Files.readAllBytes(lzwDecompressedFile.toPath());
        String decompressedHash = calculateHash(decompressedBytes);

        assertEquals(originalSize, decompressedBytes.length,
            "LZW decompressed file size should match original");
        assertEquals(originalHash, decompressedHash,
            "LZW decompressed file hash should match original");
        assertArrayEquals(originalBytes, decompressedBytes,
            "LZW decompressed content should match original byte-by-byte");

        System.out.println("LZW decompressed hash: " + decompressedHash);
        System.out.println("LZW decompression: SUCCESS ✓");

        // Cleanup for next test
        Files.delete(lzwDecompressedFile.toPath());
        Files.delete(lzwCompressedFile.toPath());
    }

    private void testHuffmanCompression(byte[] originalBytes, long originalSize, String originalHash,
                                        String expectedCompressedHash, String testName) throws IOException {
        System.out.println("\n--- Huffman Compression ---");

        // Compress first time
        HuffmanCompressor compressor = new HuffmanCompressor(originalFile.getAbsolutePath());
        compressor.compress();
        compressor.cleanup();
        assertTrue(huffmanCompressedFile.exists(), "Huffman compressed file should exist");

        byte[] compressedBytes1 = Files.readAllBytes(huffmanCompressedFile.toPath());
        String compressedHash1 = calculateHash(compressedBytes1);
        long compressedSize = compressedBytes1.length;
        double compressionRatio = (1.0 - (double) compressedSize / originalSize) * 100;

        System.out.println("Huffman compressed size: " + formatBytes(compressedSize));
        System.out.println("Huffman compression ratio: " + String.format("%.2f%%", compressionRatio));
        System.out.println("Huffman compressed hash: " + compressedHash1);

        // Verify compressed hash matches expected constant value
        assertEquals(expectedCompressedHash, compressedHash1,
            "Huffman compressed file hash should match expected constant value");
        System.out.println("Huffman compressed hash matches expected: VERIFIED ✓");

        // Compress second time to verify deterministic compression
        Files.delete(huffmanCompressedFile.toPath());
        HuffmanCompressor compressor2 = new HuffmanCompressor(originalFile.getAbsolutePath());
        compressor2.compress();
        compressor2.cleanup();
        byte[] compressedBytes2 = Files.readAllBytes(huffmanCompressedFile.toPath());
        String compressedHash2 = calculateHash(compressedBytes2);

        assertEquals(compressedHash1, compressedHash2,
            "Huffman compression should be deterministic - same input should produce same compressed output");
        System.out.println("Huffman deterministic compression: VERIFIED ✓");

        // Delete original
        Files.delete(originalFile.toPath());
        assertFalse(originalFile.exists(), "Original file should be deleted");

        // Decompress
        HuffmanDecompressor decompressor = new HuffmanDecompressor(huffmanCompressedFile.getAbsolutePath());
        decompressor.decompress();
        assertTrue(huffmanDecompressedFile.exists(), "Huffman decompressed file should exist");

        // Verify decompression
        byte[] decompressedBytes = Files.readAllBytes(huffmanDecompressedFile.toPath());
        String decompressedHash = calculateHash(decompressedBytes);

        assertEquals(originalSize, decompressedBytes.length,
            "Huffman decompressed file size should match original");
        assertEquals(originalHash, decompressedHash,
            "Huffman decompressed file hash should match original");
        assertArrayEquals(originalBytes, decompressedBytes,
            "Huffman decompressed content should match original byte-by-byte");

        System.out.println("Huffman decompressed hash: " + decompressedHash);
        System.out.println("Huffman decompression: SUCCESS ✓");

        // Cleanup for next test
        Files.delete(huffmanDecompressedFile.toPath());
        Files.delete(huffmanCompressedFile.toPath());
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }

    @Test
    void testLargeRepetitiveText() throws IOException {
        copyTestFile("test_repetitive_50kb.txt");
        testBothCompressionAlgorithms("50KB Highly Repetitive Text",
            "2670808c4caee20e0c5af69e708744afafd2e376aa8d3cef66a4c8e23705fa98",
            "c21366c52946c2ef959c518c768ea87d4b7dec245d064aafa74b0ac86ac1b486",
            "471b99a387b552864c4ea720699c004cc0ff8cc258bf6f670b2883a6df824aad");
    }

    @Test
    void testLargeRandomText() throws IOException {
        copyTestFile("test_random_50kb.txt");
        testBothCompressionAlgorithms("50KB Random Text (Low Compressibility)",
            "c7b97338a868a9a737328642e2240b047da8aa6bd98222b774e1c70d1c39d2ff",
            "98a29f993e01443bf0d3a0178c80ce4cecb59d3eb7a1ddb5790b77a0c6faedd7",
            "610caec7097ab98470a613828a5267f10478fa4c43db3eaec4337cb09d0eb633");
    }

    @Test
    void testLargeStructuredData() throws IOException {
        copyTestFile("test_structured_50kb.txt");
        testBothCompressionAlgorithms("50KB Structured JSON-like Data",
            "f3f80c702187f727e447d03933283cb3aad964b3271bbdfd40a0625a28d3d108",
            "4d0603e90d6bb17789ced0c9e5d481440aa6ea2f8b7b62c2f467ac5b2856b1d7",
            "6035fb4ba8654d6338db33c0302a370c115243cc3c3a753cad3ecb71b2ea7728");
    }

    @Test
    void testLargeCodeLikeContent() throws IOException {
        copyTestFile("test_code_50kb.txt");
        testBothCompressionAlgorithms("50KB Code-like Content",
            "27cf02f8c355d76c39064877aaf730bf44cf2611f88bddd64705c39bc9f925a0",
            "33af8633ca948e585c4853f11a6a9c24de05a9f6da3f00dd9802fc98b371a161",
            "d8da051291a9f9bb918864dc889dcf847a9cd18f76306d8d93c139422a2d13bc");
    }

    @Test
    void testLargeMixedContent() throws IOException {
        copyTestFile("test_mixed_50kb.txt");
        testBothCompressionAlgorithms("50KB Mixed Content",
            "140449846813d77328a27cb6169343b31801c66b18dbd93c1a913fca344e9920",
            "7bd9a03cd59c3f9a490553300b8b13c9f4016c581822fdeba7122105f131258c",
            "c42f6b9000076ca4a3c7734e91eaf1c0007d3adaa4f21f6a66fe813d5c71dad1");
    }

    @Test
    void testUnicodeEmojis() throws IOException {
        copyTestFile("test_unicode_50kb.txt");
        testBothCompressionAlgorithms("50KB Unicode Emojis",
            "0d374e0af9cbfff908d104701ddffa907129ca84b05826a10e0f26552611afbd",
            "9637beede96cc9c0b28e312ded5719d83266e0d4b54093465dbccabfdf0694f3",
            "696820d81725ec9f53e84662ef8e5f7c5cdd1a0c780f72b1b5715d2de154c38b");
    }

    @Test
    void testMultilingualText() throws IOException {
        copyTestFile("test_multilingual_50kb.txt");
        testBothCompressionAlgorithms("50KB Multilingual Text",
            "9eef57003fb38f94d9e36b535720d4521925ae823e308e8bc1195d20fdf5c771",
            "8415dea2b9e88ba650b130bc96b578b2af964e78adee74dfdda95747ae0d5ba1",
            "144b02cee289e020955ef7cff1b04694aad580c58bd2e662185ad2bfb4a4c161");
    }

    @Test
    void testXmlContent() throws IOException {
        copyTestFile("test_xml_50kb.txt");
        testBothCompressionAlgorithms("50KB XML Content",
            "c144a1778e7d948520da3cb28e150904ebe9e5f760d33bbdd1006eae2a717eb0",
            "374bc7f08d26b261d91561d25a0443c8aa963830f2a144d00d367d683239309d",
            "e6f7a3fa4bd3242ce448bbe35316240c21060384bcd718910930c0e6a927dd09");
    }

    @Test
    void testLogFiles() throws IOException {
        copyTestFile("test_logs_50kb.txt");
        testBothCompressionAlgorithms("50KB Log Files",
            "706d860c817b8e743b88c4c7a41019c20a64a473bd01f02114da0b0d8de8f5c0",
            "3f883d3e6114a48dbee6d9187e6eb303daab40d3259d68a49fa155f0ab162120",
            "9f19af9fd794c117d9a913510bd6bae39ffaf9cf8e341a150f6923c7d81911e1");
    }

    @Test
    void testBase64Content() throws IOException {
        copyTestFile("test_base64_50kb.txt");
        testBothCompressionAlgorithms("50KB Base64 Content",
            "c803a74666750ce37945535c1637c4daa775fd2b9da11d80ed964467e6a0308b",
            "ac34da2503e786763ed88d10a74099e33cbfa8ab3c26b521711666abf20db9ff",
            "e5de28e78c7c23b04333e70a41af007763ef5361cc5c8c42e18d5d37345a56ee");
    }
}
