package prog.huffman;

public class HuffmanNode implements Comparable<HuffmanNode> {
    HuffmanNode leftChild;
    HuffmanNode rightChild;
    public String code;
    public int byteValue;
    public int frequency;

    /**
     * Default constructor for creating an empty node
     */
    public HuffmanNode() {
    }

    /**
     * Constructor for creating a leaf node with byte value and frequency
     */
    public HuffmanNode(int byteValue, int frequency) {
        this.byteValue = byteValue;
        this.frequency = frequency;
    }

    /**
     * Constructor for creating a parent node from two child nodes
     */
    public HuffmanNode(HuffmanNode leftChild, HuffmanNode rightChild) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.frequency = leftChild.frequency + rightChild.frequency;
    }

    public int compareTo(HuffmanNode other) {
        if (this.frequency < other.frequency)
            return -1;
        if (this.frequency > other.frequency)
            return 1;
        return 0;
    }

    public boolean isLeaf() {
        return this.leftChild == null && this.rightChild == null;
    }
}
