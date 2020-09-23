package kernbeisser.Windows.PermissionManagement;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class PermissionView implements IView<PermissionController> {
  private ObjectTable<Permission> permission;
  private JPanel main;
  private JComboBox<Class> category;
  private JButton back;
  private JButton add;
  private JButton delete;
  private JButton exportPermissions;
  private JButton importPermissions;

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
    for (Class<?> keyCategory : categories) {
      category.addItem(keyCategory);
    }
  }

  public Class<?> getCategory() {
    return (Class<?>) category.getSelectedItem();
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
    exportPermissions.addActionListener(this::exportPermissions);
    importPermissions.addActionListener(this::importPermissions);
  }

  private void importPermissions(ActionEvent event) {
    JFileChooser jFileChooser = new JFileChooser();
    jFileChooser.setFileFilter(new FileNameExtensionFilter("Berechtigungs-JSON", "json"));
    jFileChooser.addActionListener(
        e -> {
          if (jFileChooser.getSelectedFile() != null) {
            try {
              controller.importFrom(jFileChooser.getSelectedFile());
              JOptionPane.showMessageDialog(
                  getTopComponent(), "Es wurden erfolgreich alle Berechtigungen importiert");
            } catch (FileNotFoundException fileNotFoundException) {
              JOptionPane.showMessageDialog(
                  getTopComponent(),
                  "Die angegebene Datei "
                      + jFileChooser.getSelectedFile()
                      + " kann nicht gefunden werden");
            }
          }
        });
    jFileChooser.showDialog(getTopComponent(), "Importieren");
  }

  private void exportPermissions(ActionEvent event) {
    JFileChooser jFileChooser = new JFileChooser();
    jFileChooser.setFileFilter(new FileNameExtensionFilter("Berechtigungs-JSON", "json"));
    jFileChooser.addActionListener(
        e -> {
          if (jFileChooser.getSelectedFile() != null) {
            try {
              controller.exportTo(jFileChooser.getSelectedFile());
            } catch (IOException ioException) {
              JOptionPane.showMessageDialog(
                  getTopComponent(),
                  "Auf die angegebene Datei "
                      + jFileChooser.getSelectedFile()
                      + " kann nicht zugegriffen werden");
            }
          }
        });
    jFileChooser.showDialog(getTopComponent(), "Speichern");
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  public void nameIsNotUnique() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Der gewählte name ist bereits vergeben,\nbitte wählen sie einen anderen.");
  }
}
