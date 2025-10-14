package prog.huffman;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prog.compression.Decompressor;
import prog.util.ByteReader;
import prog.util.ByteWriter;
import prog.util.CommonUtil;
import prog.util.Constants;
import prog.util.FileUtils;

public class HuffmanDecompressor implements Decompressor {
	private static final Logger logger = LoggerFactory.getLogger(HuffmanDecompressor.class);

	/**
	 * Path to the compressed file that will be decompressed
	 * Example: "/home/user/document.txt.huffz"
	 */
	private final String compressedFilePath;

	/**
	 * Map from Huffman code strings to their corresponding byte values
	 * Key: Binary string code (e.g., "101", "0", "110")
	 * Value: The original byte value (0-255) that the code represents
	 * Example: {"101" -> 65, "0" -> 32} means "101" decodes to 'A', "0" to space
	 */
	private final Map<String, Integer> huffmancodeToByteMap;

	/**
	 * Path to the output file after decompression
	 */
	private final String outputFilePath;

	/**
	 * Byte reader for the compressed file
	 */
	private ByteReader byteReader;

	/**
	 * Byte writer for the decompressed output file
	 */
	private ByteWriter byteWriter;

	/**
	 * Root node of the Huffman tree
	 */
	private HuffmanNode huffmanTreeRoot;

	/**
	 * Constructor that takes a compressed file path and generates the Huffman code mapping
	 * @param compressedFilePath The path to the compressed file to be decompressed
	 */
	public HuffmanDecompressor(String compressedFilePath) {
		logger.debug("Initializing HuffmanDecompressor for file: {}", compressedFilePath);
		this.compressedFilePath = compressedFilePath;
		// Remove the .huffz extension to get the output file path
		this.outputFilePath = FileUtils.getUniqueFilePath(compressedFilePath.substring(0,
			compressedFilePath.length() - Constants.HUFFMAN_FILE_EXTENSION.length()));
		this.huffmancodeToByteMap = generateHuffmanCodesFromZipFile(compressedFilePath);

		try {
			this.byteReader = new ByteReader(compressedFilePath);
			this.byteWriter = new ByteWriter(outputFilePath);
		} catch (IOException e) {
			logger.error("Failed to initialize byte reader and writer: {}", e.getMessage());
			throw new RuntimeException("Failed to initialize byte reader and writer: " + e.getMessage());
		}

		logger.debug("Huffman code mapping generated successfully");
	}
	/*******************************************************************************
	 * Reads frequency table from compressed file and builds Huffman tree
	 ******************************************************************************/
	private Map<String, Integer> generateHuffmanCodesFromZipFile(String compressedFilePath) {
		int frequencyValue, i;
		Byte byteValue;
		int[] frequency = new int[Constants.BYTE_VALUES_COUNT];
		Map<String, Integer> huffmancodeToByteMap = new HashMap<>(); // Fast lookup for Huffman code to byte
		try (ByteReader reader = new ByteReader(compressedFilePath)) {
			int uniqueCharCount = reader.readInt();
			for (i = 0; i < uniqueCharCount; i++) {
				byteValue = reader.readNextByte();
				frequencyValue = reader.readInt();
				frequency[CommonUtil.byteToUnsignedInt(byteValue)] = frequencyValue;
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read frequency table from compressed file: " + compressedFilePath, e);
		}
		huffmanTreeRoot = HuffmanUtils.buildHuffmanTree(frequency);
		String[] huffmanCodes = HuffmanUtils.generateHuffmanCodes(huffmanTreeRoot);
		for(i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
			if(huffmanCodes[i] != null && huffmanCodes[i].length() > 0) {
				huffmancodeToByteMap.put(huffmanCodes[i], i);
			}
		}
		return huffmancodeToByteMap;
	}

	/**
	 * Step 1: Read the number of unique characters from the compressed file header.
	 *
	 * @return The number of unique byte values in the original file
	 * @throws IOException If reading fails
	 */
	private int getUniqueCharCount() throws IOException {
		return this.byteReader.readInt();
	}

	/**
	 * Step 2: Skip frequency table and read the number of extra padding bits.
	 *
	 * This method skips over the frequency table entries and reads the padding bits value
	 * that indicates how many bits were added to make the compressed data byte-aligned.
	 *
	 * @param uniqueCharCount Number of unique characters to skip in the frequency table
	 * @return The number of padding bits (0-7) used in compression
	 * @throws IOException If reading fails
	 */
	private int getExtraBits(int uniqueCharCount) throws IOException {
		int i;
		for (i = 0; i < uniqueCharCount; i++) {
			this.byteReader.readNextByte();
			this.byteReader.readInt();
		}
		return this.byteReader.readInt();
	}

	/**
	 * Step 3: Process compressed bytes and decode them using Huffman codes.
	 *
	 * This method reads compressed bytes, converts them to bits, and decodes them
	 * using the Huffman code mapping to reconstruct the original data.
	 *
	 * @param bitReader Buffer for accumulating and processing bits
	 * @throws IOException If reading or writing fails
	 */
	private void processCompressedBytes(BitReader bitReader) throws IOException {
		String[] byteToBinaryStrings = HuffmanUtils.createBinaryStringsForBytes();
		HuffmanNode currentNode = huffmanTreeRoot;
		while (true) {
			Byte currentByte = this.byteReader.readNextByte();
			if (currentByte == null) break;

			int byteAsInt = CommonUtil.byteToUnsignedInt(currentByte);
			bitReader.append(CommonUtil.padToEightBits(byteToBinaryStrings[byteAsInt]));

			while (true) {
				boolean codeFound = false;
				int i;
				StringBuilder codeBuilder = new StringBuilder();
				for (i = 0; i < bitReader.getAvailableBits(); i++) {
					codeBuilder.append(bitReader.charAt(i));
					String currentCode = codeBuilder.toString();
					Integer decodedByte = this.huffmancodeToByteMap.get(currentCode);
					if (decodedByte != null) {
						this.byteWriter.writeByte(decodedByte);
						codeFound = true;
						bitReader.consume(currentCode.length());
						break;
					}
				}
				if (!codeFound) break;
			}
		}
	}

	/***********************************************************************************
	 * Decompresses file using Huffman codes
	 **************************************************************************************/
	private void decompressFile() {
		logger.info("Decompressing file: {} -> {}", compressedFilePath, outputFilePath);
		try {
			// Step1: Read the unique char count
			logger.debug("Reading frequency table");
			int uniqueCharCount = getUniqueCharCount();
			// Step2: Read the extra padding bits
			int extraBits = getExtraBits(uniqueCharCount);
			BitReader bitReader = new BitReader(extraBits);
			// Step3: Process the compressed bytes
			logger.debug("Decoding compressed content");
			processCompressedBytes(bitReader);
			logger.info("Decompression completed successfully");
		} catch (IOException e) {
			logger.error("Failed to decompress file: {}", compressedFilePath, e);
			throw new RuntimeException("Failed to decompress file: " + compressedFilePath, e);
		}
	}

	/**
	 * Decompresses the file using the pre-calculated Huffman codes
	 * Creates a decompressed file by removing the .huffz extension
	 * If a file with the same name already exists, creates a unique filename
	 *
	 * @throws RuntimeException if decompression fails
	 */
	@Override
	public void decompress() {
		decompressFile();
	}
}