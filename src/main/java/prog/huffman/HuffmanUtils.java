package prog.huffman;

public class HuffmanUtils {
    /***********************************************************************************
     * byte to binary conversion
     ***********************************************************************************/
    public static int to(Byte b) {
        int ret = b;
        if (ret < 0) {
            ret = ~b;
            ret = ret + 1;
            ret = ret ^ 255;
            ret += 1;
        }
        return ret;
    }

    /**********************************************************************************
     * dfs to free memory
     *********************************************************************************/
    public static <T> void fredfs(T now, TreeAccessor<T> accessor) {
        if (accessor.getLeftChild(now) == null && accessor.getRightChild(now) == null) {
            return;
        }
        if (accessor.getLeftChild(now) != null)
            fredfs(accessor.getLeftChild(now), accessor);
        if (accessor.getRightChild(now) != null)
            fredfs(accessor.getRightChild(now), accessor);
    }

    // Interface to access tree nodes generically
    public interface TreeAccessor<T> {
        T getLeftChild(T node);
        T getRightChild(T node);
    }

    // Accessor for HuffmanCompressor.HuffmanNode
    public static final TreeAccessor<HuffmanCompressor.HuffmanNode> HZIPPING_TREE_ACCESSOR = new TreeAccessor<HuffmanCompressor.HuffmanNode>() {
        public HuffmanCompressor.HuffmanNode getLeftChild(HuffmanCompressor.HuffmanNode node) {
            return node.leftChild;
        }
        public HuffmanCompressor.HuffmanNode getRightChild(HuffmanCompressor.HuffmanNode node) {
            return node.rightChild;
        }
    };

    // Accessor for HuffmanDecompressor.HuffmanNode
    public static final TreeAccessor<HuffmanDecompressor.HuffmanNode> HUNZIPPING_HuffmanNode_ACCESSOR = new TreeAccessor<HuffmanDecompressor.HuffmanNode>() {
        public HuffmanDecompressor.HuffmanNode getLeftChild(HuffmanDecompressor.HuffmanNode node) {
            return node.leftChild;
        }
        public HuffmanDecompressor.HuffmanNode getRightChild(HuffmanDecompressor.HuffmanNode node) {
            return node.rightChild;
        }
    };

    /***********************************************************************************
     * convert any string into eight digit string
     ***********************************************************************************/
    public static String makeeight(String b) {
        String ret = "";
        int i;
        int len = b.length();
        for (i = 0; i < (8 - len); i++)
            ret += "0";
        ret += b;
        return ret;
    }
} 