/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernbeisser.Windows.ManageUser;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.CustomComponents.Column;
import kernbeisser.CustomComponents.DBTable;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.Job;
import kernbeisser.DBEntitys.User;
import kernbeisser.DBEntitys.UserGroup;
import kernbeisser.Enums.Permission;
import kernbeisser.Useful.Tools;
import kernbeisser.Useful.Translator;
import kernbeisser.Windows.Finishable;
import kernbeisser.Windows.Finisher;
import kernbeisser.Windows.JobSelector;
import kernbeisser.Windows.UserGroupSelector.UserGroupSelector;
import kernbeisser.Windows.Window;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author julik
 */
public class ManageUser extends Window {
    private UserGroup userGroup;
    private Set<Job> jobs = new HashSet<>();
    private DBTable<User> userSelector;
    private Translator t = new Translator();
    /**
     * Creates new form ManageUser
     */
    public ManageUser(Window current,Permission permission) {
        super(current);
        initComponents();
        editUser.setEnabled(true);
        userSelector = new DBTable<User>(
                "select u from User u",
                Column.create("Username", User::getUsername),
                Column.create("Vorname", User::getFirstName),
                Column.create("Nachname", User::getSurname)
        );
        userSelector.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        EntityManager em = DBConnection.getEntityManager();
                        paste(em.createQuery(
                                "select u from User u where u.username like '"
                                        +userSelector.getValueAt(userSelector.getSelectedRow(),0)+
                                        "'",User.class)
                                .getSingleResult());
                    }
                }
        );
        JScrollPane jScrollPane = new JScrollPane(userSelector);
        jScrollPane.setBounds(0,0,getWidth(),getHeight());
        tableContainer.addTab("All",jScrollPane);
        setLocationRelativeTo(null);
        switch (permission){
            case ADMIN:
                this.permission.addItem(t.translate(Permission.ADMIN));
            case MONEY_MANAGER:
                this.permission.addItem(t.translate(Permission.MONEY_MANAGER));
            case MANAGER:
                this.permission.addItem(t.translate(Permission.MANAGER));
                this.permission.addItem(t.translate(Permission.STANDARD));
                this.permission.addItem(t.translate(Permission.BEGINNER));
                break;
            case BEGINNER:
            case STANDARD:
                kill();
                JOptionPane.showMessageDialog(this,"Sie haben leider kein zugriff auf dieses fenster");
                return;
        }
        setVisible(true);
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
        fistName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        surname = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        phoneNumber1 = new javax.swing.JTextField();
        phoneNumber2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jobsSelector = new javax.swing.JButton();
        extraJobs = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        address = new javax.swing.JTextField();
        email = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        back = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        street = new javax.swing.JTextField();
        shares = new javax.swing.JSpinner();
        solidaritySurcharge = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        permission = new javax.swing.JComboBox<>();
        kernbeisserKey = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        employee = new javax.swing.JCheckBox();
        addUser = new javax.swing.JButton();
        editUser = new javax.swing.JButton();
        password = new javax.swing.JPasswordField();
        username = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        tableContainer = new javax.swing.JTabbedPane();
        alone = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        enterUserGroup = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Vorname");

        jLabel2.setText("Nachname");

        jLabel3.setText("Telefonnummer 1");

        jLabel4.setText("Telefonummer 2");

        jobsSelector.setText("Dienste");
        jobsSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jobsSelectorActionPerformed(evt);
            }
        });

        jLabel7.setText("Zusatzdienste");

        jLabel8.setText("Addresse");

        jLabel9.setText("Email");

        back.setText("<-Zur\u00fcck");
        back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backActionPerformed(evt);
            }
        });

        jLabel10.setText("Straße");

        jLabel11.setText("Anteile");

        jLabel12.setText("Solidarzuschlag");

        kernbeisserKey.setText("Schl\u00fcssel");

        jLabel13.setText("Berechtigung");

        employee.setText("Mitarbeiter");

        addUser.setText("Nutzer Erstellen");
        addUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addUserActionPerformed(evt);
            }
        });

        editUser.setText("Nutzer Bearbeiten");
        editUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editUserActionPerformed(evt);
            }
        });

        jLabel14.setText("Nutzername");

        jLabel15.setText("Password");

        alone.setText("Alleine");

        jLabel5.setText("Gemeinschaft");

        enterUserGroup.setText("Beiteten");
        enterUserGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterUserGroupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(back)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addComponent(username)
                                                                        .addGap(32, 32, 32))
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(jobsSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(permission, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(jLabel13)
                                                                                .addComponent(addUser, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(jLabel14))
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(jLabel15)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(password)
                                                                        .addComponent(extraJobs)
                                                                        .addComponent(jLabel7)
                                                                        .addComponent(editUser, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))))
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                .addComponent(fistName)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel3)
                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                                .addComponent(phoneNumber1, javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(email, javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addGap(8, 8, 8))
                                                                                .addComponent(shares, javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(street, javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                        .addComponent(jLabel10)
                                                                                        .addGap(92, 92, 92)))))
                                                        .addGap(32, 32, 32)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                                                .addComponent(address, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                        .addGap(35, 35, 35)
                                                                                                        .addComponent(jLabel8)))
                                                                                        .addComponent(phoneNumber2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(jLabel4)
                                                                                        .addComponent(enterUserGroup))
                                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                                        .addComponent(solidaritySurcharge, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(jLabel12)
                                                                                        .addComponent(kernbeisserKey)
                                                                                        .addComponent(employee)))
                                                                        .addComponent(jLabel2))
                                                                .addComponent(surname, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(101, 101, 101)
                                                .addComponent(jLabel5))
                                        .addComponent(alone))
                                .addGap(18, 18, 18)
                                .addComponent(tableContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 735, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(back)
                                                .addGap(15, 15, 15)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel1)
                                                        .addComponent(jLabel2))
                                                .addGap(10, 10, 10)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(fistName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(surname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(32, 32, 32)
                                                                .addComponent(alone)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jLabel3)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(phoneNumber1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(11, 11, 11)
                                                                .addComponent(jLabel10)
                                                                .addGap(3, 3, 3)
                                                                .addComponent(street, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jLabel5)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(enterUserGroup)
                                                                .addGap(11, 11, 11)
                                                                .addComponent(jLabel4)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(phoneNumber2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jLabel8)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(1, 1, 1)
                                                .addComponent(jLabel9)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(jLabel11)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(shares, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(jLabel12)
                                                                                .addGap(7, 7, 7)
                                                                                .addComponent(solidaritySurcharge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                .addGap(14, 14, 14)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel13)
                                                                        .addComponent(kernbeisserKey))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(permission, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(employee))
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jobsSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jLabel7)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(extraJobs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel14)
                                                        .addComponent(jLabel15))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(addUser, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(editUser, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(20, 20, 20)
                                                .addComponent(tableContainer)))
                                .addGap(23, 23, 23))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jobsSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jobsSelectorActionPerformed
        new JobSelector(this,jobs);
    }//GEN-LAST:event_jobsSelectorActionPerformed

    private void addUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addUserActionPerformed
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        User user = collectData();
        if(user==null)return;
        if(user.getUsername().length()<3){
            JOptionPane.showMessageDialog(this,"Der gew\u00e4hlte Benutzername ist zu kurz!");
            Tools.ping(username);
            return;
        }else
        if(JOptionPane.showConfirmDialog(this,"Wollen sie diesen Benutzer wirklich erstellen?")==0){
            try {
                if(alone.isSelected()){
                    userGroup=new UserGroup();
                    em.persist(userGroup);
                    user.setUserGroup(userGroup);
                }
                em.persist(user);
            }catch (PersistenceException e){
                JOptionPane.showMessageDialog(this,"Der Benutzername ist bereits vergeben!");
                et.rollback();
                em.close();
                return;
            }
            em.flush();
            et.commit();
            JOptionPane.showMessageDialog(this,"Nutzer erfolgreich erstellt!","Erfolg",JOptionPane.INFORMATION_MESSAGE);
            em.close();
            userSelector.refresh();
        }else {
            em.close();
        }
    }//GEN-LAST:event_addUserActionPerformed
    private void editUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editUserActionPerformed
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        User dbContent= em.createQuery(
                "select u from User u where username like '"+ userSelector.getValueAt(userSelector.getSelectedRow(),0)+ "'",
                User.class).getSingleResult();
        String dbPassword = dbContent.getPassword();
        User newContent = collectData(dbContent);
        if(password.getPassword().length==0)
            newContent.setPassword(dbPassword);
        et.begin();
        em.persist(newContent);
        em.flush();
        et.commit();
        em.close();
        userSelector.refresh();
    }//GEN-LAST:event_editUserActionPerformed

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed
        back();
    }//GEN-LAST:event_backActionPerformed

    private void enterUserGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterUserGroupActionPerformed
        new UserGroupSelector(this,e -> {
            userGroup=e;
            alone.setSelected(false);
        });
    }//GEN-LAST:event_enterUserGroupActionPerformed

    private User collectData(){
        return collectData(new User());
    }
    private User collectData(User user){
        user.setFirstName(fistName.getText());
        user.setSurname(surname.getText());
        user.setPhoneNumber1(phoneNumber1.getText());
        user.setPhoneNumber2(phoneNumber2.getText());
        user.setAddress(address.getText()+" ; "+street.getText());
        user.setEmail(email.getText());
        user.setShares((Integer) shares.getValue());
        user.setSolidaritySurcharge((Integer) solidaritySurcharge.getValue());
        user.setPermission(t.translate(Permission.class,permission.getSelectedItem().toString()));
        user.setKernbeisserKey(kernbeisserKey.isSelected());
        user.setEmployee(employee.isSelected());
        user.getJobs().clear();
        user.getJobs().addAll(jobs);
        user.setExtraJobs(extraJobs.getText());
        user.setUsername(username.getText());
        if(!alone.isSelected()&&userGroup==null){
            Tools.ping(enterUserGroup);
            Tools.ping(alone);
            return null;
        }
        user.setUserGroup(userGroup);
        user.setPassword(BCrypt.withDefaults().hashToString(12,password.getPassword()));
        return user;
    }
    private void paste(User user){
        fistName.setText(user.getFirstName());
        surname.setText(user.getSurname());
        phoneNumber1.setText(user.getPhoneNumber1());
        phoneNumber2.setText(user.getPhoneNumber2());
        String[] a = user.getAddress().split(";");
        address.setText(a[0]);
        street.setText(a[1]);
        email.setText(user.getEmail());
        shares.setValue(user.getShares());
        solidaritySurcharge.setValue(user.getSolidaritySurcharge());
        permission.setSelectedItem(t.translate(user.getPermission()));
        kernbeisserKey.setSelected(user.isKernbeisserKey());
        employee.setSelected(user.isEmployee());
        jobs=user.getJobs();
        extraJobs.setText(user.getExtraJobs());
        username.setText(user.getUsername());
        userGroup=user.getUserGroup();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addUser;
    private javax.swing.JTextField address;
    private javax.swing.JCheckBox alone;
    private javax.swing.JButton back;
    private javax.swing.JButton editUser;
    private javax.swing.JTextField email;
    private javax.swing.JCheckBox employee;
    private javax.swing.JButton enterUserGroup;
    private javax.swing.JTextField extraJobs;
    private javax.swing.JTextField fistName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton jobsSelector;
    private javax.swing.JCheckBox kernbeisserKey;
    private javax.swing.JPasswordField password;
    private javax.swing.JComboBox<String> permission;
    private javax.swing.JTextField phoneNumber1;
    private javax.swing.JTextField phoneNumber2;
    private javax.swing.JSpinner shares;
    private javax.swing.JSpinner solidaritySurcharge;
    private javax.swing.JTextField street;
    private javax.swing.JTextField surname;
    private javax.swing.JTabbedPane tableContainer;
    private javax.swing.JTextField username;

    // End of variables declaration//GEN-END:variables
}
