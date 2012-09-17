package lt.banelis.aurelijus.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author Aurelijus Banelis
 */
public class Text extends AbstractDataStructure {
    private LinkedList<Boolean> data = new LinkedList<Boolean>();
    private String representation;
    private static final int BIT_16 = 32768;
    public Text(boolean inputEnabled) {
        super(inputEnabled);
    }
    
    /*
     * Data storage
     */
    
    @Override
    protected void putDataImplementation(Collection<Boolean> data) {
        this.data.addAll(data);
    }

    @Override
    protected Collection<Boolean> viewData() {
        return data;
    }

    @Override
    protected Collection<Boolean> retrieveDataImplementation() {
        Collection<Boolean> toRetrieve = data;
        data = new LinkedList<Boolean>();
        return toRetrieve;
    }

    
    /*
     * Data transformation
     */
    
    protected static String toText(Collection<Boolean> data) {
        StringBuilder result = new StringBuilder();
        char word = 0;
        int upper = BIT_16;
        int i = 0;
        for (Boolean bit : data) {
            if (i != 0 && i % Character.SIZE == 0) {
                result.append(word);
                word = 0;
                upper = BIT_16;
            }
            if (bit) {
                word += upper;
            }
            upper >>>= 1;
            i++;
        }
        result.append(word);
        return result.toString();
    }
    
    protected static Collection<Boolean> toBinary(String text) {
        LinkedList<Boolean> result = new LinkedList<Boolean>();
        for (int i = 0; i < text.length(); i++) {
            char word = text.charAt(i);
            int bit = BIT_16;
            for (int j = 0; j < Character.SIZE; j++) {
                result.add((word & bit) != 0);
                bit >>>= 1;
            }
        }
        return result;
    }
    
    /*
     * Graphical user interface
     */
}
