package prog.lzw;

import prog.util.ByteReader;
import prog.util.ByteWriter;
import prog.util.CommonUtil;
import prog.util.Constants;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LzwDecompressor {

	public static int bitSize;
	public static String[] byteToBinaryLookup;
	public static String bitBuffer;

	/**
	 * Initializes an LZW decompression dictionary with all single-byte characters (0-255).
	 *
	 * @return Map with byte values mapped to their character representations
	 */
	private static Map<Integer, String> initializeDecompressionDictionary() {
		Map<Integer, String> dictionary = new HashMap<Integer, String>();
		for (int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
			dictionary.put(i, "" + (char) i);
		}
		return dictionary;
	}

	/**
	 * Writes a string to the output as individual bytes.
	 *
	 * @param writer The output writer
	 * @param text The text to write
	 * @throws IOException If writing fails
	 */
	private static void writeString(ByteWriter writer, String text) throws IOException {
		for (char ch : text.toCharArray()) {
			writer.writeByte((byte) ch);
		}
	}

	public static void decompressFile(String filename) {
		int code;
		int dictionarySize = Constants.BYTE_VALUES_COUNT;
		int dictionaryMemorySize = Constants.BYTE_VALUES_COUNT;
		String newEntry;
		Map<Integer, String> dictionary = initializeDecompressionDictionary();

		String outputFilePath = filename.substring(0, filename.length() - Constants.LZW_FILE_EXTENSION.length());

		try (ByteReader reader = new ByteReader(filename);
		     ByteWriter writer = new ByteWriter(outputFilePath)) {

			Byte currentByte;
			bitSize = reader.readInt();

			// Read initial bits to get first code
			while ((currentByte = reader.readNextByte()) != null) {
				bitBuffer += byteToBinaryLookup[CommonUtil.byteToUnsignedInt(currentByte)];
				if (bitBuffer.length() >= bitSize)
					break;
			}

			if (bitBuffer.length() >= bitSize) {
				code = CommonUtil.binaryStringToInt(bitBuffer.substring(0, bitSize));
				bitBuffer = bitBuffer.substring(bitSize);
			} else {
				return;
			}

			String previousEntry = "" + (char) code;
			writeString(writer, previousEntry);

			// Process remaining codes
			while (true) {
				// Fill buffer with enough bits for next code
				while (bitBuffer.length() < bitSize) {
					currentByte = reader.readNextByte();
					if (currentByte == null) break;
					bitBuffer += byteToBinaryLookup[CommonUtil.byteToUnsignedInt(currentByte)];
				}

				if (bitBuffer.length() < bitSize) break;

				code = CommonUtil.binaryStringToInt(bitBuffer.substring(0, bitSize));
				bitBuffer = bitBuffer.substring(bitSize);

				String currentEntry = "";
				if (dictionary.containsKey(code)) {
					currentEntry = dictionary.get(code);
				} else if (code == dictionarySize) {
					currentEntry = previousEntry + previousEntry.charAt(0);
				}

				writeString(writer, currentEntry);

				if (dictionaryMemorySize < Constants.MAX_DICTIONARY_MEMORY_SIZE) {
					newEntry = previousEntry + currentEntry.charAt(0);
					dictionary.put(dictionarySize++, newEntry);
					dictionaryMemorySize += newEntry.length();
				}
				previousEntry = currentEntry;
			}
		} catch (IOException e) {
			// IO Exception occurred
		}
	}

	public static void beginLzwDecompression(String filePath) {
		bitBuffer = "";
		bitSize = 0;
		byteToBinaryLookup = CommonUtil.createByteToBinaryLookupTable();
		decompressFile(filePath);
		bitBuffer = "";
		bitSize = 0;
	}
}