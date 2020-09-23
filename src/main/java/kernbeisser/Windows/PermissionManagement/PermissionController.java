package kernbeisser.Windows.PermissionManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.ActionPermission;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IController;
import org.jetbrains.annotations.NotNull;

public class PermissionController implements IController<PermissionView, PermissionModel> {
  private PermissionView view;
  private final PermissionModel model;

  public PermissionController() {
    this.model = new PermissionModel();
  }

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
    view.setValues(model.getAllPermissions());
    loadSolutions();
  }

  void loadSolutions() {
    Column<Permission> nameColumn = Column.create("Berechtigung", Permission::getName);
    Collection<Column<Permission>> keyColumns =
        view.getCategory() != ActionPermission.class
            ? Tools.transform(
                PermissionKey.find(view.getCategory(), true, false),
                e ->
                    Column.create(
                        e.name()
                            .replaceFirst(e.name().split("_")[0] + "_", "")
                            .replace("_READ", ""),
                        p -> {
                          boolean read = p.contains(e);
                          boolean write =
                              p.contains(
                                  PermissionKey.valueOf(e.name().replaceAll("_READ", "_WRITE")));

                          return read ? write ? "Lesen & schreiben" : "Lesen" : "Keine";
                        },
                        s -> change(s, e)))
            : Tools.transform(
                PermissionKey.find(view.getCategory()),
                e ->
                    Column.create(
                        e.name().replace(e.name().split("_")[0] + "_", ""),
                        permission -> permission.contains(e) ? "Ja" : "Nein",
                        s -> soloChange(s, e)));
    ArrayList<Column<Permission>> columns = new ArrayList<>(keyColumns.size() + 2);
    columns.add(nameColumn);
    if (view.getCategory() != ActionPermission.class) {
      columns.add(
          Column.create(
              "Alle " + view.getCategory() + " Berechtigungen",
              permission -> {
                boolean read = true;
                boolean write = true;
                for (PermissionKey key : PermissionKey.find(view.getCategory())) {
                  if (!permission.contains(key)) {
                    read = (read && (!key.name().endsWith("READ")));
                    write = (write && (!key.name().endsWith("WRITE")));
                    if (!read && !write) {
                      break;
                    }
                  }
                }
                return read ? write ? "Lesen & schreiben" : "Lesen" : "Keine";
              },
              permission -> {
                Collection<PermissionKey> keys = PermissionKey.find(view.getCategory());
                boolean read = true;
                boolean write = true;
                for (PermissionKey key : keys) {
                  if (!permission.contains(key)) {
                    read = (read && (!key.name().endsWith("READ")));
                    write = (write && (!key.name().endsWith("WRITE")));
                    if (!read && !write) {
                      break;
                    }
                  }
                }
                if (read && write) {
                  model.removeKeys(permission, keys);
                } else if (read) {
                  model.addKeys(
                      permission,
                      keys.stream()
                          .filter(e -> e.name().endsWith("WRITE"))
                          .collect(Collectors.toCollection(ArrayList::new)));
                } else {
                  model.addKeys(
                      permission,
                      keys.stream()
                          .filter(e -> e.name().endsWith("READ"))
                          .collect(Collectors.toCollection(ArrayList::new)));
                }
                view.setValues(model.getAllPermissions());
                loadSolutions();
              }));
    }
    columns.addAll(keyColumns);
    view.setColumns(columns);
  }

  private void soloChange(Permission s, PermissionKey e) {
    if (s.contains(e)) {
      model.removeKey(s, e);
    } else {
      model.addKey(s, e);
    }
    view.setValues(model.getAllPermissions());
    loadSolutions();
  }

  public void addPermission() {
    model.addPermission(view.getPermissionName());
    view.setValues(model.getAllPermissions());
  }

  public void deletePermission() {
    model.deletePermission(view.getSelectedObject());
    view.setValues(model.getAllPermissions());
  }

  @Override
  public @NotNull PermissionModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setCategories(model.getAllKeyCategories());
    // boolean p = LogInModel.getLoggedIn().hasPermission(Key.PERMISSION_KEY_SET_WRITE);
    // view.setAddEnable(p);
    // view.setDeleteEnable(p);
    view.setValues(model.getAllPermissions());
    loadSolutions();
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return PermissionKey.find(Permission.class).toArray(new PermissionKey[0]);
  }

  public void importFrom(File selectedFile) throws FileNotFoundException {
    PermissionRepresentation.putInDB(selectedFile);
  }

  public void exportTo(File selectedFile) throws IOException {
    PermissionRepresentation.write(
        selectedFile, new PermissionRepresentation(Permission.getAll(null)));
  }
}
