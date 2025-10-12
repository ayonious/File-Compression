package prog.lzw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prog.compression.Decompressor;
import prog.util.ByteReader;
import prog.util.ByteWriter;
import prog.util.CommonUtil;
import prog.util.Constants;
import prog.util.FileUtils;
import java.io.IOException;
import java.util.Map;

public class LzwDecompressor implements Decompressor {
	private static final Logger logger = LoggerFactory.getLogger(LzwDecompressor.class);

	/**
	 * Path to the compressed file that will be decompressed
	 * Example: "/home/user/document.txt.LmZWp"
	 */
	private final String compressedFilePath;

	/**
	 * Path to the output file that will be decompressed
	 * Example: "/home/user/document.txt"
	 */
	private final String outputFilePath;

	/**
	 * Number of bits used to encode dictionary entries
	 * Read from the compressed file header
	 * Example: 9 bits can represent dictionary entries 0-511
	 */
	private int bitSize;

	/**
	 * Lookup table for fast conversion of bytes to 8-bit binary strings
	 * Example: byteToBinaryLookup[65] = "01000001" (byte 65 = 'A')
	 */
	private String[] byteToBinaryLookup;

	/**
	 * Buffer to accumulate bits while reading compressed data
	 * Example: "1010110" (7 bits accumulated from reading compressed bytes)
	 */
	private String bitBuffer;

	/**
	 * Byte reader for the compressed file
	 */
	private ByteReader byteReader;

	/**
	 * Byte writer for the decompressed output file
	 */
	private ByteWriter byteWriter;

	/**
	 * Constructor that takes a compressed file path
	 * @param compressedFilePath The path to the compressed file to be decompressed
	 */
	public LzwDecompressor(String compressedFilePath) {
		logger.debug("Initializing LzwDecompressor for file: {}", compressedFilePath);
		this.compressedFilePath = compressedFilePath;
		this.outputFilePath = FileUtils.getUniqueFilePath(compressedFilePath.substring(0,
			compressedFilePath.length() - Constants.LZW_FILE_EXTENSION.length()));
		this.bitSize = 0;
		this.bitBuffer = "";
		this.byteToBinaryLookup = CommonUtil.createByteToBinaryLookupTable();

		try {
			this.byteReader = new ByteReader(compressedFilePath);
			this.byteWriter = new ByteWriter(outputFilePath);
		} catch (IOException e) {
			logger.error("Failed to initialize byte reader and writer: {}", e.getMessage());
			throw new RuntimeException("Failed to initialize byte reader and writer: " + e.getMessage());
		}
	}

	/**
	 * Writes a string to the output as individual bytes.
	 *
	 * @param text The text to write
	 * @throws IOException If writing fails
	 */
	private void writeString(String text) throws IOException {
		for (char ch : text.toCharArray()) {
			this.byteWriter.writeByte((byte) ch);
		}
	}

	/**
	 * Decompresses a file using the LZW algorithm.
	 *
	 * <h3>Algorithm Overview:</h3>
	 * <p>The LZW decompression algorithm rebuilds the original data by reading compressed codes
	 * and reconstructing the dictionary that was built during compression. The key insight is
	 * that both compressor and decompressor build the same dictionary in the same order.</p>
	 *
	 * <h3>Decompression Steps:</h3>
	 * <ol>
	 *   <li><b>Read bit size</b>: Determine how many bits are used for each code</li>
	 *   <li><b>Initialize dictionary</b>: Start with all single-byte sequences (0-255)</li>
	 *   <li><b>Read first code</b>: Output the corresponding character</li>
	 *   <li><b>Process codes</b>: For each subsequent code:
	 *     <ul>
	 *       <li>If code exists in dictionary: output the sequence</li>
	 *       <li>If code doesn't exist (special case): it's previous + first char of previous</li>
	 *       <li>Add new sequence to dictionary: previous + first char of current</li>
	 *     </ul>
	 *   </li>
	 *   <li><b>Write output</b>: Write decompressed bytes to output file</li>
	 * </ol>
	 *
	 * <h3>File Format:</h3>
	 * <pre>
	 * [4 bytes: bit size] [variable: compressed codes packed into bytes]
	 * </pre>
	 *
	 * <h3>Example:</h3>
	 * <pre>
	 * Input codes: [65, 66, 256, 65] (where 65='A', 66='B')
	 * Dictionary starts: {0-255: single chars}
	 * - Code 65: output 'A', previous = "A"
	 * - Code 66: output 'B', add "AB" (256) to dict, previous = "B"
	 * - Code 256: output "AB", add "BA" (257) to dict, previous = "AB"
	 * - Code 65: output 'A', add "ABA" (258) to dict
	 * Output: "ABABA"
	 * </pre>
	 *
	 * <h3>Special Case - Code Not in Dictionary:</h3>
	 * <p>When a code is encountered that's not yet in the dictionary, it means the code being
	 * added is the one we're currently processing. In this case, the sequence is:
	 * previous sequence + first character of previous sequence.</p>
	 *
	 * @throws RuntimeException if an IO error occurs during decompression
	 */
	private void decompressFile() {
		logger.info("Decompressing file: {} -> {}", compressedFilePath, outputFilePath);
		int code;
		int dictionarySize = Constants.BYTE_VALUES_COUNT;
		int dictionaryMemorySize = Constants.BYTE_VALUES_COUNT;
		String newEntry;
		Map<Integer, String> dictionary = LzwUtils.initializeDecompressionDictionary();

		try {
			Byte currentByte;
			bitSize = this.byteReader.readInt();
			logger.debug("Reading compressed data with bit size: {}", bitSize);

			// Read initial bits to get first code
			while ((currentByte = this.byteReader.readNextByte()) != null) {
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
			writeString(previousEntry);

			// Process remaining codes
			while (true) {
				// Fill buffer with enough bits for next code
				while (bitBuffer.length() < bitSize) {
					currentByte = this.byteReader.readNextByte();
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

				writeString(currentEntry);

				if (dictionaryMemorySize < Constants.MAX_DICTIONARY_MEMORY_SIZE) {
					newEntry = previousEntry + currentEntry.charAt(0);
					dictionary.put(dictionarySize++, newEntry);
					dictionaryMemorySize += newEntry.length();
				}
				previousEntry = currentEntry;
			}

			logger.info("Decompression completed successfully");
		} catch (IOException e) {
			logger.error("Failed to decompress file: {}", compressedFilePath, e);
			throw new RuntimeException("Failed to decompress file: " + compressedFilePath, e);
		}
	}

	/**
	 * Decompresses the file using the LZW algorithm
	 * Creates a decompressed file by removing the .LmZWp extension
	 * If a file with the same name already exists, creates a unique filename
	 *
	 * @throws RuntimeException if decompression fails
	 */
	@Override
	public void decompress() {
		decompressFile();
	}
}