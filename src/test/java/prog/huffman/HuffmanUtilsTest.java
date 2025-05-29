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
        // Create a simple Hzipping tree structure
        Hzipping.TREE root = new Hzipping.TREE();
        Hzipping.TREE left = new Hzipping.TREE();
        Hzipping.TREE right = new Hzipping.TREE();
        
        root.Lchild = left;
        root.Rchild = right;
        root.Bite = 1;
        left.Bite = 2;
        right.Bite = 3;

        // Test that no exception is thrown when traversing
        assertDoesNotThrow(() -> 
            HuffmanUtils.fredfs(root, HuffmanUtils.HZIPPING_TREE_ACCESSOR)
        );
    }

    @Test
    void testFreeDfsWithHunzippingTree() {
        // Create a simple Hunzipping tree structure
        Hunzipping.TREE root = new Hunzipping.TREE();
        Hunzipping.TREE left = new Hunzipping.TREE();
        Hunzipping.TREE right = new Hunzipping.TREE();
        
        root.Lchild = left;
        root.Rchild = right;
        root.Bite = 1;
        left.Bite = 2;
        right.Bite = 3;

        // Test that no exception is thrown when traversing
        assertDoesNotThrow(() -> 
            HuffmanUtils.fredfs(root, HuffmanUtils.HUNZIPPING_TREE_ACCESSOR)
        );
    }

    @Test
    void testTreeAccessors() {
        // Test Hzipping tree accessor
        Hzipping.TREE hzipRoot = new Hzipping.TREE();
        Hzipping.TREE hzipLeft = new Hzipping.TREE();
        Hzipping.TREE hzipRight = new Hzipping.TREE();
        hzipRoot.Lchild = hzipLeft;
        hzipRoot.Rchild = hzipRight;

        assertEquals(hzipLeft, HuffmanUtils.HZIPPING_TREE_ACCESSOR.getLeftChild(hzipRoot));
        assertEquals(hzipRight, HuffmanUtils.HZIPPING_TREE_ACCESSOR.getRightChild(hzipRoot));

        // Test Hunzipping tree accessor
        Hunzipping.TREE hunzipRoot = new Hunzipping.TREE();
        Hunzipping.TREE hunzipLeft = new Hunzipping.TREE();
        Hunzipping.TREE hunzipRight = new Hunzipping.TREE();
        hunzipRoot.Lchild = hunzipLeft;
        hunzipRoot.Rchild = hunzipRight;

        assertEquals(hunzipLeft, HuffmanUtils.HUNZIPPING_TREE_ACCESSOR.getLeftChild(hunzipRoot));
        assertEquals(hunzipRight, HuffmanUtils.HUNZIPPING_TREE_ACCESSOR.getRightChild(hunzipRoot));
    }

    @Test
    void testNullTreeNodes() {
        // Test with null children
        Hzipping.TREE hzipRoot = new Hzipping.TREE();
        assertNull(HuffmanUtils.HZIPPING_TREE_ACCESSOR.getLeftChild(hzipRoot));
        assertNull(HuffmanUtils.HZIPPING_TREE_ACCESSOR.getRightChild(hzipRoot));

        Hunzipping.TREE hunzipRoot = new Hunzipping.TREE();
        assertNull(HuffmanUtils.HUNZIPPING_TREE_ACCESSOR.getLeftChild(hunzipRoot));
        assertNull(HuffmanUtils.HUNZIPPING_TREE_ACCESSOR.getRightChild(hunzipRoot));
    }
} 