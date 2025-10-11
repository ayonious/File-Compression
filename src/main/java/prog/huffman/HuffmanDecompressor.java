package prog.huffman;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import prog.util.ByteReader;
import prog.util.ByteWriter;
import prog.util.CommonUtil;
import prog.util.Constants;

public class HuffmanDecompressor {
	static PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<HuffmanNode>();
	static int[] frequency = new int[Constants.BYTE_VALUES_COUNT];
	static String[] huffmanCodes = new String[Constants.BYTE_VALUES_COUNT]; // INT TO CODE
	static Map<String, Integer> codeToByteMap = new HashMap<>(); // Fast lookup for Huffman code to byte
	static String[] byteToString; // INT TO BIN - initialized by HuffmanUtils.createBinaryStringsForBytes()
	static String bitBuffer; // THE BIG STRING
	static int extraBits; // EXTRA BITS ADDED AT THE LAST TO MAKE THE FINAL ZIP
						// CODE MULTIPLE OF 8
	static int uniqueCharCount; // NUMBER OF freqs available

	static HuffmanNode root;

	/**********************************************************************************
	 * Frees the memory and resets all state
	 *********************************************************************************/
	public static void initHuffmanDecompressor() {
		if (root != null)
			HuffmanUtils.freeHuffmanTree(root);
		Arrays.fill(frequency, 0);
		Arrays.fill(huffmanCodes, "");
		priorityQueue.clear();
		codeToByteMap.clear();
		bitBuffer = "";
		extraBits = 0;
		uniqueCharCount = 0;
	}

	/**********************************************************************************/

	/**********************************************************************************
	 * DFS traversal to generate Huffman codes and populate the lookup map
	 *********************************************************************************/
	public static void generateHuffmanCodes(HuffmanNode node, String code) {
		node.code = code;
		if (node.isLeaf()) {
			huffmanCodes[node.byteValue] = code;
			codeToByteMap.put(code, node.byteValue);
			return;
		}
		if (node.leftChild != null)
			generateHuffmanCodes(node.leftChild, code + "0");
		if (node.rightChild != null)
			generateHuffmanCodes(node.rightChild, code + "1");
	}

	/**********************************************************************************/

	/*******************************************************************************
	 * Builds Huffman tree from frequency data using priority queue
	 *******************************************************************************/
	public static void buildHuffmanTree() {
		int i;
		uniqueCharCount = 0;
		for (i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
			if (frequency[i] != 0) {
				HuffmanNode leafNode = new HuffmanNode(i, frequency[i]);
				priorityQueue.add(leafNode);
				uniqueCharCount++;
			}
		}
		HuffmanNode leftNode, rightNode;

		if (uniqueCharCount == 0) {
			return;
		} else if (uniqueCharCount == 1) {
			for (i = 0; i < Constants.BYTE_VALUES_COUNT; i++)
				if (frequency[i] != 0) {
					huffmanCodes[i] = "0";
					codeToByteMap.put("0", i);
					break;
				}
			return;
		}

		// Combine nodes until we have a single tree
		while (priorityQueue.size() != 1) {
			leftNode = priorityQueue.poll();
			rightNode = priorityQueue.poll();
			HuffmanNode parentNode = new HuffmanNode(leftNode, rightNode);
			priorityQueue.add(parentNode);
		}
		root = priorityQueue.poll();
	}

	/*******************************************************************************/

	/*******************************************************************************
	 * Reads frequency table from compressed file and builds Huffman tree
	 ******************************************************************************/
	public static void readFrequencyTable(String compressedFilePath) {
		int frequencyValue, i;
		Byte byteValue;

		try (ByteReader reader = new ByteReader(compressedFilePath)) {
			uniqueCharCount = reader.readInt();

			for (i = 0; i < uniqueCharCount; i++) {
				byteValue = reader.readNextByte();
				frequencyValue = reader.readInt();
				frequency[CommonUtil.byteToUnsignedInt(byteValue)] = frequencyValue;
			}
		} catch (IOException e) {
			// IO Exception occurred
		}

		buildHuffmanTree(); // Building Huffman tree from frequencies
		if (uniqueCharCount > 1)
			generateHuffmanCodes(root, ""); // Generate codes via DFS traversal

		for (i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
			if (huffmanCodes[i] == null)
				huffmanCodes[i] = "";
		}
	}

	/****************************************************************************/

	/***********************************************************************************
	 * Decompresses file using Huffman codes
	 **************************************************************************************/
	public static void decompressFile(String compressedFilePath, String outputFilePath) {
		boolean codeFound;
		Byte currentByte;
		int frequencyValue, i;

		BitReader bitReader = new BitReader();

		try (ByteReader byteReader = new ByteReader(compressedFilePath);
		     ByteWriter byteWriter = new ByteWriter(outputFilePath)) {

			try {
				uniqueCharCount = byteReader.readInt();
				for (i = 0; i < uniqueCharCount; i++) {
					currentByte = byteReader.readNextByte();
					frequencyValue = byteReader.readInt();
				}
				int extraBits = byteReader.readInt();
				bitReader.setExtraBits(extraBits);
			} catch (EOFException eof) {}

			while (true) {
				currentByte = byteReader.readNextByte();
				if (currentByte == null) {
					break;
				}

				int byteAsInt = CommonUtil.byteToUnsignedInt(currentByte);
				bitReader.append(CommonUtil.padToEightBits(byteToString[byteAsInt]));

				while (true) {
					codeFound = false;
					StringBuilder codeBuilder = new StringBuilder();
					for (i = 0; i < bitReader.getAvailableBits(); i++) {
						codeBuilder.append(bitReader.charAt(i));
						String currentCode = codeBuilder.toString();
						Integer decodedByte = codeToByteMap.get(currentCode);
						if (decodedByte != null) {
							byteWriter.writeByte(decodedByte);
							codeFound = true;
							bitReader.consume(currentCode.length());
							break;
						}
					}
					if (!codeFound) break;
				}
			}

		} catch (IOException e) {
			// IO Exception occurred
		}
	}

	/************************************************************************************/
	public static void beginHuffmanDecompression(String compressedFilePath) {
		initHuffmanDecompressor();
		readFrequencyTable(compressedFilePath);
		byteToString = HuffmanUtils.createBinaryStringsForBytes();

		// Remove the .huffz extension to get the output file path
		String outputFilePath = compressedFilePath.substring(0,
			compressedFilePath.length() - Constants.HUFFMAN_FILE_EXTENSION.length());

		decompressFile(compressedFilePath, outputFilePath);
		initHuffmanDecompressor();
	}
}