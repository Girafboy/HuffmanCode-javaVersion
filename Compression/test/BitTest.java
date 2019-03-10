import org.junit.Test;

import static org.junit.Assert.*;

public class BitTest {

    @Test
    public void append() {
        Bit bit = new Bit();
        Bit bit1 =new Bit();
        bit.Add(true);
        bit.Add(true);
        bit.Add(false);
        bit.Add(false);
        assertEquals(4,bit.length());
        bit1.Add(false);
        bit.Append(bit1);
        assertEquals(5,bit.length());
        bit1.Append(bit);
        assertEquals(6,bit1.length());
        bit1.Add(false);
        bit1.Add(true);
        bit1.Add(false);
        bit1.Add(false);
        assertEquals(10,bit1.length());
    }

    @Test
    public void length() {
        Bit bit = new Bit();
        bit.Add(true);
        assertEquals(1,bit.length());
        bit.Add(false);
        assertEquals(2,bit.length());
        bit.Add(false);
        assertEquals(3,bit.length());
        bit.Add(false);
        assertEquals(4,bit.length());
        bit.Add(true);
        assertEquals(5,bit.length());
    }

    @Test
    public void toByteArray() {
        Bit bit = new Bit();
        byte[] bytes =new byte[1];

        bit.Add(true);
        bytes[0]=(byte) 0x80;
        assertArrayEquals(bytes,bit.toByteArray());

        bit.Add(false);
        bytes[0]=(byte) 0x80;
        assertArrayEquals(bytes,bit.toByteArray());

        bit.Add(true);
        bytes[0]=(byte) 0xa0;
        assertArrayEquals(bytes,bit.toByteArray());

        bit.Add(false);
        bytes[0]=(byte) 0xa0;
        assertArrayEquals(bytes,bit.toByteArray());

        bit.Add(false);
        bytes[0]=(byte) 0xa0;
        assertArrayEquals(bytes,bit.toByteArray());

        bit.Add(true);
        bytes[0]=(byte) 0xa4;
        assertArrayEquals(bytes,bit.toByteArray());

        bit.Add(true);
        bit.Add(true);
        bytes[0]=(byte) 0xa7;
        assertArrayEquals(bytes,bit.toByteArray());

        byte[] bytes1 =new byte[2];
        bytes1[0]=(byte) 0xa7;

        bit.Add(false);
        bytes1[1] =(byte) 0x00;
        assertArrayEquals(bytes1,bit.toByteArray());

        bit.Add(true);
        bytes1[1] =(byte) 0x40;
        assertArrayEquals(bytes1,bit.toByteArray());
    }
}