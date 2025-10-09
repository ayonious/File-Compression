package prog.huffman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.PriorityQueue;

//if the frequency of a byte is more than 2^32 then there will be problem
public class HuffmanCompressor {

	static PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<HuffmanNode>();
	static int[] frequency = new int[300];
	static String[] huffmanCodes = new String[300];
	static int extraBits;
	static byte currentByte;
	static int uniqueCharCount; // number of different characters

	// for keeping frequncies of all the bytes

	// main tree class

	static class HuffmanNode implements Comparable<HuffmanNode> {
		HuffmanNode leftChild;
		HuffmanNode rightChild;
		public String code;
		public int byteValue;
		public int frequency;

		public int compareTo(HuffmanNode other) {
			if (this.frequency < other.frequency)
				return -1;
			if (this.frequency > other.frequency)
				return 1;
			return 0;
		}
	}

	static HuffmanNode root;

	/*******************************************************************************
	 * calculating frequence of file filename
	 ******************************************************************************/

	public static void calculateFrequency(String filename) {
		File file = null;
		Byte currentByte;

		file = new File(filename);
		try {
			FileInputStream file_input = new FileInputStream(file);
			DataInputStream data_in = new DataInputStream(file_input);
			while (true) {
				try {

					currentByte = data_in.readByte();
					frequency[HuffmanUtils.to(currentByte)]++;
				} catch (EOFException eof) {
					System.out.println("End of File");
					break;
				}
			}
			file_input.close();
			data_in.close();
		} catch (IOException e) {
			System.out.println("IO Exception =: " + e);
		}
		file = null;
	}

	/************************************** ============ ************************/

	/***********************************************************************************
	 * byte to binary conversion
	 ***********************************************************************************/
	public static int to(Byte b) {
		int ret = b;
		if (ret < 0) {
			ret = ~b;
			ret = ret + 1;
			ret = ret ^ 255;
			ret += 1;
		}
		return ret;
	}

	/***********************************************************************************/

	/**********************************************************************************
	 * freing the memory
	 *********************************************************************************/
	public static void initHuffmanCompressor() {
		int i;
		uniqueCharCount = 0;
		if (root != null)
			HuffmanUtils.fredfs(root, HuffmanUtils.HZIPPING_TREE_ACCESSOR);
		for (i = 0; i < 300; i++)
			frequency[i] = 0;
		for (i = 0; i < 300; i++)
			huffmanCodes[i] = "";
		priorityQueue.clear();
	}

	/**********************************************************************************/
	/**********************************************************************************
	 * dfs to free memory
	 *********************************************************************************/
	public static void fredfs(HuffmanNode node) {

		if (node.leftChild == null && node.rightChild == null) {
			node = null;
			return;
		}
		if (node.leftChild != null)
			fredfs(node.leftChild);
		if (node.rightChild != null)
			fredfs(node.rightChild);
	}

	/**********************************************************************************/

	/**********************************************************************************
	 * dfs to make the codes
	 *********************************************************************************/
	public static void generateHuffmanCodes(HuffmanNode node, String code) {
		node.code = code;
		if ((node.leftChild == null) && (node.rightChild == null)) {
			huffmanCodes[node.byteValue] = code;
			return;
		}
		if (node.leftChild != null)
			generateHuffmanCodes(node.leftChild, code + "0");
		if (node.rightChild != null)
			generateHuffmanCodes(node.rightChild, code + "1");
	}

	/**********************************************************************************/

	/*******************************************************************************
	 * Making all the nodes in a priority Q making the tree
	 *******************************************************************************/
	public static void buildHuffmanTree() {
		int i;
		priorityQueue.clear();

		for (i = 0; i < 300; i++) {
			if (frequency[i] != 0) {
				HuffmanNode Temp = new HuffmanNode();
				Temp.byteValue = i;
				Temp.frequency = frequency[i];
				Temp.leftChild = null;
				Temp.rightChild = null;
				priorityQueue.add(Temp);
				uniqueCharCount++;
			}

		}
		HuffmanNode Temp1, Temp2;

		if (uniqueCharCount == 0) {
			return;
		} else if (uniqueCharCount == 1) {
			for (i = 0; i < 300; i++)
				if (frequency[i] != 0) {
					huffmanCodes[i] = "0";
					break;
				}
			return;
		}

		// will there b a problem if the file is empty
		// a bug is found if there is only one character
		while (priorityQueue.size() != 1) {
			HuffmanNode Temp = new HuffmanNode();
			Temp1 = priorityQueue.poll();
			Temp2 = priorityQueue.poll();
			Temp.leftChild = Temp1;
			Temp.rightChild = Temp2;
			Temp.frequency = Temp1.frequency + Temp2.frequency;
			priorityQueue.add(Temp);
		}
		root = priorityQueue.poll();
	}

