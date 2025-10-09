package prog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import prog.huffman.HuffmanCompressor;
import prog.huffman.HuffmanDecompressor;
import prog.lzw.LzwCompressor;
import prog.lzw.LzwDecompressor;

public class Main extends JFrame implements ActionListener {
	// Definition of global values and items that are part of the GUI.

	static public File openedFile, outputFile;
	static long originalSize, compressedSize;
	static JLabel originalSizeLabel, compressedSizeLabel, originalSizeValue, compressedSizeValue;
	static JPanel buttonPanel, titlePanel, scorePanel;
	static JButton huffmanCompressButton, huffmanDecompressButton, lzwCompressButton, lzwDecompressButton, exitButton;

	public JPanel createContentPane() {
		return MainPanel.createContentPane(this);
	}

	private void handleHuffmanCompression() {
		HuffmanCompressor.beginHuffmanCompression(openedFile.getPath());
		showCompressionCompleteDialog("Zipping");
		updateFileStats(".huffz");
	}

	private void handleHuffmanDecompression() {
		HuffmanDecompressor.beginHuffmanDecompression(openedFile.getPath());
		showCompressionCompleteDialog("UnZipping");
		updateDecompressionStats();
	}

	private void handleLZWCompression() {
		LzwCompressor.beginLzwCompression(openedFile.getPath());
		showCompressionCompleteDialog("Zipping");
		updateFileStats(".LmZWp");
	}

	private void handleLZWDecompression() {
		LzwDecompressor.beginLzwDecompression(openedFile.getPath());
		showCompressionCompleteDialog("UnZipping");
		updateDecompressionStats();
	}

	private void showCompressionCompleteDialog(String operation) {
		JOptionPane.showMessageDialog(null,
				"..........................." + operation + " Finished..........................",
				"Status", JOptionPane.PLAIN_MESSAGE);
	}

	private void updateFileStats(String extension) {
		originalSizeValue.setText(openedFile.length() + "Bytes");
		outputFile = new File(openedFile.getPath() + extension);
		compressedSize = outputFile.length();
		compressedSizeValue.setText(compressedSize + "Bytes");
	}

	private void updateDecompressionStats() {
		originalSizeValue.setText(openedFile.length() + "Bytes");
		String s = openedFile.getPath();
		s = s.substring(0, s.length() - 6);
		outputFile = new File(s);
		compressedSize = outputFile.length();
		compressedSizeValue.setText(compressedSize + "Bytes");
	}

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

	public static void main(String[] args) {
		MainFrame.init();
	}
}
