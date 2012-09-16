package lt.banelis.aurelijus.data;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.swing.JPanel;

/**
 * Bit representation.
 * 
 * @author Aurelijus Banelis
 */
public class Bit extends AbstractDataStructure {
    private Boolean data = Boolean.FALSE;
    private boolean notEmpty = false;
    private Button one = null;
    private Button zero = null;
    private JPanel historyPanel = new JPanel() {
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
        historyPanel.setFont(super.font);
    }

    
    /*
     * Storing and retrieving data
     */
    
    @Override
    protected void putDataImplementation(Collection<Boolean> data) {
        if (data != null && data.iterator().hasNext()) {
            this.data = data.iterator().next();
        }
        notEmpty = true;
    }
    
    public void putData(boolean data) {
        putData(oneElement(data));
    }

    @Override
    protected Collection<Boolean> viewData() {
        if (notEmpty) {
            return oneElement(data);
        } else {
            return Collections.EMPTY_LIST;
        }
    }
    
    private Collection<Boolean> oneElement(boolean data) {
        ArrayList<Boolean> list = new ArrayList<Boolean>(1);
        list.add(data);
        return list;
    }

    @Override
    protected Collection<Boolean> retrieveDataImplementation() {
         Collection<Boolean> list = viewData();
         notEmpty = false;
         return list;
    }
    
    
    /*
     * Graphical user interface
     */
       
    private void initialiseEditable() {
        one = new Button("1");
        zero = new Button("0");
        
        one.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                putData(true);
            }
        });
        zero.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                putData(false);
            }
        });
        
        JPanel buttons = new JPanel();
        buttons.add(zero);
        buttons.add(one);
        
        setLayout(new BorderLayout());
        add(buttons, BorderLayout.NORTH);
        add(historyPanel, BorderLayout.CENTER);
    }
    
    private void initialiseVisible() {
        super.setMinimumSize(new Dimension(20, 20));
        add(historyPanel);
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
