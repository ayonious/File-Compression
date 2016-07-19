package prog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Lzipping {

	public static int btsz;
	public static String big;

	public static int btoi(Byte bt) {
		int ret = bt;
		if (ret < 0) {
			ret += 256;
		}
		return ret;

	}

	/*
	 * =========================================================================
	 * = make the integer to bin then return btsz size's string
	 * =========================================================================
	 */
	public static String fil(int inp) {
		String ret = "", r1 = "";
		if (inp == 0)
			ret = "0";
		int i;
		while (inp != 0) {
			if ((inp % 2) == 1)
				ret += "1";
			else
				ret += "0";
			inp /= 2;
		}
		for (i = ret.length() - 1; i >= 0; i--) {
			r1 += ret.charAt(i);
		}
		while (r1.length() != btsz) {
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

	public static Byte strtobt(String in) {

		int i, n = in.length();
		byte ret = 0;
		for (i = 0; i < n; i++) {
			ret *= 2.;
			if (in.charAt(i) == '1')
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
	public static void precalc(String fileis) {
		Map<String, Integer> dictionary = new HashMap<String, Integer>();
		int dictSize = 256;
		for (int i = 0; i < 256; i++)
			dictionary.put("" + (char) i, i);
		int mpsz = 256;
		String w = "";

		File filei = null;
		filei = new File(fileis);

		try {
			FileInputStream file_input = new FileInputStream(filei);
			DataInputStream data_in = new DataInputStream(file_input);

			Byte c;
			int ch;
			while (true) {
				try {
					c = data_in.readByte();
					ch = btoi(c);
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
			file_input.close();
			data_in.close();
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
		}

		// if empty file
		if (dictSize <= 1) {
			btsz = 1;
		} else {
			btsz = 0;
			long i = 1;
			while (i < dictSize) {
				i *= 2;
				btsz++;
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

	public static void Lamzip(String fileis) {
		Map<String, Integer> dictionary = new HashMap<String, Integer>();
		int dictSize = 256;
		big = "";
		for (int i = 0; i < 256; i++)
			dictionary.put("" + (char) i, i);
		int mpsz = 256;
		String w = "";
		String fileos = fileis + ".LmZWp";
		File filei, fileo;
		filei = new File(fileis);
		fileo = new File(fileos);

		try {
			FileInputStream file_input = new FileInputStream(filei);
			DataInputStream data_in = new DataInputStream(file_input);
			FileOutputStream file_output = new FileOutputStream(fileo);
			DataOutputStream data_out = new DataOutputStream(file_output);

			data_out.writeInt(btsz);
			Byte c;
			int ch;
			while (true) {
				try {
					c = data_in.readByte();
					ch = btoi(c);

					String wc = w + (char) ch;
					if (dictionary.containsKey(wc))
						w = wc;
					else {
						big += fil(dictionary.get(w));
						while (big.length() >= 8) {
							data_out.write(strtobt(big.substring(0, 8)));
							big = big.substring(8, big.length());
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
				big += fil(dictionary.get(w));
				while (big.length() >= 8) {
					data_out.write(strtobt(big.substring(0, 8)));
					big = big.substring(8, big.length());
				}
				if (big.length() >= 1) {
					data_out.write(strtobt(big));
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
	/*
	 * =========================================================================
	 * ====
	 */

	public static void beginLzipping(String arg1) {
		btsz = 0;
		big = "";
		precalc(arg1);
		Lamzip(arg1);
		btsz = 0;
		big = "";
	}

	/*
	 * public static void main(String[] args) { btsz=0; big="";
	 * precalc("in.txt"); Lamzip("in.txt"); btsz=0; big=""; }
	 */
}