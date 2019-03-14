import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.BitSet;

import static org.junit.Assert.*;

public class TreeTest {

    @Test
    public void getCode() {
        int[] data = {1,2,3,4};
        Tree tree =new Tree(data);
        assertEquals (3,tree.codes.getCode((byte) 0).length());
        assertEquals (3,tree.codes.getCode((byte) 1).length());
        assertEquals (2,tree.codes.getCode((byte)2).length());
        assertEquals (1,tree.codes.getCode((byte)3).length());
    }
    }