package prog.lzw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prog.compression.Compressor;
import prog.util.ByteReader;
import prog.util.ByteWriter;
import prog.util.CommonUtil;
import prog.util.Constants;
import prog.util.FileUtils;

import java.io.IOException;
import java.util.Map;

public class LzwCompressor implements Compressor {
	private static final Logger logger = LoggerFactory.getLogger(LzwCompressor.class);

	/**
	 * Path to the input file that will be compressed
	 * Example: "/home/user/document.txt"
	 */
	private final String inputFilePath;


	/**
	 * Path to the output file that will be compressed
	 * Example: "/home/user/document.txt.LmZWp"
	 */
	private final String outputFilePath;

	/**
	 * Number of bits required to encode dictionary entries
	 * Calculated based on the final dictionary size after a preliminary scan
	 * Example: If dictionary grows to 512 entries, bitSize = 9
	 */
	private int bitSize;

	/**
	 * Buffer to accumulate bits before writing complete bytes
	 * Example: "101011" (6 bits accumulated, need 2 more for a complete byte)
	 */
	private String bitBuffer;

	/**
	 * Byte reader for the input file
	 */
	private ByteReader byteReader;

	/**
	 * Byte writer for the output file
	 */
	private ByteWriter byteWriter;

	/**
	 * Constructor that takes a file path and calculates the required bit size
	 * @param inputFilePath The path to the file to be compressed
	 */
	public LzwCompressor(String inputFilePath) {
		this.inputFilePath = inputFilePath;
		this.outputFilePath = inputFilePath + Constants.LZW_FILE_EXTENSION;
		logger.debug("Initializing LzwCompressor for file: {}", inputFilePath);
		if (FileUtils.isEmptyFile(inputFilePath)) {
			logger.error("Attempted to compress empty file: {}", inputFilePath);
			throw new IllegalArgumentException("Cannot compress empty file: " + inputFilePath);
		}
		this.bitSize = 0;
		this.bitBuffer = "";
		calculateBitSize();
		logger.debug("Required bit size calculated: {} bits", bitSize);

		try {
			this.byteReader = new ByteReader(inputFilePath);
			this.byteWriter = new ByteWriter(outputFilePath);
		} catch (IOException e) {
			logger.error("Failed to initialize byte reader and writer: {}", e.getMessage());
			throw new RuntimeException("Failed to initialize byte reader and writer: " + e.getMessage());
		}
	}

