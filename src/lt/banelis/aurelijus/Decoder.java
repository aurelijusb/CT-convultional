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
    public static final int synchronisationLenth = Encoder.synchronisationLenth;
    private int received = 0;
    
    public Decoder(Channel channel) {
        this.channel = channel;
        for (int i = 0; i < registers.length; i++) {
           registers[i] = Boolean.FALSE; 
           sumRegisters[i] = Boolean.FALSE;
        }
    }

    public Boolean read() {
        Boolean data = channel.retrieve();
        Boolean syndrome = channel.retrieve();        
        return read(data, syndrome);
    }
    
    private Boolean read(Boolean data, Boolean syndrome) {
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
    
    public Iterable<Boolean> readAll() {
        LinkedList<Boolean> all = new LinkedList<Boolean>();
        Boolean bit;
        while ((bit = read()) != null) {
            if (received > synchronisationLenth) {
                all.add(bit);
            } else {
                all.add(null);
            }
            received++;
        }
        return all;
    }
    
    public Iterable<Boolean> resetSynchronisationCounter() {
        LinkedList<Boolean> all = new LinkedList<Boolean>();
        for (int i = 0; i < synchronisationLenth; i++) {
            all.add(read(Boolean.FALSE, Boolean.FALSE));
        }
        received = 0;
        return all;
    }
    
    /**
     * @deprecated use GUI
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
}
