package kernbeisser.Windows.PermissionManagement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import rs.groump.PermissionKey;

public class PermissionRepresentation {
  private final List<String> permissionName = new ArrayList<>();
  private final List<Set<PermissionKey>> permissionKey = new ArrayList<>();
  private final List<List<String>> usernames = new ArrayList<>();

  public PermissionRepresentation(Collection<Permission> permission) {
    permission.forEach(e -> permissionName.add(e.getName()));
    permission.forEach(e -> permissionKey.add(e.getKeySet()));
    permission.forEach(e -> usernames.add(Tools.transform(e.getAllUsers(), User::getUsername)));
  }

  public static PermissionRepresentation read(File file) throws FileNotFoundException {
    Gson gson = new GsonBuilder().create();
    return gson.fromJson(new FileReader(file), PermissionRepresentation.class);
  }

  public static boolean putInDB(File file) throws FileNotFoundException {
    JPanel optionsPanel = new JPanel();
    optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

    ButtonGroup options = new ButtonGroup();
    JRadioButton keepUserPermissions = new JRadioButton();
    keepUserPermissions.setText(
        "Rollendefinition überschreiben, Benutzerberechtigungen beibehalten");
    keepUserPermissions.setSelected(true);
    options.add(keepUserPermissions);
    JRadioButton overwriteUserPermissions = new JRadioButton();
    overwriteUserPermissions.setText("Rollendefinition und Benutzerberechtigungen überschreiben");
    options.add(overwriteUserPermissions);
    JRadioButton dropUserPermissions = new JRadioButton();
    dropUserPermissions.setText(
        "Rollendefinition überschreiben, alle Benutzerberechtigungen verwerfen");
    options.add(dropUserPermissions);
    JRadioButton onlyUserPermissions = new JRadioButton();
    onlyUserPermissions.setText(
        "<html>Rollendefinition beibehalten, Benutzerberechtigungen überschreiben");
    options.add(onlyUserPermissions);

    JTextArea warningMessage =
        new JTextArea(
            "\nAchtung, nach dem Berechtigungsimport muss das Programm neu gestartet\n"
                + "werden. Es ist möglich, dass Du dich dann nicht mehr einloggen kannst!\n"
                + "Bist Du sicher, dass Du fortfahren willst?");
    warningMessage.setEditable(false);
    warningMessage.setOpaque(false);
    warningMessage.setAlignmentX(Component.LEFT_ALIGNMENT);

    optionsPanel.add(keepUserPermissions);
    optionsPanel.add(overwriteUserPermissions);
    optionsPanel.add(dropUserPermissions);
    optionsPanel.add(onlyUserPermissions);
    optionsPanel.add(warningMessage);

    if (JOptionPane.showConfirmDialog(
            null,
            optionsPanel,
            "Vorhandene Benutzerberechtigungen",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE)
        != JOptionPane.OK_OPTION) return false;

    PermissionRepresentation representation = read(file);
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    HashMap<String, User> userHashMap = new HashMap<>();
    HashMap<User, Set<String>> userPermissionMap = new HashMap<>();
    QueryBuilder.selectAll(User.class)
        .getResultList(em)
        .forEach(
            e -> {
              userHashMap.put(e.getUsername(), e);
              userPermissionMap.put(
                  e,
                  e.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet()));
            });
    userHashMap.values().forEach(e -> e.getPermissions().clear());

    List<Permission> permissions;
    if (onlyUserPermissions.isSelected()) {
      permissions = Tools.getAll(Permission.class);
    } else {
      QueryBuilder.selectAll(Permission.class).getResultList().forEach(em::remove);
      em.flush();
      permissions = parsePermissions(representation);
      permissions.forEach(em::persist);
    }

    for (int i = 0; i < permissions.size(); i++) {
      Permission permission = permissions.get(i);
      if (overwriteUserPermissions.isSelected() || onlyUserPermissions.isSelected()) {
        for (String username : representation.usernames.get(i)) {
          User user = userHashMap.get(username);
          if (user != null) {
            user.getPermissions().add(permission);
          }
        }
      } else if (keepUserPermissions.isSelected()) {
        userPermissionMap.forEach(
            (u, p) -> {
              if (p.contains(permission.getName())) {
                u.getPermissions().add(permission);
              }
            });
      }
    }
    userHashMap.values().forEach(em::persist);
    em.flush();
    return true;
  }

  public static List<Permission> parsePermissions(PermissionRepresentation representation) {
    ArrayList<Permission> permissions = new ArrayList<>(representation.permissionName.size());
    for (int i = 0; i < representation.permissionName.size(); i++) {
      Permission permission = new Permission();
      permission.setKeySet(representation.permissionKey.get(i));
      permission.setName(representation.permissionName.get(i));
      permissions.add(permission);
    }
    return permissions;
  }

  public static void write(File file, PermissionRepresentation representation) throws IOException {
    FileWriter fileWriter = new FileWriter(file);
    fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(representation));
    fileWriter.flush();
    fileWriter.close();
  }
}
