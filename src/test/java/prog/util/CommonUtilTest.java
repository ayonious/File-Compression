package prog.util;

import org.junit.jupiter.api.Test;
import prog.huffman.HuffmanUtils;
import prog.util.Constants;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the CommonUtil class.
 * Tests both the new method names and deprecated methods for backward compatibility.
 */
public class CommonUtilTest {

    @Test
    void testByteToUnsignedIntConversion() {
        // Test edge cases
        assertEquals(0, CommonUtil.byteToUnsignedInt((byte) 0), "Zero byte should convert to 0");
        assertEquals(127, CommonUtil.byteToUnsignedInt((byte) 127), "Max positive byte should convert to 127");

        // Test common ASCII values
        assertEquals(65, CommonUtil.byteToUnsignedInt((byte) 65), "ASCII 'A' should convert to 65");
        assertEquals(90, CommonUtil.byteToUnsignedInt((byte) 90), "ASCII 'Z' should convert to 90");
        assertEquals(48, CommonUtil.byteToUnsignedInt((byte) 48), "ASCII '0' should convert to 48");

        // Test negative byte values (should convert to positive values 128-255)
        assertEquals(128, CommonUtil.byteToUnsignedInt((byte) -128), "Min byte should convert to 128");
        assertEquals(200, CommonUtil.byteToUnsignedInt((byte) -56), "Negative byte -56 should convert to 200");
        assertEquals(255, CommonUtil.byteToUnsignedInt((byte) -1), "-1 byte should convert to 255");
    }

    @Test
    void testBinaryStringToIntConversion() {
        // Test single bit values
        assertEquals(0, CommonUtil.binaryStringToInt("0"), "Binary '0' should convert to 0");
        assertEquals(1, CommonUtil.binaryStringToInt("1"), "Binary '1' should convert to 1");

        // Test multi-bit values
        assertEquals(2, CommonUtil.binaryStringToInt("10"), "Binary '10' should convert to 2");
        assertEquals(3, CommonUtil.binaryStringToInt("11"), "Binary '11' should convert to 3");
        assertEquals(10, CommonUtil.binaryStringToInt("1010"), "Binary '1010' should convert to 10");
        assertEquals(15, CommonUtil.binaryStringToInt("1111"), "Binary '1111' should convert to 15");

        // Test byte boundary values
        assertEquals(255, CommonUtil.binaryStringToInt("11111111"), "8 bits all ones should convert to 255");
        assertEquals(1023, CommonUtil.binaryStringToInt("1111111111"), "10 bits all ones should convert to 1023");

        // Test with leading zeros
        assertEquals(10, CommonUtil.binaryStringToInt("00001010"), "Binary with leading zeros should work correctly");
    }

    @Test
    void testBinaryStringToIntWithInvalidInput() {
        // Test invalid characters
        assertThrows(IllegalArgumentException.class,
            () -> CommonUtil.binaryStringToInt("10201"),
            "Should throw exception for non-binary character '2'");

        assertThrows(IllegalArgumentException.class,
            () -> CommonUtil.binaryStringToInt("abc"),
            "Should throw exception for non-binary characters");
    }

    @Test
    void testAllPossibleByteValues() {
        // Comprehensive test for all Constants.BYTE_VALUES_COUNT possible byte values
        for (int i = -128; i <= 127; i++) {
            byte b = (byte) i;
            int unsigned = CommonUtil.byteToUnsignedInt(b);

            // Verify the result is in the valid range [0, 255]
            assertTrue(unsigned >= 0 && unsigned <= 255,
                String.format("Byte %d converted to %d (should be 0-255)", i, unsigned));

            // Verify specific conversions
            if (i >= 0) {
                assertEquals(i, unsigned,
                    String.format("Positive byte %d should convert to itself", i));
            } else {
                assertEquals(i + Constants.BYTE_VALUES_COUNT, unsigned,
                    String.format("Negative byte %d should convert to %d", i, i + Constants.BYTE_VALUES_COUNT));
            }
        }
    }

