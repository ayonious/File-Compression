package prog.huffman;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prog.compression.Compressor;
import prog.util.ByteReader;
import prog.util.ByteWriter;
import prog.util.CommonUtil;
import prog.util.Constants;

//if the frequency of a byte is more than 2^32 then there will be problem
public class HuffmanCompressor implements Compressor {
	private static final Logger logger = LoggerFactory.getLogger(HuffmanCompressor.class);

	/**
	 * Path to the input file that will be compressed
	 * Example: "/home/user/document.txt"
	 */
	private final String inputFilePath;

	/**
	 * Path to the output file that will be compressed
	 * Example: "/home/user/document.txt.huffz"
	 */
	private final String outputFilePath;

	/**
	 * Array storing the frequency of each byte value (0-255) in the input file
	 * Index represents the byte value, value represents the count
	 * Example: frequency[65] = 10 means byte 'A' (ASCII 65) appears 10 times
	 */
	private final int[] frequency;

	/**
	 * Array storing the Huffman code for each byte value
	 * Index represents the byte value, value is the binary string code
	 * Example: huffmanCodes[65] = "101" means byte 'A' is encoded as "101"
	 * More frequent bytes get shorter codes (e.g., "0" or "10")
	 * Less frequent bytes get longer codes (e.g., "110101")
	 */
	private final String[] huffmanCodes;

	/**
	 * Root node of the Huffman tree used for generating the codes
	 * Built from the frequency data using a priority queue
	 * Left branches represent '0', right branches represent '1'
	 */
	private HuffmanNode huffmanTree;


	/**
	 * Byte reader for the input file
	 */
	private ByteReader byteReader;

	/**
	 * Byte writer for the output file
	 */
	private ByteWriter byteWriter;

	/**
	 * Constructor that takes a file path and generates Huffman codes and frequency
	 * @param inputFilePath The path to the file to be compressed
	 */
	public HuffmanCompressor(String inputFilePath) {
		logger.debug("Initializing HuffmanCompressor for file: {}", inputFilePath);
		this.inputFilePath = inputFilePath;
		this.outputFilePath = inputFilePath + Constants.HUFFMAN_FILE_EXTENSION;
		this.frequency = HuffmanUtils.calculateFrequencyOfBytesInFile(inputFilePath);

		if (HuffmanUtils.isEmptyFile(frequency)) {
			logger.error("Attempted to compress empty file: {}", inputFilePath);
			throw new IllegalArgumentException("Cannot compress empty file: " + inputFilePath);
		}

		logger.debug("Building Huffman tree");
		this.huffmanTree = HuffmanUtils.buildHuffmanTree(frequency);
		this.huffmanCodes = HuffmanUtils.generateHuffmanCodes(huffmanTree);

		try {
			this.byteReader = new ByteReader(inputFilePath);
			this.byteWriter = new ByteWriter(outputFilePath);
		} catch (IOException e) {
			logger.error("Failed to initialize byte reader and writer: {}", e.getMessage());
			throw new RuntimeException("Failed to initialize byte reader and writer: " + e.getMessage());
		}

		logger.debug("Huffman codes generated successfully");
	}
	/**********************************************************************************/

	/**
	 * Step 1: Write the table size
	 */
	private void writeTableSize() throws IOException {
		this.byteWriter.writeInt(HuffmanUtils.calculateUniqueByteCount(this.frequency));
	}

	/**
	 * Step 2: Write the frequency table
	 */
	private void writeFrequencyTable() throws IOException {
		for (int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
			if (this.frequency[i] != 0) {
				byte currentByte = (byte) i;
				this.byteWriter.writeByte(currentByte);
				this.byteWriter.writeInt(this.frequency[i]);
			}
		}
	}

	/**
	 * Step 3: Calculate and write extra bits needed for padding
	 */
	private void writeExtraBits() throws IOException {
		int totalBinaryDigitsMod8 = 0;
		for(int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
			if (this.huffmanCodes[i] != null) {
				totalBinaryDigitsMod8 += this.huffmanCodes[i].length() * this.frequency[i];
				totalBinaryDigitsMod8 %= Constants.BITS_PER_BYTE;
			}
		}
		int extraBits = (Constants.BITS_PER_BYTE - totalBinaryDigitsMod8) % Constants.BITS_PER_BYTE;
		this.byteWriter.writeInt(extraBits);
	}

	/**
	 * Step 4: Encode and write the compressed content using Huffman codes
	 * Reads each byte from input, converts to its Huffman code, and writes compressed output
	 */
	private void encodeAndWriteContent() throws IOException {
		StringBuilder bitBuffer = new StringBuilder();
		Byte currentByte;

		while ((currentByte = this.byteReader.readNextByte()) != null) {
			String huffmanCodeOfCurrentByte = this.huffmanCodes[CommonUtil.byteToUnsignedInt(currentByte)];
			bitBuffer.append(huffmanCodeOfCurrentByte);

			while(bitBuffer.length() >= Constants.BITS_PER_BYTE) {
				this.byteWriter.writeByte(CommonUtil.stringToByte(bitBuffer.substring(0, Constants.BITS_PER_BYTE)));
				bitBuffer.delete(0, Constants.BITS_PER_BYTE);
			}
		}

		if (bitBuffer.length() != 0) {
			this.byteWriter.writeByte(CommonUtil.stringToByte(bitBuffer.toString()));
		}
	}

	private void compressFile() {
		logger.info("Compressing file: {} -> {}", inputFilePath, outputFilePath);
		try {
			// Step1: Write the table size
			logger.debug("Writing frequency table");
			writeTableSize();

			// Step2: Write the table
			writeFrequencyTable();

			// Step3: Write extra bits needed for padding
			logger.debug("Writing padding information");
			writeExtraBits();

			// Step4: Encode and write the compressed content
			logger.debug("Encoding and writing compressed content");
			encodeAndWriteContent();

			logger.info("Compression completed successfully");
		} catch (IOException e) {
			logger.error("Failed to compress file: {}", inputFilePath, e);
			throw new RuntimeException("Failed to compress file: " + inputFilePath, e);
		}
	}

	/**
	 * Compresses the file using the pre-calculated Huffman codes
	 * Creates a compressed file with .huffz extension
	 *
	 * @throws RuntimeException if compression fails
	 */
	@Override
	public void compress() {
		compressFile();
	}

	/**
	 * Clean up resources by freeing the Huffman tree from memory
	 */
	@Override
	public void cleanup() {
		if (huffmanTree != null) {
			HuffmanUtils.freeHuffmanTree(huffmanTree);
		}
	}
}