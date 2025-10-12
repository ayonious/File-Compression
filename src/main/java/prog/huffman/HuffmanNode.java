package prog.huffman;

/**
 * Represents a node in the Huffman tree.
 * Can be either a leaf node (containing a byte value) or an internal node (with children).
 *
 * Examples:
 * - Leaf node: byteValue='A'(65), frequency=10, no children
 * - Internal node: frequency=25 (sum of children), has left and right children
 */
public class HuffmanNode implements Comparable<HuffmanNode> {
    private HuffmanNode leftChild;
    private HuffmanNode rightChild;
    private String code;
    private int byteValue;
    private int frequency;

    /**
     * Default constructor for creating an empty node
     */
    public HuffmanNode() {
    }

    /**
     * Constructor for creating a leaf node with byte value and frequency
     *
     * @param byteValue The byte value this leaf represents (0-255)
     * @param frequency How many times this byte appears in the input
     */
    public HuffmanNode(int byteValue, int frequency) {
        this.byteValue = byteValue;
        this.frequency = frequency;
    }

    /**
     * Constructor for creating a parent node from two child nodes
     * The frequency is automatically set to the sum of children's frequencies
     *
     * @param leftChild The left child node (typically lower frequency or '0' bit)
     * @param rightChild The right child node (typically higher frequency or '1' bit)
     */
    public HuffmanNode(HuffmanNode leftChild, HuffmanNode rightChild) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        if(rightChild == null) this.frequency = leftChild.frequency;
        else this.frequency = leftChild.frequency + rightChild.frequency;
    }

    // Getters
    public HuffmanNode getLeftChild() {
        return leftChild;
    }

    public HuffmanNode getRightChild() {
        return rightChild;
    }

    public String getCode() {
        return code;
    }

    public int getByteValue() {
        return byteValue;
    }

    public int getFrequency() {
        return frequency;
    }

    // Setters (package-private for use within huffman package)
    void setLeftChild(HuffmanNode leftChild) {
        this.leftChild = leftChild;
    }

    void setRightChild(HuffmanNode rightChild) {
        this.rightChild = rightChild;
    }

    void setCode(String code) {
        this.code = code;
    }

    void setByteValue(int byteValue) {
        this.byteValue = byteValue;
    }

    void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * Compares nodes by frequency for priority queue ordering.
     * Lower frequency nodes have higher priority.
     *
     * @param other The node to compare with
     * @return -1 if this node has lower frequency, 1 if higher, 0 if equal
     */
    public int compareTo(HuffmanNode other) {
        return Integer.compare(this.frequency, other.frequency);
    }

    /**
     * Checks if this node is a leaf node (has no children).
     * Leaf nodes represent actual byte values in the input.
     *
     * @return true if this is a leaf node, false if it's an internal node
     */
    public boolean isLeaf() {
        return this.leftChild == null && this.rightChild == null;
    }
}
