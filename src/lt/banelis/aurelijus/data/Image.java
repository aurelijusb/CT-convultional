package lt.banelis.aurelijus.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import lt.banelis.aurelijus.connectors.Synchronizer;

/**
 * Duomenų struktūra paveikslėlio pridėjimui, vaizdavimui ir persiuntimui.
 *
 * @author Aurelijus Banelis
 */
public class Image extends AbstractDataStructure {
    private static final int MAX_WIDTH = 400;
    private static final int MAX_HEIGHT = 400;
    private static final int MAX_CATCH = 10000;
    private Collection<Boolean> data = new LinkedList<Boolean>();
    private BufferedImage image = null;
    private boolean loading = false;
    private JScrollPane container = new JScrollPane();
    private JButton sendButton = new JButton("Siųsti kanalu");
    private static Component self = null;
            
    /**
     * Naujos duomenų strukūros sukūrimas.
     * 
     * @param inputEnabled <code>true</code>, jei ji skirta duomeų įvedimui,
     *                     <code>false</code> jei ji skirta tik atvaizdavimui.
     */
    public Image(boolean inputEnabled) {
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
        if (data.size() < MAX_CATCH) {
            this.data.addAll(data);
        } else if (this.data.size() > 0) {
            this.data = skipSynchronization(data);
        } else {
            this.data = data;
        }
        image = toImage(this.data);
        self = this;
        updateImage();
    }
    
    /**
     * Praleidžiami dėl sinchronizacijos pavėlavę bitai.
     * 
     * Persiunčiant paveikslėlį paskutini 6 bitai turi mažai įtakos paveikslėlio
     * taškų vaizdavimui, tačiau neteisingas paveikslėlio dydžio nuskaitymas
     * turi daug didensnių pasėkmių.
     * 
     * @param data  bitų seka (kurios pradžioje yra seno paveikslėlio bitų)
     * @return      bitų seką tik iš dabartinio paveikslėlio duomenų.
     */
    private Collection<Boolean> skipSynchronization(Collection<Boolean> data) {
        int toSynchronize = dataToSynchronize().size();
        int size = data.size() - toSynchronize;
        if (size > 0) {
            ArrayList<Boolean> result = new ArrayList<Boolean>(size);
            long i = 0;
            for (Boolean bit : data) {
                if (i >= toSynchronize) {
                    result.add(bit);
                }
                i++;
            }
            return result;
        } else {
            return new LinkedList<Boolean>();
        }
    }
    
    /**
     * Peržiūrima išsaugoto paveikslėlio bitų seka.
     * 
     * @return  bitų seka (pradžioje dydis, toliau taškų duomenys)
     */
    protected Collection<Boolean> viewData() {
        return data;
    }

    /**
     * Išimamama dabartinio paveikslėlio bitų seka.
     * 
     * @return  bitų seka (pradžioje dydis, toliau taškų duomenys)
     */
    @Override
    protected Collection<Boolean> retrieveDataImplementation() {
        Collection<Boolean> toRetrieve = data;
        data = new LinkedList<Boolean>();
        updateImage();
        return toRetrieve;
    }

    /**
     * Atsatoma pradinė būsena.
     * 
     * Pradinė būsena taip pat reiškia išsaugoto paveikslėlio ištrynimą.
     */
    @Override
    public void resetOwn() {
        data = new LinkedList<Boolean>();
        image = null;
        loading = false;
        sendButton.setEnabled(true);
        updateImage();
    }

    
    /*
     * Funkcijos skirtos duomenų transfromacijai (paveikslėlis, skaičius, bitai)
     */
    
