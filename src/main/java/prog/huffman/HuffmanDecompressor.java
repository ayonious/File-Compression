package prog.huffman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.PriorityQueue;

public class HuffmanDecompressor {
	static PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<HuffmanNode>();
	static int[] frequency = new int[300];
	static String[] huffmanCodes = new String[300]; // INT TO CODE
	static String[] byteToString = new String[300]; // INT TO BIN
	static String bitBuffer; // THE BIG STRING
	static String tempCode; // TEMPORARY STRING
	static int extraBits; // EXTRA BITS ADDED AT THE LAST TO MAKE THE FINAL ZIP
						// CODE MULTIPLE OF 8
	static int decodedByte; //
	static int uniqueCharCount; // NUMBER OF freqs available

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

	/**********************************************************************************
	 * freing the memory
	 *********************************************************************************/
	public static void initHuffmanDecompressor() {
		int i;
		if (root != null)
			HuffmanUtils.fredfs(root, HuffmanUtils.HUNZIPPING_HuffmanNode_ACCESSOR);
		for (i = 0; i < 300; i++)
			frequency[i] = 0;
		for (i = 0; i < 300; i++)
			huffmanCodes[i] = "";
		priorityQueue.clear();
		bitBuffer = ""; // THE BIG STRING
		tempCode = ""; // TEMPORARY STRING
		extraBits = 0; // EXTRA BITS ADDED AT THE LAST TO MAKE THE FINAL ZIP CODE
						// MULTIPLE OF 8
		decodedByte = 0; //
		uniqueCharCount = 0;
	}

	/**********************************************************************************/
	/**********************************************************************************
	 * dfs1 to free memory
	 *********************************************************************************/
	public static void fredfs1(HuffmanNode node) {

		if (node.leftChild == null && node.rightChild == null) {
			node = null;
			return;
		}
		if (node.leftChild != null)
			fredfs1(node.leftChild);
		if (node.rightChild != null)
			fredfs1(node.rightChild);
	}

	/**********************************************************************************/

	/**********************************************************************************
	 * dfs1 to make the codes
	 *********************************************************************************/
	// public static void generateHuffmanCodes(HuffmanNode node,int code,String code)
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
		uniqueCharCount = 0;
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
	 * reading the freq1 from "codes.txt"//updating ss
	 ******************************************************************************/
	public static void readfreq1(String cc) {

		File filei = new File(cc);
		int fey, i;
		Byte baital;
		try {
			FileInputStream file_input = new FileInputStream(filei);
			DataInputStream data_in = new DataInputStream(file_input);
			uniqueCharCount = data_in.readInt();

			for (i = 0; i < uniqueCharCount; i++) {
				baital = data_in.readByte();
				fey = data_in.readInt();
				frequency[HuffmanUtils.to(baital)] = fey;
			}
			data_in.close();
			file_input.close();
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
		}

		buildHuffmanTree(); // makeing corresponding nodes
		if (uniqueCharCount > 1)
			generateHuffmanCodes(root, ""); // dfs1 to make the codes

		for (i = 0; i < 256; i++) {
			if (huffmanCodes[i] == null)
				huffmanCodes[i] = "";
		}
		filei = null;
	}

	/*******************************************************************************/

	/***********************************************************************************
	 * int to bin string conversion code creating
	 ***********************************************************************************/
	public static void createbin() {
		int i, j;
		String t;
		for (i = 0; i < 256; i++) {
			byteToString[i] = "";
			j = i;
			while (j != 0) {
				if (j % 2 == 1)
					byteToString[i] += "1";
				else
					byteToString[i] += "0";
				j /= 2;
			}
			t = "";
			for (j = byteToString[i].length() - 1; j >= 0; j--) {
				t += byteToString[i].charAt(j);
			}
			byteToString[i] = t;
			// System.out.println(byteToString[i]);
		}
		byteToString[0] = "0";
	}

	/****************************************************************************/

	/******************************************************************************
	 * got yes means tempCode is a valid code and decodedByte is the code's corresponding
	 * val
	 ******************************************************************************/
	public static int got() {
		int i;

		for (i = 0; i < 256; i++) {
			if (huffmanCodes[i].compareTo(tempCode) == 0) {
				decodedByte = i;
				return 1;
			}
		}
		return 0;

	}

	/********************************************************************************/

	/***********************************************************************************
	 * byte to int conversion
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

	/***********************************************************************************
	 * convert any string into eight digit string
	 ***********************************************************************************/
	public static String makeeight(String b) {
		String ret = "";
		int i;
		int len = b.length();
		for (i = 0; i < (8 - len); i++)
			ret += "0";
		ret += b;
		return ret;
	}

	/***********************************************************************************/

	/***********************************************************************************
	 * unzipping function
	 **************************************************************************************/
	public static void readbin(String zip, String unz) {
		File f1 = null, f2 = null;
		int ok, bt;
		Byte b;
		int j, i;
		bitBuffer = "";
		f1 = new File(zip);
		f2 = new File(unz);
		try {
			FileOutputStream file_output = new FileOutputStream(f2);
			DataOutputStream data_out = new DataOutputStream(file_output);
			FileInputStream file_input = new FileInputStream(f1);
			DataInputStream data_in = new DataInputStream(file_input);
			try {
				uniqueCharCount = data_in.readInt();
				System.out.println(uniqueCharCount);
				for (i = 0; i < uniqueCharCount; i++) {
					b = data_in.readByte();
					j = data_in.readInt();

					// System.out.println(ss[to(b)]);
				}
				extraBits = data_in.readInt();
				System.out.println(extraBits);

			} catch (EOFException eof) {
				System.out.println("End of File");
			}

			while (true) {
				try {
					b = data_in.readByte();
					bt = HuffmanUtils.to(b);
					bitBuffer += HuffmanUtils.makeeight(byteToString[bt]);

					// System.out.println(bitBuffer);

					while (true) {
						ok = 1;
						tempCode = "";
						for (i = 0; i < bitBuffer.length() - extraBits; i++) {
							tempCode += bitBuffer.charAt(i);
							// System.out.println(tempCode);
							if (got() == 1) {
								data_out.write(decodedByte);
								ok = 0;
								String s = "";
								for (j = tempCode.length(); j < bitBuffer.length(); j++) {
									s += bitBuffer.charAt(j);
								}
								bitBuffer = s;
								break;
							}
						}

						if (ok == 1)
							break;
					}
				} catch (EOFException eof) {
					System.out.println("End of File");
					break;
				}
			}
			file_output.close();
			data_out.close();
			file_input.close();
			data_in.close();
		} catch (IOException e) {
			System.out.println("IO Exception =: " + e);
		}

		f1 = null;
		f2 = null;
	}

	/************************************************************************************/
	// again improve the to function
	// error if only <=one character in the input file
	public static void beginHuffmanDecompression(String arg1) {
		initHuffmanDecompressor();
		readfreq1(arg1);
		createbin();
		int n = arg1.length();
		String arg2 = arg1.substring(0, n - 6);
		readbin(arg1, arg2);
		initHuffmanDecompressor();
	}

	/*
	 * public static void main (String arg[]) { initHunzipping(); String
	 * arg1="in.txt.huffz"; int n=arg1.length(); readfreq1(arg1); createbin();
	 * String arg2=arg1.substring(0,n-6); //mame of the zipped file,name
	 * afterextracting readbin(arg1,arg2); initHunzipping(); }
	 */
}