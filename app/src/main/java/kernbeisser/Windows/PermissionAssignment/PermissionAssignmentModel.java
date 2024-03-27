package kernbeisser.Windows.PermissionAssignment;

import static kernbeisser.DBConnection.ExpressionFactory.asExpression;
import static kernbeisser.DBConnection.ExpressionFactory.concat;
import static kernbeisser.DBConnection.PredicateFactory.isMember;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.*;
import java.util.stream.Collectors;
import kernbeisser.CustomComponents.ClipboardFilter;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.TypeFields.UserField;
import kernbeisser.DBEntities.User;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Setter;
import org.apache.logging.log4j.util.Supplier;
import rs.groump.Access;
import rs.groump.AccessManager;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class PermissionAssignmentModel implements IModel<PermissionAssignmentController> {

  @Setter private Permission recent;
  private boolean showFilterExplanation = true;

  public List<Permission> getPermissions() {
    return QueryBuilder.selectAll(Permission.class).getResultList();
  }

  public Optional<Permission> getRecent() {
    return Optional.ofNullable(recent);
  }

  public Collection<User> allUsers() {
    List<User> c = Tools.getAll(User.class);
    c.removeAll(User.getGenericUsers());
    c.sort(Comparator.comparing(User::getFullName));
    return c;
  }

  public Collection<User> assignedUsers(EntityManager em, Permission permission) {
    return QueryBuilder.selectAll(User.class)
        .where(
            User.GENERIC_USERS_PREDICATE.not(),
            isMember(asExpression(permission), UserField.permissions))
        .getResultList(em);
  }

  public Collection<User> assignedUsers(Permission permission) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return assignedUsers(em, permission);
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
    Collection<User> hadBefore = assignedUsers(em, permission);
    ArrayList<User> notToRemove = new ArrayList<>(loaded);
    loaded.removeAll(hadBefore);
    hadBefore.removeAll(notToRemove);
    Collection<User> willGet = loaded.stream().map(e -> em.find(User.class, e.getId())).toList();
    boolean skipUserAccessChecking =
        ignoreUserPermission && Tools.canInvoke(this::checkGrantCashierPermission);
    Access.runWithAccessManager(
        skipUserAccessChecking ? AccessManager.ACCESS_GRANTED : Access.getAccessManager(),
        () -> {
          if (!(hadBefore.isEmpty() && willGet.isEmpty()) && confirm.get()) {
            hadBefore.stream()
                .peek(e -> e.getPermissions().remove(permission))
                .forEach(em::persist);
            willGet.stream().peek(e -> e.getPermissions().add(permission)).forEach(em::persist);
          }
          em.flush();
        });
  }

  private Collection<User> getUserRowFilter(String[] rows) {
    List<String> userNames =
        Arrays.stream(rows).map(r -> r.replace("\t", "&")).collect(Collectors.toList());
    return QueryBuilder.selectAll(User.class)
        .where(
            concat(concat(UserField.surname, asExpression("&")), UserField.firstName).in(userNames))
        .getResultList();
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
