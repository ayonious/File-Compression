package prog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import prog.huffman.Hzipping;
import prog.huffman.Hunzipping;
import prog.lzw.Lzipping;
import prog.lzw.Lunzipping;

public class Main extends JFrame implements ActionListener {
	// Definition of global values and items that are part of the GUI.

	static public File opened_file, other_file;
	static long past, future;
	static JLabel redLabel, blueLabel, redScore, blueScore;
	static JPanel buttonPanel, titlePanel, scorePanel;
	static JButton ZH, UH, ZL, UL, EX;

	public JPanel createContentPane() {
		return MainPanel.createContentPane(this);
	}

	private void handleHuffmanCompression() {
		Hzipping.beginHzipping(opened_file.getPath());
		showCompressionCompleteDialog("Zipping");
		updateFileStats(".huffz");
	}

	private void handleHuffmanDecompression() {
		Hunzipping.beginHunzipping(opened_file.getPath());
		showCompressionCompleteDialog("UnZipping");
		updateDecompressionStats();
	}

	private void handleLZWCompression() {
		Lzipping.beginLzipping(opened_file.getPath());
		showCompressionCompleteDialog("Zipping");
		updateFileStats(".LmZWp");
	}

	private void handleLZWDecompression() {
		Lunzipping.beginLunzipping(opened_file.getPath());
		showCompressionCompleteDialog("UnZipping");
		updateDecompressionStats();
	}

	private void showCompressionCompleteDialog(String operation) {
		JOptionPane.showMessageDialog(null,
				"..........................." + operation + " Finished..........................",
				"Status", JOptionPane.PLAIN_MESSAGE);
	}

	private void updateFileStats(String extension) {
		redScore.setText(opened_file.length() + "Bytes");
		other_file = new File(opened_file.getPath() + extension);
		future = other_file.length();
		blueScore.setText(future + "Bytes");
	}

	private void updateDecompressionStats() {
		redScore.setText(opened_file.length() + "Bytes");
		String s = opened_file.getPath();
		s = s.substring(0, s.length() - 6);
		other_file = new File(s);
		future = other_file.length();
		blueScore.setText(future + "Bytes");
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ZH) {
			handleHuffmanCompression();
		} else if (e.getSource() == UH) {
			handleHuffmanDecompression();
		} else if (e.getSource() == ZL) {
			handleLZWCompression();
		} else if (e.getSource() == UL) {
			handleLZWDecompression();
		} else if (e.getSource() == EX) {
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		MainFrame.init();
	}
}
