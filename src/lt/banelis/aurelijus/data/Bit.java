package lt.banelis.aurelijus.data;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import javax.swing.Action;
import javax.swing.BoxLayout;

/**
 * Bit representation.
 * 
 * @author Aurelijus Banelis
 */
public class Bit extends AbstractDataStructure {
    private Boolean data;
    private One<Boolean> iterator = new One() {
        @Override
        protected Object getElement() {
            return data;
        }
    };
    private Button one = null;
    private Button zero = null;
    
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
            if (data) {
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
    
    private Action action(final ActionListener action) {
        return new Action() {
            private boolean enabled;
            public Object getValue(String key) {
                return null;
            }

            public void putValue(String key, Object value) {
            }

            public void setEnabled(boolean b) {
                enabled = b;
            }

            public boolean isEnabled() {
                return enabled;
            }

            public void addPropertyChangeListener(PropertyChangeListener listener) {
            }

            public void removePropertyChangeListener(PropertyChangeListener listener) {
            }

            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(e);
            }
        };
    }
    
    private void initialiseEditable() {
        one = new Button("1");
        zero = new Button("0");
        
        one.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setData(true);
            }
        });
        one.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setData(false);
            }
        });
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(zero);
        add(one);
    }
    
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
    
    private void initialiseVisible() {
        super.setOpaque(true);
        super.setMinimumSize(new Dimension(20, 20));
    }
    
    private void updateRepresentation() {
        if (data != null && data) {
            setBackground(Color.BLUE);
            setForeground(Color.WHITE);
        } else {
            setBackground(Color.BLACK);
            setForeground(Color.GRAY);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        String symbol;
        if (data != null && data) {
            g.drawString("1", getWidth() / 4 * 3, getHeight() / 2 + 5);
            g.drawRect(getWidth() / 2 + 2, 2,
                       getWidth() / 2 - 4, getHeight() - 4);
        } else {
            g.drawString("0", getWidth() / 4, getHeight() / 2 + 5);
            g.drawRect(2, 2, getWidth() / 2 - 4, getHeight() - 4);
        }
    }

    @Override
    protected boolean equalData(AbstractDataStructure object) {
        return object instanceof Bit &&
               object.getData().iterator().next() == data;
    }
}
