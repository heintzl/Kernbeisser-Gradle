package kernbeisser.Security.Access;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Security.PermissionSet;
import lombok.Cleanup;
import rs.groump.PermissionKey;

/** Visualizes the access keys by the current user */
public class AccessAnalyser implements AccessManager, PermissionKeyBasedAccessManager {

  private final PermissionSet keySet = new PermissionSet();
  private final HashMap<PermissionKey, String> originMap = new HashMap<>();
  private final ObjectTable<PermissionKey> keyObjectForm =
      new ObjectTable<>(
          Columns.create("Name", PermissionKey::name), Columns.create("Origin", originMap::get));

  public AccessAnalyser() {
    JFrame jFrame = new JFrame();
    JScrollPane jScrollPane = new JScrollPane(keyObjectForm);
    jFrame.add(jScrollPane, BorderLayout.CENTER);
    jFrame.setSize(500, 500);
    jFrame.setTitle("Access PermissionKeys:");
    JButton clear = new JButton("Clear");
    JButton button = new JButton("Dump in DB");
    button.addActionListener(e -> dumpInDB(JOptionPane.showInputDialog("Permission name:")));
    jFrame.add(button, BorderLayout.AFTER_LAST_LINE);
    jFrame.add(clear, BorderLayout.BEFORE_FIRST_LINE);
    clear.addActionListener(
        e -> {
          keyObjectForm.setObjects(new ArrayList<>());
          keySet.setAllBits(false);
        });
    jFrame.setVisible(true);
    jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  }

  @Override
  public boolean hasAccess(Object object, String methodName, String signature, PermissionSet keys) {
    for (PermissionKey key : keys) {
      String origin = object.getClass().getSimpleName() + "::" + methodName + ";";
      Optional<String> before = Optional.ofNullable(originMap.get(key));
      if (before.isPresent()) {
        originMap.replace(key, before.get().replace(origin, "") + origin);
      } else {
        originMap.put(key, origin);
      }

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
