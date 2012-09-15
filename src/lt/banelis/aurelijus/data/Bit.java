package lt.banelis.aurelijus.data;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Bit representation.
 * 
 * @author Aurelijus Banelis
 */
public class Bit extends AbstractDataStructure {
    private static final Font font = new Font("monospaced", Font.BOLD, 12);
    private Boolean data;
    private One<Boolean> iterator = new One() {
        @Override
        protected Object getElement() {
            return data;
        }
    };
    private Button one = null;
    private Button zero = null;
    private JLabel current = null;
    private LinkedList<Boolean> buffer = new LinkedList<Boolean>();
    private int synchronisation = 0;
    private JPanel bufferPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintBuffer(g);
        }
    };
    
    public Bit(boolean inputEnabled) {
        super(inputEnabled);
        if (inputEnabled) {
            initialiseEditable();
        } else {
            initialiseVisible();
        }
    }
    
    /*
     * Storing data
     */
    
    /**
     * Encapsulating one element in iterator.
     * 
     * @param <T> type
     */
    protected abstract static class One<T> implements Iterable<T> {
        protected abstract T getElement();
        
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private boolean first = true;
                
                public boolean hasNext() {
                    return first;
                }

                public T next() {
                    if (first) {
                        first = false;
                        return getElement();
                    } else {
                        return null;
                    }
                }

                public void remove() { }
            };
        }
    }
      
    @Override
    protected void storeData(Iterable<Boolean> data) {
        if (data.iterator().hasNext()) {
            this.data = data.iterator().next();
            updateRepresentation();
        } else {
            //TODO: throw
            System.err.println("Data empty");
        }
        if (this.data != null) {
            buffer.add(this.data);
        } else {
            synchronisation++;
        }
    }

    public void setData(final Boolean data) {
        setData(new One<Boolean>() {
            @Override
            protected Boolean getElement() {
                return data;
            }
        });
//        if (isInputEnabled()) {
//            if (data != null && data.booleanValue()) {
//                one.requestFocus();
//            } else {
//                zero.requestFocus();
//            }
//        }
    }
    
    @Override
    public Iterable<Boolean> getData() {
        return iterator;
    }
    
    
    /*
     * Representation
     */
    
    private void initialiseEditable() {
        one = new Button("1");
        zero = new Button("0");
        
        one.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setData(true);
            }
        });
        zero.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setData(false);
            }
        });
        
        JPanel buttons = new JPanel();
        buttons.add(zero);
        buttons.add(one);
        
        setLayout(new BorderLayout());
        add(buttons, BorderLayout.NORTH);
        add(bufferPanel, BorderLayout.CENTER);
    }
    
    private void initialiseVisible() {
        super.setOpaque(true);
        super.setMinimumSize(new Dimension(20, 20));
        setLayout(new BorderLayout());
        
        setLayout(new BorderLayout());
        add(bufferPanel, BorderLayout.CENTER);
        current = new JLabel("Dabartine");
        add(current, BorderLayout.SOUTH);
    }
    
    /**
     * @deprecated 
     */
    private void updateRepresentation() {
//        if (data != null && data) {
//            setBackground(Color.BLUE);
//            setForeground(Color.WHITE);
//        } else {
//            setBackground(Color.BLACK);
//            setForeground(Color.GRAY);
//        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        String symbol;
//        if (data != null && data) {
//            g.drawString("1", getWidth() / 4 * 3, getHeight() / 2 + 5);
//            g.drawRect(getWidth() / 2 + 2, 2,
//                       getWidth() / 2 - 4, getHeight() - 4);
//        } else {
//            g.drawString("0", getWidth() / 4, getHeight() / 2 + 5);
//            g.drawRect(2, 2, getWidth() / 2 - 4, getHeight() - 4);
//        }
    }

    /**
     * @deprecated 
     */
    @Override
    protected boolean equalData(AbstractDataStructure object) {
        return object instanceof Bit &&
               object.getData().iterator().next() == data;
    }
    
    private Color[] backgrounds = {new Color(220, 255, 220),
                                   new Color(220, 220, 255)};
    private Color[] foregrounds = {new Color(200, 235, 200),
                                   new Color(200, 200, 235)};
    private void paintBuffer(Graphics g) {
        setFont(font);
        final int w = 12;
        final int h = 12;
        int padding = synchronisation * w;
        final int length = buffer.size() - 1;
        int i = length;        
        Color background = backgrounds[0];
        Color foreground = foregrounds[0];
        for (Boolean bit : buffer) {
            /* Position and value */
            int x = padding + w * i;
            int symbol = bit ? 1 : 0;
            
            /* Color */
            if ((length - i) % 4 == 0) {
                int colorIndex = ((length - i) % 8 == 0) ? 0 : 1;
                background = backgrounds[colorIndex];
                foreground = foregrounds[colorIndex];
            }
                       
            /* Drawing */
            if (x + w < getWidth()) {
                g.setColor(background);
                g.fillRect(x, 0, w, h);
                if (bit) {
                    g.setColor(foreground);
                    g.drawRect(x - 1, 1, w - 2, h - 2);
                }
                g.setColor(Color.BLACK);
                g.drawString(symbol + "", x, h);
            }
            i--;
        }
    }
    
    
    /*
     * Utilities
     */
    
    public static void globalKeyShortcuts(Container root,
                                          KeyListener listener) {
        for (Component component : root.getComponents()) {
            if (component.isFocusable()) {
                component.addKeyListener(listener);
                if (component instanceof Container) {
                    globalKeyShortcuts((Container) component, listener);
                }
            }
        }
    }
}