    /**
     * Paveikslėlio pavertimas bitų seka.
     * 
     * Kadangi pavertimas gali užtrukti, sistema informuojama apie progresą.
     * Paveikslėlis koduojamas Integer dydžio (t.y. 32) bitų sekomis:
     *  Pirmi du žodžiai atitinka plotį ir aukšti
     *  Kiti žodžiai atitinka taško RGB reikšmę (eilutės, tada stulpeliai).
     * 
     * @param image paveikslėlis
     * @return      bitų seka (pradžioje dydis, toliau taškų duomenys)
     * 
     * @see Synchronizer#setProgressUpdater(java.lang.Runnable) 
     */
    protected static Collection<Boolean> toBinary(BufferedImage image) {
        int size = image.getWidth() * image.getHeight() + 2;
        ArrayList<Boolean> result = new ArrayList<Boolean>(size);
        result.addAll(toBinary(image.getWidth()));
        result.addAll(toBinary(image.getHeight()));
        for (int y = 0; y < image.getHeight(); y++) {
            if (y % 20 == 0) {
                Synchronizer.progress = y / (double) image.getHeight();
                Synchronizer.updateProgress();
            }
            for (int x = 0; x < image.getWidth(); x++) {
                result.addAll(toBinary(image.getRGB(x, y)));
            }
        }
        return result;
    }
    
    /**
     * Bitų sekos pavertimas atgal į paveikslėlį.
     * 
     * Paveikslėlis nebus sugeneruotas, jei sekoje bus neigiami paveikslėlio 
     * dydžiai arba paveikslėlis bus didesnis už MAX_WIDTH (plotis) arba
     * MAX_HEIGHT (aukštis).
     * Klaidų atveju iššoks klaidos pranešimas.
     * Per didelio paveikslėlio atveju bus sukurtas mažesnis paveikslėlis.
     * Kadangi pavertimas gali užtrukti, sistema informuojama apie progresą.
     * 
     * @param data  bitų seka (pradžioje dydis, toliau taškų duomenys)
     * @return      paveikslėlis arba <code>null</code>, jei nepavyko sukurti
     *              paveikslėlio.
     * 
     * @see #MAX_WIDTH
     * @see #MAX_HEIGHT
     * @see #toBinary(java.awt.image.BufferedImage) 
     * @see Synchronizer#setProgressUpdater(java.lang.Runnable) 
     */
    protected static BufferedImage toImage(Collection<Boolean> data) {
        Iterator<Boolean> iterator = data.iterator();
        int width = toInteger(getNextInteger(iterator));
        int height = toInteger(getNextInteger(iterator));
        if (width > 0 && height > 0) {
            BufferedImage image;
            if (width < MAX_WIDTH && height < MAX_HEIGHT) {
                image = new BufferedImage(width, height,
                                          BufferedImage.TYPE_INT_RGB);
            } else {
                errorMessage("Atkuriamas mažesnis " + MAX_WIDTH + "x" +
                             MAX_HEIGHT + " vietoj " + width + "x" + height +
                             "paveikslėlis");
                image = new BufferedImage(MAX_WIDTH, MAX_HEIGHT,
                                          BufferedImage.TYPE_INT_RGB);
            }
            for (int y = 0; y < height && y < MAX_HEIGHT; y++) {
                if (y % 20 == 0) {
                    Synchronizer.progress = y / (double) height;
                    Synchronizer.updateProgress();
                }
                for (int x = 0; x < width && x < MAX_WIDTH; x++) {
                    int color = toInteger(getNextInteger(iterator));
                    image.setRGB(x, y, color);
                }
            }
            return image;
        } else {
            errorMessage("Paveikslėlio dydis neigiamas: " + width + "x" +
                                                            height); 
            return null;
        }
    }
    
    /**
     * Skaičius vertimas bitų seka.
     * 
     * @param number    skaičius.
     * @return          skaičių atitinkanti bitų seka.
     */
    protected static ArrayList<Boolean> toBinary(int number) {
        ArrayList<Boolean> result = new ArrayList<Boolean>(Integer.SIZE);
        long multipier = 1;
        for (int i = 0; i < Integer.SIZE; i++) {
            if ((number & multipier) != 0) {
                result.add(Boolean.TRUE);
            } else {
                result.add(Boolean.FALSE);
            }
            multipier <<= 1;
        }
        return result;
    }
    