    @Test
    void testReverseString() {
        // Test empty string
        assertEquals("", CommonUtil.reverseString(""), "Empty string should return empty string");

        // Test single character
        assertEquals("a", CommonUtil.reverseString("a"), "Single character should return itself");

        // Test palindromes
        assertEquals("racecar", CommonUtil.reverseString("racecar"), "Palindrome should remain the same");
        assertEquals("noon", CommonUtil.reverseString("noon"), "Palindrome should remain the same");

        // Test regular strings
        assertEquals("olleh", CommonUtil.reverseString("hello"), "hello reversed should be olleh");
        assertEquals("dlrow", CommonUtil.reverseString("world"), "world reversed should be dlrow");
        assertEquals("321", CommonUtil.reverseString("123"), "123 reversed should be 321");

        // Test with special characters
        assertEquals("!dlroW ,olleH", CommonUtil.reverseString("Hello, World!"),
            "Should handle punctuation correctly");
        assertEquals("54321 ", CommonUtil.reverseString(" 12345"),
            "Should handle spaces correctly");

        // Test binary strings (common use case in compression)
        assertEquals("101010", CommonUtil.reverseString("010101"),
            "Binary string should be reversed correctly");
        assertEquals("11110000", CommonUtil.reverseString("00001111"),
            "Binary string with different patterns");
    }

    @Test
    void testIntegerToBinaryString() {
        // Test zero
        assertEquals("0", CommonUtil.integerToBinaryString(0), "Zero should convert to '0'");

        // Test powers of 2
        assertEquals("1", CommonUtil.integerToBinaryString(1), "1 should convert to '1'");
        assertEquals("10", CommonUtil.integerToBinaryString(2), "2 should convert to '10'");
        assertEquals("100", CommonUtil.integerToBinaryString(4), "4 should convert to '100'");
        assertEquals("1000", CommonUtil.integerToBinaryString(8), "8 should convert to '1000'");
        assertEquals("10000", CommonUtil.integerToBinaryString(16), "16 should convert to '10000'");
        assertEquals("10000000", CommonUtil.integerToBinaryString(128), "128 should convert to '10000000'");

        // Test common values
        assertEquals("11", CommonUtil.integerToBinaryString(3), "3 should convert to '11'");
        assertEquals("101", CommonUtil.integerToBinaryString(5), "5 should convert to '101'");
        assertEquals("111", CommonUtil.integerToBinaryString(7), "7 should convert to '111'");
        assertEquals("1010", CommonUtil.integerToBinaryString(10), "10 should convert to '1010'");
        assertEquals("1111", CommonUtil.integerToBinaryString(15), "15 should convert to '1111'");

        // Test byte boundary values
        assertEquals("11111111", CommonUtil.integerToBinaryString(255),
            "255 (max byte value) should convert to '11111111'");
        assertEquals("100000000", CommonUtil.integerToBinaryString(256),
            "256 should convert to '100000000'");

        // Test larger values
        assertEquals("1111111111", CommonUtil.integerToBinaryString(1023),
            "1023 should convert to '1111111111'");
        assertEquals("10000000000", CommonUtil.integerToBinaryString(1024),
            "1024 should convert to '10000000000'");

        // Test some random values
        assertEquals("1000001", CommonUtil.integerToBinaryString(65),
            "65 (ASCII 'A') should convert to '1000001'");
        assertEquals("1100100", CommonUtil.integerToBinaryString(100),
            "100 should convert to '1100100'");
    }

    @Test
    void testIntegerToBinaryStringRoundTrip() {
        // Test that conversion back and forth works correctly
        int[] testValues = {0, 1, 2, 3, 7, 8, 15, 16, 31, 32, 63, 64, 127, 128, 255, 256, 511, 512, 1023, 1024};

        for (int value : testValues) {
            String binary = CommonUtil.integerToBinaryString(value);
            int result = CommonUtil.binaryStringToInt(binary);
            assertEquals(value, result,
                String.format("Round-trip conversion failed for %d -> %s -> %d", value, binary, result));
        }

        // Test with random values
        for (int i = 0; i < 100; i++) {
            String binary = CommonUtil.integerToBinaryString(i);
            int result = CommonUtil.binaryStringToInt(binary);
            assertEquals(i, result,
                String.format("Round-trip conversion failed for %d", i));
        }
    }

    @Test
    void testReverseStringPerformance() {
        // Test with longer strings to ensure the algorithm works correctly
        String longString = "a".repeat(1000);
        String reversed = CommonUtil.reverseString(longString);
        assertEquals(1000, reversed.length(), "Length should be preserved");
        assertEquals('a', reversed.charAt(0), "All characters should be 'a'");
        assertEquals('a', reversed.charAt(999), "All characters should be 'a'");

        // Test with a pattern
        String pattern = "0123456789".repeat(100);
        String expectedReverse = "9876543210".repeat(100);
        assertEquals(expectedReverse, CommonUtil.reverseString(pattern),
            "Pattern should be reversed correctly");
    }

