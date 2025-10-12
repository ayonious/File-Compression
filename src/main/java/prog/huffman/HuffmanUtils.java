package prog.huffman;

import prog.util.ByteReader;
import prog.util.CommonUtil;
import prog.util.Constants;

import java.io.IOException;
import java.util.Arrays;
import java.util.PriorityQueue;

public class HuffmanUtils {
    /**
     * Calculates the frequency of each byte value in the given file.
     *
     * This method reads the entire file byte by byte and counts how many times
     * each unique byte value (0-255) appears in the file. This frequency data
     * is essential for building the Huffman tree, as more frequent bytes will
     * be assigned shorter codes.
     *
     * @param filename The path to the file to analyze
     * @return An array of 256 integers where index i contains the frequency of byte value i
     * @throws RuntimeException if an IO error occurs while reading the file
     */
    public static int[] calculateFrequencyOfBytesInFile(String filename) {
        int[] frequency = new int[Constants.BYTE_VALUES_COUNT];
        Byte currentByte;

        try (ByteReader reader = new ByteReader(filename)) {
            while ((currentByte = reader.readNextByte()) != null) {
                frequency[CommonUtil.byteToUnsignedInt(currentByte)]++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to calculate frequency for file: " + filename, e);
        }
        return frequency;
    }


    /**
     * Counts the number of unique byte values with non-zero frequency.
     *
     * Examples:
     * - [0,0,0,...] returns 0 (empty file)
     * - [5,0,0,...] returns 1 (single unique byte)
     * - [3,2,1,0,...] returns 3 (three unique bytes)
     * - [1,1,1,1,...(256 times)] returns 256 (all possible bytes)
     *
     * @param frequency Array of byte frequencies
     * @return Number of unique bytes
     */
    public static int calculateUniqueByteCount(int[] frequency) {
        return (int) Arrays.stream(frequency).filter(f -> f != 0).count();
    }

    /**
     * Checks if the frequency array represents an empty file.
     *
     * Examples:
     * - [0,0,0,...] returns true (no bytes in file)
     * - [1,0,0,...] returns false (file has content)
     * - [5,3,2,...] returns false (file has multiple bytes)
     * - Arrays.fill(freq, 0) returns true (explicitly zeroed array)
     *
     * @param frequency Array of byte frequencies
     * @return true if file is empty, false otherwise
     */
    public static boolean isEmptyFile(int[] frequency) {
        return calculateUniqueByteCount(frequency) == 0;
    }

    /**
     * Recursively frees memory by traversing the Huffman tree.
     *
     * This method performs a depth-first search traversal of the Huffman tree
     * to help with garbage collection. While Java has automatic garbage collection,
     * this method can help break circular references in the tree structure.
     *
     * @param currentNode The current node to process in the tree traversal
     */
    public static void freeHuffmanTree(HuffmanNode currentNode) {
        if (currentNode.isLeaf()) {
            return;
        }
        if (currentNode.getLeftChild() != null)
            freeHuffmanTree(currentNode.getLeftChild());
        if (currentNode.getRightChild() != null)
            freeHuffmanTree(currentNode.getRightChild());
    }

    /**
     * Builds Huffman tree from frequency data using priority queue.
     *
     * Examples:
     * - [0,0,0,...] returns null (empty file)
     * - [5,0,0,...] returns special single-node tree
     * - [3,2,0,...] returns tree with 2 leaf nodes
     * - [5,9,12,13,...] returns balanced tree based on frequencies
     *
     * @param frequency Array of byte frequencies
     * @return Root of Huffman tree or null if empty
     */
    public static HuffmanNode buildHuffmanTree(int[] frequency) {
        int i;
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<HuffmanNode>();

        for (i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
            if (frequency[i] != 0) {
                HuffmanNode leafNode = new HuffmanNode(i, frequency[i]);
                priorityQueue.add(leafNode);
            }
        }
        HuffmanNode leftNode, rightNode;
        int uniqueByteCount = calculateUniqueByteCount(frequency);

        if(uniqueByteCount == 0) return null;

        if (uniqueByteCount == 1) {
            HuffmanNode leafNode = priorityQueue.poll();
            return new HuffmanNode(leafNode, null);
        }

        // Combine nodes until we have a single tree
        while (priorityQueue.size() != 1) {
            leftNode = priorityQueue.poll();
            rightNode = priorityQueue.poll();
            HuffmanNode parentNode = new HuffmanNode(leftNode, rightNode);
            priorityQueue.add(parentNode);
        }
        HuffmanNode root = priorityQueue.poll();
        return root;
    }

    /**
     * DFS traversal to generate Huffman codes for each byte value.
     *
     * Examples:
     * - Leaf node with byteValue=65 and code="01" sets huffmanCodes[65]="01"
     * - Left child gets parent code + "0"
     * - Right child gets parent code + "1"
     * - Root node starts with empty string ""
     *
     * @param node Current node in traversal
     * @param currentCode Code accumulated from root to current node
     * @param huffmanCodes Output array to store codes
     */
    public static void generateHuffmanCodes(HuffmanNode node, String currentCode, String[] huffmanCodes) {
        node.setCode(currentCode);
        if (node.isLeaf()) {
            huffmanCodes[node.getByteValue()] = currentCode;
            return;
        }
        if (node.getLeftChild() != null)
            generateHuffmanCodes(node.getLeftChild(), currentCode + "0", huffmanCodes);
        if (node.getRightChild() != null)
            generateHuffmanCodes(node.getRightChild(), currentCode + "1", huffmanCodes);
    }

    public static String[] generateHuffmanCodes(HuffmanNode node) {
        String[] huffmanCodes = new String[Constants.BYTE_VALUES_COUNT];
        generateHuffmanCodes(node, "", huffmanCodes);
        return huffmanCodes;
    }

    /**
     * Creates an array of binary string representations for all byte values (0-255).
     *
     * This utility method generates a lookup table that maps each possible byte value
     * to its binary string representation. This is used for efficient byte-to-binary
     * conversion during decompression operations.
     *
     * <h3>Implementation Details:</h3>
     * <ul>
     *   <li>Generates binary strings for values 0-255</li>
     *   <li>Uses standard binary conversion (least significant bit first, then reverses)</li>
     *   <li>Special case: 0 is represented as "0" (single digit)</li>
     *   <li>Does NOT pad strings to 8 bits (e.g., 5 becomes "101" not "00000101")</li>
     * </ul>
     *
     * <h3>Example Output:</h3>
     * <pre>
     * Index | Binary String
     * ------|---------------
     *   0   | "0"
     *   1   | "1"
     *   2   | "10"
     *   3   | "11"
     *   4   | "100"
     *   5   | "101"
     *  ...  | ...
     *  255  | "11111111"
     * </pre>
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * String[] byteToBinaryStrings = HuffmanUtils.createBinaryStringsForBytes();
     * System.out.println(byteToBinaryStrings[5]);  // Output: "101"
     * System.out.println(byteToBinaryStrings[255]); // Output: "11111111"
     * }</pre>
     *
     * @return An array of Constants.BYTE_VALUES_COUNT strings where index i contains the binary representation of byte value i
     */
    public static String[] createBinaryStringsForBytes() {
        String[] byteToBinaryStrings = new String[Constants.BYTE_VALUES_COUNT];
        for (int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
            byteToBinaryStrings[i] = CommonUtil.integerToBinaryString(i);
        }
        return byteToBinaryStrings;
    }
} 