package kernbeisser.Windows.PermissionManagement;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Enums.Key;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public class PermissionController {
    private final PermissionView view;
    private final PermissionModel model;

    PermissionController(Window window) {
        this.view = new PermissionView(this, window);
        this.model = new PermissionModel();
        view.setCategories(model.getAllKeyCategories());
        view.setSecurities(model.getAllSecurities());
        //boolean p = LogInModel.getLoggedIn().hasPermission(Key.PERMISSION_KEY_SET_WRITE);
        //view.setAddEnable(p);
        //view.setDeleteEnable(p);
        view.setValues(model.getAllPermissions());
        loadSolutions();
    }

    public static void main(String[] args)
            throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException,
                   IllegalAccessException {
        Main.buildEnvironment();
        new PermissionController(null);
    }

    private void change(Permission permission, Key key) {
        //if(LogInModel.getLoggedIn().hasPermission(Key.find(KeyCategory.PERMISSIONS)))
        if (permission.contains(key)) {
            model.removeKey(permission, key);
        } else {
            model.addKey(permission, key);
        }
        view.setValues(model.getAllPermissions());
        loadSolutions();
    }

    void loadSolutions() {
        Column<Permission> nameColumn = Column.create("Berechtigung", Permission::getName);
        Collection<Column<Permission>> keyColumns = Tools.transform(
                Key.find(
                        view.getCategory()
                        , view.getSecurity())
                , e -> Column.create(
                        e.name().replaceFirst(e.name().split("_")[0] + "_", ""),
                        p -> p.contains(e),
                        s -> change(s, e))
        );
        ArrayList<Column<Permission>> columns = new ArrayList<>(keyColumns.size() + 1);
        columns.add(nameColumn);
        columns.addAll(keyColumns);
        view.setColumns(columns);
    }

    public void addPermission() {
        model.addPermission(view.getPermissionName());
        view.setValues(model.getAllPermissions());
    }

    public void deletePermission() {
        model.deletePermission(view.getSelectedObject());
        view.setValues(model.getAllPermissions());
    }
}
