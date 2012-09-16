package lt.banelis.aurelijus;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import javax.swing.text.JTextComponent;
import lt.banelis.aurelijus.connectors.Decoder;
import lt.banelis.aurelijus.connectors.Encoder;
import lt.banelis.aurelijus.connectors.Noise;
import lt.banelis.aurelijus.data.AbstractDataStructure;
import lt.banelis.aurelijus.data.Bit;
import lt.banelis.aurelijus.data.BitsSteam;
import lt.banelis.aurelijus.data.Hightlighter;

/**
 * convolutional coding.
 * Hagelbarger
 * feedback decoder
 * 
 * @link http://www.ustudy.in/node/2654
 * 
 * @author Aurelijus Banelis
 */
public class Gui extends javax.swing.JFrame {
    private AbstractDataStructure sender = new Bit(true);
    private Encoder encoder = new Encoder();
    private AbstractDataStructure inputChannel = new BitsSteam(true);
    private Noise noise = new Noise();
    private AbstractDataStructure outputChannel = new BitsSteam(false);
    private Decoder decoder = new Decoder();
    private AbstractDataStructure receiver = new BitsSteam(false);
    
    
    public Gui() {
        initComponents();
        initialiseView();
    }
    
    private void initialiseView() {
        /* GUI */
        encoderPanel.add(sender, BorderLayout.CENTER);
        encoderPanel.add(encoder, BorderLayout.SOUTH);
        channelPanel.add(inputChannel, 0);
        channelPanel.add(noise, 1);
        channelPanel.add(outputChannel, 2);
        decoderPanel.add(decoder, BorderLayout.NORTH);
        decoderPanel.add(receiver, BorderLayout.CENTER);
        keyboardShortcuts();
        
        /* Synchronization and error marking */
        Hightlighter sourceDestination = new Hightlighter(sender, receiver, 6);
        sender.setHighliter(sourceDestination);
        receiver.setHighliter(sourceDestination);
        Hightlighter channel = new Hightlighter(inputChannel, outputChannel);
        inputChannel.setHighliter(channel);
        outputChannel.setHighliter(channel);
        inputChannel.setHalfSize(true);
        outputChannel.setHalfSize(true);
        
        /* Sending as user inputs data */
        sender.setListerer(new Runnable() {
            public void run() {
                encode();
                transfer();
                decode();
            }
        });
    }
    
    private void encode() {
        inputChannel.putData(encoder.transform(sender.retrieveData()));
    }
    
    private void transfer() {
        outputChannel.putData(noise.transform(inputChannel.retrieveData()));
    }
    
    private void decode() {
        receiver.putData(decoder.transform(outputChannel.retrieveData()));
    }
    
    private void keyboardShortcuts() {
        KeyListener globalKeyListener =  new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if ( bitRadio.isSelected() && sender instanceof Bit &&
                     !(e.getComponent() instanceof JTextComponent) ) {
                    Bit bitSender = (Bit) sender;
                    if (e.getKeyChar() == '1') {
                        bitSender.putData(true);
                    } else if (e.getKeyChar() == '0') {
                        bitSender.putData(false);
                    }
                }
            }
        };
        Bit.globalKeyShortcuts(this, globalKeyListener);
    }
    
    /*
     * Registers representation
     */
    
//    private void showEncoderRegisters() {
//        encoderRegistersLabel.setText("Registrai: " +
//                                      toDecimals(encoder.getRegisters()));
//    }
//
//    private void showDecoderRegisters() {
//        decoderRegistersLabel.setText("Registrai: " +
//                                      toDecimals(decoder.getRegisters()) +
//                                      " | " +
//                                      toDecimals(decoder.getSumRegisters()));
//    }
//    
//    private String toDecimals(Boolean[] bits) {
//        StringBuilder stringBuilder = new StringBuilder();
//        boolean first = true;
//        for (Boolean register : bits) {
//            if (first) {
//                first = false;
//            } else {
//                stringBuilder.append(" ");
//            }
//            if (register) {
//                stringBuilder.append("1");
//            } else {
//                stringBuilder.append("0");
//            }
//        }
//        return stringBuilder.toString();
//    }
//    
//    private String markSynchronized(boolean inSynchorinzation,
//                                    JComponent component) {
//        if (inSynchorinzation) {
//            component.setForeground(Color.BLACK);
//            return "Sinchronizuoti";
//        } else {
//            component.setForeground(Color.RED);
//            return "Be pradinių reikšmių";
//        }        
//    }
//
//    private void updateNoiseProbability() {
//        double probability = probabilitySlider.getValue() /
//                             (double) probabilitySlider.getMaximum();
//        channel.setNoise(probability);
//        probabilitySlider.setToolTipText(Math.round(probability * 100) + "%");
//    }
    
    /*
     * Autogenerated Swing components layout
     */
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dataRadioGroup = new javax.swing.ButtonGroup();
        viewPanel = new javax.swing.JPanel();
        bitRadio = new javax.swing.JRadioButton();
        bitsRadio = new javax.swing.JRadioButton();
        textRadio = new javax.swing.JRadioButton();
        imageRadio = new javax.swing.JRadioButton();
        encoderPanel = new javax.swing.JPanel();
        channelPanel = new javax.swing.JPanel();
        decoderPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(300, 500));
        setPreferredSize(new java.awt.Dimension(600, 500));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        viewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Duomenų tipas"));
        viewPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        dataRadioGroup.add(bitRadio);
        bitRadio.setSelected(true);
        bitRadio.setText("Bitas");
        viewPanel.add(bitRadio);

        dataRadioGroup.add(bitsRadio);
        bitsRadio.setText("Dvejetainis žodis");
        viewPanel.add(bitsRadio);

        dataRadioGroup.add(textRadio);
        textRadio.setText("Tekstas");
        viewPanel.add(textRadio);

        dataRadioGroup.add(imageRadio);
        imageRadio.setText("Paveikslėlis");
        viewPanel.add(imageRadio);

        getContentPane().add(viewPanel);

        encoderPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Kodavimas"));
        encoderPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(encoderPanel);

        channelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Kanalas"));
        channelPanel.setLayout(new javax.swing.BoxLayout(channelPanel, javax.swing.BoxLayout.Y_AXIS));
        getContentPane().add(channelPanel);

        decoderPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Dekodavimas"));
        decoderPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(decoderPanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Gui().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton bitRadio;
    private javax.swing.JRadioButton bitsRadio;
    private javax.swing.JPanel channelPanel;
    private javax.swing.ButtonGroup dataRadioGroup;
    private javax.swing.JPanel decoderPanel;
    private javax.swing.JPanel encoderPanel;
    private javax.swing.JRadioButton imageRadio;
    private javax.swing.JRadioButton textRadio;
    private javax.swing.JPanel viewPanel;
    // End of variables declaration//GEN-END:variables
}
