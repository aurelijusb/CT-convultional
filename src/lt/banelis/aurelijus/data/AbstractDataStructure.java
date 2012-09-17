package lt.banelis.aurelijus.data;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import javax.swing.JPanel;

/**
 * Data structure, that can be represented to Swing or binary.
 * 
 * 
 * WorkFlow:
 * 
 *  GUI User ___                                ___ run() [setListener()]
 *              \___ putData() -+- [current] __/
 *  System   ___/               :              \___ retrieveData()
 *                              :       
 *                              +- [history] - viewHistory()
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
    private Hightlighter hightlighter = null;
    private int received = 0;
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
        if (needSynchronisation()) {
            data = removeSynchronisationBits(data);
        }
        if (data.size() > 0) {
            putDataImplementation(data);
            if (listener != null) {
                listener.run();
            }
            received += data.size();
            repaint();
        }
    }

    private boolean needSynchronisation() {
        return hightlighter != null &&
               received < hightlighter.getSynchronisation(this);
    }
    
    private Collection<Boolean> removeSynchronisationBits(
                                Collection<Boolean> data) {
        int bits = received - hightlighter.getSynchronisation(this);
        if (data.size() < bits) {
            received += data.size();
            data.clear();
            return data;
        } else {
            ArrayList<Boolean> newData = new ArrayList<Boolean>(data.size() -
                                                                bits);
            int i = 0;
            for (Boolean bit : data) {
                if (i >= data.size() - bits) {
                    newData.add(bit);
                }
                i++;
            }
            received += data.size() - newData.size();
            return newData;
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
     * @see #viewHistory()
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
    public Collection<Boolean> viewHistory() {
        Collection<Boolean> current = viewData();
        if (viewData().size() > 0) {
            LinkedList<Boolean> whole = new LinkedList<Boolean>(history);
            whole.addAll(current);
            return whole;
        } else {
            return history;
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
    
    
    /**
     * Set object for comparing two 2 data objects.
     */    
    public void setHighliter(Hightlighter hightlighter) {
        this.hightlighter = hightlighter;
    }
    

    /*
     * Painting stream
     */
    
    protected void paintBuffer(Graphics g) {
        if (halfSize) {
            paintBuffer(g, currentFont.getSize(), font.getSize(), 8);
        } else {
            paintBuffer(g, font.getSize(), font.getSize(), 4);
        }
    }
    
    protected final int getBufferPadding(int width) {
        if (hightlighter != null) {
            return hightlighter.getSynchronisation(this) * width;
        } else {
            return 0;
        }
    }
    
    protected void paintBuffer(Graphics g, int width, int height,
                                     int step) {
        int padding = getBufferPadding(width);
        Collection<Boolean> data = viewHistory();
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
                if (hightlighter != null && hightlighter.isDestination(this) &&
                    !hightlighter.isEqual(offsetFromEnd)) {
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
    
    public void setHightlighter(Hightlighter hightlighter) {
        this.hightlighter = hightlighter;
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
