package prog.huffman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import prog.util.CommonUtil;
import prog.util.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class HuffmanUtilsTest {
    @TempDir
    Path tempDir;
    private File inputFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary input file for testing
        inputFile = tempDir.resolve("testInput.txt").toFile();
    }

    @Test
    void testFrequencyCalculation() throws IOException {
        // Create test file
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("aabbc");
        }

        int[] frequency = HuffmanUtils.calculateFrequencyOfBytesInFile(inputFile.getAbsolutePath());

        // Check frequencies
        assertEquals(2, frequency['a']);
        assertEquals(2, frequency['b']);
        assertEquals(1, frequency['c']);
    }

    @Test
    void testMakeEight() {
        // Test empty string
        assertEquals("00000000", CommonUtil.padToEightBits(""));

        // Test single digit
        assertEquals("00000001", CommonUtil.padToEightBits("1"));

        // Test multiple digits
        assertEquals("00000101", CommonUtil.padToEightBits("101"));

        // Test string of length 8
        assertEquals("11111111", CommonUtil.padToEightBits("11111111"));

        // Test shorter strings
        assertEquals("00001010", CommonUtil.padToEightBits("1010"));
        assertEquals("00110011", CommonUtil.padToEightBits("110011"));
    }

    @Test
    void testFreeDfsWithHuffmanTree() {
        // Create a simple HuffmanNode tree structure
        HuffmanNode root = new HuffmanNode();
        HuffmanNode left = new HuffmanNode();
        HuffmanNode right = new HuffmanNode();

        root.setLeftChild(left);
        root.setRightChild(right);
        root.setByteValue(1);
        left.setByteValue(2);
        right.setByteValue(3);

        // Test that no exception is thrown when traversing
        assertDoesNotThrow(() ->
            HuffmanUtils.freeHuffmanTree(root)
        );
    }

    @Test
    void testFreeDfsWithLeafNode() {
        // Create a leaf node (no children)
        HuffmanNode leaf = new HuffmanNode();
        leaf.setByteValue(65);
        leaf.setFrequency(5);

        // Should handle leaf nodes without error
        assertDoesNotThrow(() ->
            HuffmanUtils.freeHuffmanTree(leaf)
        );
    }

    @Test
    void testFreeDfsWithComplexTree() {
        // Create a more complex tree structure
        //       root
        //      /    \
        //    left   right
        //    / \      / \
        //   l1 l2    r1 r2
        HuffmanNode root = new HuffmanNode();
        HuffmanNode left = new HuffmanNode();
        HuffmanNode right = new HuffmanNode();
        HuffmanNode l1 = new HuffmanNode();
        HuffmanNode l2 = new HuffmanNode();
        HuffmanNode r1 = new HuffmanNode();
        HuffmanNode r2 = new HuffmanNode();

        root.setLeftChild(left);
        root.setRightChild(right);
        left.setLeftChild(l1);
        left.setRightChild(l2);
        right.setLeftChild(r1);
        right.setRightChild(r2);

        l1.setByteValue(65); // 'A'
        l2.setByteValue(66); // 'B'
        r1.setByteValue(67); // 'C'
        r2.setByteValue(68); // 'D'

        // Should traverse entire tree without error
        assertDoesNotThrow(() ->
            HuffmanUtils.freeHuffmanTree(root)
        );
    }

    @Test
    void testFreeDfsWithOnlyLeftChild() {
        // Create tree with only left children
        HuffmanNode root = new HuffmanNode();
        HuffmanNode left = new HuffmanNode();
        HuffmanNode leftLeft = new HuffmanNode();

        root.setLeftChild(left);
        left.setLeftChild(leftLeft);
        leftLeft.setByteValue(65);

        // Should handle unbalanced tree without error
        assertDoesNotThrow(() ->
            HuffmanUtils.freeHuffmanTree(root)
        );
    }

    @Test
    void testFreeDfsWithOnlyRightChild() {
        // Create tree with only right children
        HuffmanNode root = new HuffmanNode();
        HuffmanNode right = new HuffmanNode();
        HuffmanNode rightRight = new HuffmanNode();

        root.setRightChild(right);
        right.setRightChild(rightRight);
        rightRight.setByteValue(65);

        // Should handle unbalanced tree without error
        assertDoesNotThrow(() ->
            HuffmanUtils.freeHuffmanTree(root)
        );
    }
    
    @Test
    void testNullTreeNodes() {
        // Test with null children
        HuffmanNode root = new HuffmanNode();
        assertNull(root.getLeftChild());
        assertNull(root.getRightChild());
    }

    @Test
    void testCalculateUniqueByteCount() {
        // Test with empty frequency array
        int[] emptyFreq = new int[300];
        assertEquals(0, HuffmanUtils.calculateUniqueByteCount(emptyFreq));

        // Test with single unique byte
        int[] singleFreq = new int[300];
        singleFreq[65] = 5;
        assertEquals(1, HuffmanUtils.calculateUniqueByteCount(singleFreq));

        // Test with multiple unique bytes
        int[] multiFreq = new int[300];
        multiFreq[65] = 3;
        multiFreq[66] = 2;
        multiFreq[67] = 1;
        multiFreq[255] = 10;
        assertEquals(4, HuffmanUtils.calculateUniqueByteCount(multiFreq));

        // Test with all bytes having frequency
        int[] allFreq = new int[300];
        for (int i = 0; i < 256; i++) {
            allFreq[i] = i + 1;
        }
        assertEquals(256, HuffmanUtils.calculateUniqueByteCount(allFreq));
    }

    @Test
    void testIsEmptyFile() {
        // Test with empty frequency array
        int[] emptyFreq = new int[300];
        assertTrue(HuffmanUtils.isEmptyFile(emptyFreq));

        // Test with non-empty frequency array (single byte)
        int[] singleFreq = new int[300];
        singleFreq[65] = 1;
        assertFalse(HuffmanUtils.isEmptyFile(singleFreq));

        // Test with non-empty frequency array (multiple bytes)
        int[] multiFreq = new int[300];
        multiFreq[65] = 5;
        multiFreq[66] = 3;
        assertFalse(HuffmanUtils.isEmptyFile(multiFreq));

        // Test with zero frequencies for all positions
        int[] zeroFreq = new int[300];
        Arrays.fill(zeroFreq, 0);
        assertTrue(HuffmanUtils.isEmptyFile(zeroFreq));
    }

    @Test
    void testBuildHuffmanTreeEmptyFrequency() {
        int[] frequency = new int[300];

        HuffmanNode root = HuffmanUtils.buildHuffmanTree(frequency);

        // With all zero frequencies, should return null
        assertNull(root);
    }

    @Test
    void testBuildHuffmanTreeSingleCharacter() {
        int[] frequency = new int[300];
        frequency[65] = 5; // 'A' appears 5 times

        HuffmanNode root = HuffmanUtils.buildHuffmanTree(frequency);

        // With single character, should create a special tree with single node
        assertNotNull(root);
        // For single character, tree has the node as left child and null as right
        assertNotNull(root.getLeftChild());
        assertNull(root.getRightChild());
        assertEquals(65, root.getLeftChild().getByteValue());
    }

    @Test
    void testBuildHuffmanTreeMultipleCharacters() {
        int[] frequency = new int[300];
        frequency[65] = 3; // 'A'
        frequency[66] = 2; // 'B'
        frequency[67] = 1; // 'C'

        HuffmanNode root = HuffmanUtils.buildHuffmanTree(frequency);

        // With multiple characters, should return a valid tree
        assertNotNull(root);
        assertEquals(6, root.getFrequency()); // Total frequency: 3+2+1=6
        assertNotNull(root.getLeftChild());
        assertNotNull(root.getRightChild());
    }

    @Test
    void testBuildHuffmanTreeStructure() {
        int[] frequency = new int[300];
        frequency[65] = 5; // 'A'
        frequency[66] = 9; // 'B'
        frequency[67] = 12; // 'C'
        frequency[68] = 13; // 'D'

        HuffmanNode root = HuffmanUtils.buildHuffmanTree(frequency);

        assertNotNull(root);
        assertEquals(39, root.getFrequency()); // Total: 5+9+12+13=39
    }

    @Test
    void testGenerateHuffmanCodes() {
        // Build a simple tree manually
        HuffmanNode root = new HuffmanNode();
        root.setFrequency(5);

        HuffmanNode left = new HuffmanNode();
        left.setByteValue(65); // 'A'
        left.setFrequency(2);

        HuffmanNode right = new HuffmanNode();
        right.setByteValue(66); // 'B'
        right.setFrequency(3);

        root.setLeftChild(left);
        root.setRightChild(right);

        String[] huffmanCodes = new String[300];
        HuffmanUtils.generateHuffmanCodes(root, "", huffmanCodes);

        // Verify codes
        assertEquals("0", huffmanCodes[65]); // 'A' should get '0'
        assertEquals("1", huffmanCodes[66]); // 'B' should get '1'
        assertEquals("", root.getCode());
    }

    @Test
    void testGenerateHuffmanCodesComplexTree() {
        // Build a more complex tree
        int[] frequency = new int[300];
        frequency[65] = 3; // 'A'
        frequency[66] = 2; // 'B'
        frequency[67] = 1; // 'C'

        HuffmanNode root = HuffmanUtils.buildHuffmanTree(frequency);
        String[] huffmanCodes = new String[300];
        HuffmanUtils.generateHuffmanCodes(root, "", huffmanCodes);

        // Verify that all codes are set
        assertNotNull(huffmanCodes[65]);
        assertNotNull(huffmanCodes[66]);
        assertNotNull(huffmanCodes[67]);

        // Verify codes are different
        assertNotEquals(huffmanCodes[65], huffmanCodes[66]);
        assertNotEquals(huffmanCodes[65], huffmanCodes[67]);
        assertNotEquals(huffmanCodes[66], huffmanCodes[67]);

        // Verify codes only contain 0s and 1s
        assertTrue(huffmanCodes[65].matches("[01]+"));
        assertTrue(huffmanCodes[66].matches("[01]+"));
        assertTrue(huffmanCodes[67].matches("[01]+"));
    }

    @Test
    void testGenerateHuffmanCodesNodeCodes() {
        // Build a simple tree to test node.code assignment
        HuffmanNode root = new HuffmanNode();
        HuffmanNode left = new HuffmanNode();
        HuffmanNode right = new HuffmanNode();
        left.setByteValue(65);
        right.setByteValue(66);
        root.setLeftChild(left);
        root.setRightChild(right);

        String[] huffmanCodes = new String[300];
        HuffmanUtils.generateHuffmanCodes(root, "", huffmanCodes);

        // Verify node codes are set
        assertEquals("", root.getCode());
        assertEquals("0", left.getCode());
        assertEquals("1", right.getCode());
    }

    @Test
    void testCreateBinaryStringsForBytesArrayLength() {
        String[] result = HuffmanUtils.createBinaryStringsForBytes();

        // Should return array of size Constants.BYTE_VALUES_COUNT
        assertEquals(Constants.BYTE_VALUES_COUNT, result.length);
    }

    @Test
    void testCreateBinaryStringsForBytesSpecialCases() {
        String[] result = HuffmanUtils.createBinaryStringsForBytes();

        // Test special case: 0 should be "0"
        assertEquals("0", result[0]);

        // Test power of 2 values
        assertEquals("1", result[1]);
        assertEquals("10", result[2]);
        assertEquals("100", result[4]);
        assertEquals("1000", result[8]);
        assertEquals("10000", result[16]);
        assertEquals("100000", result[32]);
        assertEquals("1000000", result[64]);
        assertEquals("10000000", result[128]);
    }

    @Test
    void testCreateBinaryStringsForBytesCommonValues() {
        String[] result = HuffmanUtils.createBinaryStringsForBytes();

        // Test common values
        assertEquals("11", result[3]);     // 3 = 11 in binary
        assertEquals("101", result[5]);    // 5 = 101 in binary
        assertEquals("111", result[7]);    // 7 = 111 in binary
        assertEquals("1010", result[10]);  // 10 = 1010 in binary
        assertEquals("1111", result[15]);  // 15 = 1111 in binary
    }

    @Test
    void testCreateBinaryStringsForBytesMaxValue() {
        String[] result = HuffmanUtils.createBinaryStringsForBytes();

        // Test maximum byte value (255)
        assertEquals("11111111", result[255]);
    }

    @Test
    void testCreateBinaryStringsForBytesNoLeadingZeros() {
        String[] result = HuffmanUtils.createBinaryStringsForBytes();

        // Verify that strings don't have leading zeros
        for (int i = 1; i < Constants.BYTE_VALUES_COUNT; i++) {
            assertFalse(result[i].startsWith("0"),
                "Binary string for " + i + " should not start with 0: " + result[i]);
        }
    }

    @Test
    void testCreateBinaryStringsForBytesCorrectness() {
        String[] result = HuffmanUtils.createBinaryStringsForBytes();

        // Verify correctness by converting back to integer
        for (int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
            int converted = Integer.parseInt(result[i], 2);
            assertEquals(i, converted,
                "Binary string for " + i + " is incorrect: " + result[i]);
        }
    }

    @Test
    void testCreateBinaryStringsForBytesAllValuesNonNull() {
        String[] result = HuffmanUtils.createBinaryStringsForBytes();

        // Verify all values are non-null
        for (int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
            assertNotNull(result[i], "Binary string for " + i + " should not be null");
        }
    }

    @Test
    void testCreateBinaryStringsForBytesOnlyBinaryDigits() {
        String[] result = HuffmanUtils.createBinaryStringsForBytes();

        // Verify strings only contain '0' and '1'
        for (int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
            assertTrue(result[i].matches("[01]+"),
                "Binary string for " + i + " contains invalid characters: " + result[i]);
        }
    }

    @Test
    void testCreateBinaryStringsForBytesAsciiValues() {
        String[] result = HuffmanUtils.createBinaryStringsForBytes();

        // Test ASCII values
        assertEquals("1000001", result[65]);  // 'A' = 65
        assertEquals("1000010", result[66]);  // 'B' = 66
        assertEquals("1000011", result[67]);  // 'C' = 67
        assertEquals("1100001", result[97]);  // 'a' = 97
        assertEquals("1100010", result[98]);  // 'b' = 98
        assertEquals("110000", result[48]);   // '0' = 48
        assertEquals("111001", result[57]);   // '9' = 57
    }

    @Test
    void testCreateBinaryStringsForBytesLengthProgression() {
        String[] result = HuffmanUtils.createBinaryStringsForBytes();

        // Verify length progression
        assertEquals(1, result[0].length());   // 0: "0"
        assertEquals(1, result[1].length());   // 1: "1"
        assertEquals(2, result[2].length());   // 2-3: 2 digits
        assertEquals(2, result[3].length());
        assertEquals(3, result[4].length());   // 4-7: 3 digits
        assertEquals(3, result[7].length());
        assertEquals(4, result[8].length());   // 8-15: 4 digits
        assertEquals(4, result[15].length());
        assertEquals(5, result[16].length());  // 16-31: 5 digits
        assertEquals(5, result[31].length());
        assertEquals(6, result[32].length());  // 32-63: 6 digits
        assertEquals(6, result[63].length());
        assertEquals(7, result[64].length());  // 64-127: 7 digits
        assertEquals(7, result[127].length());
        assertEquals(8, result[128].length()); // 128-255: 8 digits
        assertEquals(8, result[255].length());
    }
} 