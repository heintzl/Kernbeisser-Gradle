package kernbeisser.Windows.EditUserGroup;

import static kernbeisser.Useful.Tools.optional;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Collections;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Exeptions.CannotLogInException;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class EditUserGroupView implements IView<EditUserGroupController> {

  private ObjectTable<User> currentUserGroup;
  private JButton leaveUserGroup;
  private JButton changeUserGroup;
  private JTextField username;
  private SearchBoxView<UserGroup> userGroupSearchBoxView;
  private JButton cancel;
  private JPanel main;
  private JLabel solidaritySurcharge;
  private JLabel value;
  private JLabel interestThisYear;
  private JButton editSoli;
  private JLabel updateInfo;

  @Linked
  private EditUserGroupController controller;

  @Linked
  private SearchBoxController<UserGroup> searchBoxController;

  private void createUIComponents() {
    userGroupSearchBoxView = searchBoxController.getView();
    currentUserGroup =
        new ObjectTable<>(
            Columns.create("Vorname", User::getFirstName),
            Columns.create("Nachname", User::getSurname),
            Columns.create("Benutzername", User::getUsername));
  }

  String getUsername() {
    return username.getText();
  }

  private String toEuro(double value) {
    return String.format("%.2f€", value);
  }

  void setCurrentUserGroup(UserGroup userGroup) {
    currentUserGroup.setObjects(optional(userGroup::getMembers).orElse(Collections.emptyList()));
    value.setText(
        optional(userGroup::getValue).map(this::toEuro).orElse("[Keine Leseberechtigung]"));
    interestThisYear.setText(
        optional(userGroup::getInterestThisYear)
            .map(this::toEuro)
            .orElse("[Keine Leseberechtigung]"));
    solidaritySurcharge.setText(
        optional(userGroup::getSolidaritySurcharge)
            .map(e -> String.format("%.2f%%", e * 100))
            .orElse("[Keine Leseberechtigung]"));
    updateInfo.setText(getUpdateInfo(userGroup));
  }

  private void leaveUserGroup(ActionEvent event) {
    if (controller.getMemberCount() < 2) {
      JOptionPane.showMessageDialog(
          getTopComponent(), "Du bist bereits alleine in einer Nutzergruppe!");
    } else {
      if (JOptionPane.showConfirmDialog(
          getTopComponent(),
          controller.getModel().getUser().getFullName()
              + ", möchtest du wirklich deine Nutzergruppe verlassen?\n"
              + "Guthaben und Solidaraufschlag werden nicht übernommen...",
          "Gruppe verlassen",
          JOptionPane.OK_CANCEL_OPTION)
          == 0) {
        controller.leaveUserGroup();
        boolean ownUserGroupChange = !controller.getModel().getCaller().isPresent();
        String message =
            "Du hast deine Nutzergruppe erfolgreich verlassen\n"
                + "und bist nun alleine in einer Nutzergruppe.";
        JOptionPane.showMessageDialog(getTopComponent(), message);
        if (ownUserGroupChange) {
          back();
        }
      }
    }
  }

  private void changeUserGroup(ActionEvent event) {

    String message;
    if (controller.getMemberCount() < 2) {
      message =
          "Du bist derzeit alleine in einer Nutzergruppe.\n"
              + "Willst du wirklich deine aktuelle Nutzergruppe auflösen "
              + "und ihr aktuelles Guthaben auf die neue Nutzergruppe übertragen?";
    } else {
      message =
          controller.getModel().getUser().getFullName()
              + ", willst du wirklich deine aktuelle Nutzergruppe "
              + "verlassen und in eine Gruppe mit "
              + username.getText()
              + " wechseln?\n"
              + "Das Guthaben wird nicht übertragen, da noch weitere Nutzer "
              + "in deiner Nutzergruppe sind.";
    }
    if (JOptionPane.showConfirmDialog(
        getTopComponent(), message, "Gruppe wechseln", JOptionPane.OK_CANCEL_OPTION)
        != 0) {
      return;
    }
    try {
      boolean ownUserGroupChange = !controller.getModel().getCaller().isPresent();
      if (controller.changeUserGroup()) {
        message =
            "Du bist erfolgreich der Nutzergruppe von " + username.getText() + " beigetreten.";
        JOptionPane.showMessageDialog(
            getTopComponent(),
            message,
            "Änderung der Nutzergruppe",
            JOptionPane.INFORMATION_MESSAGE);
        if (ownUserGroupChange) {
          back();
        }
      }
    } catch (CannotLogInException e) {
      JOptionPane.showMessageDialog(
          getTopComponent(), "Das eingegebene Passwort stimmt nicht überein.");
    }
  }

  @Override
  public void initialize(EditUserGroupController controller) {
    cancel.addActionListener(e -> back());
    leaveUserGroup.addActionListener(this::leaveUserGroup);
    changeUserGroup.addActionListener(this::changeUserGroup);
    editSoli.addActionListener(
        e -> {
          controller.editSoli(requestSoli());
        });
  }

  private String getUpdateInfo(UserGroup u) {
    try {
      return Date.INSTANT_DATE.format(u.getUpdateDate())
          + " durch "
          + u.getUpdateBy().getFullName();
    } catch (NullPointerException e) {
      return "(nicht gespeichert)";
    }
  }

  private double requestSoli() {
    String txt =
        JOptionPane.showInputDialog(
            getTopComponent(), "Bitte gib den neuen Solidarzuschlag ein [%]");
    try {
      return Double.parseDouble(txt.replaceAll("[^\\d,.]", "").replace(",", ".")) / 100.;
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(
          getTopComponent(), "Der eingegebene Wert konnte nicht als Zahl interpretiert werden.");
      return requestSoli();
    }
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  void setUsername(String username) {
    this.username.setText(username);
  }

  @Override
  public String getTitle() {
    return "Nutzergruppe wechseln (" + controller.getModel().getUser().getFullName() + ")";
  }

  {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR
   * call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    createUIComponents();
    main = new JPanel();
    main.setLayout(new GridLayoutManager(1, 2, new Insets(5, 5, 5, 5), -1, -1));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(8, 3, new Insets(0, 0, 0, 0), -1, -1));
    main.add(panel1,
        new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
            null, 0, false));
    final JLabel label1 = new JLabel();
    label1.setText("Aktuelle Nutzergruppe");
    panel1.add(label1,
        new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
            false));
    final JScrollPane scrollPane1 = new JScrollPane();
    panel1.add(scrollPane1,
        new GridConstraints(6, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null,
            null, null, 0, false));
    scrollPane1.setViewportView(currentUserGroup);
    final JLabel label2 = new JLabel();
    label2.setText("Guthaben");
    panel1.add(label2,
        new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
            false));
    final JLabel label3 = new JLabel();
    label3.setText("Solidarzuschlag");
    panel1.add(label3,
        new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
            false));
    final JLabel label4 = new JLabel();
    label4.setText("Mitglieder:");
    panel1.add(label4,
        new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
            false));
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    panel1.add(panel2,
        new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
            null, 0, false));
    cancel = new JButton();
    cancel.setText("Fertig");
    panel2.add(cancel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    leaveUserGroup = new JButton();
    leaveUserGroup.setText("Nutzergruppe verlassen");
    panel1.add(leaveUserGroup, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    solidaritySurcharge = new JLabel();
    solidaritySurcharge.setText("");
    panel1.add(solidaritySurcharge,
        new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
            false));
    value = new JLabel();
    value.setText("");
    panel1.add(value,
        new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
            false));
    final JLabel label5 = new JLabel();
    label5.setText("Schulden dieses Jahr");
    panel1.add(label5,
        new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
            false));
    interestThisYear = new JLabel();
    interestThisYear.setText("");
    panel1.add(interestThisYear,
        new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
            false));
    editSoli = new JButton();
    editSoli.setText("Solidarzuschlag bearbeiten");
    panel1.add(editSoli, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JLabel label6 = new JLabel();
    label6.setText("zuletzt geändert");
    panel1.add(label6,
        new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 2,
            false));
    updateInfo = new JLabel();
    updateInfo.setEnabled(false);
    updateInfo.setText("");
    panel1.add(updateInfo,
        new GridConstraints(4, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
            false));
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
    main.add(panel3,
        new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
            null, 0, false));
    final JLabel label7 = new JLabel();
    label7.setText("wechsle zu Nutzergruppe mit: ");
    panel3.add(label7,
        new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
            false));
    final JScrollPane scrollPane2 = new JScrollPane();
    panel3.add(scrollPane2,
        new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null,
            null, null, 0, false));
    scrollPane2.setViewportView(userGroupSearchBoxView.$$$getRootComponent$$$());
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
    panel3.add(panel4,
        new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
            null, 0, false));
    changeUserGroup = new JButton();
    changeUserGroup.setText("Wechseln");
    panel4.add(changeUserGroup, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    username = new JTextField();
    panel4.add(username, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST,
        GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW,
        GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    final JLabel label8 = new JLabel();
    label8.setText("Gruppenmitglieder:");
    panel4.add(label8,
        new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
            false));
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return main;
  }
}
