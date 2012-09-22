package lt.banelis.aurelijus;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import lt.banelis.aurelijus.connectors.Decoder;
import lt.banelis.aurelijus.connectors.Encoder;
import lt.banelis.aurelijus.connectors.Noise;
import lt.banelis.aurelijus.data.AbstractDataStructure;
import lt.banelis.aurelijus.data.Bit;
import lt.banelis.aurelijus.data.BitsSteam;
import lt.banelis.aurelijus.data.Image;
import lt.banelis.aurelijus.data.Synchronizer;
import lt.banelis.aurelijus.data.Text;

/**
 * Graphical user interface for Hagelbarger coding and decoding.
 * 
 * @author Aurelijus Banelis
 */
public class Gui extends javax.swing.JFrame {
    private AbstractDataStructure[] senders = {new Bit(true), new Text(true),
                                               new Image(true)};
    private AbstractDataStructure[] receivers = {new BitsSteam(false),
                                                 new Text(false),
                                                 new Image(false)};
    private AbstractDataStructure[] integrityChecks = {new Text(false),
                                                       new Image(false)};
    private AbstractDataStructure currentSender = senders[0];
    private AbstractDataStructure integrityCheck = null;
    private Encoder encoder = new Encoder();
    private AbstractDataStructure inputChannel = new BitsSteam(true);
    private Noise noise = new Noise();
    private AbstractDataStructure outputChannel = new BitsSteam(false);
    private Decoder decoder = new Decoder();
    private AbstractDataStructure currentReceiver = receivers[0];
    private Synchronizer sourceDestination = new Synchronizer(6);
    private JPanel senderCard = new JPanel();
    private JPanel receiverCard = new JPanel();
    private JPanel integrityCard = new JPanel();
    
    public Gui() {
        initComponents();
        initialiseView();
    }
    
    private void initialiseView() {
        /* GUI */
        for (AbstractDataStructure panel : senders) {
            panel.setVisible(false);
            senderCard.add(panel);
        }
        senderCard.setLayout(new CardLayout());
        for (AbstractDataStructure panel : integrityChecks) {
            panel.setVisible(false);
            integrityCard.add(panel);
        }
        integrityCard.setLayout(new CardLayout());
        integrityPanel.setLayout(new BorderLayout());
        integrityPanel.add(integrityCard, BorderLayout.CENTER);
        encoderPanel.add(senderCard, BorderLayout.CENTER);
        encoderPanel.add(encoder, BorderLayout.SOUTH);
        channelPanel.add(inputChannel);
        channelPanel.add(noise);
        channelPanel.add(noiseOptionsPanel);
        channelPanel.add(outputChannel);
        decoderPanel.add(decoder, BorderLayout.NORTH);
        receiverCard.setLayout(new CardLayout());
        for (AbstractDataStructure panel : receivers) {
            panel.setVisible(false);
            receiverCard.add(panel, BorderLayout.CENTER);
        }
        decoderPanel.add(receiverCard, BorderLayout.CENTER);
        updateView();
        keyboardShortcuts();
        updateNoiseOptions();
        
        /* Synchronization and error highlighting */
        for (int i = 0; i < senders.length; i++) {
            sourceDestination.chainDevices(senders[i], receivers[i]);
        }
        Synchronizer channel = new Synchronizer(0);
        channel.chainDevices(inputChannel, outputChannel);
        inputChannel.setHalfSize(true);
        outputChannel.setHalfSize(true);
        
        /* Sending as user inputs data */
        for (AbstractDataStructure sender : senders) {
            sender.setListerer(new Runnable() {
                public void run() {
                    checkIntegrity();
                    encode();
                    if (noiseProbabilityRadio.isSelected()) {
                        transfer();
                        decode();
                    }
                }
            });
        }
    }
    
    private void encode() {
        inputChannel.putData(encoder.transform(currentSender.retrieveData()));
    }
    
    private void checkIntegrity() {
        if (integrityCheck != null) {
            integrityCheck.retrieveData();
            integrityCheck.reset();
            integrityCheck.putData(currentSender.viewAllData());
        }
    }
    
    private void transfer() {
        outputChannel.putData(noise.transform(inputChannel.retrieveData()));
    }
    
    private void decode() {
        currentReceiver.putData(decoder.transform(outputChannel.retrieveData()));
    }

