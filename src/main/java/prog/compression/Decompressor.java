package prog.compression;

/**
 * Interface for file decompression algorithms.
 * Provides a unified API for different decompression implementations (Huffman, LZW, etc.)
 *
 * Usage example:
 * <pre>
 * Decompressor decompressor = new HuffmanDecompressor("input.txt.huffz");
 * decompressor.decompress(); // Creates input.txt
 * </pre>
 */
public interface Decompressor {
    /**
     * Decompresses the file using the algorithm's default output path.
     * The output filename is typically derived by removing the compression extension.
     *
     * For example:
     * - Huffman: input.txt.huffz -> input.txt
     * - LZW: input.txt.LmZWp -> input.txt
     *
     * @throws RuntimeException if decompression fails
     */
    void decompress();
}
