package lt.banelis.aurelijus.data;

import java.awt.Graphics;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author Aurelijus Banelis
 */
public class BitsSteam extends AbstractDataStructure {   
    private LinkedList<Boolean> data = new LinkedList<Boolean>();

    public BitsSteam(boolean inputEnabled) {
        super(inputEnabled);
    }
    
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        super.paintBuffer(g);
    }

}
