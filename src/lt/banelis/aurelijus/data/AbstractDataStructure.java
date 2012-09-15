package lt.banelis.aurelijus.data;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * Data structure, that can be represented to Swing or binary.
 * 
 * Used to enter and visualize various data structures.
 * 
 * @author Aurelijus Banelis
 */
public abstract class AbstractDataStructure extends JPanel {
    private InputListner listener;
    private boolean inputEnabled;
    private AbstractDataStructure sender = null;
    
    /**
     * Interface to handle updated data.
     */
    public static interface InputListner {
        public void onUpdated(Iterable<Boolean> data);
    }
    

    
    /**
     * Data structure, that can be represented to Swing or binary.
     * 
     * @param inputEnabler <code>true</code> if user can input data from GUI,
     *                     <code>false</code> when data from GUI is read only
     */
    public AbstractDataStructure(boolean inputEnabled) {
        this.inputEnabled = inputEnabled;
    }
    
    
    /**
     * Converts binary and stores data.
     * 
     * @see #setData(java.lang.Iterable)
     */
    protected abstract void storeData(Iterable<Boolean> data);

    
    /**
     * Converts stored data into binary.
     *
     * Updates representation.
     * 
     * @return  contained data
     */
    public abstract Iterable<Boolean> getData();
    
    
    /**
     * Compare if data is equal.
     */
    protected abstract boolean equalData(AbstractDataStructure object);
    
    
    /**
     * Converts binary into concrete data and stores inside object.
     * 
     * Representation is updated.
     * Change listeners are notified.
     * 
     * @param data information to be stored
     * @see #storeData(java.lang.Iterable) 
     * @see #setListerer(lt.banelis.aurelijus.data.AbstractDataStructure.InputListner) 
     */
    public final void setData(Iterable<Boolean> data) {
        storeData(data);
        if (listener != null) {
            listener.onUpdated(data);
        }
    }
    
    
    /**
     * Set handler, that is executed after data is updated.
     * 
     * @param listener  input handler
     * @see #getData()
     */
    public void setListerer(InputListner listener) {
        this.listener = listener;
    }

    
    /**
     * @return <code>true</code> when user can able to enter data from GUI,
     *         <code>false</code> when data from GUI is read only
     */
    protected final boolean isInputEnabled() {
        return inputEnabled;
    }
    
    
    /**
     * Save sender for comparing equality between sender and receiver
     * 
     * @param sender 
     */
    public void setSender(AbstractDataStructure sender) {
        if (sender != null && sender.isInputEnabled()) {
            this.sender = sender;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        paintEquals(g);
    }

    private void paintEquals(Graphics g) {
        if (sender != null && !inputEnabled) {
            Color oldColor = g.getColor();
            if (equalData(sender)) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.RED);
            }
            final int borderSize = 4;
            for (int i = 0; i < borderSize; i++) {
                g.drawRect(i, i, getWidth() - (i * 2), getHeight() - (i * 2));
            }
            g.setColor(oldColor);
        }
    }
}
