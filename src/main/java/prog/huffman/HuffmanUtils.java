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

    // Accessor for Hzipping.TREE
    public static final TreeAccessor<Hzipping.TREE> HZIPPING_TREE_ACCESSOR = new TreeAccessor<Hzipping.TREE>() {
        public Hzipping.TREE getLeftChild(Hzipping.TREE node) {
            return node.Lchild;
        }
        public Hzipping.TREE getRightChild(Hzipping.TREE node) {
            return node.Rchild;
        }
    };

    // Accessor for Hunzipping.TREE
    public static final TreeAccessor<Hunzipping.TREE> HUNZIPPING_TREE_ACCESSOR = new TreeAccessor<Hunzipping.TREE>() {
        public Hunzipping.TREE getLeftChild(Hunzipping.TREE node) {
            return node.Lchild;
        }
        public Hunzipping.TREE getRightChild(Hunzipping.TREE node) {
            return node.Rchild;
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