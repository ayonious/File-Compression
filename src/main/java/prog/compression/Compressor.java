package prog.compression;

/**
 * Interface for file compression algorithms.
 * Provides a unified API for different compression implementations (Huffman, LZW, etc.)
 *
 * Usage example:
 * <pre>
 * Compressor compressor = new HuffmanCompressor("input.txt");
 * compressor.compress(); // Creates input.txt.huffz
 * compressor.cleanup();
 * </pre>
 */
public interface Compressor {
    /**
     * Compresses the file using the algorithm's default output path.
     * The output filename is typically the input filename with an algorithm-specific extension.
     *
     * For example:
     * - Huffman: input.txt -> input.txt.huffz
     * - LZW: input.txt -> input.txt.LmZWp
     *
     * @throws RuntimeException if compression fails
     */
    void compress();

    /**
     * Cleans up any resources used by the compressor.
     * Should be called after compression is complete.
     *
     * For example:
     * - Huffman: Frees the Huffman tree from memory
     * - LZW: May clear internal buffers or caches
     *
     * Note: Some compressors may not need cleanup. In such cases, this method may do nothing.
     */
    default void cleanup() {
        // Default implementation does nothing
        // Subclasses can override if cleanup is needed
    }
}
