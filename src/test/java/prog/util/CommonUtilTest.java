package prog.util;

import org.junit.jupiter.api.Test;
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

}