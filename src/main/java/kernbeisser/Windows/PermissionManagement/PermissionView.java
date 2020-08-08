package kernbeisser.Windows.PermissionManagement;

import java.awt.*;
import java.util.Collection;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.MVC.View;
import org.jetbrains.annotations.NotNull;

public class PermissionView implements View<PermissionController> {
  private ObjectTable<Permission> permission;
  private JPanel main;
  private JComboBox<Object> category;
  private JButton back;
  private JButton add;
  private JButton delete;

  @Linked private PermissionController controller;

  Permission getSelectedObject() {
    return permission.getSelectedObject();
  }

  String getPermissionName() {
    return JOptionPane.showInputDialog(
        this, "Bitte geben sie den Namen der neuen Berechtigung ein");
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
      int s =
          (int)
              (permission
                      .getFontMetrics(permission.getFont())
                      .getStringBounds(permissionColumn.getName(), null)
                      .getWidth()
                  + 50);
      permission.getColumnModel().getColumn(i).setMinWidth(s);
      permission.getColumnModel().getColumn(i).setMaxWidth(s);
      permission.getColumnModel().getColumn(i).setWidth(s);
      i++;
    }
  }

  void setValues(Collection<Permission> permissions) {
    this.permission.setObjects(permissions);
  }

  void setCategories(Class<?>[] categories) {
    category.removeAllItems();
    category.addItem("Aktionen");
    for (Class<?> keyCategory : categories) {
      category.addItem(keyCategory);
    }
  }

  public Class<?> getCategory() {
    try {
      return (Class<?>) category.getSelectedItem();
    } catch (ClassCastException e) {
      return null;
    }
  }

  @Override
  public void initialize(PermissionController controller) {
    add.addActionListener(e -> controller.addPermission());
    delete.addActionListener(e -> controller.deletePermission());
    category.addActionListener(e -> controller.loadSolutions());
    category.setRenderer(
        new DefaultListCellRenderer() {
          @Override
          public Component getListCellRendererComponent(
              JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            return super.getListCellRendererComponent(
                list,
                value instanceof Class<?> ? ((Class<?>) value).getSimpleName() : value,
                index,
                isSelected,
                cellHasFocus);
          }
        });
    back.addActionListener(e -> back());
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }
}
