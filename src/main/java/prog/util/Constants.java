package prog.util;

/**
 * Common constants used throughout the file compression application.
 *
 * This class contains shared constants that are used across multiple
 * compression algorithms and utility classes.
 */
public final class Constants {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private Constants() {
        throw new UnsupportedOperationException("Constants class should not be instantiated");
    }

    /**
     * The total number of possible byte values (0 to 255).
     * Used throughout compression algorithms as the initial dictionary size
     * and for byte-related conversions.
     */
    public static final int BYTE_VALUES_COUNT = 256;

    /**
     * The file extension for Huffman compressed files.
     */
    public static final String HUFFMAN_FILE_EXTENSION = ".huffz";

    /**
     * The file extension for LZW compressed files.
     */
    public static final String LZW_FILE_EXTENSION = ".LmZWp";

    /**
     * Number of bits in a byte.
     */
    public static final int BITS_PER_BYTE = 8;

    /**
     * Maximum memory size limit for LZW dictionary (in characters).
     * Used to prevent excessive memory usage during compression/decompression.
     */
    public static final int MAX_DICTIONARY_MEMORY_SIZE = 100000;
}