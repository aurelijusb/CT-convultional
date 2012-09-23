package lt.banelis.aurelijus.data;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Duomenų struktūra skirta vaizduoti bitų seką.
 * 
 * Bitų sekos "įvedimo" realizacija gali būti panaudota bitų kanale koregavimui.
 *
 * @author Aurelijus Banelis
 */
public class BitsSteam extends AbstractDataStructure {   
    private LinkedList<Boolean> data = new LinkedList<Boolean>();
    private HashSet<Integer> errors = new HashSet<Integer>();
    private int bufferWidth;
    private int bufferHeight;
    private int markedIndex = -1;
    private boolean externalViewerInitiated = false;

    /**
     * Naujos duomenų strukūros sukūrimas.
     * 
     * @param inputEnabled <code>true</code>, jei ji skirta duomeų įvedimui,
     *                     <code>false</code> jei ji skirta tik atvaizdavimui.
     */
    public BitsSteam(boolean inputEnabled) {
        super(inputEnabled);
        if (inputEnabled) {
            initailiseEditing();
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
        if (!externalViewerInitiated) {
            addExternalViever(this);
            externalViewerInitiated = true;
        }
    }

    /**
     * Peržiūrimi duomenys.
     * 
     * @return dabartiniu metu saugojama bitų seka.
     */
    @Override
    protected Collection<Boolean> viewData() {
        return data;
    }

    /**
     * Išimami duomenys.
     * 
     * @return dabartiniu metu saugota bitų seka.
     */
    @Override
    protected Collection<Boolean> retrieveDataImplementation() {
        Collection<Boolean> toRetrieve = data;
        data = new LinkedList<Boolean>();
        errors = new HashSet<Integer>();
        return toRetrieve;
    }

    
    /**
     * Būsena atstatoma į pradinę.
     */
    @Override
    public void resetOwn() {
        data = new LinkedList<Boolean>();
        errors = new HashSet<Integer>();
    }

    
    /*
     * Funkcijos skirtos grafinei naudotojo sąsajai
     */
    
    /**
     * Komponento (įskaitant ir bitų sekos) paišymas.
     * 
     * Ši funkcija iškviečiama automatiškai naudojant standartines Swing
     * bibliotekas.
     * 
     * @param g piešimui skirtas objektas.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        super.paintBuffer(g);
        if (isInputEnabled()) {
            paintInsideData(g);
            paintOverBit(g);
        }
    }

    /**
     * Įjungiams bitų redagavimas.
     * 
     * Bitų negalima ištrinti, bet galima juos pakeisti priešingu.
     * 
     * @param inputEnabled  ar galima bus redaguoti
     */
    @Override
    public void setInputEnabled(boolean inputEnabled) {
        super.setInputEnabled(inputEnabled);
        repaint();
    }
    
    /**
     * Sukuriami elementai, skirti bitų redagavimui.
     */
    private void initailiseEditing() {
        MouseAdapter bitsEditor = new MouseAdapter() {
            /**
             * Pažymimas bitas, kurį galima bus redaguoti.
             * 
             * @param e įvykio duomenys
             */
            @Override
            public void mouseMoved(MouseEvent e) {
                int lastIndex = markedIndex;
                markedIndex = getSymbolIndex(e.getX());
                if (lastIndex != markedIndex) {
                    repaint();
                }
            }

            /**
             * Pelei nesant ant komponento, nužymimi ir visi redagavimui skirti
             * bitai.
             * 
             * @param e įvykio duomenys
             */
            @Override
            public void mouseExited(MouseEvent e) {
                markedIndex = -1;
            }
            
            /**
             * Pažymėtas bitas pakeičiamas priešingu.
             * 
             * Pakeistų bitų paryškinimui išsaugomos ir jų pozicijos.
             * 
             * @param e įvykio duomenys
             */
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
    
    /**
     * Paišoma sritis, kurioje galima redaguoti bitus.
     * 
     * @param g paišymui skirtas objektas
     */
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
    
    /**
     * Paišomas pažymėtas (užėjus virš jo su pele) bitas.
     * 
     * @param g paišymo objektas
     */
    private void paintOverBit(Graphics g) {
        if (markedIndex > -1 && markedIndex < viewData().size()) {
            int x = markedIndex * bufferWidth;
            int index = data.size() - markedIndex - 1;
            String symbol = data.get(index) ? "1" : "0";
            if (errors.contains(index)) {
                 g.setColor(Color.BLUE);
            } else {
                 g.setColor(Color.RED);
            }
            g.drawRect(x - 1, 1, bufferWidth + 1, getHeight() - 2);
            g.drawString(symbol, x, bufferHeight * 2);
        }
    }
    
    /**
     * Išsaugomi standartiniai bitų sekos nustatymai.
     * 
     * Ši funkcija yra kviečiama tėvinės klasės.
     * 
     * @param g         paišymui skirtas objetas
     * @param width     vaizduojamo bito plotis (tašakis)
     * @param height    vaizduojamo bito aukštis (tašakis)
     * @param step      bitų kiekis vienoje grupėje
     * @param data      duomenų seka
     */
    @Override
    protected void paintBuffer(Graphics g, int width, int height,
                              int step, Collection<Boolean> data) {
        super.paintBuffer(g, width, height, step, data);
        bufferWidth = width;
        bufferHeight = height;
    }
    
    /**
     * Pagal ekrano pozciiją gaunama bito pozicija sekoje.
     * 
     * @param x ekrano taškas (skydelio atžvilgiu)
     * @return  bito pozicija sekoje arba <code>-1</code> jei pagal tašką
     *          neįmanoma rasti bito (taškas nepatenka į redaguojamų bitų zoną)
     */
    private int getSymbolIndex(int x) {
        x -= getBufferPadding(bufferWidth);
        int index = x / bufferWidth;
        if (index > viewAllData().size()) {
            index = -1;
        }
        return index;
    }

    
}
