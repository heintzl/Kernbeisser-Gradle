/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernbeisser.Windows;

import kernbeisser.DBConnection;
import kernbeisser.CustomComponents.DBTable;
import kernbeisser.User;
import kernbeisser.UserGroup;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 *
 * @author julik
 */
public class UserGroupSelector extends javax.swing.JFrame {
    private DBTable userTable;
    private DBTable groupMembersTable;
    private UserGroup newUserGroup;
    private Consumer<UserGroup> finchAction;
    /**
     * Creates new form UserGroupSelector
     */
    public UserGroupSelector(Consumer<UserGroup> f) {
        initComponents();
        finchAction=f;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        try {
            userTable= new DBTable("select u from User u",
                    500,
                    User.class.getDeclaredField("username"),
                    User.class.getDeclaredField("firstName"),
                    User.class.getDeclaredField("surname"));
            groupMembersTable=new DBTable("",500,
                    User.class.getDeclaredField("username"),
                    User.class.getDeclaredField("firstName"),
                    User.class.getDeclaredField("surname"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                EntityManager em = DBConnection.getEntityManager();
                selectUser(em.createQuery("select u from User u where username = '"+userTable.getValueAt(userTable.getSelectedRow(),0)+"'",User.class).getSingleResult());
            }
        });
        searchPanel.add(new JScrollPane(userTable));
        groupMembers.add(new JScrollPane(groupMembersTable));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private void selectUser(User user){
        newUserGroup = user.getUserGroup();
        enterGroup.setText("Der Gruppe von "+user.getFirstName()+" Beitreten");
        groupMembersTable.setQuery("select u from User u where userGroup.id = "+newUserGroup.getId());
        groupMembersTable.refresh();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        searchBar = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        searchPanel = new javax.swing.JPanel();
        groupMembers = new javax.swing.JPanel();
        enterGroup = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        searchBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBarActionPerformed(evt);
            }
        });

        jLabel1.setText("Mitgliedsuchen");

        searchPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        searchPanel.setLayout(new java.awt.GridLayout(1, 1));

        groupMembers.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Mitglieder", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        groupMembers.setLayout(new java.awt.GridLayout(1, 0));

        enterGroup.setText("Gruppe von x Beitreten");
        enterGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterGroupActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Mitgliedergruppe ausw\u00e4hlen");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(searchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(searchBar, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(groupMembers, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(112, 112, 112)
                                .addComponent(enterGroup))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(112, 112, 112)
                        .addComponent(jLabel2)))
                .addGap(34, 34, 34))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(groupMembers, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
                        .addGap(15, 15, 15)
                        .addComponent(enterGroup))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(searchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    public void waitSelection(){
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void searchBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBarActionPerformed
        userTable.setQuery("select u from User u where firstName like '%"+searchBar.getText()+"%' or surname like '%"+searchBar.getText()+"%' ORDER BY firstName asc");
        userTable.refresh();
    }//GEN-LAST:event_searchBarActionPerformed
    private void enterGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterGroupActionPerformed
        finchAction.accept(newUserGroup);
        dispose();
    }//GEN-LAST:event_enterGroupActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton enterGroup;
    private javax.swing.JPanel groupMembers;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField searchBar;
    private javax.swing.JPanel searchPanel;
    // End of variables declaration//GEN-END:variables
}
