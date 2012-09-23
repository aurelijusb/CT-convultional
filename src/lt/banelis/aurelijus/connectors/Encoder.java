package lt.banelis.aurelijus.connectors;

import java.awt.FlowLayout;
import java.util.Collection;
import java.util.LinkedList;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Užkodavimui skirta klasė.
 * 
 * Naudojamas Hagelbarger(1959) kodas.
 * 
 * @author Aurelijus Banelis
 */
public class Encoder extends JPanel implements Connector {
    private Boolean[] registers = new Boolean[6];
    private JLabel registersLabel = new JLabel("Registers");
    
    /**
     * Sukūriamas kodavimui skirtas objetas.
     */
    public Encoder() {
        initialiseRegisters();
        initialiseView();
    }

    
    /*
     * Funkcijos skirtos skaičiavimams
     */
    
    /**
     * Užpildomos pradinės registrų reikšmės.
     */
    private void initialiseRegisters() {
        for (int i = 0; i < registers.length; i++) {
           registers[i] = Boolean.FALSE; 
        }
    }
    
    /**
     * Užkoduojamas duomenų (bitų) srautas.
     * 
     * @param data  duomenų srautas
     * @return      užkoduotas duomenų srautas
     */
    @Override
    public Collection<Boolean> transform(Collection<Boolean> data) {
        LinkedList<Boolean> result = new LinkedList<Boolean>();
        for (Boolean bit : data) {
            encode(bit, result);
        }
        return result;
    }
    
    /**
     * Užkoduojamas bitas ir pridedamas į rezultatų sąrašą.
     * 
     * @param bit       duomenų bitas
     * @param result    sąrašas, prie kurio turi būti pridėti užkoduoti bitai
     */
    public void encode(Boolean bit, Collection<Boolean> result) {
        result.add(bit);
        boolean syndrome = bit ^ registers[1] ^ registers[4] ^ registers[5];
        result.add(syndrome);
        moveRegisters(bit);    
    }
    
    /**
     * Išsaugomos (paslenkama) kodatoriaus registrų reikšmės.
     * 
     * Registrų reikšmės pastumiamos, į pirmąjį registrą įrašoma nauja reikšmė.
     * 
     * @param first    pirmojo registro reikšmė.
     */
    private void moveRegisters(Boolean first) {
        for (int i = registers.length - 1; i > 0; i--) {
            registers[i] = registers[i - 1];
        }
        registers[0] = first;
        updateRegistersView();
    }
    
    /**
     * Funkcija skirta pavaizduoti bitus tekstu.
     * 
     * @param bits  bitų masyvas
     * @return      tekstas, kur "1" vaizduoja "true", o "0" - "false" reikšmę
     */
    public static String toDecimals(Boolean[] bits) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (Boolean register : bits) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append(" ");
            }
            if (register) {
                stringBuilder.append("1");
            } else {
                stringBuilder.append("0");
            }
        }
        return stringBuilder.toString();
    }
    
    
    /*
     * Funkcijos skirtos naudotojo sąsajai
     */
    
    /**
     * Paruošaiamas koduotojo pavaizdavimas.
     */
    private void initialiseView() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(registersLabel);
        updateRegistersView();
    }

    /**
     * Atnaujinamas kodatoriaus registrų reikšmes vaizduojantis tekstas.
     */
    private void updateRegistersView() {
        registersLabel.setText("Kodatoriaus registrai: " +
                               toDecimals(registers));
    }
}
