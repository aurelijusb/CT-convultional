package lt.banelis.aurelijus.data;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Duomenų struktūra teksto įvedimui, vaizdavimui ir persiuntimui.
 *
 * @author Aurelijus Banelis
 */
public class Text extends AbstractDataStructure {
    private static final int BIT_16 = 32768;
    private List<Boolean> data = new LinkedList<Boolean>();
    private boolean shiftNeeded = false;
    private JTextArea text = new JTextArea();
    
    /**
     * Naujos duomenų strukūros sukūrimas.
     * 
     * @param inputEnabled <code>true</code>, jei ji skirta duomeų įvedimui,
     *                     <code>false</code> jei ji skirta tik atvaizdavimui.
     */
    public Text(boolean inputEnabled) {
        super(inputEnabled);
        if (inputEnabled) {
            initialiseEditable();
        } else {
            initialiseVisible();
        }
    }
    
    
    /*
     * Funkcijos skirtos duomenų saugojimui ir paėmimui
     */
    
    /**
     * Duomenų pridėjimas.
     * 
     * @param data  naujų bitų seka.
     */
    @Override
    protected void putDataImplementation(Collection<Boolean> data) {
        this.data.addAll(data);
        updateText();
    }
    
    /**
     * Pagal išsaugotą bitų seką pavaizduojamas tekstas.
     */
    private void updateText() {
        int modulus = viewAllData().size() % Character.SIZE;
        if (shiftNeeded && modulus != 0) {
            Collection<Boolean> shifted = shift(viewAllData(), -modulus);
            text.setText(toText(shifted));
        } else {
            text.setText(toText(viewAllData()));
        }        
    }
    
    /**
     * Bitų seka pastumiama į vieną arba kitą pusę, ištrinant netilpusius bitus.
     * 
     * Funkcija yra naudinga, kai nenorima gauti nepilnų teksto fragmentų.
     * Jei poslinkio modulis viršyja sekod dydį, gražinamas nepakeistas srautas.
     * 
     * @param data          bitų seka
     * @param difference    poslinkis (teigiams į dešinę, neigiamas - į kairę)
     * @return              bitų sekos dalis
     */
    private Collection<Boolean> shift(Collection<Boolean> data, int difference) {
        if (difference > 0 && difference < data.size()) {
            /* Ištrinama pradžia */
            int size = data.size() - difference;
            ArrayList<Boolean> result = new ArrayList<Boolean>(size);
            int i = 0;
            for (Boolean bit : data) {
                if (i >= difference) {
                    result.add(bit);
                } else {
                    i++;
                }
            }
            return result;
        } else if (difference < 0 && -difference < data.size()) {
            /* Ištrinamas galas */
            int size = data.size() + difference;
            ArrayList<Boolean> result = new ArrayList<Boolean>(size);
            int i = 0;
            for (Boolean bit : data) {
                if (i < size) {
                    result.add(bit);
                } else {
                    break;
                }
                i++;
            }
            return result;
        } else {
            /* Seka nekeičiama */
            return data;
        }
    }

    /**
     * Peržiūrimi esami duomenys.
     * 
     * @return tekstą atitinkanti bitų seka
     */
    @Override
    protected Collection<Boolean> viewData() {
        return data;
    }

    /**
     * Išimami esami duomenys.
     * 
     * @return tekstą atitinkanti bitų seka
     */
    @Override
    protected Collection<Boolean> retrieveDataImplementation() {
        Collection<Boolean> toRetrieve = data;
        data = new LinkedList<Boolean>();
        return toRetrieve;
    }

    /**
     * Atstatoma pradinė būsena.
     */
    @Override
    public void resetOwn() {
        data = new LinkedList<Boolean>();
        text.setText("");
        text.requestFocus();
    }

    
    /*
     * Funkcijos skirtos duomenų transfromacijai (simbolis, bitai)
     */

    /**
     * Teksto vertimas bitų seka.
     * 
     * Vienas simbolis atitinka 16 bitų.
     * 
     * @param text  tekstas, kurį norima paversti bitais
     * @return      tekstą atitinkanti bitų seka
     */
    protected static List<Boolean> toBinary(String text) {
        LinkedList<Boolean> result = new LinkedList<Boolean>();
        for (int i = 0; i < text.length(); i++) {
            char word = text.charAt(i);
            int bit = BIT_16;
            for (int j = 0; j < Character.SIZE; j++) {
                result.add((word & bit) != 0);
                bit >>>= 1;
            }
        }
        return result;
    }
    
    /**
     * Bitų sekos vertimas tekstu.
     * 
     * Jei bitų seka neatitinka simbolio dydžio, paskutinis simbolis sukuriamas
     * iš esamų duomenų.
     * 
     * @param data  tekstą atitinkanti bitų seka
     * @return      atkurtas tekstas
     */
    protected static String toText(Collection<Boolean> data) {
        StringBuilder result = new StringBuilder();
        char word = 0;
        int upper = BIT_16;
        int i = 0;
        for (Boolean bit : data) {
            if (i != 0 && i % Character.SIZE == 0) {
                result.append(word);
                word = 0;
                upper = BIT_16;
            }
            if (bit) {
                word += upper;
            }
            upper >>>= 1;
            i++;
        }
        result.append(word);
        return result.toString();
    }
    
    
    /*
     * Funkcijos skirtos grafinei naudotojo sąsajai.
     */

    /**
     * Sukuriami elementai teksto įvedimui.
     */
    private void initialiseEditable() {
        /* Skydelis ir persiuntimo mygtukas */
        JPanel buttons = new JPanel();
        buttons.setLayout(new BorderLayout());
        JButton send = new JButton("Į kanalą");
        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                putData(toBinary(text.getText()));
            }
        });
        
        /* Paskutinių (sinchronizacijos) bitų siuntimo mygtukas */
        JButton finalize = new JButton("Paskutinis");
        finalize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                putData(dataToSynchronize());
            }
        });
        finalize.setToolTipText("Sintėjo-gavėjo sinchronizacijai");
        buttons.add(send, BorderLayout.CENTER);
        buttons.add(finalize, BorderLayout.EAST);
        
        /* Galutinis elementų išdėliojimas */
        setLayout(new BorderLayout());
        JScrollPane pane = new JScrollPane(text);
        add(pane, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        JPanel binary = getStreamPanel();
        add(binary, BorderLayout.NORTH);
    }
    
    /**
     * Sukūriami elementai teksto (ir jį atitinkančių bitų) peržiūrai
     */
    private void initialiseVisible() {
        /* Teksto laukelis */
        text.setEditable(false);
        final JPanel binary = getStreamPanel();
        final String tooltip = "Du kartus bakstelėkite, norėdami pakeisti " +
                               "viso/dalinio vektoriaus rodymą";
        text.setToolTipText(tooltip);
        text.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    shiftNeeded = !shiftNeeded;
                    String mode;
                    if (shiftNeeded) {
                        mode = "Rodomi tik pilni. ";
                    } else {
                        mode = "Rodomi ir dalinai gauti. ";
                    }
                    binary.setToolTipText(mode + tooltip);
                    updateText();
                }
            }
        });
        
        /* Galutinis elementų išdėliojimas */
        binary.setLayout(new FlowLayout(FlowLayout.LEFT));
        setLayout(new BorderLayout());
        JScrollPane pane = new JScrollPane(text);
        add(pane, BorderLayout.CENTER);
        add(binary, BorderLayout.SOUTH);
    }
}
