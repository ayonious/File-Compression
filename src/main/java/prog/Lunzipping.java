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

public class Lunzipping {

	public static int bitsz1;

	// byte er string representation in eight digit
	public static String bttost[] = new String[256];
	public static String big1;

	/*
	 * ============================================================ make all the
	 * binary to string conversion for 8 bits
	 * =============================================================
	 */
	public static void pre() {
		int i, j;
		String r1;
		bttost[0] = "0";
		for (i = 0; i < 256; i++) {
			r1 = "";
			j = i;
			if (i != 0)
				bttost[i] = "";
			while (j != 0) {
				if ((j % 2) == 1)
					bttost[i] += "1";
				else
					bttost[i] += "0";
				j /= 2;
			}
			for (j = bttost[i].length() - 1; j >= 0; j--) {
				r1 += bttost[i].charAt(j);
			}
			while (r1.length() < 8) {
				r1 = "0" + r1;
			}
			bttost[i] = r1;
		}
	}
	/*
	 * =========================================================================
	 */

	/*
	 * =========================================================================
	 * = byte to int conversion
	 * =========================================================================
	 */
	public static int btoi(Byte bt) {
		int ret = bt;
		if (ret < 0)
			ret += 256;
		return ret;

	}

	/** ====================================================================== */

	/*
	 * =========================================================================
	 * = byte to int conversion
	 * =========================================================================
	 */
	public static int stoi(String s) {
		int ret = 0, i;
		for (i = 0; i < s.length(); i++) {
			ret *= 2;
			if (s.charAt(i) == '1')
				ret++;
		}
		return ret;
	}

	/** ====================================================================== */

	public static void Lunzip(String fileis) {
		int k;
		int dictSize = 256;
		int mpsz = 256;
		String ts;
		Map<Integer, String> dictionary = new HashMap<Integer, String>();
		for (int i = 0; i < 256; i++)
			dictionary.put(i, "" + (char) i);

		String fileos = fileis.substring(0, fileis.length() - 6);

		File filei = null, fileo = null;
		filei = new File(fileis);
		fileo = new File(fileos);
		try {
			FileInputStream file_input = new FileInputStream(filei);
			DataInputStream data_in = new DataInputStream(file_input);
			FileOutputStream file_output = new FileOutputStream(fileo);
			DataOutputStream data_out = new DataOutputStream(file_output);

			Byte c;
			bitsz1 = data_in.readInt();

			while (true) {
				try {
					c = data_in.readByte();
					big1 += bttost[btoi(c)];
					if (big1.length() >= bitsz1)
						break;
				} catch (EOFException eof) {
					System.out.println("End of File");
					break;
				}
			}

			if (big1.length() >= bitsz1) {
				k = stoi(big1.substring(0, bitsz1));
				big1 = big1.substring(bitsz1, big1.length());
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
					while (big1.length() < bitsz1) {
						c = data_in.readByte();
						big1 += bttost[btoi(c)];
					}
					k = stoi(big1.substring(0, bitsz1));
					big1 = big1.substring(bitsz1, big1.length());

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

	public static void beginLunzipping(String arg1) {
		big1 = "";
		bitsz1 = 0;
		pre();
		Lunzip(arg1);
		big1 = "";
		bitsz1 = 0;
	}

	/*
	 * public static void main(String[] args) { big1=""; bitsz1=0; pre();
	 * Lunzip("in.txt.LmZWp"); big1=""; bitsz1=0; }
	 */
}