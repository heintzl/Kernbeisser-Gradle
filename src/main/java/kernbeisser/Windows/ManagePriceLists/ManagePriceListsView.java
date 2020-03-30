/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernbeisser.Windows.ManagePriceLists;

import kernbeisser.CustomComponents.PermissionButton;
import kernbeisser.CustomComponents.PriceListTree;
import kernbeisser.CustomComponents.TextFields.PermissionField;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.swing.*;

public class ManagePriceListsView extends Window implements View {

    //TODO back or commit button can be deleted, they should have the same function
    private PermissionField priceListName;
    private PriceListTree priceListTree;
    private kernbeisser.CustomComponents.PermissionButton add;
    private kernbeisser.CustomComponents.PermissionButton edit;
    private JButton back;
    private JButton commit;
    private PermissionField superPriceListName;
    private JPanel main;
    private PermissionButton delete;

    private ManagePriceListsController controller;

    public ManagePriceListsView(Window current,ManagePriceListsController controller) {
        //TODO Benjamin is working currently on this project
        super(current /*,Key.ACTION_OPEN_MANAGE_PRICELISTS*/);
        this.controller = controller;
        add.setRequiredWriteKeys(Key.ACTION_ADD_PRICELIST,Key.PRICELIST_NAME_WRITE);
        delete.setRequiredWriteKeys(Key.ACTION_DELETE_PRICELIST);
        edit.setRequiredWriteKeys(Key.ACTION_EDIT_PRICELIST,Key.PRICELIST_NAME_WRITE,Key.PRICELIST_SUPER_PRICE_LIST_WRITE);
        priceListName.setRequiredWriteKeys(Key.PRICELIST_NAME_WRITE);
        priceListTree.addSelectionListener(e -> controller.displayCurrentSuperPriceList());
        add(main);
        add.addActionListener(e -> controller.saveAction());
        edit.addActionListener(e -> controller.renameAction());
        delete.addActionListener(e -> controller.deleteAction());
        back.addActionListener(e -> controller.back());
        commit.addActionListener((e -> controller.back()));
        windowInitialized();
    }

    PriceList getSelectedPriceList(){
        return priceListTree.getSelected();
    }

    String getPriceListName(){
        return priceListName.getText();
    }

    //If you want to support SuperPriceList editing
    String getSuperPriceListName(){
        return superPriceListName.getText();
    }

    //If you want to support SuperPriceList editing
    void setSuperPriceListNameEnable(boolean b){
        superPriceListName.setEnabled(b);
    }

    void setPriceListName(String s){
        priceListName.setText(s);
    }

    void setSuperPriceListName(String s){
        superPriceListName.setText(s);
    }


    private void createUIComponents() {
        priceListTree = new PriceListTree();
    }

    public PriceListTree getPriceListTree() {
        return priceListTree;
    }

    //TODO: Look for methods that you need for the model

