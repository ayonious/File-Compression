package prog.lzw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class LunzippingTest {
    @TempDir
    Path tempDir;
    
    private File createCompressedFile(String filename, int bitsz1, byte[] content) throws IOException {
        File file = tempDir.resolve(filename + ".LmZWp").toFile();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            dos.writeInt(bitsz1);  // Write the bit size
            dos.write(content);     // Write the compressed content
        }
        return file;
    }

    @BeforeEach
    void setUp() {
        Lunzipping.big1 = "";
        Lunzipping.bitsz1 = 0;
        Lunzipping.pre();  // Initialize the binary to string conversion table
    }

    @Test
    void testBasicDecompression() throws IOException {
        // Create a simple compressed file
        String filename = "test";
        byte[] compressedContent = {65, 0, 0};  // Simple content representing compressed 'A'
        File compressedFile = createCompressedFile(filename, 8, compressedContent);
        
        // Run decompression
        Lunzipping.beginLunzipping(compressedFile.getAbsolutePath());
        
        // Check the decompressed file
        File decompressedFile = new File(tempDir.toFile(), filename);
        assertTrue(decompressedFile.exists(), "Decompressed file should exist");
        
        byte[] content = Files.readAllBytes(decompressedFile.toPath());
        assertTrue(content.length > 0, "Decompressed content should not be empty");
        assertEquals(65, content[0], "First byte should be 'A' (65 in ASCII)");
    }

    @Test
    void testEmptyFile() throws IOException {
        // Create an empty compressed file
        String filename = "empty";
        byte[] compressedContent = {};
        File compressedFile = createCompressedFile(filename, 8, compressedContent);
        
        // Run decompression
        Lunzipping.beginLunzipping(compressedFile.getAbsolutePath());
        
        // Check the decompressed file
        File decompressedFile = new File(tempDir.toFile(), filename);
        assertTrue(decompressedFile.exists(), "Decompressed file should exist");
        assertEquals(0, decompressedFile.length(), "Decompressed file should be empty");
    }

    @Test
    void testRepeatedPatterns() throws IOException {
        // Create compressed content with repeated patterns
        String filename = "repeated";
        byte[] compressedContent = {65, 66, 0, 1};  // Represents compressed "ABAB"
        File compressedFile = createCompressedFile(filename, 8, compressedContent);
        
        // Run decompression
        Lunzipping.beginLunzipping(compressedFile.getAbsolutePath());
        
        // Check the decompressed file
        File decompressedFile = new File(tempDir.toFile(), filename);
        assertTrue(decompressedFile.exists(), "Decompressed file should exist");
        
        byte[] content = Files.readAllBytes(decompressedFile.toPath());
        assertTrue(content.length > 0, "Decompressed content should not be empty");
        // Check first two bytes are A and B
        assertTrue(content.length >= 2, "Should have at least two bytes");
        assertEquals(65, content[0], "First byte should be 'A'");
        assertEquals(66, content[1], "Second byte should be 'B'");
    }

    @Test
    void testBinaryToStringConversion() {
        Lunzipping.pre();  // Initialize conversion table
        
        // Test some known conversions
        assertEquals("00000000", Lunzipping.bttost[0], "Conversion for 0");
        assertEquals("00000001", Lunzipping.bttost[1], "Conversion for 1");
        assertEquals("10000000", Lunzipping.bttost[128], "Conversion for 128");
        assertEquals("11111111", Lunzipping.bttost[255], "Conversion for 255");
    }

    @Test
    void testByteToIntConversion() {
        // Test positive values
        assertEquals(0, Lunzipping.btoi((byte) 0));
        assertEquals(127, Lunzipping.btoi((byte) 127));
        
        // Test negative values (which should be converted to positive)
        assertEquals(128, Lunzipping.btoi((byte) -128));
        assertEquals(255, Lunzipping.btoi((byte) -1));
    }

    @Test
    void testStringToIntConversion() {
        assertEquals(0, Lunzipping.stoi("0"));
        assertEquals(1, Lunzipping.stoi("1"));
        assertEquals(2, Lunzipping.stoi("10"));
        assertEquals(255, Lunzipping.stoi("11111111"));
    }
} 