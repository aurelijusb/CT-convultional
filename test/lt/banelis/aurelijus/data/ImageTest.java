package lt.banelis.aurelijus.data;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testing binary image container.
 *
 * @author Aurelijus Banelis
 */
public class ImageTest {
    
    public ImageTest() {
    }

    
    /*
     * Numeric tests
     */
    
    @Test
    public void testNumberIntegrity1() {
        assertIntegrity(0);
    }

    @Test
    public void testNumberIntegrity2() {
        assertIntegrity(1);
    }

    @Test
    public void testNumberIntegrity3() {
        assertIntegrity(10);
        assertIntegrity(12);
        assertIntegrity(123);
        assertIntegrity(128);
        assertIntegrity(255);
    }

    @Test
    public void testNumberIntegrity4() {
        assertIntegrity(1000);
        assertIntegrity(10000);
        assertIntegrity(123456);
        assertIntegrity(Integer.MAX_VALUE);
    }

    
    /*
     * Image tests
     */
    
    @Test
    public void testImageIntegrity1() {
        assertIntegrity(Image.sampleImage(10, 10));
    }
    
    @Test
    public void testImageIntegrity2() {
        assertIntegrity(Image.sampleImage(100, 10));
    }
    
    @Test
    public void testImageIntegrity3() {
        assertIntegrity(Image.sampleImage(10, 100));
    }
    
    @Test
    public void testImageIntegrity4() {
        assertIntegrity(Image.sampleImage(1, 10000));
    }
    
    @Test
    public void testImageIntegrity5() {
        assertIntegrity(Image.sampleImage(100000, 1));
    }
    
    @Test
    public void testImageIntegrity6() {
        assertIntegrity(Image.sampleImage(1000, 1000));
    }
    
    
    /*
     * Utilities
     */
    
    private void assertIntegrity(int number) {
        ArrayList<Boolean> binary = Image.toBinary(number);
        assertNotNull("Expected stream, but got null" , binary);
        int actual = Image.toInteger(binary);
        assertEquals(number, actual, "Number");
    } 
    
    private void assertIntegrity(BufferedImage image) {
        assertNotNull(image);
        Collection<Boolean> binnary = Image.toBinary(image);
        BufferedImage actual = Image.toImage(binnary);
        assertEquals(image, actual);
    }
    
    private void assertEquals(BufferedImage expected, BufferedImage actual) {
        if (expected != null && actual == null) {
            fail("Not expected null");
        }
        assertEquals(expected.getWidth(), actual.getWidth(), "Width");
        assertEquals(expected.getHeight(), actual.getHeight(), "Height");
        assertEquals(expected, actual, "Pixels");
    }
    
    private void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            fail(message + ": expected " + expected + " != " + actual);
        }
    }
    
    private void assertEquals(BufferedImage expected, BufferedImage actual,
                              String message) {
        for (int x = 0; x < expected.getWidth(); x++) {
            for (int y = 0; y < expected.getHeight(); y++) {
                int color1 = expected.getRGB(x, y);
                int color2 = actual.getRGB(x, y);
                if (color1 != color2) {
                    fail(message + " (" + x + "x" + y + ") expected " +
                         color1 + " != " + color2);
                }
            }
            
        }
    }
    
    private ArrayList<Boolean> toList(String data) {
        ArrayList<Boolean> result = new ArrayList<Boolean>(data.length());
        for (char c : data.toCharArray()) {
            if (c == '1') {
                result.add(Boolean.TRUE);
            } else if (c == '0') {
                result.add(Boolean.FALSE);
            }
        }
        return result;
    }
}