    /**
     * Bitų sekos vertimas skaičiumi.
     * 
     * @param data  skaičių atitinkanti bitų seka.
     * @return      atkoduotas skaičius.
     */
    protected static int toInteger(Collection<Boolean> data) {
        int result = 0;
        long multiplier = 1;
        for (Boolean bit : data) {
            if (bit) {
                result += multiplier;
            }
            multiplier <<= 1;
        }
        return result;
    }

    /**
     * Nuskaito sekos gabalą.
     * 
     * Kadangi daug funkcijų duomenis skaito iš eilės, todėl ši funkcija
     * pravarti tiesiog nuskaityti reikiamo dydžio gabalą iš visos sekso.
     * 
     * @param <T>       duomenų tipas, kuris naudojamas sekoje
     * @param iterator  duomenų sekos iteratorius
     * @param size      norimo sekos gabalo dydis
     * @return          artimiausia sekos dalis
     */
    private static <T> Collection<T> getNext(Iterator<T> iterator, int size) {
        ArrayList<T> part = new ArrayList<T>(size);
        for (int i = 0; i < size; i++) {
            if (iterator.hasNext()) {
                part.add(iterator.next());
            }
        }
        return part;
    }

    /**
     * Nuskaito skaičiaus dydžio (32 elementų) sekos gabalą.
     * 
     * @param <T>       duomenų tipas, kuris naudojamas sekoje
     * @param iterator  duomenų sekos iteratorius
     * @return          sekos dalis
     */
    private static <T> Collection<T> getNextInteger(Iterator<T> iterator) {
        return getNext(iterator, Integer.SIZE);
    }
        
    
    /*
     * Funkcijos skirtos naudotojo sąsajai.
     */
    
