package lt.banelis.aurelijus.data;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Bit representation.
 * 
 * @author Aurelijus Banelis
 */
public class Bit extends AbstractDataStructure {
    private Boolean data;
    private One<Boolean> iterator = new One<Boolean>() {
        @Override
        protected Boolean getElement() {
            return data;
        }
    };
    private Button one = null;
    private Button zero = null;
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
    protected static abstract class One<T> implements Iterable<T> {
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
        } else {
            //TODO: throw
            System.err.println("Data empty");
        }
        if (this.data != null) {
            getBuffer().add(this.data);
        } else {
            increaseSinchronisation();
        }
    }

    public void setData(final Boolean data) {
        setData(new One<Boolean>() {
            @Override
            protected Boolean getElement() {
                return data;
            }
        });
        if (isInputEnabled()) {
            if (data != null && data.booleanValue()) {
                one.requestFocus();
            } else {
                zero.requestFocus();
            }
        }
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
        super.setMinimumSize(new Dimension(20, 20));
        setLayout(new BorderLayout());
        
        setLayout(new BorderLayout());
        add(bufferPanel, BorderLayout.CENTER);
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