	/*******************************************************************************/

	/*******************************************************************************
	 * encrypting
	 *******************************************************************************/
	public static void encrypt(String filename) {
		File file = null;

		file = new File(filename);
		try {
			FileInputStream file_input = new FileInputStream(file);
			DataInputStream data_in = new DataInputStream(file_input);
			while (true) {
				try {

					currentByte = data_in.readByte();
					frequency[currentByte]++;
				} catch (EOFException eof) {
					System.out.println("End of File");
					break;
				}
			}
			file_input.close();
			data_in.close();

		} catch (IOException e) {
			System.out.println("IO Exception =: " + e);
		}
		file = null;
	}

	/*******************************************************************************/

	/*******************************************************************************
	 * fake zip creates a file "fakezip.txt" where puts the final binary codes
	 * of the real zipped file
	 *******************************************************************************/
	public static void fakezip(String filename) {

		File filei, fileo;
		int i;

		filei = new File(filename);
		fileo = new File("fakezipped.txt");
		try {
			FileInputStream file_input = new FileInputStream(filei);
			DataInputStream data_in = new DataInputStream(file_input);
			PrintStream ps = new PrintStream(fileo);

			while (true) {
				try {
					currentByte = data_in.readByte();
					ps.print(huffmanCodes[to(currentByte)]);
				} catch (EOFException eof) {
					System.out.println("End of File");
					break;
				}
			}

			file_input.close();
			data_in.close();
			ps.close();

		} catch (IOException e) {
			System.out.println("IO Exception =: " + e);
		}
		filei = null;
		fileo = null;

	}

	/*******************************************************************************/

	/*******************************************************************************
	 * real zip according to codes of fakezip.txt (filename)
	 *******************************************************************************/
	public static void realzip(String filename, String filename1) {
		File filei, fileo;
		int i, j = 10;
		Byte currentBytet;

		filei = new File(filename);
		fileo = new File(filename1);

		try {
			FileInputStream file_input = new FileInputStream(filei);
			DataInputStream data_in = new DataInputStream(file_input);
			FileOutputStream file_output = new FileOutputStream(fileo);
			DataOutputStream data_out = new DataOutputStream(file_output);

			data_out.writeInt(uniqueCharCount);
			for (i = 0; i < 256; i++) {
				if (frequency[i] != 0) {
					currentBytet = (byte) i;
					data_out.write(currentBytet);
					data_out.writeInt(frequency[i]);
				}
			}
			long textraBits;
			textraBits = filei.length() % 8;
			textraBits = (8 - textraBits) % 8;
			extraBits = (int) textraBits;
			data_out.writeInt(extraBits);
			while (true) {
				try {
					currentByte = 0;
					byte ch;
					for (extraBits = 0; extraBits < 8; extraBits++) {
						ch = data_in.readByte();
						currentByte *= 2;
						if (ch == '1')
							currentByte++;
					}
					data_out.write(currentByte);

				} catch (EOFException eof) {
					int x;
					if (extraBits != 0) {
						for (x = extraBits; x < 8; x++) {
							currentByte *= 2;
						}
						data_out.write(currentByte);
					}

					extraBits = (int) textraBits;
					System.out.println("extrabits: " + extraBits);
					System.out.println("End of File");
					break;
				}
			}
			data_in.close();
			data_out.close();
			file_input.close();
			file_output.close();
			System.out.println("output file's size: " + fileo.length());

		} catch (IOException e) {
			System.out.println("IO exception = " + e);
		}
		filei.delete();
		filei = null;
		fileo = null;
	}

	/*******************************************************************************/

	/*
	 * public static void main (String[] args) { initHzipping();
	 * CalFreq("in.txt"); // calculate the frequency of each digit MakeNode();
	 * // makeing corresponding nodes if(uniqueCharCount>1) generateHuffmanCodes(root,""); // dfs to make the
	 * codes fakezip("in.txt"); // fake zip file which will have the binary of
	 * the input to fakezipped.txt file
	 * realzip("fakezipped.txt","in.txt"+".huffz"); // making the real zip
	 * according the fakezip.txt file initHzipping();
	 * 
	 * }
	 */

	public static void beginHuffmanCompression(String arg1) {
		initHuffmanCompressor();
		calculateFrequency(arg1); // calculate the frequency of each digit
		buildHuffmanTree(); // build huffman tree from frequencies
		if (uniqueCharCount > 1)
			generateHuffmanCodes(root, ""); // dfs to make the codes
		fakezip(arg1); // fake zip file which will have the binary of the input
						// to fakezipped.txt file
		realzip("fakezipped.txt", arg1 + ".huffz"); // making the real zip
													// according the fakezip.txt
													// file
		initHuffmanCompressor();
	}
}