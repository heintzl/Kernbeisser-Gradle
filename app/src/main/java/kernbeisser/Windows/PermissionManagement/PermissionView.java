package kernbeisser.Windows.PermissionManagement;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.Dialogs.SelectionDialog;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Security.PermissionKeyGroups;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import rs.groump.PermissionKey;

public class PermissionView implements IView<PermissionController> {

  @Getter private ObjectTable<PermissionKey> permission;
  private JPanel main;
  private AdvancedComboBox<PermissionKeyGroups> groupSelector;
  private JButton back;
  private JButton add;
  private JButton delete;
  private JButton exportPermissions;
  private JButton importPermissions;
  private JButton saveChanges;

  @Linked private PermissionController controller;

  String getPermissionName() {
    return JOptionPane.showInputDialog(
        getContent(),
        "Bitte gib den Namen der neuen Berechtigung ein:",
        "Berechtigung anlegen",
        JOptionPane.INFORMATION_MESSAGE);
  }

  private void createUIComponents() {
    permission = new ObjectTable<>();
    groupSelector = new AdvancedComboBox<>();
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

  void setValues(Collection<PermissionKey> permissionKeys) {
    this.permission.setObjects(permissionKeys);
  }

  void setCategories(List<PermissionKeyGroups> categories) {
    groupSelector.setItems(categories);
  }

  public Optional<PermissionKeyGroups> getSelectedGroup() {
    return groupSelector.getSelected();
  }

  @Override
  public void initialize(PermissionController controller) {
    add.addActionListener(e -> controller.addPermission());
    delete.addActionListener(e -> controller.deletePermission());
    groupSelector.addActionListener(e -> controller.loadPermissionGroup());
    groupSelector.setRenderer(
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
    back.addActionListener(e -> controller.close());
    saveChanges.addActionListener(e -> controller.saveChanges());
    exportPermissions.addActionListener(this::exportPermissions);
    importPermissions.addActionListener(this::importPermissions);
  }

  private void importPermissions(ActionEvent event) {
    File file = new File("importPath.txt");
    String importPath = ".";
    if (file.exists()) {
      try {
        List<String> fileLines = Files.readAllLines(file.toPath());
        importPath = fileLines.get(0);
      } catch (IOException e) {
        UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
      }
    }
    JFileChooser jFileChooser = new JFileChooser(importPath);
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
            DBConnection.reload();
          }
        });
    jFileChooser.showDialog(getTopComponent(), "Importieren");
  }

  private void exportPermissions(ActionEvent event) {
    Optional<ExportTypes> typeSelection =
        SelectionDialog.select(
            getTopComponent(),
            "In welchem Format sollen die Berechtigungen exportiert werden?",
            Arrays.asList(ExportTypes.JSON, ExportTypes.CSV));
    if (!typeSelection.isPresent()) return;
    ExportTypes type = typeSelection.get();
    JFileChooser jFileChooser = new JFileChooser();
    jFileChooser.setFileFilter(
        new FileNameExtensionFilter(
            "Berechtigungs-" + type.getName(), type.getFileNameExtension()));
    jFileChooser.addActionListener(
        e -> {
          if (jFileChooser.getSelectedFile() != null) {
            try {
              switch (type) {
                case JSON:
                  controller.exportTo(jFileChooser.getSelectedFile());
                  break;
                case CSV:
                  controller.exportCsv(jFileChooser.getSelectedFile());
                  break;
                default:
                  throw new IllegalStateException("Unexpected value: " + type);
              }
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

  public int confirmCloseOnDirty() {
    return JOptionPane.showConfirmDialog(
        getContent(),
        "Sollen vor dem Schließen alle Änderungen gespeichert werden?",
        "Ungespeicherte Änderungen",
        JOptionPane.YES_NO_CANCEL_OPTION,
        JOptionPane.QUESTION_MESSAGE);
  }

  // @spotless:off

  {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    createUIComponents();
    main = new JPanel();
    main.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
    final JScrollPane scrollPane1 = new JScrollPane();
    scrollPane1.setAutoscrolls(false);
    scrollPane1.setHorizontalScrollBarPolicy(30);
    main.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    permission.setAutoResizeMode(0);
    permission.setPreferredScrollableViewportSize(new Dimension(450, 400));
    scrollPane1.setViewportView(permission);
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), -1, -1));
    main.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    back = new JButton();
    back.setText("Schließen");
    panel1.add(back, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer1 = new Spacer();
    panel1.add(spacer1, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    add = new JButton();
    add.setText("Hinzufügen");
    panel1.add(add, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    delete = new JButton();
    delete.setText("Löschen");
    panel1.add(delete, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    exportPermissions = new JButton();
    exportPermissions.setText("Berechtigungen exportieren");
    panel1.add(exportPermissions, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    importPermissions = new JButton();
    importPermissions.setText("Berechtigungen importieren");
    panel1.add(importPermissions, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    saveChanges = new JButton();
    saveChanges.setText("Speichern");
    panel1.add(saveChanges, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
    main.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final Spacer spacer2 = new Spacer();
    panel2.add(spacer2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    final JLabel label1 = new JLabel();
    label1.setText("Kategorie");
    panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    panel2.add(groupSelector, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return main;
  }

  // @spotless:on
}
