package kernbeisser.Windows.PermissionManagement;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Enums.KeyCategory;
import kernbeisser.Enums.Security;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.util.Collection;

public class PermissionView extends Window {
    private ObjectTable<Permission> permission;
    private JPanel main;
    private JComboBox<KeyCategory> category;
    private JComboBox<Security> security;
    private JButton back;
    private JButton add;
    private JButton delete;

    PermissionView(PermissionController controller, Window window) {
        super(window/*,Key.PERMISSION_KEY_SET_READ,Key.PERMISSION_NAME_READ*/);
        add(main);
        add.addActionListener(e -> controller.addPermission());
        delete.addActionListener(e -> controller.deletePermission());
        category.addActionListener(e -> controller.loadSolutions());
        security.addActionListener(e -> controller.loadSolutions());
        back.addActionListener(e -> back());
    }

    Permission getSelectedObject() {
        return permission.getSelectedObject();
    }

    String getPermissionName() {
        return JOptionPane.showInputDialog(this, "Bitte geben sie den Namen der neuen Berechtigung ein");
    }

    private void createUIComponents() {
        permission = new ObjectTable<>();
    }

    void setAddEnable(boolean b) {
        add.setEnabled(b);
    }

    void setDeleteEnable(boolean b) {
        delete.setEnabled(b);
    }


    void setColumns(Collection<Column<Permission>> permissionColumns) {
        permission.setColumns(permissionColumns);
        int i = 0;
        for (Column<Permission> permissionColumn : permissionColumns) {
            int s = (int) (permission.getFontMetrics(permission.getFont())
                                     .getStringBounds(permissionColumn.getName(), null)
                                     .getWidth() + 50);
            permission.getColumnModel().getColumn(i).setMinWidth(s);
            permission.getColumnModel().getColumn(i).setMaxWidth(s);
            permission.getColumnModel().getColumn(i).setWidth(s);
            i++;
        }
    }

    void setValues(Collection<Permission> permissions) {
        this.permission.setObjects(permissions);
    }

    void setSecurities(Security[] securities) {
        security.removeAllItems();
        for (Security security : securities) {
            this.security.addItem(security);
        }
    }

    void setCategories(KeyCategory[] categories) {
        category.removeAllItems();
        for (KeyCategory keyCategory : categories) {
            category.addItem(keyCategory);
        }
    }

    public Security getSecurity() {
        return (Security) security.getSelectedItem();
    }

    public KeyCategory getCategory() {
        return (KeyCategory) category.getSelectedItem();
    }
}
