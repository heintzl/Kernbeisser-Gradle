package kernbeisser.Windows.PermissionGranterAssignment;

import java.util.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Windows.MVC.IModel;
import lombok.Setter;
import lombok.var;
import org.apache.logging.log4j.util.Supplier;

public class PermissionGranterAssignmentModel
    implements IModel<PermissionGranterAssignmentController> {

  @Setter private Permission recent;

  public Optional<Permission> getRecent() {
    return Optional.ofNullable(recent);
  }

  public List<Permission> allPermissions() {
    List<Permission> result = DBConnection.getAll(Permission.class);
    result.sort(Comparator.comparing(Permission::getName));
    return result;
  }

  public void setPermission(
      Permission permission, Collection<Permission> loaded, Supplier<Boolean> confirm) {
    Collection<Permission> hadBefore = permission.getGranters();
    var notToRemove = new ArrayList<>(loaded);
    loaded.removeAll(hadBefore);
    hadBefore.removeAll(notToRemove);
    permission.removeGranters(hadBefore.toArray(new Permission[0]));
    permission.addGranters(loaded.toArray(new Permission[0]));
  }
}
