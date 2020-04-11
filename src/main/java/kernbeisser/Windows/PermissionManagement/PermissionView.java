package kernbeisser.Windows.PermissionManagement;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Enums.KeyCategory;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;

public class PermissionView implements View<PermissionController> {
    private ObjectTable<Permission> permission;
    private JPanel main;
    private JComboBox<KeyCategory> category;
    private JButton back;
    private JButton add;
    private JButton delete;

    private PermissionController controller;

    PermissionView(PermissionController controller) {
        this.controller = controller;
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


    void setCategories(KeyCategory[] categories) {
        category.removeAllItems();
        for (KeyCategory keyCategory : categories) {
            category.addItem(keyCategory);
        }
    }

    public KeyCategory getCategory() {
        return (KeyCategory) category.getSelectedItem();
    }

    @Override
    public void initialize(PermissionController controller) {
        add.addActionListener(e -> controller.addPermission());
        delete.addActionListener(e -> controller.deletePermission());
        category.addActionListener(e -> controller.loadSolutions());
        back.addActionListener(e -> back());
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

}