    @Test
    void testIntegerToBinaryStringWithFixedLength() {
        // Test zero with various lengths
        assertEquals("0", CommonUtil.integerToBinaryStringWithFixedLength(0, 1),
            "0 with length 1 should be '0'");
        assertEquals("00000000", CommonUtil.integerToBinaryStringWithFixedLength(0, 8),
            "0 with length 8 should be '00000000'");
        assertEquals("0000000000000000", CommonUtil.integerToBinaryStringWithFixedLength(0, 16),
            "0 with length 16 should be all zeros");

        // Test powers of 2 with standard 8-bit length
        assertEquals("00000001", CommonUtil.integerToBinaryStringWithFixedLength(1, 8),
            "1 with length 8 should be '00000001'");
        assertEquals("00000010", CommonUtil.integerToBinaryStringWithFixedLength(2, 8),
            "2 with length 8 should be '00000010'");
        assertEquals("00000100", CommonUtil.integerToBinaryStringWithFixedLength(4, 8),
            "4 with length 8 should be '00000100'");
        assertEquals("00001000", CommonUtil.integerToBinaryStringWithFixedLength(8, 8),
            "8 with length 8 should be '00001000'");
        assertEquals("00010000", CommonUtil.integerToBinaryStringWithFixedLength(16, 8),
            "16 with length 8 should be '00010000'");
        assertEquals("10000000", CommonUtil.integerToBinaryStringWithFixedLength(128, 8),
            "128 with length 8 should be '10000000'");

        // Test common values
        assertEquals("00000101", CommonUtil.integerToBinaryStringWithFixedLength(5, 8),
            "5 with length 8 should be '00000101'");
        assertEquals("00001010", CommonUtil.integerToBinaryStringWithFixedLength(10, 8),
            "10 with length 8 should be '00001010'");
        assertEquals("00001111", CommonUtil.integerToBinaryStringWithFixedLength(15, 8),
            "15 with length 8 should be '00001111'");

        // Test byte boundary values
        assertEquals("11111111", CommonUtil.integerToBinaryStringWithFixedLength(255, 8),
            "255 with length 8 should be '11111111'");
        assertEquals("01111111", CommonUtil.integerToBinaryStringWithFixedLength(127, 8),
            "127 with length 8 should be '01111111'");

        // Test with different fixed lengths
        assertEquals("0101", CommonUtil.integerToBinaryStringWithFixedLength(5, 4),
            "5 with length 4 should be '0101'");
        assertEquals("1010", CommonUtil.integerToBinaryStringWithFixedLength(10, 4),
            "10 with length 4 should be '1010'");
        assertEquals("1111", CommonUtil.integerToBinaryStringWithFixedLength(15, 4),
            "15 with length 4 should be '1111'");

        // Test with 12-bit length (common in LZW)
        assertEquals("000000000000", CommonUtil.integerToBinaryStringWithFixedLength(0, 12),
            "0 with length 12 should be all zeros");
        assertEquals("000000000001", CommonUtil.integerToBinaryStringWithFixedLength(1, 12),
            "1 with length 12 should have 11 leading zeros");
        assertEquals("000011111111", CommonUtil.integerToBinaryStringWithFixedLength(255, 12),
            "255 with length 12 should be '000011111111'");
        assertEquals("111111111111", CommonUtil.integerToBinaryStringWithFixedLength(4095, 12),
            "4095 with length 12 should be all ones");

        // Test exact length match (no padding needed)
        assertEquals("101", CommonUtil.integerToBinaryStringWithFixedLength(5, 3),
            "5 with exact length 3 should be '101'");
        assertEquals("11111111", CommonUtil.integerToBinaryStringWithFixedLength(255, 8),
            "255 with exact length 8 should be '11111111'");

        // Test with larger values and 16-bit length
        assertEquals("0000000100000000", CommonUtil.integerToBinaryStringWithFixedLength(256, 16),
            "256 with length 16 should be '0000000100000000'");
        assertEquals("0000001111111111", CommonUtil.integerToBinaryStringWithFixedLength(1023, 16),
            "1023 with length 16 should be '0000001111111111'");
        assertEquals("0000010000000000", CommonUtil.integerToBinaryStringWithFixedLength(1024, 16),
            "1024 with length 16 should be '0000010000000000'");
    }

