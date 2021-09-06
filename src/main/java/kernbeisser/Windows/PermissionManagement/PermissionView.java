package kernbeisser.Windows.PermissionManagement;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class PermissionView implements IView<PermissionController> {

  private ObjectTable<PermissionKey> permission;
  private JPanel main;
  private JComboBox<Class> category;
  private JButton back;
  private JButton add;
  private JButton delete;
  private JButton exportPermissions;
  private JButton importPermissions;

  @Linked private PermissionController controller;

  Optional<PermissionKey> getSelectedObject() {
    return permission.getSelectedObject();
  }

  String getPermissionName() {
    return JOptionPane.showInputDialog(this, "Bitte gib den Namen der neuen Berechtigung ein");
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

  void setColumns(Collection<Column<PermissionKey>> permissionColumns) {
    permission.setColumns(permissionColumns);
    int i = 0;
    for (Column<PermissionKey> permissionColumn : permissionColumns) {
      int s =
          (int)
              (permission
                      .getFontMetrics(permission.getFont())
                      .getStringBounds(permissionColumn.getName(), null)
                      .getWidth()
                  + 10);
      permission.getColumnModel().getColumn(i).setMinWidth(s);
      permission.getColumnModel().getColumn(i).setPreferredWidth(s + (i == 0 ? 100 : 0));
      i++;
    }
  }

  void setValues(Collection<PermissionKey> permissions) {
    this.permission.setObjects(permissions);
  }

  void setCategories(List<Class<?>> categories) {
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
              if (controller.importFrom(jFileChooser.getSelectedFile())) {
                JOptionPane.showMessageDialog(
                    getTopComponent(), "Alle Berechtigungen erfolgreich importiert");
              }
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
        "Der gewählte Name ist bereits vergeben,\n" + "bitte wähle einen anderen.");
  }

  public boolean permissionIsInUse() {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Die Berechtigung ist noch an Nutzer vergeben,\n"
                + "soll allen Nutzern die Berechtigung entzogen werden\n"
                + "und die Berechtigung anschließend gelöscht werden?")
        == 0;
  }

  public void successfulDeleted() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Die Berechtigung wurde erfolgreich gelöscht.");
  }

  @Override
  public String getTitle() {
    return "Berechtigungen";
  }

  public Permission inputAskForPermission(Collection<Permission> allPermissions) {
    JPanel jPanel = new JPanel();
    jPanel.add(new JLabel("Welche Berechtigung soll gelöscht werden?"));
    AdvancedComboBox<Permission> permissionAdvancedComboBox =
        new AdvancedComboBox<>(Permission::getName);
    allPermissions.stream()
        .filter(e -> !e.getName().startsWith("@"))
        .forEach(permissionAdvancedComboBox::addItem);
    jPanel.add(permissionAdvancedComboBox);
    if (JOptionPane.showConfirmDialog(
            getTopComponent(), jPanel, "Berechtigung auswählen", JOptionPane.OK_CANCEL_OPTION)
        == 0) {
      return permissionAdvancedComboBox.getSelected().orElseThrow(CancellationException::new);
    } else {
      throw new CancellationException();
    }
  }
}
