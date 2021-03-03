package kernbeisser.Windows.PermissionManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.ActionPermission;
import kernbeisser.Security.StaticMethodTransformer.StaticAccessPoint;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class PermissionController extends Controller<PermissionView, PermissionModel> {

  public PermissionController() {
    super(new PermissionModel());
  }

  private Permission lastSelection;

  private void change(Permission permission, PermissionKey key) {
    // if(LogInModel.getLoggedIn().hasPermission(Key.find(KeyCategory.PERMISSIONS)))
    if (permission.contains(key)) {
      if (permission.contains(key.getWriteKey())) {
        model.removeKey(permission, key.getWriteKey());
        model.removeKey(permission, key);
      } else {
        model.addKey(permission, key.getWriteKey());
      }
    } else {
      model.addKey(permission, key);
    }
    loadSolutions();
  }

  void loadSolutions() {
    List<Permission> permissions = Permission.getAll(null);
    Collections.reverse(permissions);
    Column<PermissionKey> nameColumn =
        Column.create(
            "Schlüssel-Name",
            e ->
                PermissionKey.getPermissionHint(
                    e.name()
                        .replace("_WRITE", "")
                        .replace("_READ", "")
                        .replace("CHANGE_ALL", "Alle Bearbeiten")));
    ArrayList<Column<PermissionKey>> permissionColumns = new ArrayList<>();
    permissionColumns.add(nameColumn);
    Collection<PermissionKey> readPermission =
        PermissionKey.find(getView().getCategory(), true, false);
    Collection<PermissionKey> writePermission =
        PermissionKey.find(getView().getCategory(), false, true);
    permissionColumns.addAll(
        permissions.stream()
            .map(
                permission ->
                    Column.create(
                        permission.getName(),
                        (PermissionKey permissionKey) -> {
                          if (permissionKey.getClazz() == null) {
                            boolean read = permission.getKeySet().containsAll(readPermission);
                            boolean write = permission.getKeySet().containsAll(writePermission);
                            return read ? write ? "Lesen & schreiben" : "Lesen" : "Keine";
                          }
                          if (permissionKey.getClazz().equals(ActionPermission.class)) {
                            return permission.contains(permissionKey) ? "Ja" : "Nein";
                          }
                          boolean read = permission.contains(permissionKey);
                          boolean write = permission.contains(permissionKey.getWriteKey());
                          return read ? write ? "Lesen & schreiben" : "Lesen" : "Keine";
                        },
                        e -> {
                          lastSelection = permission;
                          if (e.getClazz() == null) {
                            boolean read = permission.getKeySet().containsAll(readPermission);
                            boolean write = permission.getKeySet().containsAll(writePermission);
                            if (read) {
                              if (write) {
                                model.removeKeys(permission, readPermission);
                                model.removeKeys(permission, writePermission);
                              } else model.addKeys(permission, writePermission);
                            } else model.addKeys(permission, readPermission);
                          }
                          change(permission, e);
                        }))
            .collect(Collectors.toCollection(ArrayList::new)));
    Collection<PermissionKey> values = new ArrayList<>();
    if (!getView().getCategory().equals(ActionPermission.class))
      values.add(PermissionKey.CHANGE_ALL);
    values.addAll(
        Arrays.asList(
            PermissionKey.with(
                e ->
                    e.getClazz() != null
                        && (e.getClazz().equals(getView().getCategory())
                            & !e.name().endsWith("_WRITE")))));
    getView().setValues(values);
    getView().setColumns(permissionColumns);
  }

  public void addPermission() {
    try {
      model.addPermission(getView().getPermissionName());
    } catch (PersistenceException e) {
      getView().nameIsNotUnique();
      addPermission();
    }
    loadSolutions();
  }

  public void deletePermission() {
    try {
      model.deletePermission(lastSelection);
      loadSolutions();
      getView().successfulDeleted();
    } catch (PersistenceException e) {
      if (getView().permissionIsInUse()) {
        model.removeUserFromPermission(lastSelection);
        loadSolutions();
        getView().successfulDeleted();
      }
    }
  }

  @Override
  public @NotNull PermissionModel getModel() {
    return model;
  }

  @Override
  public void fillView(PermissionView permissionView) {
    getView().setCategories(model.getAllKeyCategories());
    loadSolutions();
  }

  @Override
  @StaticAccessPoint
  public PermissionKey[] getRequiredKeys() {
    return PermissionKey.combine(
        PermissionKey.allReadPermissions(Permission.class),
        PermissionKey.allWritePermissions(PermissionKey.class),
        new PermissionKey[] {PermissionKey.ACTION_OPEN_PERMISSION_MANAGEMENT});
  }

  public void importFrom(File selectedFile) throws FileNotFoundException {
    PermissionRepresentation.putInDB(selectedFile);
    loadSolutions();
  }

  public void exportTo(File selectedFile) throws IOException {
    PermissionRepresentation.write(
        selectedFile, new PermissionRepresentation(Permission.getAll(null)));
  }
}