    /*

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     *
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new JScrollPane();
        priceListChooser = new JTree();
        superPriceList = new JTextField();
        jLabel1 = new JLabel();
        save = new JButton();
        jLabel4 = new JLabel();
        esc = new JButton();
        rename = new JButton();
        delete = new JButton();
        jLabel2 = new JLabel();
        priceListName = new JTextField();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        jScrollPane1.setViewportView(priceListChooser);

        jLabel1.setText("Ordner");

        save.setText("Speichern");
        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel4.setText("Preislisten Hinzuf\u00fcgen");

        esc.setText("Abbrechen");
        esc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                escActionPerformed(evt);
            }
        });

        rename.setText("Umbenenen");
        rename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameActionPerformed(evt);
            }
        });

        delete.setText("L\u00f6schen");
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });

        jLabel2.setText("Preislistenname");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                      .addComponent(jLabel4, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                      .addGroup(layout.createSequentialGroup()
                                      .addGap(38, 38, 38)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                      .addGroup(layout.createSequentialGroup()
                                                                      .addComponent(rename)
                                                                      .addGap(18, 18, 18)
                                                                      .addComponent(delete)
                                                                      .addPreferredGap(
                                                                              LayoutStyle.ComponentPlacement.RELATED,
                                                                              204, Short.MAX_VALUE)
                                                                      .addComponent(esc)
                                                                      .addGap(33, 33, 33)
                                                                      .addComponent(save))
                                                      .addGroup(layout.createSequentialGroup()
                                                                      .addComponent(jScrollPane1,
                                                                                    GroupLayout.PREFERRED_SIZE, 233,
                                                                                    GroupLayout.PREFERRED_SIZE)
                                                                      .addGap(18, 18, 18)
                                                                      .addGroup(layout.createParallelGroup(
                                                                              GroupLayout.Alignment.LEADING, false)
                                                                                      .addComponent(jLabel1)
                                                                                      .addComponent(superPriceList,
                                                                                                    GroupLayout.DEFAULT_SIZE,
                                                                                                    314,
                                                                                                    Short.MAX_VALUE)
                                                                                      .addComponent(jLabel2)
                                                                                      .addComponent(priceListName))))
                                      .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                      .addGroup(layout.createSequentialGroup()
                                      .addGap(8, 8, 8)
                                      .addComponent(jLabel4)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                      .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 422,
                                                                    GroupLayout.PREFERRED_SIZE)
                                                      .addGroup(layout.createSequentialGroup()
                                                                      .addComponent(jLabel1)
                                                                      .addPreferredGap(
                                                                              LayoutStyle.ComponentPlacement.UNRELATED)
                                                                      .addComponent(superPriceList,
                                                                                    GroupLayout.PREFERRED_SIZE,
                                                                                    GroupLayout.DEFAULT_SIZE,
                                                                                    GroupLayout.PREFERRED_SIZE)
                                                                      .addGap(16, 16, 16)
                                                                      .addComponent(jLabel2)
                                                                      .addPreferredGap(
                                                                              LayoutStyle.ComponentPlacement.RELATED)
                                                                      .addComponent(priceListName,
                                                                                    GroupLayout.PREFERRED_SIZE,
                                                                                    GroupLayout.DEFAULT_SIZE,
                                                                                    GroupLayout.PREFERRED_SIZE)))
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                      .addComponent(rename)
                                                      .addComponent(save)
                                                      .addComponent(esc)
                                                      .addComponent(delete))
                                      .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
        String pln = priceListName.getText();
        if (pln.equals("")) {
            JOptionPane.showMessageDialog(this, "Bitte w\u00e4hlen sie einen korrekten Namen");
            return;
        }
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        PriceList spl;
        try {
            spl = em.createQuery("select p from PriceList p where name like '" + superPriceList.getText() + "'",
                                 PriceList.class).getSingleResult();
        } catch (NoResultException ignored) {
            spl = null;
        }
        PriceList pl = new PriceList();
        pl.setSuperPriceList(spl);
        pl.setName(pln);
        et.begin();
        em.persist(pl);
        em.flush();
        et.commit();
        em.close();
        priceListChooser.setModel(new PriceListTree().getModel());
    }//GEN-LAST:event_saveActionPerformed





    private void escActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_escActionPerformed
        back();
        dispose();
    }//GEN-LAST:event_escActionPerformed

    private void renameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameActionPerformed
        Object o = priceListChooser.getLastSelectedPathComponent();
        if (o == null) {
            return;
        }
        EntityManager em = DBConnection.getEntityManager();
        PriceList pl = em.createQuery("select p from PriceList p where name like " + o.toString(), PriceList.class)
                         .getSingleResult();
        EntityTransaction et = em.getTransaction();
        et.begin();
        String newName = JOptionPane.showInputDialog("Bitte neuen Namen eingeben:");
        if (newName == null) {
            return;
        }
        pl.setName(newName);
        em.unwrap(Session.class).update(pl);
        et.commit();
        em.close();
        priceListChooser.setModel(new PriceListTree(false).getModel());
    }//GEN-LAST:event_renameActionPerformed

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        Object o = priceListChooser.getLastSelectedPathComponent();
        if (o == null) {
            return;
        }
        EntityManager em = DBConnection.getEntityManager();
        if (JOptionPane.showConfirmDialog(this,
                                          "Soll die Preisliste " + priceListName.getText() + " wirklich gel\u00f6scht werden") == 0) {
            em.createQuery("delete from PriceList p where name like " + priceListName.getText()).executeUpdate();
        } else {
            return;
        }
        priceListChooser.setModel(new PriceListTree(false).getModel());
    }//GEN-LAST:event_deleteActionPerformed
*/


    // End of variables declaration//GEN-END:variables
}
