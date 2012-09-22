package lt.banelis.aurelijus.data;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;

/**
 * Data structure, that can be represented to Swing or binary.
 * 
 * WorkFlow:
 * 
 *  GUI User ___                                ___ run() [setListener()]
 *              \___ putData() -+- [current] __/
 *  System   ___/               :              \___ retrieveData()
 *                              :       
 *                              +- [history] - viewAllData()
 * 
 * @author Aurelijus Banelis
 */
public abstract class AbstractDataStructure extends JPanel {
    protected static final Font font = new Font("monospaced", Font.BOLD, 16);
    private static final Color[] backgrounds = {new Color(220, 255, 220),
                                                new Color(220, 220, 255)};
    private static final Color[] foregrounds = {new Color(200, 235, 200),
                                                new Color(200, 200, 235)};
    
    private Runnable listener;
    private LinkedList<Boolean> history = new LinkedList<Boolean>();
    private boolean inputEnabled;
    private Synchronizer syncronizer = null;
    private AbstractDataStructure comparator = null;
    private boolean isDestination = false;
    private boolean halfSize = false;
    private Font currentFont = font;
    
    
    /**
     * Interface to handle updated data.
     */
    public static interface InputListner {
        public void onUpdated(AbstractDataStructure object);
    }
    
    
    /**
     * Data structure, that can be represented to Swing or binary.
     * 
     * @param inputEnabled <code>true</code> if user can input data from GUI,
     *                     <code>false</code> when data from GUI is read only
     */
    public AbstractDataStructure(boolean inputEnabled) {
        this.inputEnabled = inputEnabled;
        setPreferredSize(new Dimension(100, 50));
        super.setFont(currentFont);
    }
    
    
    /*
     * Storing data
     */
    
    /**
     * Store data into object.
     * 
     * New data is added to object.
     */
    public final void putData(Collection<Boolean> data) {
        if (isDestination && syncronizer != null) {
            data = syncronizer.synchronize(data);
        }
        if (data.size() > 0) {
            putDataImplementation(data);
            if (listener != null) {
                listener.run();
            }
            repaint();
        }
    }

    /**
     * Implementation of storing data.
     * 
     * @see #putData(java.lang.Iterable)
     */
    protected abstract void putDataImplementation(Collection<Boolean> data);
    

    /*
     * Getting data
     */
    
    /**
     * Retrieving new data.
     * 
     * Data is removed from object except history.
     * 
     * @return  data converted to binary.
     * @see #viewAllData()
     */
    public Collection<Boolean> retrieveData() {
        Collection<Boolean> data = retrieveDataImplementation();
        history.addAll(data);
        return data;
    }

    
    /**
     * View currently stored data without modifying it.
     */
    protected abstract Collection<Boolean> viewData();
    
    
    /**
     * Implementation of retrieving data.
     * 
     * @see #retrieveData()
     */
    protected abstract Collection<Boolean> retrieveDataImplementation();
    
    
    /**
     * Set handler, that is executed after data is updated.
     * 
     * @param listener  input handler
     * @see #retrieveDataImplementation()
     */
    public void setListerer(Runnable listener) {
        this.listener = listener;
    }

    
    /**
     * View current and past data.
     */
    public Collection<Boolean> viewAllData() {
        Collection<Boolean> current = viewData();
        if (viewData().size() > 0) {
            LinkedList<Boolean> whole = new LinkedList<Boolean>(history);
            whole.addAll(current);
            return whole;
        } else {
            return history;
        }
    }
    
    public void reset() {
        history = new LinkedList<Boolean>();
        repaint();
    }

    
    /*
     * Synchronization
     */
    
    public void setSyncronizer(Synchronizer syncronizer,
                              AbstractDataStructure comparator,
                              boolean isDestination) {
        this.syncronizer = syncronizer;
        this.comparator = comparator;
        this.isDestination = isDestination;
    }

    private Collection<Boolean> getSource() {
        if (isDestination) {
            return viewAllData();
        } else {
            return comparator.viewAllData();
        }
    }
    
    private Collection<Boolean> getDestination() {
        if (isDestination) {
            return comparator.viewAllData();
        } else {
            return viewAllData();
        }
    }
    
    private boolean isEqual(int offset) {
        return getBit(getSource(), offset) == getBit(getDestination(), offset);
    }
    
    private Boolean getBit(Collection<Boolean> container, int offset) {
        if (container.size() <= offset || offset < 0) {
            return null;
        } else if (container instanceof List) {
            return ((List<Boolean>) container).get(offset);
        } else {
            int i = 0;
            for (Boolean bit : container) {
                if (i == offset) {
                    return bit;
                } else if (i > offset) {
                    return null;
                }
                i++;
            }
        }
        return null;
    }
    
    protected Collection<Boolean> dataToSynchronize() {
        if (syncronizer != null) {
            return syncronizer.dataToSynchronize();
        } else {
            return Collections.EMPTY_LIST;
        }
    }
    
    
    /*
     * Graphical user interface
     */
    
    /**
     * @return <code>true</code> when user can able to enter data from GUI,
     *         <code>false</code> when data from GUI is read only
     */
    protected final boolean isInputEnabled() {
        return inputEnabled;
    }

    protected void setInputEnabled(boolean inputEnabled) {
        this.inputEnabled = inputEnabled;
    }
    
    
    /*
     * Painting stream
     */
    
    protected void paintBuffer(Graphics g) {
        if (halfSize) {
            paintBuffer(g, currentFont.getSize(), font.getSize(), 8,
                        viewAllData());
        } else {
            paintBuffer(g, font.getSize(), font.getSize(), 4, viewAllData());
        }
    }
    
    protected final int getBufferPadding(int width) {
        if (isDestination && syncronizer != null) {
            return syncronizer.getSynchronisation() * width;
        } else {
            return 0;
        }
    }
    
    protected void paintBuffer(Graphics g, int width, int height,
                              int step, Collection<Boolean> data) {
        int padding = getBufferPadding(width);
        final int length = data.size() - 1;
        int i = length;        
        Color background = backgrounds[0];
        Color foreground = foregrounds[0];
        for (Boolean bit : data) {
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
                int offsetFromEnd = data.size() - i - 1;
                if (isDestination && !isEqual(offsetFromEnd)) {
                    paintError(g, x, width, height);
                } else {
                    g.setColor(Color.BLACK);
                }
                g.drawString(symbol + "", x, height);
            }
            i--;
        }
    }
    
    protected final void paintError(Graphics g, int x, int width, int height) {
        g.setColor(Color.RED);
        g.drawRect(x, height, width, 2);
    }

    public void setHalfSize(boolean halfSize) {
        this.halfSize = halfSize;
        if (halfSize) {
            currentFont = font.deriveFont(8.f);
        } else {
            currentFont = font;
        }
        repaint();
    }
}
