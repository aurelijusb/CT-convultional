package lt.banelis.aurelijus;

/**
 *
 * @author Aurelijus Banelis
 */
public class Encoder {
    private Channel channel;
    private Boolean[] registers = new Boolean[6];
    private boolean inSynchronization = false;
    
    public Encoder(Channel channel) {
        this.channel = channel;
        for (int i = 0; i < registers.length; i++) {
           registers[i] = Boolean.FALSE; 
        }
    }
    
    public void encode(String binary) {
        for (byte b : binary.getBytes()) {
            if (b == '1') {
                encode(Boolean.TRUE);
            } else if (b == '0') {
                encode(Boolean.FALSE);
            } else {
                //TODO: error
            }
        }
    }
    
    public void encode(Boolean bit) {
        if (!inSynchronization) {
            synchroniseRegisters();
            inSynchronization = true;
        }
        encodeBit(bit);
    }

    private void synchroniseRegisters() {
        for (int i = 0; i < registers.length; i++) {
            encodeBit(Boolean.FALSE);
        }
    }
    
    private void encodeBit(Boolean bit) {
        channel.put(bit);
        boolean syndrome = bit ^ registers[1] ^ registers[4] ^ registers[5];
        channel.put(syndrome);
        moveRegisters(bit);        
    }
    
    private void moveRegisters(Boolean first) {
        for (int i = registers.length - 1; i > 0; i--) {
            registers[i] = registers[i - 1];
        }
        registers[0] = first;
    }
    
    public Boolean[] getRegisters() {
        return registers;
    }

    public boolean isSynchronized() {
        return inSynchronization;
    }
}
