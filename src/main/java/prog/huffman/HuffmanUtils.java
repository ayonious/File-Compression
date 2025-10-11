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
     * @return An array of 300 integers where index i contains the frequency of byte value i
     */
    public static int[] calculateFrequencyOfBytesInFile(String filename) {
        int[] frequency = new int[300];
        Byte currentByte;

        try (ByteReader reader = new ByteReader(filename)) {
            while ((currentByte = reader.readNextByte()) != null) {
                frequency[CommonUtil.byteToUnsignedInt(currentByte)]++;
            }
        } catch (IOException e) {
            // IO Exception occurred
        }
        return frequency;
    }


    /**
     * Counts the number of unique byte values with non-zero frequency.
     *
     * @param frequency Array of byte frequencies
     * @return Number of unique bytes
     */
    public static int calculateUniqueByteCount(int[] frequency) {
        return (int) Arrays.stream(frequency).filter(f -> f != 0).count();
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
        if (currentNode.leftChild != null)
            freeHuffmanTree(currentNode.leftChild);
        if (currentNode.rightChild != null)
            freeHuffmanTree(currentNode.rightChild);
    }

    /*******************************************************************************
     * Builds Huffman tree from frequency data using priority queue
     *******************************************************************************/
    public static HuffmanNode buildHuffmanTree(int[] frequency, String[] huffmanCodes) {
        int i;
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<HuffmanNode>();

        for (i = 0; i < 300; i++) {
            if (frequency[i] != 0) {
                HuffmanNode leafNode = new HuffmanNode(i, frequency[i]);
                priorityQueue.add(leafNode);
            }
        }
        HuffmanNode leftNode, rightNode;
        int uniqueByteCount = calculateUniqueByteCount(frequency);

        if (uniqueByteCount == 0) {
            return null;
        } else if (uniqueByteCount == 1) {
            for (i = 0; i < 300; i++)
                if (frequency[i] != 0) {
                    huffmanCodes[i] = "0";
                    break;
                }
            return null;
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

    /**********************************************************************************
     * DFS traversal to generate Huffman codes
     *********************************************************************************/
    public static void generateHuffmanCodes(HuffmanNode node, String code, String[] huffmanCodes) {
        node.code = code;
        if (node.isLeaf()) {
            huffmanCodes[node.byteValue] = code;
            return;
        }
        if (node.leftChild != null)
            generateHuffmanCodes(node.leftChild, code + "0", huffmanCodes);
        if (node.rightChild != null)
            generateHuffmanCodes(node.rightChild, code + "1", huffmanCodes);
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
     * String[] byteToString = HuffmanUtils.createBinaryStringsForBytes();
     * System.out.println(byteToString[5]);  // Output: "101"
     * System.out.println(byteToString[255]); // Output: "11111111"
     * }</pre>
     *
     * @return An array of Constants.BYTE_VALUES_COUNT strings where index i contains the binary representation of byte value i
     * @since 1.0
     */
    public static String[] createBinaryStringsForBytes() {
        String[] byteToString = new String[Constants.BYTE_VALUES_COUNT];
        for (int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
            byteToString[i] = CommonUtil.integerToBinaryString(i);
        }
        return byteToString;
    }
} 