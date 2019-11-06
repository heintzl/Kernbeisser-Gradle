/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernbeisser.Windows;

import kernbeisser.*;
import kernbeisser.CustomComponents.Column;
import kernbeisser.CustomComponents.ObjectTable;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.*;
import java.util.Collection;

/**
 *
 * @author julik
 */
public class Pay extends javax.swing.JFrame {
    private Collection<ShoppingItem> items;
    private SaleSession session;
    /**
     * Creates new form Pay
     */
    public Pay(SaleSession s, Collection<ShoppingItem> items, Runnable finish) {
        initComponents();
        session = s;
        this.items=items;
        UserGroup cug = s.getCustomer().getUserGroup();
        userGroup.setText("Konto: "+Tools.toSting(cug.getMembers(),(e)->e.getFirstName()+","));
        userGroupValue.setText("Guthaben: "+cug.getValue()/100f+"\u20AC");
        ObjectTable<ShoppingItem> itemTable = new ObjectTable<>(
                Column.create("Name", ShoppingItem::getName),
                Column.create("Anzahl", ShoppingItem::getItemAmount),
                Column.create("Preis", e-> e.getRawPrice()/100f+"\u20AC"+(e.getDiscount()==0?"":(" ("+e.getDiscount()+"% Rabatt)")))
        );
        itemTable.addAll(items);
        itemPane.add(new JScrollPane(itemTable));
        int total = 0;
        for (ShoppingItem item : items) {
            total+=item.getRawPrice();
        }
        totalPrice.setText("Total: "+total/100f+"\u20AC");
        setVisible(true);
        setLocationRelativeTo(null);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        userGroup = new javax.swing.JLabel();
        userGroupValue = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        printBonYes = new javax.swing.JCheckBox();
        printBonNo = new javax.swing.JCheckBox();
        fontSize = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        paperFomat = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        payButton = new javax.swing.JButton();
        totalPrice = new javax.swing.JLabel();
        itemPane = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Abrechnung");

        userGroup.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        userGroup.setText("Konto:");

        userGroupValue.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        userGroupValue.setText("Guthaben:");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Bon"));

        jLabel4.setText("Bon drucken:");

        printBonYes.setText("Ja");

        printBonNo.setText("Nein");

        jLabel5.setText("Schriftgröße");

        paperFomat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setText("Papierformat:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(printBonYes)
                        .addGap(33, 33, 33)
                        .addComponent(printBonNo))
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(paperFomat, 0, 153, Short.MAX_VALUE)
                    .addComponent(fontSize))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(printBonYes)
                    .addComponent(printBonNo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(paperFomat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fontSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        payButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        payButton.setForeground(new java.awt.Color(0, 153, 0));
        payButton.setText("Bezahlen");
        payButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payButtonActionPerformed(evt);
            }
        });

        totalPrice.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        totalPrice.setText("Toltal:");

        itemPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Artikel", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        itemPane.setLayout(new java.awt.GridLayout(1, 1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(userGroup)
                    .addComponent(userGroupValue)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalPrice)
                    .addComponent(payButton))
                .addGap(18, 18, 18)
                .addComponent(itemPane, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(itemPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(13, 13, 13)
                        .addComponent(userGroup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(userGroupValue)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(totalPrice)
                        .addGap(18, 18, 18)
                        .addComponent(payButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void payButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payButtonActionPerformed
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.persist(session);
        Purchase purchase = new Purchase();
        purchase.setSession(session);
        em.persist(purchase);
        for (ShoppingItem item : items) {
            item.setPurchase(purchase);
            em.persist(item);
        }
        em.flush();
        et.commit();
        em.close();
    }//GEN-LAST:event_payButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner fontSize;
    private javax.swing.JPanel itemPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox<String> paperFomat;
    private javax.swing.JButton payButton;
    private javax.swing.JCheckBox printBonNo;
    private javax.swing.JCheckBox printBonYes;
    private javax.swing.JLabel totalPrice;
    private javax.swing.JLabel userGroup;
    private javax.swing.JLabel userGroupValue;
    // End of variables declaration//GEN-END:variables
}
