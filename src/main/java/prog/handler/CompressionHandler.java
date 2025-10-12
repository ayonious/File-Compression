package prog.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prog.huffman.HuffmanCompressor;
import prog.lzw.LzwCompressor;
import prog.util.Constants;

import javax.swing.*;
import java.io.File;

/**
 * Handles compression operations for both Huffman and LZW algorithms.
 */
public class CompressionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CompressionHandler.class);

    /**
     * Compresses a file using Huffman algorithm.
     *
     * @param inputFile The file to compress
     * @return The compressed output file, or null if compression failed
     */
    public File compressWithHuffman(File inputFile) {
        try {
            logger.info("Starting Huffman compression for file: {}", inputFile.getPath());
            HuffmanCompressor compressor = new HuffmanCompressor(inputFile.getPath());
            compressor.compress();
            compressor.cleanup();

            File outputFile = new File(inputFile.getPath() + Constants.HUFFMAN_FILE_EXTENSION);
            logger.info("Huffman compression completed successfully. Output: {}", outputFile.getPath());

            showSuccessDialog("File compressed successfully!\nOutput: " + outputFile.getName());
            return outputFile;

        } catch (Exception ex) {
            logger.error("Huffman compression failed for file: {}", inputFile.getPath(), ex);
            showErrorDialog("Compression failed: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Compresses a file using LZW algorithm.
     *
     * @param inputFile The file to compress
     * @return The compressed output file, or null if compression failed
     */
    public File compressWithLZW(File inputFile) {
        try {
            logger.info("Starting LZW compression for file: {}", inputFile.getPath());
            LzwCompressor compressor = new LzwCompressor(inputFile.getPath());
            compressor.compress();

            File outputFile = new File(inputFile.getPath() + Constants.LZW_FILE_EXTENSION);
            logger.info("LZW compression completed successfully. Output: {}", outputFile.getPath());

            showSuccessDialog("File compressed successfully!\nOutput: " + outputFile.getName());
            return outputFile;

        } catch (Exception ex) {
            logger.error("LZW compression failed for file: {}", inputFile.getPath(), ex);
            showErrorDialog("Compression failed: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Calculates the compression ratio between original and compressed files.
     *
     * @param originalSize Size of the original file in bytes
     * @param compressedSize Size of the compressed file in bytes
     * @return Compression ratio as a percentage (0-100)
     */
    public double calculateCompressionRatio(long originalSize, long compressedSize) {
        double ratio = (1.0 - (double) compressedSize / originalSize) * 100;
        logger.info("Compression ratio: {:.2f}% (Original: {} bytes, Compressed: {} bytes)",
                ratio, originalSize, compressedSize);
        return ratio;
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                "Compression Complete",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                "Compression Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
