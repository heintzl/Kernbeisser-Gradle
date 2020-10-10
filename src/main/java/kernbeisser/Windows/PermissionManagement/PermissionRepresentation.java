package kernbeisser.Windows.PermissionManagement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;

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

  public static void putInDB(File file) throws FileNotFoundException {
    PermissionRepresentation representation = read(file);
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    HashMap<String, User> userHashMap = new HashMap<>();
    em.createQuery("select u from User u", User.class)
        .getResultList()
        .forEach(e -> userHashMap.put(e.getUsername(), e));
    userHashMap.values().forEach(e -> e.getPermissions().clear());
    em.createQuery("select p from Permission p", Permission.class)
        .getResultList()
        .forEach(em::remove);
    em.flush();
    List<Permission> permissions = parsePermissions(representation);
    permissions.forEach(em::persist);
    for (int i = 0; i < permissions.size(); i++) {
      Permission permission = permissions.get(i);
      for (String username : representation.usernames.get(i)) {
        User user = userHashMap.get(username);
        if (user != null) {
          user.getPermissions().add(permission);
        }
      }
    }
    userHashMap.values().forEach(em::persist);
    em.flush();
    et.commit();
    em.close();
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
