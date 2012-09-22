package lt.banelis.aurelijus.connectors;

import java.awt.FlowLayout;
import java.util.Collection;
import java.util.LinkedList;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Encoder, which use Hagelbarger code.
 * 
 * @author Aurelijus Banelis
 */
public class Encoder extends JPanel implements Connector {
    private Boolean[] registers = new Boolean[6];
    private JLabel registersLabel = new JLabel("Registers");
    
    public Encoder() {
        initialiseRegisters();
        initialiseView();
    }

    
    /*
     * Calculating
     */
    
    private void initialiseRegisters() {
        for (int i = 0; i < registers.length; i++) {
           registers[i] = Boolean.FALSE; 
        }
    }
    
    @Override
    public Collection<Boolean> transform(Collection<Boolean> data) {
        LinkedList<Boolean> result = new LinkedList<Boolean>();
        for (Boolean bit : data) {
            encode(bit, result);
        }
        return result;
    }
    
    public void encode(Boolean bit, Collection<Boolean> result) {
        result.add(bit);
        boolean syndrome = bit ^ registers[1] ^ registers[4] ^ registers[5];
        result.add(syndrome);
        moveRegisters(bit);    
    }
    
    private void moveRegisters(Boolean first) {
        for (int i = registers.length - 1; i > 0; i--) {
            registers[i] = registers[i - 1];
        }
        registers[0] = first;
        updateRegistersView();
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
        registersLabel.setText("Kodatoriaus registrai: " +
                               toDecimals(registers));
    }
    
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
}
