package lt.banelis.aurelijus.connectors;

import java.awt.FlowLayout;
import java.util.Collection;
import java.util.LinkedList;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Decoder, which use Hagelbarger code.
 *
 * @author Aurelijus Banelis
 */
public class Decoder extends JPanel implements Connector {
    private Boolean[] registers = new Boolean[6];
    private Boolean[] sumRegisters = new Boolean[6];
    private JLabel registersLabel = new JLabel("Registrai");
    
    public Decoder() {
        initialiseRegisters();
        initialiseView();
    }
    
    
    /*
     * Calculations
     */
    
    private void initialiseRegisters() {
        for (int i = 0; i < registers.length; i++) {
           registers[i] = Boolean.FALSE; 
           sumRegisters[i] = Boolean.FALSE;
        }
    }

    @Override
    public Collection<Boolean> transform(Collection<Boolean> data) {
        LinkedList<Boolean> list = new LinkedList<Boolean>();
        Boolean dataBit = null;
        int i = 0;
        for (Boolean bit : data) {
            if (i % 2 == 0) {
                dataBit = bit;
            } else {
                list.add(decode(dataBit, bit));
            }
            i++;
        }
        updateRegistersView();
        return list;
    }

    private Boolean decode(Boolean data, Boolean syndrome) {
        
        /* Variable and state diagram relation:
         *
         * data->------+-- registers ------- sumOut --> 
         *             |  ///                 / 
         * syndrome->-sum1          _________/
         *             |         ///        / 
         *              \--- sumRegisters  /
         *               \     ///        /
         *                \__ mde _______/
         */
        if (data != null && syndrome != null) {
            /* Calculating output */
            Boolean sum1 = data ^ syndrome ^ registers[1] ^ registers[4] ^
                           registers[5];
            Boolean mde = mde(new Boolean[] {sum1, sumRegisters[0],
                              sumRegisters[3], sumRegisters[5]});
            Boolean sumOut = registers[5] ^ mde;

            /* Saving registers */
            for (int i = registers.length - 1; i > 0; i--) {
                registers[i] = registers[i - 1];
            }
            registers[0] = data;
            sumRegisters[5] = sumRegisters[4];
            sumRegisters[4] = sumRegisters[3] ^ mde;
            sumRegisters[3] = sumRegisters[2];
            sumRegisters[2] = sumRegisters[1];
            sumRegisters[1] = sumRegisters[0] ^ mde;
            sumRegisters[0] = sum1 ^ mde;
            return sumOut;
        } else {
            return null;
        }
    }

    private Boolean mde(Boolean[] bits) {
        int ones = 0;
        int zeros = 0;
        for (Boolean bit : bits) {
            if (bit.booleanValue()) {
                ones++;
            } else {
                zeros++;
            }
        }
        if (ones > zeros) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
 
    
    /*
     * Graphical user interface
     */
    
    private void initialiseView() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(registersLabel);
        updateRegistersView();
    }
    
    private void updateRegistersView() {
        registersLabel.setText("Dekodatoriaus registrai: " +
                               Encoder.toDecimals(registers) + " | " +
                               Encoder.toDecimals(sumRegisters));
    }
}
