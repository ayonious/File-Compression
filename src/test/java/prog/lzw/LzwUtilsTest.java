package prog.lzw;

import org.junit.jupiter.api.Test;
import prog.util.CommonUtil;

import static org.junit.jupiter.api.Assertions.*;

class LzwUtilsTest {

    @Test
    void testIntToBinaryString() {
        // Test with bitSize = 8
        assertEquals("00000000", CommonUtil.integerToBinaryStringWithFixedLength(0, 8));
        assertEquals("00000001", CommonUtil.integerToBinaryStringWithFixedLength(1, 8));
        assertEquals("00001010", CommonUtil.integerToBinaryStringWithFixedLength(10, 8));
        assertEquals("11111111", CommonUtil.integerToBinaryStringWithFixedLength(255, 8));

        // Test with bitSize = 4
        assertEquals("0000", CommonUtil.integerToBinaryStringWithFixedLength(0, 4));
        assertEquals("0001", CommonUtil.integerToBinaryStringWithFixedLength(1, 4));
        assertEquals("1111", CommonUtil.integerToBinaryStringWithFixedLength(15, 4));

        // Test with bitSize = 12
        assertEquals("000000000101", CommonUtil.integerToBinaryStringWithFixedLength(5, 12));
    }

    @Test
    void testStringToByte() {
        assertEquals((byte) 0, CommonUtil.stringToByte("00000000"));
        assertEquals((byte) 1, CommonUtil.stringToByte("00000001"));
        assertEquals((byte) -1, CommonUtil.stringToByte("11111111"));
        assertEquals((byte) 65, CommonUtil.stringToByte("01000001")); // 'A'
        assertEquals((byte) 10, CommonUtil.stringToByte("00001010"));
    }

    @Test
    void testStringToByteWithPadding() {
        // Test shorter strings (should pad with zeros)
        assertEquals((byte) 0, CommonUtil.stringToByte("0"));
        assertEquals((byte) 64, CommonUtil.stringToByte("01")); // 01000000
        assertEquals((byte) 32, CommonUtil.stringToByte("001")); // 00100000
    }
}
