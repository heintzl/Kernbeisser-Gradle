/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernbeisser.Windows.CatalogInput;

import kernbeisser.Exeptions.FileReadException;
import kernbeisser.Exeptions.ObjectParseException;
import kernbeisser.Windows.*;

import javax.swing.*;

/**
 *
 * @author julik
 */
public class CatalogInputView extends Window implements View {
    CatalogInputController controller;
    /**
     * Creates new form CatalogInput
     */
    public CatalogInputView(Window current) {
        super(current);
        controller=new CatalogInputController(this);
        initComponents();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public CatalogInputController getController() {
        return controller;
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        importText = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        data = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        importFile = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        importText.setText("Daten aus dem Textfeld importieren");
        importText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importTextActionPerformed(evt);
            }
        });

        data.setColumns(20);
        data.setRows(5);
        jScrollPane1.setViewportView(data);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Katalog Aktualiesieren");

        importFile.setText("Daten aus einer Datei importiern");
        importFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(importFile)
                .addGap(34, 34, 34)
                .addComponent(importText)
                .addGap(18, 18, 18))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 833, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(361, 361, 361)
                        .addComponent(jLabel1)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(11, 11, 11)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(importText)
                    .addComponent(importFile))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void importTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importTextActionPerformed
        try {
            if(controller.importData(data.getText())){
                JOptionPane.showMessageDialog(this,"Der Katalog wurde erfolgreich aktualiesiert!");
            }else {
                JOptionPane.showMessageDialog(this,"Der Katalog konnte nicht eingelesen werden!","Fehler beim Einlesen des Kataloges",JOptionPane.ERROR_MESSAGE);
            }
        } catch (ObjectParseException e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }//GEN-LAST:event_importTextActionPerformed

    private void importFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importFileActionPerformed
            JFileChooser jFileChooser = new JFileChooser();
            if (jFileChooser.showOpenDialog(this)== JFileChooser.APPROVE_OPTION) {
                try {
                    if(controller.importData(jFileChooser.getSelectedFile())){
                        JOptionPane.showMessageDialog(this,"Der Katalog wurde erfolgreich aktualiesiert!");
                    }else {
                        JOptionPane.showMessageDialog(this,"Der Katalog konnte nicht eingelesen werden!","Fehler beim Einlesen des Kataloges",JOptionPane.ERROR_MESSAGE);
                    }
                } catch (FileReadException | ObjectParseException e) {
                    JOptionPane.showMessageDialog(this,e.getMessage());
                }
            }

    }//GEN-LAST:event_importFileActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea data;
    private javax.swing.JButton importFile;
    private javax.swing.JButton importText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;

    // End of variables declaration//GEN-END:variables
}
