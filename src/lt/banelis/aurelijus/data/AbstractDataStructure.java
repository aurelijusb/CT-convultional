package lt.banelis.aurelijus.data;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.LinkedList;
import javax.swing.JPanel;

/**
 * Data structure, that can be represented to Swing or binary.
 * 
 * Used to enter and visualize various data structures.
 * 
 * @author Aurelijus Banelis
 */
public abstract class AbstractDataStructure extends JPanel {
    protected static final Font font = new Font("monospaced", Font.BOLD, 16);
    private InputListner listener;
    private boolean inputEnabled;
    private AbstractDataStructure sender = null;
    private int synchronisation = 0;
    private LinkedList<Boolean> buffer = new LinkedList<Boolean>();
    private final Color[] backgrounds = {new Color(220, 255, 220),
                                         new Color(220, 220, 255)};
    private final Color[] foregrounds = {new Color(200, 235, 200),
                                         new Color(200, 200, 235)};
    
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
        repaint();
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


    protected void paintBuffer(Graphics g) {
        setFont(font);
        paintBuffer(g, font.getSize(), font.getSize(), 4);
    }
    
    protected final void paintBuffer(Graphics g, int width, int height,
                                     int step) {
        int padding = synchronisation * width;
        final int length = buffer.size() - 1;
        int i = length;        
        Color background = backgrounds[0];
        Color foreground = foregrounds[0];
        for (Boolean bit : buffer) {
            /* Position and value */
            int x = padding + width * i;
            int symbol = bit ? 1 : 0;
            
            /* Color */
            if ((length - i) % step == 0) {
                int colorIndex = ((length - i) % (step * 2) == 0) ? 0 : 1;
                background = backgrounds[colorIndex];
                foreground = foregrounds[colorIndex];
            }
                       
            /* Drawing */
            if (x + width < getWidth()) {
                g.setColor(background);
                g.fillRect(x, 0, width, height);
                if (bit) {
                    g.setColor(foreground);
                    g.drawRect(x - 1, 1, width - 2, height - 2);
                }
                g.setColor(Color.BLACK);
                g.drawString(symbol + "", x, height);
            }
            i--;
        }
    }
    
    
    protected void increaseSinchronisation() {
        synchronisation++;
    }

    
    protected LinkedList<Boolean> getBuffer() {
        return buffer;
    }
}
