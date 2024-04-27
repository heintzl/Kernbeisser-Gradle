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
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.User_;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Setter;
import org.apache.logging.log4j.util.Supplier;
import rs.groump.Access;
import rs.groump.AccessManager;

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
            isMember(asExpression(permission), User_.permissions))
        .getResultList(em);
  }

  public Collection<User> assignedUsers(Permission permission) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return assignedUsers(em, permission);
  }

  public static boolean isAccessible() {
    return !getCurrentGrantPermissions().isEmpty();
  }

  public void setPermission(
      Permission permission,
      Collection<User> loaded,
      Supplier<Boolean> confirm) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Collection<User> hadBefore = assignedUsers(em, permission);
    ArrayList<User> notToRemove = new ArrayList<>(loaded);
    loaded.removeAll(hadBefore);
    hadBefore.removeAll(notToRemove);
    Collection<User> willGet = loaded.stream().map(e -> em.find(User.class, e.getId())).toList();
    if (!(hadBefore.isEmpty() && willGet.isEmpty()) && confirm.get()) {
      hadBefore.stream()
          .peek(e -> e.getPermissions().remove(permission))
          .forEach(em::persist);
      willGet.stream().peek(e -> e.getPermissions().add(permission)).forEach(em::persist);
    }
    em.flush();
  }

  private Collection<User> getUserRowFilter(String[] rows) {
    List<String> userNames =
        Arrays.stream(rows).map(r -> r.replace("\t", "&")).collect(Collectors.toList());
    return QueryBuilder.selectAll(User.class)
        .where(concat(concat(User_.surname, asExpression("&")), User_.firstName).in(userNames))
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

  public static List<Permission> getCurrentGrantPermissions() {
    final List<Permission> result = new ArrayList<>();
    Access.runWithAccessManager(
        AccessManager.ACCESS_GRANTED,
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
