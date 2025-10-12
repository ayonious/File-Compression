package prog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prog.handler.CompressionHandler;
import prog.handler.DecompressionHandler;
import prog.handler.FileOperationHandler;
import prog.ui.FileCompressorUI;
import prog.ui.MainWindow;

/**
 * Main application class for the File Compressor.
 * Coordinates between UI components and business logic handlers.
 */
public class Main implements ActionListener {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	// Handlers
	private final FileOperationHandler fileHandler;
	private final CompressionHandler compressionHandler;
	private final DecompressionHandler decompressionHandler;

	// UI Components
	private final FileCompressorUI ui;
	private JTextField filePathField;
	private JLabel originalSizeValue;
	private JLabel compressedSizeValue;
	private JLabel statusLabel;
	private JButton huffmanCompressButton;
	private JButton huffmanDecompressButton;
	private JButton lzwCompressButton;
	private JButton lzwDecompressButton;
	private JButton exitButton;

	public Main() {
		this.ui = new FileCompressorUI();
		this.fileHandler = new FileOperationHandler();
		this.compressionHandler = new CompressionHandler();
		this.decompressionHandler = new DecompressionHandler();
	}

	/**
	 * Creates the content pane and initializes UI components
	 */
	public JPanel createContentPane() {
		JPanel contentPane = ui.createContentPane(this, this::handleBrowse);

		// Get references to UI components
		filePathField = ui.getFilePathField();
		originalSizeValue = ui.getOriginalSizeValue();
		compressedSizeValue = ui.getCompressedSizeValue();
		statusLabel = ui.getStatusLabel();
		huffmanCompressButton = ui.getHuffmanCompressButton();
		huffmanDecompressButton = ui.getHuffmanDecompressButton();
		lzwCompressButton = ui.getLzwCompressButton();
		lzwDecompressButton = ui.getLzwDecompressButton();
		exitButton = ui.getExitButton();

		return contentPane;
	}

	/**
	 * Handles the browse button action to open file chooser
	 */
	private void handleBrowse(ActionEvent e) {
		File selectedFile = fileHandler.browseForFile();
		if (selectedFile != null) {
			filePathField.setText(selectedFile.getAbsolutePath());
			originalSizeValue.setText(fileHandler.formatFileSize(selectedFile.length()));
			compressedSizeValue.setText("—");
			statusLabel.setText("File selected: " + selectedFile.getName());
		}
	}

	/**
	 * Handles the File > Open menu action
	 */
	private void handleFileOpen() {
		handleBrowse(null);
	}

	/**
	 * Validates that a file is selected before performing operations
	 */
	private boolean validateFileSelected() {
		if (!fileHandler.isFileSelected()) {
			fileHandler.showNoFileSelectedWarning();
			statusLabel.setText("⚠️ No file selected");
			return false;
		}
		return true;
	}

	private void handleHuffmanCompression() {
		if (!validateFileSelected()) return;

		statusLabel.setText("🗜️ Compressing with Huffman...");
		File outputFile = compressionHandler.compressWithHuffman(fileHandler.getSelectedFile());

		if (outputFile != null) {
			fileHandler.setOutputFile(outputFile);
			updateCompressionStats();
			statusLabel.setText("✅ Compression complete!");
		} else {
			statusLabel.setText("❌ Compression failed");
		}
	}

	private void handleHuffmanDecompression() {
		if (!validateFileSelected()) return;

		statusLabel.setText("📂 Decompressing Huffman file...");
		File outputFile = decompressionHandler.decompressHuffman(fileHandler.getSelectedFile());

		if (outputFile != null) {
			fileHandler.setOutputFile(outputFile);
			updateDecompressionStats();
			statusLabel.setText("✅ Decompression complete!");
		} else {
			statusLabel.setText("❌ Decompression failed");
		}
	}

	private void handleLZWCompression() {
		if (!validateFileSelected()) return;

		statusLabel.setText("🗜️ Compressing with LZW...");
		File outputFile = compressionHandler.compressWithLZW(fileHandler.getSelectedFile());

		if (outputFile != null) {
			fileHandler.setOutputFile(outputFile);
			updateCompressionStats();
			statusLabel.setText("✅ Compression complete!");
		} else {
			statusLabel.setText("❌ Compression failed");
		}
	}

	private void handleLZWDecompression() {
		if (!validateFileSelected()) return;

		statusLabel.setText("📂 Decompressing LZW file...");
		File outputFile = decompressionHandler.decompressLZW(fileHandler.getSelectedFile());

		if (outputFile != null) {
			fileHandler.setOutputFile(outputFile);
			updateDecompressionStats();
			statusLabel.setText("✅ Decompression complete!");
		} else {
			statusLabel.setText("❌ Decompression failed");
		}
	}

	private void updateCompressionStats() {
		File inputFile = fileHandler.getSelectedFile();
		File outputFile = fileHandler.getOutputFile();

		originalSizeValue.setText(fileHandler.formatFileSize(inputFile.length()));
		compressedSizeValue.setText(fileHandler.formatFileSize(outputFile.length()));

		// Calculate and show compression ratio
		double ratio = compressionHandler.calculateCompressionRatio(
			inputFile.length(), outputFile.length());

		if (ratio > 0) {
			statusLabel.setText(String.format("✅ Compression complete! Saved %.1f%%", ratio));
		}
	}

	private void updateDecompressionStats() {
		File inputFile = fileHandler.getSelectedFile();
		File outputFile = fileHandler.getOutputFile();

		originalSizeValue.setText(fileHandler.formatFileSize(inputFile.length()));
		compressedSizeValue.setText(fileHandler.formatFileSize(outputFile.length()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == huffmanCompressButton) {
			handleHuffmanCompression();
		} else if (e.getSource() == huffmanDecompressButton) {
			handleHuffmanDecompression();
		} else if (e.getSource() == lzwCompressButton) {
			handleLZWCompression();
		} else if (e.getSource() == lzwDecompressButton) {
			handleLZWDecompression();
		} else if (e.getSource() == exitButton) {
			System.exit(0);
		}
	}

	// Getter methods for testing
	public JTextField getFilePathField() { return filePathField; }
	public JLabel getOriginalSizeValue() { return originalSizeValue; }
	public JLabel getCompressedSizeValue() { return compressedSizeValue; }
	public JLabel getStatusLabel() { return statusLabel; }
	public JButton getHuffmanCompressButton() { return huffmanCompressButton; }
	public JButton getHuffmanDecompressButton() { return huffmanDecompressButton; }
	public JButton getLzwCompressButton() { return lzwCompressButton; }
	public JButton getLzwDecompressButton() { return lzwDecompressButton; }
	public JButton getExitButton() { return exitButton; }
	public File getOpenedFile() { return fileHandler.getSelectedFile(); }

	/**
	 * Main entry point for the application
	 */
	public static void main(String[] args) {
		logger.info("Starting File Compressor application");
		logger.debug("Java version: {}", System.getProperty("java.version"));
		logger.debug("OS: {} {}", System.getProperty("os.name"), System.getProperty("os.version"));

		Main app = new Main();
		JPanel contentPane = app.createContentPane();
		MainWindow.init(contentPane, e -> app.handleFileOpen());

		logger.info("Application UI initialized successfully");
	}
}
