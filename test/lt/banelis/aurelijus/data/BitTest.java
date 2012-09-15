package lt.banelis.aurelijus.data;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * JUnit tests for BitTest functionality.
 * 
 * @author Aurelijus Banelis
 */
public class BitTest {
    public Boolean bit2 = null;
    public int storing1 = 0;
    public Boolean storing1Expected = null;
    
    public BitTest() {
    }

    @Test
    public void testOne() {
        /* Initiating */
        bit2 = Boolean.FALSE;
        Bit.One<Boolean> one = new Bit.One<Boolean>() {
            @Override
            protected Boolean getElement() {
                return bit2;
            }
        };
        
        /* Read */
        assertEquals(Boolean.FALSE, bit2);
        assertIteratorOne(bit2, one);
        
        /* Change */
        bit2 = Boolean.TRUE;
        assertEquals(Boolean.TRUE, bit2);
        assertIteratorOne(bit2, one);
    }
    
    @Test
    public void testStoring1() {
        Bit bit = new Bit(false);
        bit.setListerer(new AbstractDataStructure.InputListner() {
            public void onUpdated(Iterable<Boolean> data) {
                assertIteratorOne(storing1Expected, data);
                storing1++;
            }
        });
     
        storing1Expected = Boolean.FALSE;
        storing1 = 0;
        bit.setData(false);
        assertEquals(1, storing1);
        
        storing1Expected = Boolean.TRUE;
        bit.setData(true);
        assertEquals(2, storing1);
    }
    

    /*
     * Utilities
     */
    
    public static <T> void assertIteratorOne(T expected, Iterable<T> interator) {
        int i = 0;
        T last =  null;
        for (T t : interator) {
            i++;
            last = t;
        }
        assertEquals(1, i);
        assertEquals(expected, last);
    }
}
