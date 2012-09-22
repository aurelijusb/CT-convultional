package lt.banelis.aurelijus.data;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;

/**
 * Image representation.
 *
 * @author Aurelijus Banelis
 */
public class Image extends AbstractDataStructure {

    public Image(boolean inputEnabled) {
        super(inputEnabled);
    }
    
    @Override
    protected void putDataImplementation(Collection<Boolean> data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Collection<Boolean> viewData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Collection<Boolean> retrieveDataImplementation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.PINK);
        g.fillRect(1, 1, 100, 200);
    }
    
}
