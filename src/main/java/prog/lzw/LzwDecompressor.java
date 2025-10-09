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

public class LzwDecompressor {

	public static int bitSize;

	// byte er string representation in eight digit
	public static String byteToString[] = new String[256];
	public static String bitBuffer;

	/*
	 * ============================================================ make all the
	 * binary to string conversion for 8 bits
	 * =============================================================
	 */
	public static void initializeByteToStringTable() {
		int i, j;
		String r1;
		byteToString[0] = "0";
		for (i = 0; i < 256; i++) {
			r1 = "";
			j = i;
			if (i != 0)
				byteToString[i] = "";
			while (j != 0) {
				if ((j % 2) == 1)
					byteToString[i] += "1";
				else
					byteToString[i] += "0";
				j /= 2;
			}
			for (j = byteToString[i].length() - 1; j >= 0; j--) {
				r1 += byteToString[i].charAt(j);
			}
			while (r1.length() < 8) {
				r1 = "0" + r1;
			}
			byteToString[i] = r1;
		}
	}
	/*
	 * =========================================================================
	 */

	public static void decompressFile(String filename) {
		int k;
		int dictSize = 256;
		int mpsz = 256;
		String ts;
		Map<Integer, String> dictionary = new HashMap<Integer, String>();
		for (int i = 0; i < 256; i++)
			dictionary.put(i, "" + (char) i);

		String fileos = filename.substring(0, filename.length() - 6);

		File filei = null, fileo = null;
		filei = new File(filename);
		fileo = new File(fileos);
		try {
			FileInputStream file_input = new FileInputStream(filei);
			DataInputStream data_in = new DataInputStream(file_input);
			FileOutputStream file_output = new FileOutputStream(fileo);
			DataOutputStream data_out = new DataOutputStream(file_output);

			Byte c;
			bitSize = data_in.readInt();

			while (true) {
				try {
					c = data_in.readByte();
					bitBuffer += byteToString[CommonUtil.byteToUnsignedInt(c)];
					if (bitBuffer.length() >= bitSize)
						break;
				} catch (EOFException eof) {
					System.out.println("End of File");
					break;
				}
			}

			if (bitBuffer.length() >= bitSize) {
				k = CommonUtil.binaryStringToInt(bitBuffer.substring(0, bitSize));
				bitBuffer = bitBuffer.substring(bitSize, bitBuffer.length());
			} else {
				data_in.close();
				data_out.close();
				return;
			}

			String w = "" + (char) k;

			data_out.writeBytes(w);
			// System.out.println(w);

			while (true) {
				try {
					while (bitBuffer.length() < bitSize) {
						c = data_in.readByte();
						bitBuffer += byteToString[CommonUtil.byteToUnsignedInt(c)];
					}
					k = CommonUtil.binaryStringToInt(bitBuffer.substring(0, bitSize));
					bitBuffer = bitBuffer.substring(bitSize, bitBuffer.length());

					String entry = "";
					if (dictionary.containsKey(k)) {

						entry = dictionary.get(k);
					} else if (k == dictSize) {
						entry = w + w.charAt(0);

					}
					data_out.writeBytes(entry);

					if (mpsz < 100000) {
						ts = w + entry.charAt(0);
						dictionary.put(dictSize++, ts);
						mpsz += ts.length();
					}
					w = entry;
				} catch (EOFException eof) {
					System.out.println("End of File");
					break;
				}
			}
			data_in.close();
			data_out.close();
			file_input.close();
			file_output.close();
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
		}

		filei = null;
		fileo = null;

	}

	public static void beginLzwDecompression(String arg1) {
		bitBuffer = "";
		bitSize = 0;
		initializeByteToStringTable();
		decompressFile(arg1);
		bitBuffer = "";
		bitSize = 0;
	}

	/*
	 * public static void main(String[] args) { bitBuffer=""; bitSize=0; pre();
	 * Lunzip("in.txt.LmZWp"); bitBuffer=""; bitSize=0; }
	 */
}