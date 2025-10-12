package prog.huffman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BitReader class.
 * Tests bit buffering and manipulation operations.
 */
class BitReaderTest {
    private BitReader bitReader;

    @BeforeEach
    void setUp() {
        bitReader = new BitReader(0);
    }

    @Test
    void testConstructorWithNoExtraBits() {
        BitReader reader = new BitReader(0);
        assertEquals(0, reader.length());
        assertEquals(0, reader.getAvailableBits());
    }

    @Test
    void testConstructorWithExtraBits() {
        BitReader reader = new BitReader(3);
        reader.append("10101010");
        assertEquals(8, reader.length());
        assertEquals(5, reader.getAvailableBits()); // 8 - 3 extra bits
    }

    @Test
    void testAppendSingleBinaryString() {
        bitReader.append("101");
        assertEquals(3, bitReader.length());
        assertEquals(3, bitReader.getAvailableBits());
    }

    @Test
    void testAppendMultipleBinaryStrings() {
        bitReader.append("101");
        bitReader.append("110");
        assertEquals(6, bitReader.length());
        assertEquals(6, bitReader.getAvailableBits());
    }

    @Test
    void testAppendEmptyString() {
        bitReader.append("");
        assertEquals(0, bitReader.length());
        assertEquals(0, bitReader.getAvailableBits());
    }

    @Test
    void testPeekValidLength() {
        bitReader.append("10101");
        String peeked = bitReader.peek(3);
        assertEquals("101", peeked);
        // Verify peek doesn't consume
        assertEquals(5, bitReader.length());
    }

    @Test
    void testPeekExceedsAvailableBits() {
        bitReader.append("101");
        String peeked = bitReader.peek(5);
        assertNull(peeked); // Not enough bits
    }

    @Test
    void testPeekWithExtraBits() {
        BitReader reader = new BitReader(2);
        reader.append("10101"); // 5 bits total, 3 available (5-2)
        String peeked = reader.peek(3);
        assertEquals("101", peeked);

        // Try to peek more than available
        String peeked2 = reader.peek(4);
        assertNull(peeked2);
    }

    @Test
    void testConsumeValidLength() {
        bitReader.append("10101");
        bitReader.consume(3);
        assertEquals(2, bitReader.length());
        assertEquals('0', bitReader.charAt(0));
        assertEquals('1', bitReader.charAt(1));
    }

    @Test
    void testConsumeAll() {
        bitReader.append("101");
        bitReader.consume(3);
        assertEquals(0, bitReader.length());
    }

    @Test
    void testConsumeMoreThanAvailable() {
        bitReader.append("101");
        bitReader.consume(10); // Should handle gracefully - won't consume if length > buffer
        assertEquals(3, bitReader.length()); // Buffer remains unchanged
    }

    @Test
    void testConsumeZero() {
        bitReader.append("101");
        bitReader.consume(0);
        assertEquals(3, bitReader.length());
    }

    @Test
    void testGetAvailableBitsWithNoExtraBits() {
        bitReader.append("10101010");
        assertEquals(8, bitReader.getAvailableBits());
    }

    @Test
    void testGetAvailableBitsWithExtraBits() {
        BitReader reader = new BitReader(3);
        reader.append("10101010");
        assertEquals(5, reader.getAvailableBits()); // 8 - 3
    }

    @Test
    void testGetAvailableBitsWhenBufferSmallerThanExtraBits() {
        BitReader reader = new BitReader(5);
        reader.append("101");
        assertEquals(0, reader.getAvailableBits()); // Max(0, 3-5) = 0
    }

    @Test
    void testLength() {
        assertEquals(0, bitReader.length());
        bitReader.append("101");
        assertEquals(3, bitReader.length());
        bitReader.append("11");
        assertEquals(5, bitReader.length());
    }

    @Test
    void testCharAt() {
        bitReader.append("10101");
        assertEquals('1', bitReader.charAt(0));
        assertEquals('0', bitReader.charAt(1));
        assertEquals('1', bitReader.charAt(2));
        assertEquals('0', bitReader.charAt(3));
        assertEquals('1', bitReader.charAt(4));
    }

    @Test
    void testCharAtThrowsExceptionForInvalidIndex() {
        bitReader.append("101");
        assertThrows(StringIndexOutOfBoundsException.class, () ->
            bitReader.charAt(5)
        );
    }

    @Test
    void testClear() {
        bitReader.append("10101010");
        bitReader.clear();
        assertEquals(0, bitReader.length());
        assertEquals(0, bitReader.getAvailableBits());
    }

    @Test
    void testClearWithExtraBits() {
        BitReader reader = new BitReader(3);
        reader.append("10101010");
        reader.clear();
        assertEquals(0, reader.length());
        assertEquals(0, reader.getAvailableBits());
    }

    @Test
    void testSequentialOperations() {
        // Simulate real usage pattern
        bitReader.append("11010110");

        // Peek first 3 bits
        assertEquals("110", bitReader.peek(3));

        // Consume them
        bitReader.consume(3);
        assertEquals(5, bitReader.length());

        // Append more bits
        bitReader.append("01");
        assertEquals(7, bitReader.length());

        // Peek and consume again
        assertEquals("101", bitReader.peek(3));
        bitReader.consume(3);
        assertEquals(4, bitReader.length());
    }

    @Test
    void testHuffmanDecodingScenario() {
        // Simulate Huffman decoding workflow
        BitReader reader = new BitReader(2); // 2 extra padding bits

        // Add first byte worth of bits
        reader.append("11010110");
        assertEquals(6, reader.getAvailableBits()); // 8 - 2

        // Decode codes of varying lengths
        String code1 = reader.peek(1);
        assertEquals("1", code1);
        reader.consume(1);

        String code2 = reader.peek(2);
        assertEquals("10", code2);
        reader.consume(2);

        String code3 = reader.peek(3);
        assertEquals("101", code3);
        reader.consume(3);

        // Try to peek beyond available (only 0 bits left available)
        assertNull(reader.peek(1));
    }

    @Test
    void testEdgeCaseEmptyBuffer() {
        assertNull(bitReader.peek(1));
        assertEquals(0, bitReader.getAvailableBits());
        bitReader.consume(1); // Should not crash
        assertEquals(0, bitReader.length());
    }

    @Test
    void testLargeBinaryString() {
        StringBuilder largeBinary = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeBinary.append(i % 2);
        }
        bitReader.append(largeBinary.toString());
        assertEquals(1000, bitReader.length());
        assertEquals(1000, bitReader.getAvailableBits());
    }

    @Test
    void testMultipleAppendAndConsumeOperations() {
        // Test buffer management with multiple operations
        for (int i = 0; i < 10; i++) {
            bitReader.append("101");
            assertEquals(3 * (i + 1), bitReader.length());
        }

        for (int i = 0; i < 10; i++) {
            bitReader.consume(3);
            assertEquals(3 * (9 - i), bitReader.length());
        }

        assertEquals(0, bitReader.length());
    }
}
