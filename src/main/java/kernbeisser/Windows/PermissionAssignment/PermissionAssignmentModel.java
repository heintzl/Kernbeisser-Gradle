package kernbeisser.Windows.PermissionAssignment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Security.Proxy;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Setter;

public class PermissionAssignmentModel implements IModel<PermissionAssignmentController> {

  @Setter private Permission recent;

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

  public Source<User> allUsers() {
    return Source.of(User.class);
  }

  public Collection<User> assignedUsers(Permission permission) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return Proxy.getSecureInstances(
        em.createQuery("select u from User u where :pid in elements(u.permissions)", User.class)
            .setParameter("pid", permission)
            .getResultList());
  }

  public void setPermission(Permission permission, Collection<User> loaded) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Collection<User> hadBefore =
        em.createQuery("select u from User u where :pid in elements(u.permissions)", User.class)
            .setParameter("pid", permission)
            .getResultList();
    hadBefore.removeAll(new ArrayList<>(loaded));
    loaded.removeAll(hadBefore);
    hadBefore.stream().peek(e -> e.getPermissions().remove(permission)).forEach(em::persist);
    loaded.stream()
        .map(e -> em.find(User.class, e.getId()))
        .peek(e -> e.getPermissions().add(permission))
        .forEach(em::persist);
    em.flush();
  }
}
