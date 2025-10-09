package prog.util;

/**
 * Common utility class for byte and binary string conversions.
 *
 * This utility class provides essential conversion methods used throughout the
 * file compression application, particularly for the LZW compression algorithm.
 * It handles the conversion between different data representations that are
 * crucial for encoding and decoding compressed data.
 *
 */
public class CommonUtil {

    /**
     * Converts a signed byte to an unsigned integer value (0-255).
     *
     * In Java, bytes are signed (-128 to 127), but in many compression algorithms,
     * we need to treat bytes as unsigned values (0 to 255). This method performs
     * that conversion by adding 256 to negative values.
     *
     * Example usage:
     * - byteToUnsignedInt((byte) 65) returns 65 (ASCII 'A')
     * - byteToUnsignedInt((byte) -1) returns 255
     * - byteToUnsignedInt((byte) -128) returns 128
     *
     * @param byteValue The signed byte value to convert
     * @return The unsigned integer representation (0-255)
     */
    public static int byteToUnsignedInt(Byte byteValue) {
        int result = byteValue;
        if (result < 0) {
            result += 256;
        }
        return result;
    }

    /**
     * Converts a binary string to its decimal integer representation.
     *
     * This method parses a string containing only '0' and '1' characters and
     * converts it to its corresponding decimal integer value. It's commonly used
     * in the decompression process to convert binary encoded data back to integers.
     *
     * The conversion is performed from left to right (most significant bit first).
     *
     * Example usage:
     * - binaryStringToInt("0") returns 0
     * - binaryStringToInt("1010") returns 10
     * - binaryStringToInt("11111111") returns 255
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
}