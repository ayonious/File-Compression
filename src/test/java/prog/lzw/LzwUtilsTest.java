package prog.lzw;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LzwUtilsTest {

    @Test
    public void testBtoi() {
        // Test positive byte values
        assertEquals(0, LzwUtils.btoi((byte) 0));
        assertEquals(65, LzwUtils.btoi((byte) 65));  // 'A'
        assertEquals(90, LzwUtils.btoi((byte) 90));  // 'Z'
        assertEquals(127, LzwUtils.btoi((byte) 127));
        
        // Test negative byte values (should convert to positive values 128-255)
        assertEquals(128, LzwUtils.btoi((byte) -128));
        assertEquals(200, LzwUtils.btoi((byte) -56));
        assertEquals(255, LzwUtils.btoi((byte) -1));
    }
    
    @Test
    public void testStoi() {
        // Test binary string to integer conversion
        assertEquals(0, LzwUtils.stoi("0"));
        assertEquals(1, LzwUtils.stoi("1"));
        assertEquals(2, LzwUtils.stoi("10"));
        assertEquals(3, LzwUtils.stoi("11"));
        assertEquals(10, LzwUtils.stoi("1010"));
        assertEquals(15, LzwUtils.stoi("1111"));
        assertEquals(255, LzwUtils.stoi("11111111"));
        assertEquals(1023, LzwUtils.stoi("1111111111"));
    }
} 