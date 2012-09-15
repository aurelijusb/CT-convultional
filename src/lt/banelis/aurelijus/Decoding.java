package lt.banelis.aurelijus;

/**
 *
 * @author Aurelijus Banelis
 */
public class Decoding {
    private Channel channel;
    private Boolean[] registers = new Boolean[6];
    private Boolean[] sumRegisters = new Boolean[6];
    
    public Decoding(Channel channel) {
        this.channel = channel;
        for (int i = 0; i < registers.length; i++) {
           registers[i] = Boolean.FALSE; 
           sumRegisters[i] = Boolean.FALSE;
        }
    }
    
    public Boolean read() {
        Boolean data = channel.retrieve();
        if (data != null) {
            Boolean syndrome = channel.retrieve();

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
    
    public String readAll() {
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
}
