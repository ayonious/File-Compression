package prog.lzw;

public class LzwUtils {

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
} 