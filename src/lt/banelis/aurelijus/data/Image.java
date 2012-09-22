package lt.banelis.aurelijus.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Image representation.
 *
 * @author Aurelijus Banelis
 */
public class Image extends AbstractDataStructure {
    private List<Boolean> data = new LinkedList<Boolean>();
    private BufferedImage image = null;
    private boolean loading = false;
    private JScrollPane container = new JScrollPane();
    
    public Image(boolean inputEnabled) {
        super(inputEnabled);
        if (inputEnabled) {
            initialiseEditable();
        } else {
            initialiseVisible();
        }
    }
    
    
    /*
     * Data storage
     */
    
    @Override
    protected void putDataImplementation(Collection<Boolean> data) {
        this.data.addAll(data);
    }
    
    protected Collection<Boolean> viewData() {
        return data;
    }

    @Override
    protected Collection<Boolean> retrieveDataImplementation() {
        Collection<Boolean> toRetrieve = data;
        data = new LinkedList<Boolean>();
        return toRetrieve;
    }

    @Override
    public void resetOwn() {
        data = new LinkedList<Boolean>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.PINK);
        g.fillRect(1, 1, 100, 200);
    }
    
    
    /*
     * Data transformation
     */
    
    
    
    /*
     * Graphical user interface
     */
    
    private void initialiseEditable() {
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
                
        JButton example = new JButton("Pavyzdinis paveikslėlis");
        example.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setImage(sampleImage(100, 100));
            }
        });
        controlls.add(example);
        
        JButton send = new JButton("Siųsti kanalu");
        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("SIUNČIAME");
            }
        });
        controlls.add(send);
        
        JButton finalize = new JButton("Paskutinis");
        finalize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                putData(dataToSynchronize());
            }
        });
        finalize.setToolTipText("Sintėjo-gavėjo sinchronizacijai");
        controlls.add(finalize);
        
        setLayout(new BorderLayout());
        add(container, BorderLayout.CENTER);
        add(controlls, BorderLayout.SOUTH);
    }
    
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
                setImage(image);
                loading = false;
            }
        };
        loading = true;
        setImage(null);
        openning.start();
    }
    
    private void errorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Klaida",
                                      JOptionPane.WARNING_MESSAGE);
    }
    
    protected BufferedImage sampleImage(int width, int heigh) {
        BufferedImage result = new BufferedImage(width, heigh,
                                                BufferedImage.TYPE_INT_RGB);
        Graphics g = result.getGraphics();
        for (int x = 0; x < width; x++) {
            int y = (int) (x / (double) width * heigh);
            float r = x / (float) width;
            float b = 1 - x / (float) width;
            float green = r * 0.5f;
            g.setColor(new Color(r, b, b));
            g.drawLine(x, 0, x, y);
            g.setColor(new Color(r, green, b));
            g.drawLine(x, y, x, heigh);
        }
        return result;
    }
    
    private void setImage(final BufferedImage image) {
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
        } else {
            canvas.setPreferredSize(new Dimension(50, 100));
        }
        container.add(canvas);
        container.setViewportView(canvas);
        container.setVerticalScrollBarPolicy(
                  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        container.setHorizontalScrollBarPolicy(
                  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        container.repaint();
    }
    
    private void initialiseVisible() {
        setLayout(new BorderLayout());
        add(container, BorderLayout.CENTER);
    }
}
