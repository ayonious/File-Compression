package prog.huffman;

/**
 * Reads bits from a buffer of binary strings.
 * Manages bit buffering for Huffman decoding.
 */
public class BitReader {
    private StringBuilder bitBuffer;
    private int extraBits;

    public BitReader(int extraBits) {
        this.bitBuffer = new StringBuilder();
        this.extraBits = extraBits;
    }

    /**
     * Appends a binary string to the buffer.
     */
    public void append(String binaryString) {
        bitBuffer.append(binaryString);
    }

    /**
     * Reads the next bit sequence of specified length.
     * Returns null if not enough bits available (excluding extra padding bits).
     */
    public String peek(int length) {
        if (length > getAvailableBits()) {
            return null;
        }
        return bitBuffer.substring(0, length);
    }

    /**
     * Consumes (removes) the specified number of bits from the buffer.
     */
    public void consume(int length) {
        if (length <= bitBuffer.length()) {
            bitBuffer.delete(0, length);
        }
    }

    /**
     * Returns the number of available bits (excluding extra padding bits).
     */
    public int getAvailableBits() {
        return Math.max(0, bitBuffer.length() - extraBits);
    }

    /**
     * Returns the current buffer length.
     */
    public int length() {
        return bitBuffer.length();
    }

    /**
     * Returns the bit at the specified index.
     */
    public char charAt(int index) {
        return bitBuffer.charAt(index);
    }

    /**
     * Clears the buffer.
     */
    public void clear() {
        bitBuffer.setLength(0);
        extraBits = 0;
    }
}
