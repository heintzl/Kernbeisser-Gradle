package kernbeisser.Windows.PermissionAssignment;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.CustomComponents.ClipboardFilter;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Setter;
import lombok.var;
import org.apache.logging.log4j.util.Supplier;

public class PermissionAssignmentModel implements IModel<PermissionAssignmentController> {

  @Setter private Permission recent;
  private boolean showFilterExplanation = true;

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

  public static boolean isAccessible() {
    return !getCurrentGrantPermissions().isEmpty();
  }

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
    var notToRemove = new ArrayList<>(loaded);
    loaded.removeAll(hadBefore);
    hadBefore.removeAll(notToRemove);
    Collection<User> willGet =
        loaded.stream().map(e -> em.find(User.class, e.getId())).collect(Collectors.toList());
    if ((hadBefore.isEmpty() && willGet.isEmpty()) || !isAccessible() || !confirm.get()) {
      return;
    }
    Access.runWithAccessManager(
        AccessManager.NO_ACCESS_CHECKING,
        () -> {
          hadBefore.stream().peek(e -> e.getPermissions().remove(permission)).forEach(em::persist);
          willGet.stream().peek(e -> e.getPermissions().add(permission)).forEach(em::persist);
        });
    em.flush();
  }

  private Collection<User> getUserRowFilter(String[] rows) {
    var userNames = Arrays.stream(rows).map(r -> r.replace("\t", "&")).collect(Collectors.toList());
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    var result =
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

  public static List<Permission> getCurrentGrantPermissions() {
    final List<Permission> result = new ArrayList<>();
    Access.runWithAccessManager(
        AccessManager.NO_ACCESS_CHECKING,
        () -> {
          if (LogInModel.getLoggedIn()
              .getPermissions()
              .contains(PermissionConstants.ADMIN.getPermission())) {
            result.addAll(DBConnection.getAll(Permission.class));
          } else {
            result.addAll(
                LogInModel.getLoggedIn().getPermissions().stream()
                    .flatMap(p -> p.getGrantees().stream())
                    .collect(Collectors.toList()));
          }
        });
    return result;
  }
}
