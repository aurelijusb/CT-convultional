package lt.banelis.aurelijus.data;

import java.util.Collection;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Testing binary text container.
 *
 * @author Aurelijus Banelis
 */
public class TextTest {
    
    public TextTest() {}

    @Test
    public void testIntegrity1() {
        testIntegrity("Test");
    }

    @Test
    public void testIntegrity2() {
        testIntegrity("Hello world!");
        testIntegrity("Hello world!\nHow are you");
    }
    
    @Test
    public void testIntegrity3() {
        testIntegrity("Žąsinas su šešiais ančiukais");
        testIntegrity("Aš\ntu\rjie\tjos");
    }
    
    private void testIntegrity(String expected) {
        Collection<Boolean> binnary = Text.toBinary(expected);
        String result = Text.toText(binnary);
        assertEquals(expected, result);
    }
}
