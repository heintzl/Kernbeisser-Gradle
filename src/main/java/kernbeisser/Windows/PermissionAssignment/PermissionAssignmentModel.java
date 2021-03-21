package kernbeisser.Windows.PermissionAssignment;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class PermissionAssignmentModel implements IModel<PermissionAssignmentController> {

  public List<Permission> getPermissions() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select p from Permission p", Permission.class).getResultList();
  }
}
