package kernbeisser.Windows.PermissionManagement;

import jakarta.persistence.PersistenceException;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Enums.Colors;
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
    initialize();
  }

  private void initialize() {
    Column<PermissionKey> nameColumn =
        Columns.create(
            "Schlüssel-Name",
            e ->
                PermissionKeys.getPermissionHint(
                    e.name()
                        .replace("_WRITE", "")
                        .replace("_READ", "")
                        .replace("CHANGE_ALL", "Alle Bearbeiten")));
    List<Column<PermissionKey>> permissionColumns = new ArrayList<>();
    permissionColumns.add(nameColumn);
    permissionColumns.addAll(
        model.readAllPermissions().stream().map(this::createPermissionColumn).toList());
    getView().setColumns(permissionColumns);
  }

  public Column<PermissionKey> createPermissionColumn(Permission permission) {
    return Columns.<PermissionKey>create(
            permission.getNeatName(), (k) -> model.getPermissionLevel(k, permission).getName())
        .withDoubleClickConsumer(k -> cycleAccess(permission, k))
        .withTooltip(k -> PermissionKeys.getPermissionHint(k.toString()))
        .withBgColor(k -> model.isDirty(permission, k) ? Colors.BACKGROUND_DIRTY.getColor() : null);
  }

  public void loadPermissionGroup() {
    Optional<PermissionKeyGroups> selectedGroup = getView().getSelectedGroup();
    if (selectedGroup.isEmpty()) return;
    List<PermissionKey> groupKeys = model.selectGroup(selectedGroup.get());
    getView().setValues(groupKeys);
  }

  private void cycleAccess(Permission permission, PermissionKey key) {
    getView().getPermission().replace(key, model.cycleAccess(key, permission));
  }

  public void addPermission() {
    String permissionName;
    do {
      permissionName = getView().getPermissionName();
      if (permissionName == null) return;
    } while (permissionName.isEmpty());
    try {
      model.addPermission(permissionName);
      initialize();
      loadPermissionGroup();
    } catch (PersistenceException e) {
      getView().nameIsNotUnique();
      addPermission();
    }
  }

  public void deletePermission() {
    Permission permission = null;
    String permissionName = "";
    try {
      permission = getView().inputAskForPermission(model.getAllPermissions());
      permissionName = permission.getName();
      model.deletePermission(permission);
      initialize();
      loadPermissionGroup();
      getView().successfulDeleted();
    } catch (CancellationException ignored) {
    } catch (PersistenceException e) {
      if (getView().permissionIsInUse()) {
        model.removeUserFromPermission(permission);
        initialize();
        loadPermissionGroup();
        getView().successfulDeleted();
      }
    }
  }

  public void saveChanges() {
    model.persistChanges(model.dirtyPermissionKeys());
  }

  public void close() {
    PermissionView view = getView();
    Map<Permission, Map<PermissionKey, AccessLevel>> dirtyPermissionKeys =
        model.dirtyPermissionKeys();
    if (dirtyPermissionKeys.isEmpty()) {
      view.back();
    } else {
      switch (view.confirmCloseOnDirty()) {
        case JOptionPane.CANCEL_OPTION -> {
          return;
        }
        case JOptionPane.YES_OPTION -> model.persistChanges(dirtyPermissionKeys);
      }
      view.back();
    }
  }

  @Override
  public @NotNull PermissionModel getModel() {
    return model;
  }

  @Override
  public void fillView(PermissionView permissionView) {
    getView().setCategories(model.getAllKeyCategories());
    loadPermissionGroup();
  }

  public boolean importFrom(File selectedFile) throws FileNotFoundException {
    if (!PermissionRepresentation.putInDB(selectedFile)) {
      return false;
    }
    initialize();
    loadPermissionGroup();
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
