package lt.banelis.aurelijus.data;

import java.awt.Font;
import java.awt.Graphics;
import java.util.Collection;

/**
 *
 * @author Aurelijus Banelis
 */
public class BitsSteam extends AbstractDataStructure {   
    private static final Font smallFont = font.deriveFont(10);
    
    public BitsSteam() {
        super(false);
    }
    
    @Override
    protected void paintBuffer(Graphics g) {
        setFont(smallFont);
        super.paintBuffer(g, font.getSize() / 2, font.getSize(), 8);
    }

    @Override
    protected void storeData(Iterable<Boolean> data) {
        if (data instanceof Collection) {
            getBuffer().addAll((Collection<Boolean>) data);
        } else {
            for (Boolean bit : data) {
                getBuffer().add(bit);
            }
        }
    }

    @Override
    public Iterable<Boolean> getData() {
        return getBuffer();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintBuffer(g);
    }
}
