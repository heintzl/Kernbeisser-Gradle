package kernbeisser.Security.Access;

import java.awt.BorderLayout;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.PermissionSet;
import lombok.Cleanup;

/** Visualizes the access keys by the current user */
public class AccessAnalyser implements AccessManager, PermissionKeyBasedAccessManager {

  private final PermissionSet keySet = new PermissionSet();
  private final ObjectTable<PermissionKey> keyObjectForm =
      new ObjectTable<>(Column.create("Name", PermissionKey::name));

  public AccessAnalyser() {
    JFrame jFrame = new JFrame();
    JScrollPane jScrollPane = new JScrollPane(keyObjectForm);
    jFrame.add(jScrollPane, BorderLayout.CENTER);
    jFrame.setSize(500, 500);
    jFrame.setTitle("Access PermissionKeys:");
    JButton button = new JButton("Dump in DB");
    button.addActionListener(e -> dumpInDB(JOptionPane.showInputDialog("Permission name:")));
    jFrame.add(button, BorderLayout.AFTER_LAST_LINE);
    jFrame.setVisible(true);
  }

  @Override
  public boolean hasAccess(Object object, String methodName, String signature, PermissionSet keys) {
    for (PermissionKey key : keys) {
      if (keySet.add(key)) {
        keyObjectForm.add(key);
      }
    }
    return true;
  }

  @Override
  public boolean hasPermission(PermissionSet keys) {
    return true;
  }

  public void dumpInDB(String permissionName) {
    if (permissionName == null) return;
    Permission permission = new Permission();
    permission.getKeySet().addAll(keySet);
    permission.setName(permissionName);
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.persist(permission);
    em.flush();
  }
}
