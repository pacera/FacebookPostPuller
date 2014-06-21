/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facebookpostpuller;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

/**
 *
 * @author RJ
 */
public class RemoveEntriesGUI extends javax.swing.JFrame {

    private Instances data;

    /**
     * Creates new form RemoveEntriesGUI
     */
    public RemoveEntriesGUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtFacebookName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnDeletePosts = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtOutput = new javax.swing.JTextArea();
        btnLoadArff = new javax.swing.JButton();
        btnSaveArff = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Facebook Name:");

        btnDeletePosts.setText("Delete Posts!");
        btnDeletePosts.setEnabled(false);
        btnDeletePosts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeletePostsActionPerformed(evt);
            }
        });

        txtOutput.setColumns(20);
        txtOutput.setRows(5);
        jScrollPane1.setViewportView(txtOutput);

        btnLoadArff.setText("Load ARFF");
        btnLoadArff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadArffActionPerformed(evt);
            }
        });

        btnSaveArff.setText("Save ARFF");
        btnSaveArff.setEnabled(false);
        btnSaveArff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveArffActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtFacebookName, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnLoadArff, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnDeletePosts, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                            .addComponent(btnSaveArff, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFacebookName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btnDeletePosts))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLoadArff)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSaveArff)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDeletePostsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletePostsActionPerformed
        // TODO add your handling code here:
        String name = txtFacebookName.getText();
        btnDeletePosts.setVisible(false);
        btnSaveArff.setVisible(false);
        btnLoadArff.setVisible(false);
        int ctr = 0;
        for (int i = data.numInstances() - 1; i >= 0; i--) {
            Instance inst = data.instance(i);
            if (inst.stringValue(0).equals(name)) {
                System.out.println("Deleted!");
                data.delete(i);
                ctr++;
            }
        }
        btnDeletePosts.setVisible(true);
        btnSaveArff.setVisible(true);
        btnLoadArff.setVisible(true);
        txtOutput.append(name + ": " + ctr + " posts deleted!\n");
    }//GEN-LAST:event_btnDeletePostsActionPerformed

    private void btnLoadArffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadArffActionPerformed
        // TODO add your handling code here:
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        FileFilter filter = new FileNameExtensionFilter("ARFF File", "arff");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                ArffLoader loader = new ArffLoader();
                loader.setFile(fileChooser.getSelectedFile());
                data = loader.getDataSet();
                btnDeletePosts.setEnabled(true);
                btnSaveArff.setEnabled(true);

            } catch (IOException ex) {
                Logger.getLogger(RemoveEntriesGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnLoadArffActionPerformed

    private void btnSaveArffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveArffActionPerformed
        // TODO add your handling code here:
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        FileFilter filter = new FileNameExtensionFilter("ARFF File", "arff");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(filter);
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                ArffSaver saver = new ArffSaver();
                saver.setInstances(data);
                saver.setFile(new File(fileChooser.getSelectedFile() + ".arff"));
                saver.writeBatch();
            } catch (IOException ex) {
                Logger.getLogger(RemoveEntriesGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnSaveArffActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeletePosts;
    private javax.swing.JButton btnLoadArff;
    private javax.swing.JButton btnSaveArff;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtFacebookName;
    private javax.swing.JTextArea txtOutput;
    // End of variables declaration//GEN-END:variables
}