package lt.banelis.aurelijus.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Duomenų struktūra skirta įvesti, saugoti ir vaizduoti bitą (q=2 vektorių).
 * 
 * @author Aurelijus Banelis
 */
public class Bit extends AbstractDataStructure {
    private Boolean data = Boolean.FALSE;
    private boolean notEmpty = false;
    private JButton one = null;
    private JButton zero = null;
    private JPanel historyPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintBuffer(g);
        }
    };

    /**
     * Sukuriamas nauja duomenų struktūra
     * 
     * @param inputEnabled <code>true</code>, jei ji skirta duomeų įvedimui,
     *                     <code>false</code> jei ji skirta tik atvaizdavimui.
     */
    public Bit(boolean inputEnabled) {
        super(inputEnabled);
        if (inputEnabled) {
            initialiseEditable();
        } else {
            initialiseVisible();
        }
        super.setBackground(Color.red);
        historyPanel.setFont(AbstractDataStructure.font);
    }

    
    /*
     * Funkcijos skirtos duomenų saugojimui ir paėmimui
     */
    
    /**
     * Duomenų pridėjimas.
     * 
     * Duomenų struktūroje saugomas tik bitas, todėl iš visos sekos paiimamas
     * tik pirmas elementas.
     * 
     * @param data  naujų duomenų seka.
     */
    @Override
    protected void putDataImplementation(Collection<Boolean> data) {
        if (data != null && data.iterator().hasNext()) {
            this.data = data.iterator().next();
        }
        notEmpty = true;
    }
    
    /**
     * Pridedami duomenys.
     * 
     * @param data  domuo (bitas)
     */
    public void putData(boolean data) {
        putData(oneElement(data));
    }

    /**
     * Peržiūrimi dabartiniai duomenys.
     * 
     * Kadangi struktūroje galima saugoti tik 1 bitą, tai ir rezultatas bus
     * arba iš vieno elemento arba tuščias.
     * 
     * @return  sąrašas su duomeninmis.
     */
    @Override
    protected Collection<Boolean> viewData() {
        if (notEmpty) {
            return oneElement(data);
        } else {
            return new LinkedList<Boolean>();
        }
    }
    
    /**
     * Sugeneruojamas sąrašas iš vieno elemento.
     * 
     * @param data  domuo (bitas)
     * @return      sąrašas iš vieno elemento.
     */
    private static Collection<Boolean> oneElement(boolean data) {
        ArrayList<Boolean> list = new ArrayList<Boolean>(1);
        list.add(data);
        return list;
    }

    /**
     * Išimamas bitas.
     * 
     * @return  sąrašą iš vieno elemento, jei struktūroje yra bitas,
     *          arba tuščias sąrašas, jei ši struktūra tuščia
     */
    @Override
    protected Collection<Boolean> retrieveDataImplementation() {
         Collection<Boolean> list = viewData();
         notEmpty = false;
         return list;
    }

    /**
     * Būsena atstatoma į pradinę.
     */
    @Override
    public void resetOwn() {
        notEmpty = false;
    }
    
    
    /*
     * Funkcijos skirtos grafinei naudotojo sąsajai.
     */

    /**
     * Sugeneruojami elementai, skirti bitų įvedimui.
     */
    private void initialiseEditable() {
        /* Mygtukų sukūrimas */
        one = new JButton(" 1 ");
        zero = new JButton(" 0 ");
        
        /* Veiksmai paspaudus mygtukus */
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
        
        /* Mygtukų skydelis */
        JPanel controlls = new JPanel();
        controlls.add(zero);
        controlls.add(one);
        
        /* Galutinis skydelis */
        setLayout(new BorderLayout());
        add(controlls, BorderLayout.NORTH);
        addExternalViever(historyPanel);
        add(historyPanel, BorderLayout.CENTER);
    }
    
    /**
     * Sugeneruojami elementai, skirti duomenų strukūros pavaizdavimui.
     */
    private void initialiseVisible() {
        super.setMinimumSize(new Dimension(20, 20));
        addExternalViever(historyPanel);
        add(historyPanel);
    }
}
