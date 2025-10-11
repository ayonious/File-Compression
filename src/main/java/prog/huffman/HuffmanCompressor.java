package prog.huffman;

import java.io.IOException;

import prog.util.ByteReader;
import prog.util.ByteWriter;
import prog.util.CommonUtil;
import prog.util.Constants;

//if the frequency of a byte is more than 2^32 then there will be problem
public class HuffmanCompressor {
	/**********************************************************************************
	 * Frees the memory by cleaning up Huffman tree
	 *********************************************************************************/
	public static void initHuffmanCompressor(HuffmanNode root) {
		if (root != null) HuffmanUtils.freeHuffmanTree(root);
	}

	/**********************************************************************************/

	/**
	 * Step 1: Write the table size
	 */
	private static void writeTableSize(ByteWriter writer, int[] frequency) throws IOException {
		writer.writeInt(HuffmanUtils.calculateUniqueByteCount(frequency));
	}

	/**
	 * Step 2: Write the frequency table
	 */
	private static void writeFrequencyTable(ByteWriter writer, int[] frequency) throws IOException {
		for (int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
			if (frequency[i] != 0) {
				byte currentByte = (byte) i;
				writer.writeByte(currentByte);
				writer.writeInt(frequency[i]);
			}
		}
	}

	/**
	 * Step 3: Calculate and write extra bits needed for padding
	 */
	private static void writeExtraBits(ByteWriter writer, int[] frequency, String[] huffmanCodes) throws IOException {
		int totalBinaryDigitsMod8 = 0;
		for(int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
			if (huffmanCodes[i] != null) {
				totalBinaryDigitsMod8 += huffmanCodes[i].length() * frequency[i];
				totalBinaryDigitsMod8 %= Constants.BITS_PER_BYTE;
			}
		}
		int extraBits = (Constants.BITS_PER_BYTE - totalBinaryDigitsMod8) % Constants.BITS_PER_BYTE;
		writer.writeInt(extraBits);
	}

	/**
	 * Step4: Write the huffman codes
	 */
	private static void writeTableHuffmanCodes(ByteReader reader, ByteWriter writer, String[] huffmanCodes) throws IOException {
		StringBuilder bitBuffer = new StringBuilder();
		Byte currentByte;

		while ((currentByte = reader.readNextByte()) != null) {
			String huffmanCodeOfCurrentByte = huffmanCodes[CommonUtil.byteToUnsignedInt(currentByte)];
			bitBuffer.append(huffmanCodeOfCurrentByte);

			while(bitBuffer.length() >= Constants.BITS_PER_BYTE) {
				writer.writeByte(CommonUtil.stringToByte(bitBuffer.substring(0, Constants.BITS_PER_BYTE)));
				bitBuffer.delete(0, Constants.BITS_PER_BYTE);
			}
		}

		if (bitBuffer.length() != 0) {
			writer.writeByte(CommonUtil.stringToByte(bitBuffer.toString()));
		}
	}

	public static void compressFile(String inputFilePath, String outputFilePath, int[] frequency, String[] huffmanCodes) {
		try (ByteReader reader = new ByteReader(inputFilePath);
		     ByteWriter writer = new ByteWriter(outputFilePath)) {

			// Step1: Write the table size
			writeTableSize(writer, frequency);

			// Step2: Write the table
			writeFrequencyTable(writer, frequency);

			// Step3: Write extra bits needed for padding
			writeExtraBits(writer, frequency, huffmanCodes);

			// Step4: Write the huffman codes
			writeTableHuffmanCodes(reader, writer, huffmanCodes);

		} catch (IOException e) {
			// IO Exception occurred
		}
	}

	public static void beginHuffmanCompression(String inputFilePath) {
		initHuffmanCompressor(null);
		int[] frequency = HuffmanUtils.calculateFrequencyOfBytesInFile(inputFilePath); // Calculate the frequency of each byte
		String[] huffmanCodes = new String[Constants.BYTE_VALUES_COUNT];
		HuffmanNode huffmanTree = HuffmanUtils.buildHuffmanTree(frequency, huffmanCodes); // Build Huffman tree from frequencies
		if (HuffmanUtils.calculateUniqueByteCount(frequency) > 1)
			HuffmanUtils.generateHuffmanCodes(huffmanTree, "", huffmanCodes); // Generate codes via DFS traversal
		compressFile(inputFilePath, inputFilePath + Constants.HUFFMAN_FILE_EXTENSION, frequency, huffmanCodes); // Compress the file
		initHuffmanCompressor(huffmanTree);
	}
}