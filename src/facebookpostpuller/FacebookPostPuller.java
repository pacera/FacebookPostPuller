/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package facebookpostpuller;

import java.nio.charset.Charset;

/**
 *
 * @author RJ
 */
public class FacebookPostPuller {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FacebookPostPullerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        // TODO code application logic here
        int NUMBER_OF_THREADS = 6;
        
        FacebookPostPullerGUI facebookPostPullerGUI = new FacebookPostPullerGUI(NUMBER_OF_THREADS);
        facebookPostPullerGUI.setVisible(true);
        
        System.out.println("Character Encoding: " + Charset.defaultCharset());
        Preprocess pre = new Preprocess();
        System.out.println(pre.emoji("🍔🍟🍵"));
    }
    
}