    @Test
    void testIntegerToBinaryStringWithFixedLengthRoundTrip() {
        // Test that conversion with fixed length can be converted back
        int[] testValues = {0, 1, 2, 3, 7, 8, 15, 16, 31, 32, 63, 64, 127, 128, 255};
        int[] testLengths = {8, 12, 16};

        for (int value : testValues) {
            for (int length : testLengths) {
                String binary = CommonUtil.integerToBinaryStringWithFixedLength(value, length);
                assertEquals(length, binary.length(),
                    String.format("Binary string for %d with length %d should have exactly %d characters",
                        value, length, length));

                int result = CommonUtil.binaryStringToInt(binary);
                assertEquals(value, result,
                    String.format("Round-trip conversion failed for %d with length %d -> %s -> %d",
                        value, length, binary, result));
            }
        }
    }

    @Test
    void testIntegerToBinaryStringWithFixedLengthEdgeCases() {
        // Test minimum length (1 bit)
        assertEquals("0", CommonUtil.integerToBinaryStringWithFixedLength(0, 1),
            "0 with length 1 should work");
        assertEquals("1", CommonUtil.integerToBinaryStringWithFixedLength(1, 1),
            "1 with length 1 should work");

        // Test that leading zeros are properly added
        String result = CommonUtil.integerToBinaryStringWithFixedLength(1, 10);
        assertEquals(10, result.length(), "Result should have exactly 10 characters");
        assertEquals("0000000001", result, "1 with length 10 should have 9 leading zeros");

        // Test ASCII values with 8-bit representation
        assertEquals("01000001", CommonUtil.integerToBinaryStringWithFixedLength(65, 8),
            "ASCII 'A' (65) with length 8 should be '01000001'");
        assertEquals("01011010", CommonUtil.integerToBinaryStringWithFixedLength(90, 8),
            "ASCII 'Z' (90) with length 8 should be '01011010'");
    }

    @Test
    void testPadBinaryString() {
        // Test padding to 8 bits
        assertEquals("00000000", CommonUtil.padBinaryString("", 8));
        assertEquals("00000001", CommonUtil.padBinaryString("1", 8));
        assertEquals("00000101", CommonUtil.padBinaryString("101", 8));
        assertEquals("11111111", CommonUtil.padBinaryString("11111111", 8));

        // Test padding to different lengths
        assertEquals("0001", CommonUtil.padBinaryString("1", 4));
        assertEquals("000000000001", CommonUtil.padBinaryString("1", 12));
        assertEquals("0000000000001111", CommonUtil.padBinaryString("1111", 16));

        // Test no padding needed (already at target length)
        assertEquals("1010", CommonUtil.padBinaryString("1010", 4));
        assertEquals("11111111", CommonUtil.padBinaryString("11111111", 8));

        // Test no padding needed (longer than target)
        assertEquals("11111111", CommonUtil.padBinaryString("11111111", 4));
        assertEquals("10101010", CommonUtil.padBinaryString("10101010", 4));
    }

    @Test
    void testPadToEightBits() {
        // Test empty string
        assertEquals("00000000", CommonUtil.padToEightBits(""));

        // Test single digit
        assertEquals("00000001", CommonUtil.padToEightBits("1"));

        // Test multiple digits
        assertEquals("00000101", CommonUtil.padToEightBits("101"));

        // Test string of length 8
        assertEquals("11111111", CommonUtil.padToEightBits("11111111"));

        // Test shorter strings
        assertEquals("00001010", CommonUtil.padToEightBits("1010"));
        assertEquals("00110011", CommonUtil.padToEightBits("110011"));

        // Test all zeros
        assertEquals("00000000", CommonUtil.padToEightBits("0"));
    }

    @Test
    void testCreateByteToBinaryLookupTable() {
        String[] lookupTable = CommonUtil.createByteToBinaryLookupTable();

        // Test table size
        assertEquals(256, lookupTable.length, "Lookup table should have 256 entries");

        // Test known conversions
        assertEquals("00000000", lookupTable[0], "Entry for 0");
        assertEquals("00000001", lookupTable[1], "Entry for 1");
        assertEquals("01000001", lookupTable[65], "Entry for 65 (ASCII 'A')");
        assertEquals("10000000", lookupTable[128], "Entry for 128");
        assertEquals("11111111", lookupTable[255], "Entry for 255");

        // Test that all entries have correct length
        for (int i = 0; i < 256; i++) {
            assertEquals(8, lookupTable[i].length(),
                String.format("Entry %d should have length 8", i));
        }

        // Test round-trip conversion
        for (int i = 0; i < 256; i++) {
            int result = CommonUtil.binaryStringToInt(lookupTable[i]);
            assertEquals(i, result,
                String.format("Round-trip conversion failed for %d", i));
        }
    }

}