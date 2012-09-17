package lt.banelis.aurelijus.data;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

/**
 *
 * @author Aurelijus Banelis
 */
public class BitsSteam extends AbstractDataStructure {   
    private LinkedList<Boolean> data = new LinkedList<Boolean>();
    private HashSet<Integer> errors = new HashSet<Integer>();
    private int bufferWidth;
    private int bufferHeight;
    private int markedIndex = -1;

    public BitsSteam(boolean inputEnabled) {
        super(inputEnabled);
        if (inputEnabled) {
            initailiseEditing();
        }
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
        errors = new HashSet<Integer>();
        return toRetrieve;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        super.paintBuffer(g);
        if (isInputEnabled()) {
            paintInsideData(g);
            paintOverBit(g);
        }
    }
    
    /*
     * Editing
     */

    @Override
    public void setInputEnabled(boolean inputEnabled) {
        super.setInputEnabled(inputEnabled);
        repaint();
    }
    
    private void initailiseEditing() {
        MouseAdapter bitsEditor = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int lastIndex = markedIndex;
                markedIndex = getSymbolIndex(e.getX());
                if (lastIndex != markedIndex) {
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                markedIndex = -1;
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (markedIndex > -1 && markedIndex < data.size()) {
                    int index = data.size() - markedIndex - 1;
                    data.set(index, !data.get(index));
                    if (errors.contains(index)) {
                        errors.remove(index);
                    } else {
                        errors.add(index);
                    }
                    repaint();
                }
            }
        };
        addMouseListener(bitsEditor);
        addMouseMotionListener(bitsEditor);
    }
    
    private void paintInsideData(Graphics g) {
        if (viewData().size() > 0) {
            int x1 = getBufferPadding(bufferWidth);
            int x2 = x1 + viewData().size() * bufferWidth;
            g.setColor(Color.BLUE);
            g.drawArc(x1, 0, bufferWidth / 2, bufferHeight * 2,
                      180, 90);
            g.drawArc(x2, 0, bufferWidth / 2, bufferHeight * 2,
                      270, 90);
            for (int i = 0; i < data.size(); i++) {
                int index = data.size() - i - 1;
                if (errors.contains(index)) {
                    paintError(g, x1 + i * bufferWidth, bufferWidth,
                               bufferHeight);
                }
            }
        }
    }
    
    private void paintOverBit(Graphics g) {
        if (markedIndex > -1 && markedIndex < viewData().size()) {
            int x = markedIndex * bufferWidth;
            int index = data.size() - markedIndex - 1;
            String symbol = data.get(index) ? "1" : "0";;
            if (errors.contains(index)) {
                 g.setColor(Color.BLUE);
            } else {
                 g.setColor(Color.RED);
            }
            g.drawRect(x - 1, 1, bufferWidth + 1, getHeight() - 2);
            g.drawString(symbol, x, bufferHeight * 2);
        }
    }
    
    @Override
    protected void paintBuffer(Graphics g, int width, int height, int step) {
        super.paintBuffer(g, width, height, step);
        bufferWidth = width;
        bufferHeight = height;
    }
    
    private int getSymbolIndex(int x) {
        x -= getBufferPadding(bufferWidth);
        int index = x / bufferWidth;
        if (index > viewHistory().size()) {
            index = -1;
        }
        return index;
    }

    
}
