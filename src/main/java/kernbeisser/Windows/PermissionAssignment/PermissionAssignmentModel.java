package kernbeisser.Windows.PermissionAssignment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.*;
import java.util.stream.Collectors;
import kernbeisser.CustomComponents.ClipboardFilter;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.Supplier;
import rs.groump.PermissionKey;

public class PermissionAssignmentModel implements IModel<PermissionAssignmentController> {

  @Setter private Permission recent;
  private boolean showFilterExplanation = true;

  public List<Permission> getPermissions() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select p from Permission p", Permission.class).getResultList();
  }

  public Optional<Permission> getRecent() {
    return Optional.ofNullable(recent);
  }

  public Collection<User> allUsers() {
    List<User> c = User.getAll(null);
    c.removeAll(User.getGenericUsers());
    c.sort(Comparator.comparing(User::getFullName));
    return c;
  }

  public Collection<User> assignedUsers(Permission permission) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select u from User u where :pid in elements(u.permissions) and not "
                + User.GENERIC_USERS_CONDITION
                + " order by u.firstName, u.surname",
            User.class)
        .setParameter("pid", permission)
        .getResultList();
  }

  @Key(PermissionKey.ACTION_GRANT_CASHIER_PERMISSION)
  private void checkGrantCashierPermission() {}

  public void setPermission(
      Permission permission,
      Collection<User> loaded,
      Supplier<Boolean> confirm,
      boolean ignoreUserPermission) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Collection<User> hadBefore =
        em.createQuery(
                "select u from User u where :pid in elements(u.permissions) and not "
                    + User.GENERIC_USERS_CONDITION,
                User.class)
            .setParameter("pid", permission)
            .getResultList();
    ArrayList<User> notToRemove = new ArrayList<>(loaded);
    loaded.removeAll(hadBefore);
    hadBefore.removeAll(notToRemove);
    Collection<User> willGet =
        loaded.stream().map(e -> em.find(User.class, e.getId())).collect(Collectors.toList());
    boolean skipUserAccessChecking =
        ignoreUserPermission && Tools.canInvoke(this::checkGrantCashierPermission);
    if (skipUserAccessChecking) {
      for (Object u : CollectionUtils.union(willGet, hadBefore)) {
        Access.putException(u, AccessManager.NO_ACCESS_CHECKING);
      }
    }
    if (!(hadBefore.isEmpty() && willGet.isEmpty()) && confirm.get()) {
      hadBefore.stream().peek(e -> e.getPermissions().remove(permission)).forEach(em::persist);
      willGet.stream().peek(e -> e.getPermissions().add(permission)).forEach(em::persist);
    }
    em.flush();
    if (skipUserAccessChecking) {
      for (Object u : CollectionUtils.union(willGet, hadBefore)) {
        Access.removeException(u);
      }
    }
  }

  private Collection<User> getUserRowFilter(String[] rows) {
    List<String> userNames =
        Arrays.stream(rows).map(r -> r.replace("\t", "&")).collect(Collectors.toList());
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    List<User> result =
        em.createQuery(
                "Select u from User u where concat(u.surname, '&', u.firstName) in (:ul)",
                User.class)
            .setParameter("ul", userNames)
            .getResultList();
    return result;
  }

  public ClipboardFilter<User> getClpBoardRowFilter() {
    String explanation =
        showFilterExplanation
            ? "Bitte Nachname und Vorname als 2 Tabellenspalten in die Zwischenablage kopieren!"
            : "";
    showFilterExplanation = false;
    return new ClipboardFilter<>(
        this::getUserRowFilter, explanation, "clpBoardFilterPermissionAssignment");
  }
}
