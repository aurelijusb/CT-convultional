package lt.banelis.aurelijus.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import lt.banelis.aurelijus.connectors.Synchronizer;

/**
 * Duomenų struktūra, skirta dirbti su dvejetainiais duomenimis ir jų srautais.
 * 
 * Funkcijų ryšys:
 * 
 *  Naudotojas__                                    ___ onUpdated()
 *              \___ putData() -+- [dabartiniai] __/
 *  Sistema  ___/               :                  \___ retrieveData()
 *                              :       
 *                              +- [istoriniai] - viewAllData()
 * 
 * Schemos paaiškinimas:
 *  Į sistemą duomenis galima pridėti tiek sistema, tiek naudotojas per duomenų
 *  struktūros grafinę sąsają. Abiem atvejais naudojama funkcija putData().
 * 
 *  Duomenų struktūroje saugomi ir dabartiniai duomenys, ir istoriniai.
 * 
 *  Jei dabartiniai duomenys pakeičiami per naudotojo sąsają, tai iškviečiama
 *  runkcija onUpdated(), kuri priskirta per setListener() procedūrą.
 *  Dabartinius duomenis galima išsiimti pasinaudojus retrieveData() funkcija.
 * 
 *  Išsiėmus dabartinius duomenis, jie perkeliami į istorinius ir pasiekiami
 *  per kitą funkciją: viewAllData()
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
     * Sąsaja skirta informavimui apie naudotojo pridėtus/pakeistus duomenis.
     */
    public static interface InputListner {
        /**
         * Funkcija skirta informuoti, kad prisidėjo/pasikeitė šaltinio
         * duomenys.
         * 
         * @param object    šaltinio/siuntėjo objektas.
         */
        public void onUpdated(AbstractDataStructure object);
    }
    
    
    /**
     * Sukuriama nauja duomenų struktūra
     * 
     * @param inputEnabled <code>true</code>, jei ji skirta duomeų įvedimui,
     *                     <code>false</code> jei ji skirta tik atvaizdavimui.
     */
    public AbstractDataStructure(boolean inputEnabled) {
        this.inputEnabled = inputEnabled;
        setPreferredSize(new Dimension(100, 50));
        super.setFont(currentFont);
    }
    
    
    /*
     * Funkcijos, skirtos duomenų saugojimui
     */
    
    /**
     * Pridėti naujus duomenis į duomenų struktūrą.
     * 
     * Funkcija iškviečiama pridedant duomenis ir sistemos, ir naudotojo per
     * grafinę sistemą.
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
        }
        repaint();
    }

    /**
     * Specifinės duomenų struktūros išsugojimas.3
     * 
     * @see #putData(java.util.Collection) 
     */
    protected abstract void putDataImplementation(Collection<Boolean> data);
    

    /*
     * Funkcijos, skirtos duomenų gavimui
     */
    
    /**
     * Paimti duomenis iš stuktūros.
     * 
     * Paimti duomenys yra perkeliami į duomeų istoriją ir antrą karta su ta
     * pačia funkcija nėra piimami.
     * 
     * @return  duomenys paversti į dvejetainę seką
     * 
     * @see #viewAllData()
     */
    public Collection<Boolean> retrieveData() {
        Collection<Boolean> data = retrieveDataImplementation();
        history.addAll(data);
        return data;
    }
    
    /**
     * Dabartinių duomenų peržiūros įgyvendinimas.
     * 
     * Duomenys nėa išimami, t.y. šią funkciją galima naudoti daug kartų ir
     * duomenys nedings.
     * 
     * @return  dabartiniai duomenys paversti dvejetaine seka.
     */
    protected abstract Collection<Boolean> viewData();
        
    /**
     * Duomenų išėmimo įgyvendinimas.
     * 
     * @see #retrieveData()
     */
    protected abstract Collection<Boolean> retrieveDataImplementation();
        
    /**
     * Funkcijos, skirtos pranešimui apie naudotojo atnaujintus duomenis,
     * priskyrimas
     * 
     * @param listener  objektas, su veiksmais, skirtais atlikti po duomenų
     *                  pridėjimo/pasikeitimo
     * 
     * @see #retrieveDataImplementation()
     */
    public void setListerer(Runnable listener) {
        this.listener = listener;
    }
    
    /**
     * Funkcija skirta pasižiūrėti esamus ir istorinius duomenis kartu.
     * 
     * Duomenys tik peržiūrimi, bet nesugadinami.
     * 
     * @return bitų seka
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
        
    /**
     * Ištinami esami duomenys bei atliekami kiti atsatymo į pradinę būseną
     * veiksmai.
     */
    public void reset() {
        history = new LinkedList<Boolean>();
        resetOwn();
        repaint();
    }
    
    /**
     * Konkrečios duomenų struktūros perėjimas į pradinę būseną.
     */
    protected abstract void resetOwn();

    
    /*
     * Funkcijos skirtos siuntėjo-gavėjo sąryšiui
     */
    
    /**
     * Priskiriamas sąryšio palaikymui skirtas ojektas.
     * 
     * @param syncronizer   sąryšiui skirtas objetas.
     * @param comparator    struktūra, su kuria bus lyginami duomenys
     * @param isDestination ar šis duomenų struktūra yra gavėjas
     */
    public void setSyncronizer(Synchronizer syncronizer,
                              AbstractDataStructure comparator,
                              boolean isDestination) {
        this.syncronizer = syncronizer;
        this.comparator = comparator;
        this.isDestination = isDestination;
    }

    /**
     * Grąžinamas šaltinis.
     * 
     * @return duomenų šaltinio objekas
     */
    private Collection<Boolean> getSource() {
        if (isDestination) {
            return viewAllData();
        } else {
            return comparator.viewAllData();
        }
    }
    
    /**
     * Grąžinamas gavėjas.
     * 
     * @return duomenų gavėjo objetas
     */
    private Collection<Boolean> getDestination() {
        if (isDestination) {
            return comparator.viewAllData();
        } else {
            return viewAllData();
        }
    }
    
    /**
     * Palyginama, ar siuntėjo ir gavėjo bitas konkrečioje pozcijoje yra toks
     * pats.
     * 
     * @param offset    bito pozicija (skaičiuojant nuo 0, pradžios)
     * @return          ar bitai sutapo
     */
    private boolean isEqual(int offset) {
        return getBit(getSource(), offset) == getBit(getDestination(), offset);
    }
    
    /**
     * Gaunamas konkretus bitų srauto elemetnas.
     * 
     * @param container bitų srautas
     * @param offset    elemento pozicija
     * @return          elemetnas, nurodytoje pozicijoje,
     *                  arba <code>null</code>, jei bitas nerastas
     */
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
    
    /**
     * Deleguojama sinchronizacijai naudojama funkcija.
     * 
     * @return bitų seka, skirta užbaigti paskutinį pranešimą.
     */
    protected Collection<Boolean> dataToSynchronize() {
        if (syncronizer != null) {
            return syncronizer.dataToSynchronize();
        } else {
            return new LinkedList<Boolean>();
        }
    }
    
    
    /*
     * Funkcijos skirtos grafinei naudotojo sąsajai
     */
    
    /**
     * Grąžinama, ar komponentas yra skirtas duomenų įvedimui.
     * 
     * @return inputEnabled <code>true</code>, jei ji skirta duomeų įvedimui,
     *                     <code>false</code> jei ji skirta tik atvaizdavimui.
     */
    protected final boolean isInputEnabled() {
        return inputEnabled;
    }

    /**
     * Nustatoma, ar komponentas yra skirtas duomenų įvedimui.
     * 
     * Įprastai komponentai nekeičia galimybės įvesti ar tik rodyti duomenis.
     * 
     * @param inputEnabled <code>true</code>, jei ji skirta duomeų įvedimui,
     *                     <code>false</code> jei ji skirta tik atvaizdavimui.
     */
    protected void setInputEnabled(boolean inputEnabled) {
        this.inputEnabled = inputEnabled;
    }
    
    
    /*
     * Funkcijos skirtos paišyti bitų seką
     */
    
    /**
     * Bendriausia bitų paišymo funkcija.
     * 
     * @param g paišymui skirtas objetas.
     */
    protected void paintBuffer(Graphics g) {
        if (halfSize) {
            paintBuffer(g, currentFont.getSize(), font.getSize(), 8,
                        viewAllData());
        } else {
            paintBuffer(g, font.getSize(), font.getSize(), 4, viewAllData());
        }
    }
    
    /**
     * Grąžinamas paslinkimas ekrane, kad siuntėjo ir gavėjo bitų sekos
     * susiligiuotų
     * 
     * @param width vaizduojamo bito plotis
     * 
     * @return      poslinkio atstumas taškais nuo kairio krašto
     */
    protected final int getBufferPadding(int width) {
        if (isDestination && syncronizer != null) {
            return syncronizer.getSynchronisation() * width;
        } else {
            return 0;
        }
    }
    
    /**
     * Bitų sekos paišymo funkcija.
     * 
     * Lengvesniam duomenų palyginimui naudojamas grupavimas, paremntas
     * skirtingu fonu.
     * 
     * @param g         paišymui skirtas objetas
     * @param width     vaizduojamo bito plotis (tašakis)
     * @param height    vaizduojamo bito aukštis (tašakis)
     * @param step      bitų kiekis vienoje grupėje
     * @param data      duomenų seka
     */
    protected void paintBuffer(Graphics g, int width, int height,
                              int step, final Collection<Boolean> data) {
        int padding = getBufferPadding(width);
        final int length = data.size() - 1;
        int i = length;        
        Color background = backgrounds[0];
        Color foreground = foregrounds[0];
        for (Boolean bit : data) {
            /* Bito vieta ir reikšmė */
            int x = padding + width * i;
            int symbol = bit ? 1 : 0;

            /* Bito spalva */
            if ((length - i) % step == 0) {
                int colorIndex = ((length - i) % (step * 2) == 0) ? 0 : 1;
                background = backgrounds[colorIndex];
                foreground = foregrounds[colorIndex];
            }

            /* Paišymas */
            if (x + width < getWidth()) {
                g.setColor(background);
                g.fillRect(x, 0, width, height);
                if (bit) {
                    g.setColor(foreground);
                    g.drawRect(x - 1, 1, width - 2, height - 2);
                }
                int offsetFromEnd = data.size() - i - 1;
                if (isDestination && !isEqual(offsetFromEnd)) {
                    /* Nesutampatis bitas */
                    paintError(g, x, width, height);
                } else {
                    /* Sutampatnis bitas */
                    g.setColor(Color.BLACK);
                }
                g.drawString(symbol + "", x, height);
            }
            i--;
        }
    }
    
    /**
     * Pažymimas (užpiešiamas) klaidingas bitas.
     * 
     * @param g         piešimui skirtas objektas
     * @param x         bito pozicija ekrane (taškais)
     * @param width     bito plotis ekrane (taškais)
     * @param height    bito aukštis ekrane (taškais)
     */
    protected final void paintError(Graphics g, int x, int width, int height) {
        g.setColor(Color.RED);
        g.drawRect(x, height, width, 2);
    }

    /**
     * Individualių bitų dydžio nustatymas.
     * 
     * Funkcija naudojama, kai reikia vaizdžiai susieti 1:2 bitais.
     * 
     * @param halfSize  ar sumažiniti bitų plotš per pusę
     */
    public void setHalfSize(boolean halfSize) {
        this.halfSize = halfSize;
        if (halfSize) {
            currentFont = font.deriveFont(8.f);
        } else {
            currentFont = font;
        }
        repaint();
    }
    
    /**
     * Sugeneruojama bitų sekos vaizdavimui pritaikytas skydelis.
     * 
     * @return  sugeneruotas sludelis.
     */
    protected JPanel getStreamPanel() {
        JPanel binary = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                paintBuffer(g);
            }
        };
        addExternalViever(binary);
        binary.setFont(font);
        binary.setPreferredSize(new Dimension(100, font.getSize()));
        return binary;
    }
    
    
    /*
     * Funkcijos skirtos peržiūrėti bitų seką naujame lange
     */
    
    /**
     * Skydeliui priskiriamas iššokančio lango su pilna bitų seka
     * funkcionalumas.
     * 
     * @param panel skydelis, kuriam reikia priskirti šį funkcionalumą.
     * 
     * @see #showBinaryTextExternally() 
     */
    protected void addExternalViever(JPanel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    showBinaryTextExternally();
                }
            }
        });
        panel.setToolTipText("Dvigubas bakstelėjimas visiems vektoriams " +
                              "parodyti");
    }
    
    /**
     * Sukuriamas ir parodomas langas su pilna duomenų struktūros bitų seka.
     */
    private void showBinaryTextExternally() {
        JFrame frame = new JFrame("Vektorių seka");
        frame.setLayout(new BorderLayout());
        frame.setSize(400, 400);
        frame.setLocation(100, 100);
        JScrollPane pane = new JScrollPane();
        final JTextArea text = new JTextArea();
        updateBinnaryTextExternal(text);
        pane.add(text);
        pane.setViewportView(text);
        JButton button = new JButton("Atnaujinti");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateBinnaryTextExternal(text);
            }
        });
        frame.getContentPane().add(pane, BorderLayout.CENTER);
        frame.getContentPane().add(button, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
    
    /**
     * Atnaujinamas visų bitų seką vaizduojantis langas.
     * 
     * @param text  naujas tekstas.
     */
    private void updateBinnaryTextExternal(JTextArea text) {
        text.setText(allDataToText());
    }
    
    /**
     * Bitų seka paverčiama žmogui lengviau skaitomu formatu.
     * 
     * @return  bitų sekos tekstinė išraiška.
     */
    private String allDataToText() {
        Collection<Boolean> data = viewAllData();
        StringBuilder builder = new StringBuilder(data.size());
        int i = 0;
        for (Boolean bit : data) {
            if (i % 32 == 0 && i != 0) {
                builder.append("\n");
            } else if (i % 16 == 0 && i != 0) {
                builder.append("  ");
            } else if (i % 4 == 0 && i != 0) {
                builder.append(" ");
            }
            if (bit) {
                builder.append("1");
            } else {
                builder.append("0");
            }
            i++;
        }
        return builder.toString();
    }
}
