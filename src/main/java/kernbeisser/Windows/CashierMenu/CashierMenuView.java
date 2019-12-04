package kernbeisser.Windows.CashierMenu;

import kernbeisser.DBEntitys.User;
import kernbeisser.Useful.Images;
import kernbeisser.Windows.Background;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

public class CashierMenuView extends Window implements View {

    private CashierMenuController controller;

    /**
     * Creates new form CashierMenu
     */
    public CashierMenuView(User user,Window current){
        super(current);
        controller=new CashierMenuController(user,this);
        initComponents();
        add(new Background(Images.getImage("carrots.png"),this));
        setSize(1070,678);
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

        addItem = new javax.swing.JButton();
        addUser = new javax.swing.JButton();
        addPriceLists = new javax.swing.JButton();
        startCash = new javax.swing.JButton();
        catalogRefresh = new javax.swing.JButton();
        back = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        addItem.setText("Artikel Bearbeiten");
        addItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addItemActionPerformed(evt);
            }
        });

        addUser.setText("Nutzer Bearbeiten");
        addUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addUserActionPerformed(evt);
            }
        });

        addPriceLists.setText("Preislisten Bearbeiten");
        addPriceLists.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPriceListsActionPerformed(evt);
            }
        });

        startCash.setText("Kassieren");
        startCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startCashActionPerformed(evt);
            }
        });

        catalogRefresh.setText("Katalog Aktualiesieren");
        catalogRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                catalogRefreshActionPerformed(evt);
            }
        });

        back.setText("<-Zur\u00fcck");
        back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(back)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(catalogRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                                .addComponent(startCash, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(addUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(addItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(addPriceLists, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)))
                                .addContainerGap(887, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(back)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                                .addComponent(startCash, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(37, 37, 37)
                                .addComponent(catalogRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33)
                                .addComponent(addUser, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36)
                                .addComponent(addItem, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(37, 37, 37)
                                .addComponent(addPriceLists, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(44, 44, 44))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void addItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addItemActionPerformed
        controller.openManageItems();
    }//GEN-LAST:event_addItemActionPerformed

    private void addUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addUserActionPerformed
        controller.openManageUsers();
    }//GEN-LAST:event_addUserActionPerformed

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed
        back();
    }//GEN-LAST:event_backActionPerformed

    private void addPriceListsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPriceListsActionPerformed
        controller.openManagePriceLists();
    }//GEN-LAST:event_addPriceListsActionPerformed

    private void startCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startCashActionPerformed
        controller.openCashierMask();
    }//GEN-LAST:event_startCashActionPerformed

    private void catalogRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_catalogRefreshActionPerformed
        controller.openCatalogInput();
    }//GEN-LAST:event_catalogRefreshActionPerformed

    @Override
    public CashierMenuController getController(){
        return controller;
    }



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addItem;
    private javax.swing.JButton addPriceLists;
    private javax.swing.JButton addUser;
    private javax.swing.JButton back;
    private javax.swing.JButton catalogRefresh;
    private javax.swing.JButton startCash;


    // End of variables declaration//GEN-END:variables
}