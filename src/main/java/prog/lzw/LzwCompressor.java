package prog.lzw;

import prog.util.ByteReader;
import prog.util.ByteWriter;
import prog.util.CommonUtil;
import prog.util.Constants;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LzwCompressor {

	public static int bitSize;
	public static String bitBuffer;

	/**
	 * Initializes an LZW dictionary with all single-byte characters (0-255).
	 *
	 * @return Map with single characters mapped to their byte values
	 */
	private static Map<String, Integer> initializeCompressionDictionary() {
		Map<String, Integer> dictionary = new HashMap<String, Integer>();
		for (int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
			dictionary.put("" + (char) i, i);
		}
		return dictionary;
	}

	/**
	 * Calculates the minimum number of bits needed to represent a given dictionary size.
	 *
	 * @param dictionarySize The size of the dictionary
	 * @return The number of bits required
	 */
	private static int calculateRequiredBits(int dictionarySize) {
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

	/**
	 * Calculates the required bit size for encoding the dictionary.
	 *
	 * This method performs a preliminary pass through the file to build the
	 * LZW dictionary and determine the minimum number of bits needed to
	 * represent all dictionary entries. This ensures efficient encoding
	 * with the smallest possible bit width.
	 *
	 * @param filename The path to the file to analyze
	 */
	public static void calculateBitSize(String filename) {
		Map<String, Integer> dictionary = initializeCompressionDictionary();
		int dictionarySize = Constants.BYTE_VALUES_COUNT;
		int dictionaryMemorySize = Constants.BYTE_VALUES_COUNT;
		String currentSequence = "";

		try (ByteReader reader = new ByteReader(filename)) {
			Byte currentByte;
			int unsignedByteValue;
			while ((currentByte = reader.readNextByte()) != null) {
				unsignedByteValue = CommonUtil.byteToUnsignedInt(currentByte);
				String nextSequence = currentSequence + (char) unsignedByteValue;
				if (dictionary.containsKey(nextSequence))
					currentSequence = nextSequence;
				else {
					if (dictionaryMemorySize < Constants.MAX_DICTIONARY_MEMORY_SIZE) {
						dictionary.put(nextSequence, dictionarySize++);
						dictionaryMemorySize += nextSequence.length();
					}
					currentSequence = "" + (char) unsignedByteValue;
				}
			}
		} catch (IOException e) {
			// IO Exception occurred
		}

		bitSize = calculateRequiredBits(dictionarySize);
	}
	/**
	 * Compresses a file using the LZW algorithm.
	 *
	 * This method implements the Lempel-Ziv-Welch compression algorithm, which
	 * builds a dictionary of frequently occurring patterns and replaces them
	 * with shorter codes. The compressed file is saved with a .LmZWp extension.
	 *
	 * @param filename The path to the file to compress
	 */
	public static void compressFile(String filename) {
		Map<String, Integer> dictionary = initializeCompressionDictionary();
		int dictionarySize = Constants.BYTE_VALUES_COUNT;
		bitBuffer = "";
		int dictionaryMemorySize = Constants.BYTE_VALUES_COUNT;
		String currentSequence = "";
		String outputFilePath = filename + Constants.LZW_FILE_EXTENSION;

		try (ByteReader reader = new ByteReader(filename);
		     ByteWriter writer = new ByteWriter(outputFilePath)) {

			writer.writeInt(bitSize);
			Byte currentByte;
			int unsignedByteValue;
			while ((currentByte = reader.readNextByte()) != null) {
				unsignedByteValue = CommonUtil.byteToUnsignedInt(currentByte);

				String nextSequence = currentSequence + (char) unsignedByteValue;
				if (dictionary.containsKey(nextSequence))
					currentSequence = nextSequence;
				else {
					bitBuffer += CommonUtil.integerToBinaryStringWithFixedLength(dictionary.get(currentSequence), bitSize);
					flushCompleteBytesFromBuffer(writer);

					if (dictionaryMemorySize < Constants.MAX_DICTIONARY_MEMORY_SIZE) {
						dictionary.put(nextSequence, dictionarySize++);
						dictionaryMemorySize += nextSequence.length();
					}
					currentSequence = "" + (char) unsignedByteValue;
				}
			}

			if (!currentSequence.equals("")) {
				bitBuffer += CommonUtil.integerToBinaryStringWithFixedLength(dictionary.get(currentSequence), bitSize);
				flushCompleteBytesFromBuffer(writer);
				if (bitBuffer.length() >= 1) {
					writer.writeByte(CommonUtil.stringToByte(bitBuffer));
				}
			}
		} catch (IOException e) {
			// IO Exception occurred
		}
	}

	/**
	 * Flushes complete bytes from the bit buffer to the output writer.
	 * Extracts and writes all complete 8-bit sequences from the buffer.
	 */
	private static void flushCompleteBytesFromBuffer(ByteWriter writer) throws IOException {
		while (bitBuffer.length() >= Constants.BITS_PER_BYTE) {
			writer.writeByte(CommonUtil.stringToByte(bitBuffer.substring(0, Constants.BITS_PER_BYTE)));
			bitBuffer = bitBuffer.substring(Constants.BITS_PER_BYTE);
		}
	}

	public static void beginLzwCompression(String filePath) {
		bitSize = 0;
		bitBuffer = "";
		calculateBitSize(filePath);
		compressFile(filePath);
		bitSize = 0;
		bitBuffer = "";
	}
}