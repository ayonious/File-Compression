package prog.huffman;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HuffmanUtilsTest {

    @Test
    void testByteToUnsignedInt() {
        // Test positive bytes
        assertEquals(0, HuffmanUtils.to((byte) 0));
        assertEquals(1, HuffmanUtils.to((byte) 1));
        assertEquals(127, HuffmanUtils.to((byte) 127));

        // Test negative bytes
        assertEquals(128, HuffmanUtils.to((byte) -128));
        assertEquals(255, HuffmanUtils.to((byte) -1));
        assertEquals(129, HuffmanUtils.to((byte) -127));
    }

    @Test
    void testMakeEight() {
        // Test empty string
        assertEquals("00000000", HuffmanUtils.makeeight(""));

        // Test single digit
        assertEquals("00000001", HuffmanUtils.makeeight("1"));

        // Test multiple digits
        assertEquals("00000101", HuffmanUtils.makeeight("101"));

        // Test string of length 8
        assertEquals("11111111", HuffmanUtils.makeeight("11111111"));

        // Test shorter strings
        assertEquals("00001010", HuffmanUtils.makeeight("1010"));
        assertEquals("00110011", HuffmanUtils.makeeight("110011"));
    }

    @Test
    void testFreeDfsWithHzippingTree() {
        // Create a simple HuffmanCompressor tree structure
        HuffmanCompressor.HuffmanNode root = new HuffmanCompressor.HuffmanNode();
        HuffmanCompressor.HuffmanNode left = new HuffmanCompressor.HuffmanNode();
        HuffmanCompressor.HuffmanNode right = new HuffmanCompressor.HuffmanNode();

        root.leftChild = left;
        root.rightChild = right;
        root.byteValue = 1;
        left.byteValue = 2;
        right.byteValue = 3;

        // Test that no exception is thrown when traversing
        assertDoesNotThrow(() ->
            HuffmanUtils.fredfs(root, HuffmanUtils.HZIPPING_TREE_ACCESSOR)
        );
    }

    @Test
    void testFreeDfsWithHunzippingTree() {
        // Create a simple HuffmanDecompressor tree structure
        HuffmanDecompressor.HuffmanNode root = new HuffmanDecompressor.HuffmanNode();
        HuffmanDecompressor.HuffmanNode left = new HuffmanDecompressor.HuffmanNode();
        HuffmanDecompressor.HuffmanNode right = new HuffmanDecompressor.HuffmanNode();

        root.leftChild = left;
        root.rightChild = right;
        root.byteValue = 1;
        left.byteValue = 2;
        right.byteValue = 3;

        // Test that no exception is thrown when traversing
        assertDoesNotThrow(() ->
            HuffmanUtils.fredfs(root, HuffmanUtils.HUNZIPPING_HuffmanNode_ACCESSOR)
        );
    }

    @Test
    void testTreeAccessors() {
        // Test HuffmanCompressor tree accessor
        HuffmanCompressor.HuffmanNode hzipRoot = new HuffmanCompressor.HuffmanNode();
        HuffmanCompressor.HuffmanNode hzipLeft = new HuffmanCompressor.HuffmanNode();
        HuffmanCompressor.HuffmanNode hzipRight = new HuffmanCompressor.HuffmanNode();
        hzipRoot.leftChild = hzipLeft;
        hzipRoot.rightChild = hzipRight;

        assertEquals(hzipLeft, HuffmanUtils.HZIPPING_TREE_ACCESSOR.getLeftChild(hzipRoot));
        assertEquals(hzipRight, HuffmanUtils.HZIPPING_TREE_ACCESSOR.getRightChild(hzipRoot));

        // Test HuffmanDecompressor tree accessor
        HuffmanDecompressor.HuffmanNode hunzipRoot = new HuffmanDecompressor.HuffmanNode();
        HuffmanDecompressor.HuffmanNode hunzipLeft = new HuffmanDecompressor.HuffmanNode();
        HuffmanDecompressor.HuffmanNode hunzipRight = new HuffmanDecompressor.HuffmanNode();
        hunzipRoot.leftChild = hunzipLeft;
        hunzipRoot.rightChild = hunzipRight;

        assertEquals(hunzipLeft, HuffmanUtils.HUNZIPPING_HuffmanNode_ACCESSOR.getLeftChild(hunzipRoot));
        assertEquals(hunzipRight, HuffmanUtils.HUNZIPPING_HuffmanNode_ACCESSOR.getRightChild(hunzipRoot));
    }

    @Test
    void testNullTreeNodes() {
        // Test with null children
        HuffmanCompressor.HuffmanNode hzipRoot = new HuffmanCompressor.HuffmanNode();
        assertNull(HuffmanUtils.HZIPPING_TREE_ACCESSOR.getLeftChild(hzipRoot));
        assertNull(HuffmanUtils.HZIPPING_TREE_ACCESSOR.getRightChild(hzipRoot));

        HuffmanDecompressor.HuffmanNode hunzipRoot = new HuffmanDecompressor.HuffmanNode();
        assertNull(HuffmanUtils.HUNZIPPING_HuffmanNode_ACCESSOR.getLeftChild(hunzipRoot));
        assertNull(HuffmanUtils.HUNZIPPING_HuffmanNode_ACCESSOR.getRightChild(hunzipRoot));
    }
} 