package lt.banelis.aurelijus;

import java.awt.Color;
import javax.swing.JComponent;

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
    private Channel channel = new Channel();
    private Encoder encoder = new Encoder(channel);
    private Decoder decoder = new Decoder(channel);
    private final static Color equal = new Color(128, 255, 128);
    private final static Color different = new Color(255, 128, 128);
    
    public Gui() {
        initComponents();
        showEncoderRegisters();
        showDecoderRegisters();
    }
    
    public final void updateSender() {
        encoder.encode(senderTextField.getText());
        channelTextField.setText(channel.toString());
        updateReceiver();
        showEncoderRegisters();
    }
    
    private void showEncoderRegisters() {
        String synchronised = markSynchronized(encoder.isSynchronized(),
                              encoderRegistersLabel);
        encoderRegistersLabel.setText("Registrai: " +
                                      toDecimals(encoder.getRegisters()) + " " +
                                      synchronised);
    }

    private void showDecoderRegisters() {
        String synchronised = markSynchronized(decoder.isSynchronized(),
                              decoderRegistersLabel);
        decoderRegistersLabel.setText("Registrai: " +
                                      toDecimals(decoder.getRegisters()) +
                                      " | " +
                                      toDecimals(decoder.getSumRegisters()) +
                                      " " +
                                      synchronised);
    }
    
    private String toDecimals(Boolean[] bits) {
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
    
    private String markSynchronized(boolean inSynchorinzation,
                                    JComponent component) {
        if (inSynchorinzation) {
            component.setForeground(Color.BLACK);
            return "Sinchronizuoti";
        } else {
            component.setForeground(Color.RED);
            return "Be pradinių reikšmių";
        }        
    }

    public final void updateChannel() {
        channel.replace(channelTextField.getText());
        updateReceiver();
    }
    
    public final void updateReceiver() {
        receiverTextField.setText(decoder.readAll());
        if (receiverTextField.getText().equals(senderTextField.getText())) {
            receiverTextField.setBackground(equal);
        } else {
            receiverTextField.setBackground(different);
        }
        showDecoderRegisters();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        senderTextField = new javax.swing.JTextField();
        encoderRegistersLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        channelTextField = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        receiverTextField = new javax.swing.JTextField();
        decoderRegistersLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(200, 201));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Kodavimas"));
        jPanel1.setLayout(new java.awt.BorderLayout());

        senderTextField.setText("0");
        senderTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                senderTextFieldKeyReleased(evt);
            }
        });
        jPanel1.add(senderTextField, java.awt.BorderLayout.CENTER);

        encoderRegistersLabel.setText("REG");
        jPanel1.add(encoderRegistersLabel, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Kanalas"));
        jPanel2.setLayout(new java.awt.BorderLayout());

        channelTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                channelTextFieldKeyReleased(evt);
            }
        });
        jPanel2.add(channelTextField, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Dekodavimas"));
        jPanel3.setLayout(new java.awt.BorderLayout());

        receiverTextField.setEditable(false);
        jPanel3.add(receiverTextField, java.awt.BorderLayout.CENTER);

        decoderRegistersLabel.setText("REG");
        jPanel3.add(decoderRegistersLabel, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void senderTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_senderTextFieldKeyReleased
        updateSender();
    }//GEN-LAST:event_senderTextFieldKeyReleased

    private void channelTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_channelTextFieldKeyReleased
        updateChannel();
    }//GEN-LAST:event_channelTextFieldKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Gui().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField channelTextField;
    private javax.swing.JLabel decoderRegistersLabel;
    private javax.swing.JLabel encoderRegistersLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField receiverTextField;
    private javax.swing.JTextField senderTextField;
    // End of variables declaration//GEN-END:variables
}
