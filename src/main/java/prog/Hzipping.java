package prog;

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
public class Hzipping {

	static PriorityQueue<TREE> pq = new PriorityQueue<TREE>();
	static int[] freq = new int[300];
	static String[] ss = new String[300];
	static int exbits;
	static byte bt;
	static int cnt; // number of different characters

	// for keeping frequncies of all the bytes

	// main tree class

	static class TREE implements Comparable<TREE> {
		TREE Lchild;
		TREE Rchild;
		public String deb;
		public int Bite;
		public int Freqnc;

		public int compareTo(TREE T) {
			if (this.Freqnc < T.Freqnc)
				return -1;
			if (this.Freqnc > T.Freqnc)
				return 1;
			return 0;
		}
	}

	static TREE Root;

	/*******************************************************************************
	 * calculating frequence of file fname
	 ******************************************************************************/

	public static void CalFreq(String fname) {
		File file = null;
		Byte bt;

		file = new File(fname);
		try {
			FileInputStream file_input = new FileInputStream(file);
			DataInputStream data_in = new DataInputStream(file_input);
			while (true) {
				try {

					bt = data_in.readByte();
					freq[to(bt)]++;
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
	public static void initHzipping() {
		int i;
		cnt = 0;
		if (Root != null)
			fredfs(Root);
		for (i = 0; i < 300; i++)
			freq[i] = 0;
		for (i = 0; i < 300; i++)
			ss[i] = "";
		pq.clear();
	}

	/**********************************************************************************/
	/**********************************************************************************
	 * dfs to free memory
	 *********************************************************************************/
	public static void fredfs(TREE now) {

		if (now.Lchild == null && now.Rchild == null) {
			now = null;
			return;
		}
		if (now.Lchild != null)
			fredfs(now.Lchild);
		if (now.Rchild != null)
			fredfs(now.Rchild);
	}

	/**********************************************************************************/

	/**********************************************************************************
	 * dfs to make the codes
	 *********************************************************************************/
	public static void dfs(TREE now, String st) {
		now.deb = st;
		if ((now.Lchild == null) && (now.Rchild == null)) {
			ss[now.Bite] = st;
			return;
		}
		if (now.Lchild != null)
			dfs(now.Lchild, st + "0");
		if (now.Rchild != null)
			dfs(now.Rchild, st + "1");
	}

	/**********************************************************************************/

	/*******************************************************************************
	 * Making all the nodes in a priority Q making the tree
	 *******************************************************************************/
	public static void MakeNode() {
		int i;
		pq.clear();

		for (i = 0; i < 300; i++) {
			if (freq[i] != 0) {
				TREE Temp = new TREE();
				Temp.Bite = i;
				Temp.Freqnc = freq[i];
				Temp.Lchild = null;
				Temp.Rchild = null;
				pq.add(Temp);
				cnt++;
			}

		}
		TREE Temp1, Temp2;

		if (cnt == 0) {
			return;
		} else if (cnt == 1) {
			for (i = 0; i < 300; i++)
				if (freq[i] != 0) {
					ss[i] = "0";
					break;
				}
			return;
		}

		// will there b a problem if the file is empty
		// a bug is found if there is only one character
		while (pq.size() != 1) {
			TREE Temp = new TREE();
			Temp1 = pq.poll();
			Temp2 = pq.poll();
			Temp.Lchild = Temp1;
			Temp.Rchild = Temp2;
			Temp.Freqnc = Temp1.Freqnc + Temp2.Freqnc;
			pq.add(Temp);
		}
		Root = pq.poll();
	}

	/*******************************************************************************/

	/*******************************************************************************
	 * encrypting
	 *******************************************************************************/
	public static void encrypt(String fname) {
		File file = null;

		file = new File(fname);
		try {
			FileInputStream file_input = new FileInputStream(file);
			DataInputStream data_in = new DataInputStream(file_input);
			while (true) {
				try {

					bt = data_in.readByte();
					freq[bt]++;
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
	public static void fakezip(String fname) {

		File filei, fileo;
		int i;

		filei = new File(fname);
		fileo = new File("fakezipped.txt");
		try {
			FileInputStream file_input = new FileInputStream(filei);
			DataInputStream data_in = new DataInputStream(file_input);
			PrintStream ps = new PrintStream(fileo);

			while (true) {
				try {
					bt = data_in.readByte();
					ps.print(ss[to(bt)]);
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
	 * real zip according to codes of fakezip.txt (fname)
	 *******************************************************************************/
	public static void realzip(String fname, String fname1) {
		File filei, fileo;
		int i, j = 10;
		Byte btt;

		filei = new File(fname);
		fileo = new File(fname1);

		try {
			FileInputStream file_input = new FileInputStream(filei);
			DataInputStream data_in = new DataInputStream(file_input);
			FileOutputStream file_output = new FileOutputStream(fileo);
			DataOutputStream data_out = new DataOutputStream(file_output);

			data_out.writeInt(cnt);
			for (i = 0; i < 256; i++) {
				if (freq[i] != 0) {
					btt = (byte) i;
					data_out.write(btt);
					data_out.writeInt(freq[i]);
				}
			}
			long texbits;
			texbits = filei.length() % 8;
			texbits = (8 - texbits) % 8;
			exbits = (int) texbits;
			data_out.writeInt(exbits);
			while (true) {
				try {
					bt = 0;
					byte ch;
					for (exbits = 0; exbits < 8; exbits++) {
						ch = data_in.readByte();
						bt *= 2;
						if (ch == '1')
							bt++;
					}
					data_out.write(bt);

				} catch (EOFException eof) {
					int x;
					if (exbits != 0) {
						for (x = exbits; x < 8; x++) {
							bt *= 2;
						}
						data_out.write(bt);
					}

					exbits = (int) texbits;
					System.out.println("extrabits: " + exbits);
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
	 * // makeing corresponding nodes if(cnt>1) dfs(Root,""); // dfs to make the
	 * codes fakezip("in.txt"); // fake zip file which will have the binary of
	 * the input to fakezipped.txt file
	 * realzip("fakezipped.txt","in.txt"+".huffz"); // making the real zip
	 * according the fakezip.txt file initHzipping();
	 * 
	 * }
	 */

	public static void beginHzipping(String arg1) {
		initHzipping();
		CalFreq(arg1); // calculate the frequency of each digit
		MakeNode(); // makeing corresponding nodes
		if (cnt > 1)
			dfs(Root, ""); // dfs to make the codes
		fakezip(arg1); // fake zip file which will have the binary of the input
						// to fakezipped.txt file
		realzip("fakezipped.txt", arg1 + ".huffz"); // making the real zip
													// according the fakezip.txt
													// file
		initHzipping();
	}
}