package lt.banelis.aurelijus;

import java.util.LinkedList;

/**
 *
 * @author Aurelijus Banelis
 */
public class Decoder {
    private Channel channel;
    private Boolean[] registers = new Boolean[6];
    private Boolean[] sumRegisters = new Boolean[6];
    private boolean inSynchronization = false;
    
    public Decoder(Channel channel) {
        this.channel = channel;
        for (int i = 0; i < registers.length; i++) {
           registers[i] = Boolean.FALSE; 
           sumRegisters[i] = Boolean.FALSE;
        }
    }
    
    public Boolean read() {
        if (!inSynchronization) {
            synchroniseRegisters();
            inSynchronization = true;
        }
        return readBit();
    }
    
    private Boolean readBit() {
        Boolean data = channel.retrieve();
        Boolean syndrome = channel.retrieve();
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
            registers[0] = syndrome;
            for (int i = sumRegisters.length - 1; i > 0; i--) {
                sumRegisters[i] = sumRegisters[i - 1];
            }
            sumRegisters[0] = sum1;
            
            //FIXME: error corecting
//            return sumOut;
            return data;
        } else {
            return null;
        }
    }

    private void synchroniseRegisters() {
        for (int i = 0; i < registers.length; i++) {
            readBit();
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
    
    public Iterable<Boolean> readAll() {
        LinkedList<Boolean> all = new LinkedList<Boolean>();
        Boolean bit;
        while ((bit = read()) != null) {
            all.add(bit);
        }
        return all;
    }
    
    /**
     * @depracated use GUI
     */
    public String readToString() {
        StringBuilder result = new StringBuilder();
        Boolean bit;
        while ((bit = read()) != null) {
            if (bit.booleanValue()) {
                result.append("1");
            } else {
                result.append("0");
            }
        }
        return result.toString();
    }

    public Boolean[] getRegisters() {
        return registers;
    }

    public Boolean[] getSumRegisters() {
        return sumRegisters;
    }

    public boolean isSynchronized() {
        return inSynchronization;
    }
    
    
}