    private void keyboardShortcuts() {
        KeyListener globalKeyListener =  new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if ( bitRadio.isSelected() && currentSender instanceof Bit &&
                     !(e.getComponent() instanceof JTextComponent) ) {
                    Bit bitSender = (Bit) currentSender;
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
    
    private void updateNoiseOptions() {
        noise.setEnabled(noiseProbabilityRadio.isSelected());
        if (inputChannel instanceof BitsSteam) {
            BitsSteam bitsSteam = (BitsSteam) inputChannel;
            bitsSteam.setInputEnabled(noiseManualRadio.isSelected());
        }
        resendButton.setVisible(noiseManualRadio.isSelected());
    }
    
    private void updateView() {
        int index = 0;
        if (textRadio.isSelected()) {
            index = 1;
        } else if (imageRadio.isSelected()) {
            index = 2;
        }
        
        currentSender = senders[index];
        currentReceiver = receivers[index];
        
        for (int i = 0; i < senders.length; i++) {
            if (i == index) {
                senders[i].setVisible(true);
                receivers[i].setVisible(true);
            } else {
                senders[i].setVisible(false);
                receivers[i].setVisible(false);
            }
        }
        
        integrityPanel.setVisible(index != 0);
        if (index != 0) {
            integrityCheck = integrityChecks[index - 1];
            for (int i = 1; i < senders.length; i++) {
                if (i == index) {
                    integrityChecks[i - 1].setVisible(true);
                } else {
                    integrityChecks[i - 1].setVisible(false);
                }
            }
        } else {
            integrityCheck = null;
        }
        resetData();
    }
    
    private void resetData() {
        for (AbstractDataStructure sender : senders) {
            sender.reset();
        }
        for (AbstractDataStructure receiver : receivers) {
            receiver.reset();
        }
        for (AbstractDataStructure check : integrityChecks) {
            check.reset();
        }
        inputChannel.reset();
        outputChannel.reset();
        sourceDestination.reset();
    }
    
    
    /*
     * Autogenerated Swing components layout
     */
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dataRadioGroup = new javax.swing.ButtonGroup();
        noiseOptionsGroup = new javax.swing.ButtonGroup();
        viewPanel = new javax.swing.JPanel();
        bitRadio = new javax.swing.JRadioButton();
        textRadio = new javax.swing.JRadioButton();
        imageRadio = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        resetButton = new javax.swing.JButton();
        encoderPanel = new javax.swing.JPanel();
        integrityPanel = new javax.swing.JPanel();
        channelPanel = new javax.swing.JPanel();
        noiseOptionsPanel = new javax.swing.JPanel();
        noiseProbabilityRadio = new javax.swing.JRadioButton();
        noiseManualRadio = new javax.swing.JRadioButton();
        resendButton = new javax.swing.JButton();
        decoderPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(300, 500));
        setPreferredSize(new java.awt.Dimension(600, 800));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        viewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Duomenų tipas", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        viewPanel.setLayout(new javax.swing.BoxLayout(viewPanel, javax.swing.BoxLayout.LINE_AXIS));

        dataRadioGroup.add(bitRadio);
        bitRadio.setSelected(true);
        bitRadio.setText("Bitas");
        bitRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bitRadioActionPerformed(evt);
            }
        });
        viewPanel.add(bitRadio);

        dataRadioGroup.add(textRadio);
        textRadio.setText("Tekstas");
        textRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textRadioActionPerformed(evt);
            }
        });
        viewPanel.add(textRadio);

        dataRadioGroup.add(imageRadio);
        imageRadio.setText("Paveikslėlis");
        imageRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageRadioActionPerformed(evt);
            }
        });
        viewPanel.add(imageRadio);
        viewPanel.add(jPanel1);

        resetButton.setText("Atsatyti pradinę būseną");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        viewPanel.add(resetButton);

        getContentPane().add(viewPanel);

        encoderPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kodavimas", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        encoderPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(encoderPanel);

        integrityPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Po pavertimo dvejetainiu", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        getContentPane().add(integrityPanel);

        channelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kanalas", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        channelPanel.setLayout(new javax.swing.BoxLayout(channelPanel, javax.swing.BoxLayout.Y_AXIS));

        noiseOptionsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        noiseOptionsGroup.add(noiseProbabilityRadio);
        noiseProbabilityRadio.setSelected(true);
        noiseProbabilityRadio.setText("Triukšmai pagal tikimybę");
        noiseProbabilityRadio.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                noiseProbabilityRadioStateChanged(evt);
            }
        });
        noiseOptionsPanel.add(noiseProbabilityRadio);

        noiseOptionsGroup.add(noiseManualRadio);
        noiseManualRadio.setText("Pasirinktų bitų iškraipymas");
        noiseManualRadio.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                noiseManualRadioStateChanged(evt);
            }
        });
        noiseOptionsPanel.add(noiseManualRadio);

        resendButton.setText("Siųsti kanalu");
        resendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resendButtonActionPerformed(evt);
            }
        });
        noiseOptionsPanel.add(resendButton);

        channelPanel.add(noiseOptionsPanel);

        getContentPane().add(channelPanel);

        decoderPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Dekodavimas", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        decoderPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(decoderPanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void noiseManualRadioStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_noiseManualRadioStateChanged
        updateNoiseOptions();
    }//GEN-LAST:event_noiseManualRadioStateChanged

    private void noiseProbabilityRadioStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_noiseProbabilityRadioStateChanged
        updateNoiseOptions();
    }//GEN-LAST:event_noiseProbabilityRadioStateChanged

    private void resendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resendButtonActionPerformed
        transfer();
        decode();
        inputChannel.repaint();
    }//GEN-LAST:event_resendButtonActionPerformed

    private void bitRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bitRadioActionPerformed
        updateView();
    }//GEN-LAST:event_bitRadioActionPerformed

    private void textRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textRadioActionPerformed
        updateView();
    }//GEN-LAST:event_textRadioActionPerformed

    private void imageRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageRadioActionPerformed
        updateView();
    }//GEN-LAST:event_imageRadioActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        resetData();
    }//GEN-LAST:event_resetButtonActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Gui().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton bitRadio;
    private javax.swing.JPanel channelPanel;
    private javax.swing.ButtonGroup dataRadioGroup;
    private javax.swing.JPanel decoderPanel;
    private javax.swing.JPanel encoderPanel;
    private javax.swing.JRadioButton imageRadio;
    private javax.swing.JPanel integrityPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton noiseManualRadio;
    private javax.swing.ButtonGroup noiseOptionsGroup;
    private javax.swing.JPanel noiseOptionsPanel;
    private javax.swing.JRadioButton noiseProbabilityRadio;
    private javax.swing.JButton resendButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JRadioButton textRadio;
    private javax.swing.JPanel viewPanel;
    // End of variables declaration//GEN-END:variables
}
