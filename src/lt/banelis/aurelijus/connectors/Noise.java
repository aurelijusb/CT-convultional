package lt.banelis.aurelijus.connectors;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Automatinis kanalo triukšmų generatorius.
 *
 * @author Aurelijus Banelis
 */
public class Noise extends JPanel implements Connector {
    private double noise = 0;
    private Random randomGenerator = new Random();
    private JSlider probabilitySlider = new JSlider();
    private JLabel probabilityLabel = new JLabel("Triukšmai: 0%");
    
    /**
     * Suykuriamas triukšmų kūrimui skirtas objektas
     */
    public Noise() {
        initialiseView();
    }
    
    
    /*
     * Funkcijos skirtos skaičiavimams
     */
    
    /**
     * Iškraipomos duomenų srauto reikšmės.
     * 
     * Iškraipymas atliekamas pagal iškraipimo tikimybę, kuri nustatoma per
     * grafinę naudotojo sąsają.
     * 
     * @param data  pradiniai duomenys.
     * @return      duomenys, kuriuose gali būti triukšmų.
     */
    @Override
    public Collection<Boolean> transform(Collection<Boolean> data) {
        int size = data.size();
        ArrayList<Boolean> result = new ArrayList<Boolean>(size);
        long i = 0;
        for (Boolean bit : data) {
            double random = randomGenerator.nextFloat();
            if (isEnabled() && random <= noise) {
                bit = !bit;
            }
            result.add(bit);
            if (i % 500 == 0) {
                Synchronizer.progress = i / (double) size;
                Synchronizer.updateProgress();
            }
            i++;
        }
        return result;
    }
    
    
    /*
     * Funkcijos skirtos grafinei naudotojo sąsajai.
     */
    
    /**
     * Sukuriami grafiniai elementai skirti triukšmų nustatymui.
     */
    private void initialiseView() {
        setLayout(new BorderLayout());
        probabilitySlider.setValue(0);
        add(probabilityLabel, BorderLayout.WEST);
        add(probabilitySlider, BorderLayout.CENTER);
        setPreferredSize(new Dimension(100, 30));
        probabilitySlider.setPaintLabels(true);
        probabilitySlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                noise = probabilitySlider.getValue() /
                        (double) probabilitySlider.getMaximum();
                String text = Math.round(noise * 100) + "%";
                probabilitySlider.setToolTipText(text);
                if (text.length() < 3) {
                    text = " " + text;
                }
                probabilityLabel.setText("Triukšmai: " + text);
            }
        });
    }

    /**
     * Triukšmų keitimo įjungimas arba išjungimas
     * 
     * @param enabled   ar galima keisti triukšmo lygį
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        probabilitySlider.setEnabled(enabled);
        probabilityLabel.setEnabled(enabled);
    }
}