    /**
     * Sukuriami elementai, skirti paveikslėlio pridėjimui ir siuntimui.
     */
    private void initialiseEditable() {
        /* Pagrinis skydelis ir failo pridėjimo mygtukas */
        JPanel controlls = new JPanel();
        controlls.setLayout(new BoxLayout(controlls, BoxLayout.X_AXIS));
        JButton load = new JButton("Pasirinkti failą");
        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (fileChooser.showOpenDialog(container) ==
                    JFileChooser.APPROVE_OPTION) {
                    loadImage(fileChooser.getSelectedFile().getPath());
                }
            }
        });
        controlls.add(load);
                
        /* Pavyzdinio paveikslėlio mygtukas */
        JButton example = new JButton("Pavyzdinis paveikslėlis");
        example.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                image = sampleImage(100, 100);
                updateImage();
                sendButton.setEnabled(true);
            }
        });
        controlls.add(example);
        
        /* Paveikslėlio siuntimo mygtukas */
        sendButton.setEnabled(false);
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (image != null && (image.getWidth() > MAX_WIDTH ||
                                     image.getHeight() > MAX_HEIGHT)) {
                    self = Image.this;
                    errorMessage("Dideli paveikslėliai bus ilgai koduojami " +
                            "ir užims daug atminties. Naudokite " + MAX_WIDTH +
                            "x" + MAX_HEIGHT + " ir mažesnius paveikslėlius");
                } else if (image != null) {
                    BufferedImage toSend = image;
                    reset();
                    putData(toBinary(toSend));
                    sendButton.setText("Siųsti kanalu");
                    sendButton.setEnabled(true);
                }
            }
        });
        controlls.add(sendButton);
        
        /* Siuntimo užbaigimo (sinchronizacijos bitų) mygtukas */
        JButton finalize = new JButton("Paskutinis");
        finalize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                putData(dataToSynchronize());
            }
        });
        finalize.setToolTipText("Sintėjo-gavėjo sinchronizacijai");
        controlls.add(finalize);
        
        /* Galutinis elementų sudėjimas į skydelį */
        setLayout(new BorderLayout());
        add(container, BorderLayout.CENTER);
        add(controlls, BorderLayout.SOUTH);
    }
    
    /**
     * Paveikslėlio užkrovimas.
     * 
     * Jei nepavyksta užkrauti paveikslėlio, išmetamas klaidos pranešimas.
     * 
     * @param file  paveikslėlio failas.
     */
    private void loadImage(final String file) {
        Thread openning = new Thread() {
            @Override
            public void run() {
                try {
                    image = ImageIO.read(new File(file));
                } catch (IOException ex) {
                    image = null;
                    errorMessage("Nepavyko atidaryti paveikslėlio: " + file);
                }
                updateImage();
                loading = false;
                sendButton.setEnabled(true);
            }
        };
        loading = true;
        sendButton.setEnabled(false);
        image = null;
        openning.start();
    }
    
    /**
     * Išmetamas klaidos pranešimas.
     * 
     * @param message   klaidos pranešimo tekstas.
     */
    private static void errorMessage(String message) {
        if (self != null) {
            JOptionPane.showMessageDialog(self, message, "Klaida",
                                          JOptionPane.WARNING_MESSAGE);
        } else {
            System.err.println(message);
        }
        
    }
    
    /**
     * Sukuriamas pavyzdinis paveikslėlis.
     * 
     * Paveikslėlį sudaro spalvų perėjimas.
     * 
     * @param width     paveikslėlio plotis
     * @param height    paveikslėlio aukštis
     * @return          sugeneruotas paveikslėlis
     */
    protected static BufferedImage sampleImage(int width, int height) {
        BufferedImage result = new BufferedImage(width, height,
                                                BufferedImage.TYPE_INT_RGB);
        Graphics g = result.getGraphics();
        for (int x = 0; x < width; x++) {
            int y = (int) (x / (double) width * height);
            float r = x / (float) width;
            float b = 1 - x / (float) width;
            float green = r * 0.5f;
            g.setColor(new Color(r, b, b));
            g.drawLine(x, 0, x, y);
            g.setColor(new Color(r, green, b));
            g.drawLine(x, y, x, height);
        }
        return result;
    }
    
    /**
     * Pasirūpinama, kad atmintyje rodomas paveikslėlis būtų matomas ir
     * naudotojui.
     */
    private void updateImage() {
        /* Skydelis su paveikslėliu */
        JPanel canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null) {
                    g.drawImage(image, 0, 0, null);
                } else if (loading) {
                    g.setColor(Color.BLUE);
                    g.drawString("Atidaroma...", 5, 5);
                } else {
                    g.setColor(Color.RED);
                    g.drawLine(0, 0, getWidth(), getHeight());
                    g.drawLine(0, getHeight(), getWidth(), 0);
                }
            }
        };
        if (image != null) {
            canvas.setPreferredSize(new Dimension(image.getWidth(),
                                                  image.getHeight()));
            canvas.setToolTipText("Paveikslėlis: " + image.getWidth() + "x" +
                                                     image.getHeight());
        } else {
            canvas.setPreferredSize(new Dimension(100, 50));
            canvas.setToolTipText("Nėra paveikslėlio");
        }
        
        /* Šliaužyklės, jei paveikslėlis būtų didesnis */
        container.add(canvas);
        container.setViewportView(canvas);
        container.setVerticalScrollBarPolicy(
                  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        container.setHorizontalScrollBarPolicy(
                  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        container.repaint();
    }
    
    /**
     * Sukuriami elementai paprastam paveikslėlio rodymui (ne pridėjimui).
     */
    private void initialiseVisible() {
        setLayout(new BorderLayout());
        add(container, BorderLayout.CENTER);
        add(getStreamPanel(), BorderLayout.SOUTH);
    }
}