	/**
	 * Calculates the required bit size for encoding the dictionary.
	 *
	 * This method performs a preliminary pass through the file to build the
	 * LZW dictionary and determine the minimum number of bits needed to
	 * represent all dictionary entries. This ensures efficient encoding
	 * with the smallest possible bit width.
	 */
	private void calculateBitSize() {
		Map<String, Integer> dictionary = LzwUtils.initializeCompressionDictionary();
		int dictionarySize = Constants.BYTE_VALUES_COUNT;
		int dictionaryMemorySize = Constants.BYTE_VALUES_COUNT;
		String currentSequence = "";

		try (ByteReader reader = new ByteReader(inputFilePath)) {
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
			throw new RuntimeException("Failed to calculate bit size for file: " + inputFilePath, e);
		}

		this.bitSize = LzwUtils.calculateRequiredBits(dictionarySize);
	}
	/**
	 * Compresses the file using the LZW algorithm.
	 *
	 * <h3>Algorithm Overview:</h3>
	 * <p>The Lempel-Ziv-Welch (LZW) compression algorithm builds a dictionary of sequences
	 * encountered in the input data and replaces repeated sequences with shorter codes.</p>
	 *
	 * <h3>Compression Steps:</h3>
	 * <ol>
	 *   <li><b>Initialize dictionary</b>: Start with all single-byte sequences (0-255)</li>
	 *   <li><b>Read input</b>: Process each byte from the input file</li>
	 *   <li><b>Build sequences</b>: Extend current sequence with each new byte</li>
	 *   <li><b>Output codes</b>: When sequence not in dictionary, output code for previous sequence</li>
	 *   <li><b>Update dictionary</b>: Add new sequence to dictionary for future use</li>
	 *   <li><b>Write output</b>: Pack codes into bytes and write to compressed file</li>
	 * </ol>
	 *
	 * <h3>File Format:</h3>
	 * <pre>
	 * [4 bytes: bit size] [variable: compressed codes packed into bytes]
	 * </pre>
	 *
	 * <h3>Example:</h3>
	 * <pre>
	 * Input: "ABABABA"
	 * Dictionary starts: {0-255: single chars}
	 * - Read 'A', then 'B': output code for 'A', add "AB" to dictionary (code 256)
	 * - Read 'A', then 'B': output code for 'A', add "AB" to dictionary (already exists)
	 * - Read "AB": found in dictionary! output code 256, add "ABA" (code 257)
	 * - Read 'A': output code for 'A'
	 * Output: codes for ['A', 'B', 256, 'A']
	 * </pre>
	 *
	 * @throws RuntimeException if an IO error occurs during compression
	 */
	private void compressFile() {
		Map<String, Integer> dictionary = LzwUtils.initializeCompressionDictionary();
		int dictionarySize = Constants.BYTE_VALUES_COUNT;
		bitBuffer = "";
		int dictionaryMemorySize = Constants.BYTE_VALUES_COUNT;
		String currentSequence = "";

		logger.info("Compressing file: {} -> {}", this.inputFilePath, this.outputFilePath);
		try {
			logger.debug("Writing bit size: {}", bitSize);
			this.byteWriter.writeInt(bitSize);
			Byte currentByte;
			int unsignedByteValue;
			while ((currentByte = this.byteReader.readNextByte()) != null) {
				unsignedByteValue = CommonUtil.byteToUnsignedInt(currentByte);

				String nextSequence = currentSequence + (char) unsignedByteValue;
				if (dictionary.containsKey(nextSequence))
					currentSequence = nextSequence;
				else {
					bitBuffer += CommonUtil.integerToBinaryStringWithFixedLength(dictionary.get(currentSequence), bitSize);
					flushCompleteBytesFromBuffer();

					if (dictionaryMemorySize < Constants.MAX_DICTIONARY_MEMORY_SIZE) {
						dictionary.put(nextSequence, dictionarySize++);
						dictionaryMemorySize += nextSequence.length();
					}
					currentSequence = "" + (char) unsignedByteValue;
				}
			}

			if (!currentSequence.equals("")) {
				bitBuffer += CommonUtil.integerToBinaryStringWithFixedLength(dictionary.get(currentSequence), bitSize);
				flushCompleteBytesFromBuffer();
				if (bitBuffer.length() >= 1) {
					this.byteWriter.writeByte(CommonUtil.stringToByte(bitBuffer));
				}
			}

			logger.info("Compression completed successfully");
		} catch (IOException e) {
			logger.error("Failed to compress file: {}", inputFilePath, e);
			throw new RuntimeException("Failed to compress file: " + inputFilePath, e);
		}
	}

	/**
	 * Flushes complete bytes from the bit buffer to the output writer.
	 * Extracts and writes all complete 8-bit sequences from the buffer.
	 */
	private void flushCompleteBytesFromBuffer() throws IOException {
		while (bitBuffer.length() >= Constants.BITS_PER_BYTE) {
			this.byteWriter.writeByte(CommonUtil.stringToByte(bitBuffer.substring(0, Constants.BITS_PER_BYTE)));
			bitBuffer = bitBuffer.substring(Constants.BITS_PER_BYTE);
		}
	}

	/**
	 * Compresses the file using the pre-calculated bit size
	 * Creates a compressed file with .LmZWp extension
	 *
	 * @throws RuntimeException if compression fails
	 */
	@Override
	public void compress() {
		compressFile();
	}
}