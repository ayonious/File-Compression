package prog.util;

/**
 * Common utility class for byte and binary string conversions.
 * Provides essential conversion methods used throughout the file compression application.
 */
public class CommonUtil {

    /**
     * Converts a signed byte to an unsigned integer value (0-255).
     *
     * Examples:
     * - byteToUnsignedInt((byte) 0) returns 0
     * - byteToUnsignedInt((byte) 65) returns 65  // 'A'
     * - byteToUnsignedInt((byte) 127) returns 127
     * - byteToUnsignedInt((byte) -1) returns 255
     * - byteToUnsignedInt((byte) -128) returns 128
     * - byteToUnsignedInt((byte) -50) returns 206
     *
     * @param byteValue The signed byte value to convert
     * @return The unsigned integer representation (0-255)
     */
    public static int byteToUnsignedInt(Byte byteValue) {
        int result = byteValue;
        if (result < 0) {
            result += Constants.BYTE_VALUES_COUNT;
        }
        return result;
    }

    /**
     * Converts a binary string to its decimal integer representation.
     *
     * Examples:
     * - binaryStringToInt("0") returns 0
     * - binaryStringToInt("1") returns 1
     * - binaryStringToInt("1010") returns 10
     * - binaryStringToInt("11111111") returns 255
     * - binaryStringToInt("10000000") returns 128
     * - binaryStringToInt("101010") returns 42
     *
     * @param binaryString The binary string to convert (must contain only '0' and '1')
     * @return The decimal integer representation of the binary string
     * @throws IllegalArgumentException if the string contains non-binary characters
     */
    public static int binaryStringToInt(String binaryString) {
        int result = 0;
        for (int i = 0; i < binaryString.length(); i++) {
            result *= 2;
            char bit = binaryString.charAt(i);
            if (bit == '1') {
                result++;
            } else if (bit != '0') {
                throw new IllegalArgumentException(
                    "Invalid binary string: contains character '" + bit + "' at position " + i);
            }
        }
        return result;
    }


    /**
     * Reverses a string character by character.
     *
     * Examples:
     * - reverseString("hello") returns "olleh"
     * - reverseString("123") returns "321"
     * - reverseString("abc") returns "cba"
     * - reverseString("a") returns "a"
     * - reverseString("") returns ""
     *
     * @param input The string to reverse
     * @return The reversed string
     */
    public static String reverseString(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = input.length() - 1; i >= 0; i--) {
            result.append(input.charAt(i));
        }
        return result.toString();
    }


    /**
     * Converts an integer to its binary string representation (no padding).
     *
     * Examples:
     * - integerToBinaryString(0) returns "0"
     * - integerToBinaryString(1) returns "1"
     * - integerToBinaryString(5) returns "101"
     * - integerToBinaryString(10) returns "1010"
     * - integerToBinaryString(255) returns "11111111"
     * - integerToBinaryString(42) returns "101010"
     *
     * @param input The integer to convert
     * @return The binary string representation
     */
    public static String integerToBinaryString(int input) {
        if (input == 0) {
            return "0";
        }

        StringBuilder result = new StringBuilder();
        int value = input;

        while (value != 0) {
            if (value % 2 == 1) {
                result.append("1");
            } else {
                result.append("0");
            }
            value /= 2;
        }
        return CommonUtil.reverseString(result.toString());
    }

    /**
     * Pads a binary string with leading zeros to reach the specified length.
     *
     * Examples:
     * - padBinaryString("", 8) returns "00000000"
     * - padBinaryString("1", 8) returns "00000001"
     * - padBinaryString("101", 8) returns "00000101"
     * - padBinaryString("1010", 4) returns "1010"
     * - padBinaryString("11111111", 8) returns "11111111"
     * - padBinaryString("1", 12) returns "000000000001"
     *
     * @param binaryString The binary string to pad
     * @param length The target length
     * @return Binary string padded to specified length with leading zeros
     */
    public static String padBinaryString(String binaryString, int length) {
        return String.format("%" + length + "s", binaryString).replace(' ', '0');
    }

    /**
     * Converts an integer to its binary string representation with padding.
     *
     * Examples:
     * - integerToBinaryStringWithFixedLength(0, 8) returns "00000000"
     * - integerToBinaryStringWithFixedLength(5, 8) returns "00000101"
     * - integerToBinaryStringWithFixedLength(10, 8) returns "00001010"
     * - integerToBinaryStringWithFixedLength(255, 8) returns "11111111"
     * - integerToBinaryStringWithFixedLength(15, 4) returns "1111"
     * - integerToBinaryStringWithFixedLength(1, 12) returns "000000000001"
     *
     * @param input The integer to convert
     * @param length The target bit width (result will be padded with leading zeros)
     * @return Binary string padded to specified length
     */
    public static String integerToBinaryStringWithFixedLength(int input, int length) {
        return padBinaryString(integerToBinaryString(input), length);
    }

    /**
     * Converts a binary string to a byte value.
     *
     * Examples:
     * - stringToByte("00000000") returns (byte) 0
     * - stringToByte("00000001") returns (byte) 1
     * - stringToByte("01000001") returns (byte) 65  // 'A'
     * - stringToByte("10110011") returns (byte) 179
     * - stringToByte("11111111") returns (byte) -1
     * - stringToByte("0") returns (byte) 0  // Padded to 8 bits
     * - stringToByte("01") returns (byte) 64  // Becomes "01000000"
     *
     * @param binaryString Binary string (up to 8 bits, shorter strings are padded with trailing zeros)
     * @return Byte representation of the binary string
     */
    public static Byte stringToByte(String binaryString) {
        int i, stringLength = binaryString.length();
        byte byteValue = 0;
        for (i = 0; i < stringLength; i++) {
            byteValue *= 2;
            if (binaryString.charAt(i) == '1')
                byteValue++;
        }
        for (; stringLength < Constants.BITS_PER_BYTE; stringLength++)
            byteValue *= 2;
        return byteValue;
    }

    /**
     * Pads a binary string to 8 digits with leading zeros.
     * Convenience method for the common case of padding to byte length.
     *
     * Examples:
     * - padToEightBits("") returns "00000000"
     * - padToEightBits("1") returns "00000001"
     * - padToEightBits("101") returns "00000101"
     * - padToEightBits("1010") returns "00001010"
     * - padToEightBits("110011") returns "00110011"
     * - padToEightBits("11111111") returns "11111111"
     *
     * @param binaryString The binary string to pad
     * @return An 8-character binary string with leading zeros
     */
    public static String padToEightBits(String binaryString) {
        return padBinaryString(binaryString, Constants.BITS_PER_BYTE);
    }

    /**
     * Initializes a lookup table for converting bytes to 8-bit binary strings.
     * Pre-computing these values improves performance for operations requiring
     * frequent byte-to-binary conversions.
     *
     * Examples of lookup table entries:
     * - table[0] = "00000000"
     * - table[1] = "00000001"
     * - table[65] = "01000001"  // 'A'
     * - table[128] = "10000000"
     * - table[255] = "11111111"
     *
     * @return Array mapping byte values (0-255) to their 8-bit binary string representations
     */
    public static String[] createByteToBinaryLookupTable() {
        String[] lookupTable = new String[Constants.BYTE_VALUES_COUNT];
        for (int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
            lookupTable[i] = integerToBinaryStringWithFixedLength(i, Constants.BITS_PER_BYTE);
        }
        return lookupTable;
    }
}