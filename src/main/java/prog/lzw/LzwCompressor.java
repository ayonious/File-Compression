package prog.lzw;

import prog.util.CommonUtil;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LzwCompressor {

	public static int bitSize;
	public static String bitBuffer;


	/*
	 * =========================================================================
	 * = make the integer to bin then return bitSize size's string
	 * =========================================================================
	 */
	public static String intToBinaryString(int input) {
		String ret = "", r1 = "";
		if (input == 0)
			ret = "0";
		int i;
		while (input != 0) {
			if ((input % 2) == 1)
				ret += "1";
			else
				ret += "0";
			input /= 2;
		}
		for (i = ret.length() - 1; i >= 0; i--) {
			r1 += ret.charAt(i);
		}
		while (r1.length() != bitSize) {
			r1 = "0" + r1;
		}
		return r1;
	}
	/*
	 * =========================================================================
	 * =
	 */

	/*
	 * =========================================================================
	 * = string to byte
	 * =========================================================================
	 * =
	 */

	public static Byte stringToByte(String binaryString) {

		int i, n = binaryString.length();
		byte ret = 0;
		for (i = 0; i < n; i++) {
			ret *= 2.;
			if (binaryString.charAt(i) == '1')
				ret++;
		}
		for (; n < 8; n++)
			ret *= 2.;
		Byte r = ret;
		return r;
	}

	/*
	 * =======================================================================
	 */

	/*
	 * =========================================================================
	 * = precalcs the length of the
	 * =========================================================================
	 */
	public static void calculateBitSize(String filename) {
		Map<String, Integer> dictionary = new HashMap<String, Integer>();
		int dictSize = 256;
		for (int i = 0; i < 256; i++)
			dictionary.put("" + (char) i, i);
		int mpsz = 256;
		String w = "";

		File filei = null;
		filei = new File(filename);

		try {
			FileInputStream file_inputut = new FileInputStream(filei);
			DataInputStream data_in = new DataInputStream(file_inputut);

			Byte c;
			int ch;
			while (true) {
				try {
					c = data_in.readByte();
					ch = CommonUtil.byteToUnsignedInt(c);
					String wc = w + (char) ch;
					if (dictionary.containsKey(wc))
						w = wc;
					else {
						if (mpsz < 100000) {
							dictionary.put(wc, dictSize++);
							mpsz += wc.length();
						}
						w = "" + (char) ch;
					}
				} catch (EOFException eof) {
					System.out.println("End of File");
					break;
				}
			}
			file_inputut.close();
			data_in.close();
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
		}

		// if empty file
		if (dictSize <= 1) {
			bitSize = 1;
		} else {
			bitSize = 0;
			long i = 1;
			while (i < dictSize) {
				i *= 2;
				bitSize++;
			}
		}
		filei = null;

	}
	/*
	 * =========================================================================
	 * ==
	 */

	/*
	 * =========================================================================
	 * === zipping function
	 * =========================================================================
	 * ====
	 */

	public static void compressFile(String filename) {
		Map<String, Integer> dictionary = new HashMap<String, Integer>();
		int dictSize = 256;
		bitBuffer = "";
		for (int i = 0; i < 256; i++)
			dictionary.put("" + (char) i, i);
		int mpsz = 256;
		String w = "";
		String fileos = filename + ".LmZWp";
		File filei, fileo;
		filei = new File(filename);
		fileo = new File(fileos);

		try {
			FileInputStream file_inputut = new FileInputStream(filei);
			DataInputStream data_in = new DataInputStream(file_inputut);
			FileOutputStream file_output = new FileOutputStream(fileo);
			DataOutputStream data_out = new DataOutputStream(file_output);

			data_out.writeInt(bitSize);
			Byte c;
			int ch;
			while (true) {
				try {
					c = data_in.readByte();
					ch = CommonUtil.byteToUnsignedInt(c);

					String wc = w + (char) ch;
					if (dictionary.containsKey(wc))
						w = wc;
					else {
						bitBuffer += intToBinaryString(dictionary.get(w));
						while (bitBuffer.length() >= 8) {
							data_out.write(stringToByte(bitBuffer.substring(0, 8)));
							bitBuffer = bitBuffer.substring(8, bitBuffer.length());
						}

						if (mpsz < 100000) {
							dictionary.put(wc, dictSize++);
							mpsz += wc.length();
						}
						w = "" + (char) ch;
					}
				} catch (EOFException eof) {
					System.out.println("End of File");
					break;
				}
			}

			if (!w.equals("")) {
				bitBuffer += intToBinaryString(dictionary.get(w));
				while (bitBuffer.length() >= 8) {
					data_out.write(stringToByte(bitBuffer.substring(0, 8)));
					bitBuffer = bitBuffer.substring(8, bitBuffer.length());
				}
				if (bitBuffer.length() >= 1) {
					data_out.write(stringToByte(bitBuffer));
				}
			}
			data_in.close();
			data_out.close();
			file_inputut.close();
			file_output.close();
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
		}

		filei = null;
		fileo = null;

	}
	/*
	 * =========================================================================
	 * ====
	 */

	public static void beginLzwCompression(String arg1) {
		bitSize = 0;
		bitBuffer = "";
		calculateBitSize(arg1);
		compressFile(arg1);
		bitSize = 0;
		bitBuffer = "";
	}

	/*
	 * public static void main(String[] args) { bitSize=0; bitBuffer="";
	 * precalc("binaryString.txt"); Lamzip("binaryString.txt"); bitSize=0; bitBuffer=""; }
	 */
}