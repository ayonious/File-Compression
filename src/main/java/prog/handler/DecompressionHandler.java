package prog.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prog.huffman.HuffmanDecompressor;
import prog.lzw.LzwDecompressor;
import prog.util.Constants;

import javax.swing.*;
import java.io.File;

/**
 * Handles decompression operations for both Huffman and LZW algorithms.
 */
public class DecompressionHandler {
    private static final Logger logger = LoggerFactory.getLogger(DecompressionHandler.class);

    /**
     * Decompresses a Huffman-compressed file.
     *
     * @param compressedFile The compressed file to decompress
     * @return The decompressed output file, or null if decompression failed
     */
    public File decompressHuffman(File compressedFile) {
        try {
            logger.info("Starting Huffman decompression for file: {}", compressedFile.getPath());
            HuffmanDecompressor decompressor = new HuffmanDecompressor(compressedFile.getPath());
            decompressor.decompress();

            // Calculate output file path
            String outputPath = compressedFile.getPath();
            outputPath = outputPath.substring(0, outputPath.length() - Constants.HUFFMAN_FILE_EXTENSION.length());
            File outputFile = new File(outputPath);

            logger.info("Huffman decompression completed successfully. Output: {}", outputFile.getPath());

            showSuccessDialog("File decompressed successfully!\nOutput: " + outputFile.getName());
            return outputFile;

        } catch (Exception ex) {
            logger.error("Huffman decompression failed for file: {}", compressedFile.getPath(), ex);
            showErrorDialog("Decompression failed: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Decompresses an LZW-compressed file.
     *
     * @param compressedFile The compressed file to decompress
     * @return The decompressed output file, or null if decompression failed
     */
    public File decompressLZW(File compressedFile) {
        try {
            logger.info("Starting LZW decompression for file: {}", compressedFile.getPath());
            LzwDecompressor decompressor = new LzwDecompressor(compressedFile.getPath());
            decompressor.decompress();

            // Calculate output file path
            String outputPath = compressedFile.getPath();
            outputPath = outputPath.substring(0, outputPath.length() - Constants.LZW_FILE_EXTENSION.length());
            File outputFile = new File(outputPath);

            logger.info("LZW decompression completed successfully. Output: {}", outputFile.getPath());

            showSuccessDialog("File decompressed successfully!\nOutput: " + outputFile.getName());
            return outputFile;

        } catch (Exception ex) {
            logger.error("LZW decompression failed for file: {}", compressedFile.getPath(), ex);
            showErrorDialog("Decompression failed: " + ex.getMessage());
            return null;
        }
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                "Decompression Complete",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                "Decompression Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
