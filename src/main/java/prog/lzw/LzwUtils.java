package prog.lzw;

import prog.util.Constants;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class containing shared methods for LZW compression and decompression.
 * Provides common functionality like dictionary initialization and bit calculations.
 */
public class LzwUtils {

	/**
	 * Initializes an LZW compression dictionary with all single-byte characters (0-255).
	 * Each character is mapped to its byte value as the initial code.
	 *
	 * Example entries:
	 * - "A" (char 65) -> 65
	 * - "B" (char 66) -> 66
	 * - " " (char 32) -> 32
	 * - All 256 possible byte values are pre-populated
	 *
	 * @return Map with single characters mapped to their byte values (0-255)
	 */
	public static Map<String, Integer> initializeCompressionDictionary() {
		Map<String, Integer> dictionary = new HashMap<>();
		for (int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
			dictionary.put("" + (char) i, i);
		}
		return dictionary;
	}

	/**
	 * Initializes an LZW decompression dictionary with all single-byte characters (0-255).
	 * Each byte value is mapped to its character representation.
	 *
	 * Example entries:
	 * - 65 -> "A"
	 * - 66 -> "B"
	 * - 32 -> " "
	 * - All 256 possible byte values are pre-populated
	 *
	 * @return Map with byte values (0-255) mapped to their character representations
	 */
	public static Map<Integer, String> initializeDecompressionDictionary() {
		Map<Integer, String> dictionary = new HashMap<>();
		for (int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
			dictionary.put(i, "" + (char) i);
		}
		return dictionary;
	}

	/**
	 * Calculates the minimum number of bits needed to represent a given dictionary size.
	 *
	 * This uses the formula: bits = ceil(log2(dictionarySize))
	 *
	 * Examples:
	 * - dictionarySize = 1 -> 1 bit (can represent 0-1)
	 * - dictionarySize = 256 -> 8 bits (can represent 0-255)
	 * - dictionarySize = 257 -> 9 bits (can represent 0-511)
	 * - dictionarySize = 512 -> 9 bits (can represent 0-511)
	 * - dictionarySize = 513 -> 10 bits (can represent 0-1023)
	 * - dictionarySize = 65536 -> 16 bits
	 *
	 * @param dictionarySize The size of the dictionary
	 * @return The number of bits required to represent all dictionary entries
	 */
	public static int calculateRequiredBits(int dictionarySize) {
		if (dictionarySize <= 1) {
			return 1;
		}
		int bits = 0;
		long powerOfTwo = 1;
		while (powerOfTwo < dictionarySize) {
			powerOfTwo *= 2;
			bits++;
		}
		return bits;
	}
}
