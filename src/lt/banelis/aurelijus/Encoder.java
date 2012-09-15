package lt.banelis.aurelijus;

/**
 *
 * @author Aurelijus Banelis
 */
public class Encoder {
    private Channel channel;
    private Boolean[] registers = new Boolean[6];
    public static final int synchronisationLenth = 5;
    
    public Encoder(Channel channel) {
        this.channel = channel;
        for (int i = 0; i < registers.length; i++) {
           registers[i] = Boolean.FALSE; 
        }
    }
       
    public void encode(Boolean bit) {
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
}
