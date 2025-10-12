package prog.lzw;

import org.junit.jupiter.api.Test;
import prog.util.Constants;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LzwUtilsTest {

	@Test
	void testInitializeCompressionDictionary() {
		Map<String, Integer> dictionary = LzwUtils.initializeCompressionDictionary();

		// Check size
		assertEquals(Constants.BYTE_VALUES_COUNT, dictionary.size());

		// Check some known mappings
		assertEquals(65, dictionary.get("A"));
		assertEquals(66, dictionary.get("B"));
		assertEquals(32, dictionary.get(" "));
		assertEquals(0, dictionary.get("" + (char) 0));
		assertEquals(255, dictionary.get("" + (char) 255));

		// Verify all 256 entries exist
		for (int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
			assertTrue(dictionary.containsKey("" + (char) i));
			assertEquals(i, dictionary.get("" + (char) i));
		}
	}

	@Test
	void testInitializeDecompressionDictionary() {
		Map<Integer, String> dictionary = LzwUtils.initializeDecompressionDictionary();

		// Check size
		assertEquals(Constants.BYTE_VALUES_COUNT, dictionary.size());

		// Check some known mappings
		assertEquals("A", dictionary.get(65));
		assertEquals("B", dictionary.get(66));
		assertEquals(" ", dictionary.get(32));
		assertEquals("" + (char) 0, dictionary.get(0));
		assertEquals("" + (char) 255, dictionary.get(255));

		// Verify all 256 entries exist
		for (int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
			assertTrue(dictionary.containsKey(i));
			assertEquals("" + (char) i, dictionary.get(i));
		}
	}

	@Test
	void testCalculateRequiredBits() {
		// Edge cases
		assertEquals(1, LzwUtils.calculateRequiredBits(0));
		assertEquals(1, LzwUtils.calculateRequiredBits(1));

		// Powers of 2
		assertEquals(1, LzwUtils.calculateRequiredBits(2));   // 2^1 = 2
		assertEquals(2, LzwUtils.calculateRequiredBits(4));   // 2^2 = 4
		assertEquals(3, LzwUtils.calculateRequiredBits(8));   // 2^3 = 8
		assertEquals(8, LzwUtils.calculateRequiredBits(256)); // 2^8 = 256
		assertEquals(16, LzwUtils.calculateRequiredBits(65536)); // 2^16 = 65536

		// Non-powers of 2 (should round up)
		assertEquals(2, LzwUtils.calculateRequiredBits(3));   // 3 needs 2 bits (can store 0-3)
		assertEquals(3, LzwUtils.calculateRequiredBits(5));   // 5 needs 3 bits (can store 0-7)
		assertEquals(9, LzwUtils.calculateRequiredBits(257)); // 257 needs 9 bits (can store 0-511)
		assertEquals(9, LzwUtils.calculateRequiredBits(512)); // 512 needs 9 bits
		assertEquals(10, LzwUtils.calculateRequiredBits(513)); // 513 needs 10 bits (can store 0-1023)
	}

	@Test
	void testCalculateRequiredBitsTypicalDictionarySizes() {
		// Test typical dictionary sizes during compression
		assertEquals(8, LzwUtils.calculateRequiredBits(256));   // Initial dictionary
		assertEquals(9, LzwUtils.calculateRequiredBits(300));   // Small file
		assertEquals(10, LzwUtils.calculateRequiredBits(1000)); // Medium file
		assertEquals(12, LzwUtils.calculateRequiredBits(4096)); // Larger dictionary
	}

	@Test
	void testCompressionAndDecompressionDictionariesAreInverse() {
		Map<String, Integer> compressionDict = LzwUtils.initializeCompressionDictionary();
		Map<Integer, String> decompressionDict = LzwUtils.initializeDecompressionDictionary();

		// Verify they are inverses of each other
		for (int i = 0; i < Constants.BYTE_VALUES_COUNT; i++) {
			String charStr = "" + (char) i;
			// compression: charStr -> i
			assertEquals(i, compressionDict.get(charStr));
			// decompression: i -> charStr
			assertEquals(charStr, decompressionDict.get(i));
		}
	}
}
