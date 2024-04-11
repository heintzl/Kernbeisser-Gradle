package kernbeisser.Windows.PermissionManagement;

import jakarta.persistence.PersistenceException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.Permission_;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Security.PermissionKeyGroups;
import kernbeisser.Security.PermissionKeys;
import kernbeisser.Useful.CSV;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class PermissionController extends Controller<PermissionView, PermissionModel> {

  @Key({
    PermissionKey.ACTION_OPEN_PERMISSION_MANAGEMENT,
    PermissionKey.PERMISSION_ID_READ,
    PermissionKey.PERMISSION_ID_WRITE,
    PermissionKey.PERMISSION_KEY_SET_READ,
    PermissionKey.PERMISSION_KEY_SET_WRITE,
    PermissionKey.PERMISSION_NAME_READ,
    PermissionKey.PERMISSION_NAME_WRITE,
  })
  public PermissionController() {
    super(new PermissionModel());
  }

  private Permission lastSelection;

  private void change(Permission permission, PermissionKey key) {
    // if(LogInModel.getLoggedIn().hasPermission(Key.find(KeyCategory.PERMISSIONS)))
    if (permission.contains(key)) {
      if (permission.contains(PermissionKeys.getWriteKey(key))) {
        model.removeKey(permission, PermissionKeys.getWriteKey(key));
        model.removeKey(permission, key);
      } else {
        model.addKey(permission, PermissionKeys.getWriteKey(key));
      }
    } else {
      model.addKey(permission, key);
    }
    loadSolutions();
  }

  void loadSolutions() {
    Optional<PermissionKeyGroups> selectedPermissionKerOrdering = getView().getCategory();
    if (selectedPermissionKerOrdering.isEmpty()) return;
    List<Permission> permissions =
        DBConnection.getConditioned(
            Permission.class, Permission_.name.eq(PermissionConstants.ADMIN.nameId()).not());
    Collections.reverse(permissions);
    Column<PermissionKey> nameColumn =
        Columns.create(
            "Schlüssel-Name",
            e ->
                PermissionKeys.getPermissionHint(
                    e.name()
                        .replace("_WRITE", "")
                        .replace("_READ", "")
                        .replace("CHANGE_ALL", "Alle Bearbeiten")));
    ArrayList<Column<PermissionKey>> permissionColumns = new ArrayList<>();
    permissionColumns.add(nameColumn);
    PermissionKeyGroups selected = selectedPermissionKerOrdering.get();
    Collection<PermissionKey> readPermission = PermissionKeys.find(selected, true, false);
    Collection<PermissionKey> writePermission = PermissionKeys.find(selected, false, true);
    permissionColumns.addAll(
        permissions.stream()
            .map(
                permission ->
                    Columns.create(
                        permission.getNeatName(),
                        (PermissionKey permissionKey) -> {
                          if (permissionKey == PermissionKey.CHANGE_ALL) {
                            boolean read = permission.getKeySet().containsAll(readPermission);
                            boolean write = permission.getKeySet().containsAll(writePermission);
                            return read ? write ? "Lesen & schreiben" : "Lesen" : "Keine";
                          }

                          if (PermissionKeyGroups.isInGroup(
                              permissionKey, PermissionKeyGroups.ACTIONS)) {
                            return permission.contains(permissionKey) ? "Ja" : "Nein";
                          }
                          boolean read = permission.contains(permissionKey);
                          boolean write =
                              permission.contains(PermissionKeys.getWriteKey(permissionKey));
                          return read ? write ? "Lesen & schreiben" : "Lesen" : "Keine";
                        },
                        e -> {
                          lastSelection = permission;
                          if (e == PermissionKey.CHANGE_ALL) {
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
    List<PermissionKey> values =
        Arrays.stream(selected.getKeys())
            .filter(key -> selected == PermissionKeyGroups.ACTIONS || key.name().endsWith("_WRITE"))
            .sorted(Comparator.comparing(p -> PermissionKeys.getPermissionHint(p.toString())))
            .collect(Collectors.toList());
    if (!selected.equals(PermissionKeyGroups.ACTIONS)) values.addFirst(PermissionKey.CHANGE_ALL);
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
    Permission permission = null;
    try {
      permission = getView().inputAskForPermission(model.getAllPermissions());
      model.deletePermission(permission);
      loadSolutions();
      getView().successfulDeleted();
    } catch (CancellationException ignored) {
    } catch (PersistenceException e) {
      if (getView().permissionIsInUse()) {
        model.removeUserFromPermission(permission);
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

  public boolean importFrom(File selectedFile) throws FileNotFoundException {
    if (!PermissionRepresentation.putInDB(selectedFile)) {
      return false;
    }
    loadSolutions();
    return true;
  }

  public void exportTo(File selectedFile) throws IOException {
    PermissionRepresentation.write(
        selectedFile, new PermissionRepresentation(Tools.getAll(Permission.class)));
  }

  public void exportCsv(File file) throws IOException {
    CSV.dumpIntoCsv(getView().getPermission(), file);
  }
}
