/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernbeisser.StartUp;

import kernbeisser.BackGroundWorker;
import kernbeisser.DataImporter;

import javax.persistence.Persistence;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author julik
 */
public class DataSourceSelector extends javax.swing.JFrame {

    /**
     * Creates new form DataSourceWindow
     */
    public DataSourceSelector() {
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public synchronized Map<String,String> getConfiguration(){
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String,String> out = collect();
        try {
            Persistence.createEntityManagerFactory("Kernbeisser", out);
        }catch (Exception e){
            JOptionPane.showMessageDialog(this,"Verbindung kann nicht hergestellt werden!\n"+e.getMessage());
            out = getConfiguration();
        }
        if(importDataset.isSelected()){
            BackGroundWorker.addTask(() -> new DataImporter(new File(datasetLocation.getText())));
        }
        return out;
    }
    private Map<String,String> collect(){
        Map<String,String> out = new HashMap<>();
        out.put("javax.persistence.jdbc.user",username.getText());
        out.put("javax.persistence.jdbc.url",databaseURL.getText());
        out.put("javax.persistence.jdbc.password",new String(password.getPassword()));
        return out;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        finish = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        databaseURL = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        test = new javax.swing.JButton();
        importDataset = new javax.swing.JCheckBox();
        datasetLocation = new javax.swing.JTextField();
        datasetLable = new javax.swing.JLabel();
        search = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        finish.setText("Bestätigen");
        finish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishActionPerformed(evt);
            }
        });

        jLabel1.setText("Database URL");

        databaseURL.setText("jdbc:mysql://localhost/kernbeisser");
        databaseURL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                databaseURLActionPerformed(evt);
            }
        });

        jLabel2.setText("Username");

        username.setText("Application");
        username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameActionPerformed(evt);
            }
        });

        jLabel3.setText("Password");

        password.setText("KB29.06.2019CreateDate");
        password.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordActionPerformed(evt);
            }
        });

        test.setText("Test");
        test.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testActionPerformed(evt);
            }
        });

        importDataset.setText("Import dataset");
        importDataset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importDatasetActionPerformed(evt);
            }
        });

        datasetLocation.setEnabled(false);

        datasetLable.setText("Dataset location");
        datasetLable.setEnabled(false);

        search.setText("search");
        search.setEnabled(false);
        search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(test)
                        .addGap(31, 31, 31)
                        .addComponent(finish))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(databaseURL, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                            .addComponent(username)
                            .addComponent(password))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(datasetLable)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(search, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(datasetLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(importDataset))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(databaseURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(importDataset))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(datasetLable)
                    .addComponent(search))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(datasetLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(finish)
                    .addComponent(test))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void passwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordActionPerformed
        testActionPerformed(null);
    }//GEN-LAST:event_passwordActionPerformed

    private void databaseURLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_databaseURLActionPerformed
        username.requestFocus();
    }//GEN-LAST:event_databaseURLActionPerformed

    private synchronized void finishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finishActionPerformed
        notify();
    }//GEN-LAST:event_finishActionPerformed

    private void testActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testActionPerformed
        try {
            Persistence.createEntityManagerFactory("Kernbeisser", collect());
        }catch (Exception e){
            JOptionPane.showMessageDialog(this,"Verbindung kann nicht hergestellt werden!\n"+e.getMessage(),"Verbindung nicht möglich",JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,"Verbindung wurde erfolgreich hergestellt!");
    }//GEN-LAST:event_testActionPerformed

    private void usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameActionPerformed
        password.requestFocus();
    }//GEN-LAST:event_usernameActionPerformed

    private void importDatasetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importDatasetActionPerformed
        boolean s = importDataset.isSelected();
        datasetLocation.setEnabled(s);
        datasetLable.setEnabled(s);
        search.setEnabled(s);
    }//GEN-LAST:event_importDatasetActionPerformed

    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.addActionListener(e -> {
            File f = fileChooser.getSelectedFile();
            if(f!=null){
                datasetLocation.setText(f.getAbsolutePath());
            }
        });
        fileChooser.showOpenDialog(this);
    }//GEN-LAST:event_searchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField databaseURL;
    private javax.swing.JLabel datasetLable;
    private javax.swing.JTextField datasetLocation;
    private javax.swing.JButton finish;
    private javax.swing.JCheckBox importDataset;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPasswordField password;
    private javax.swing.JToggleButton search;
    private javax.swing.JButton test;
    private javax.swing.JTextField username;
    // End of variables declaration//GEN-END:variables
}
