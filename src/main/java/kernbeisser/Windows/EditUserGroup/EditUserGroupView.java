package kernbeisser.Windows.EditUserGroup;

import java.awt.event.ActionEvent;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Exeptions.CannotLogInException;
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

  @Linked private EditUserGroupController controller;

  @Linked private SearchBoxController<UserGroup> searchBoxController;

  private void createUIComponents() {
    userGroupSearchBoxView = searchBoxController.getView();
    currentUserGroup =
        new ObjectTable<>(
            Column.create("Vorname", User::getFirstName),
            Column.create("Nachname", User::getSurname),
            Column.create("Benutzername", User::getUsername));
  }

  String getUsername() {
    return username.getText();
  }

  private String toEuro(double value) {
    return String.format("%.2f€", value);
  }

  void setCurrentUserGroup(UserGroup userGroup) {
    currentUserGroup.setObjects(userGroup.getMembers());
    value.setText(toEuro(userGroup.getValue()));
    interestThisYear.setText(toEuro(userGroup.getInterestThisYear() / 100.));
    solidaritySurcharge.setText(String.format("%.2f%%", userGroup.getSolidaritySurcharge() * 100));
  }

  private void leaveUserGroup(ActionEvent event) {
    if (controller.getMemberCount() < 2) {
      JOptionPane.showMessageDialog(
          getTopComponent(), "Sie sind bereits alleine in einer Nutzergruppe!");
    } else {
      if (JOptionPane.showConfirmDialog(
              getTopComponent(),
              "Möchten sie wirklich ihre Nutzergruppe verlassen?\nGuthaben und Solidaraufschlag werden nicht übernommen")
          == 0) {
        controller.leaveUserGroup();
        JOptionPane.showMessageDialog(
            getTopComponent(),
            "Sie sind erfolgreich aus ihrer Nutzergruppe ausgestiegen\nund nun alleine in einer Nutzergruppe.");
      }
    }
  }

  private void changeUserGroup(ActionEvent event) {

    if (controller.getMemberCount() < 2) {
      if (JOptionPane.showConfirmDialog(
              getTopComponent(),
              "Sie sind derzeit alleine in einer Nutzergruppe.\nWollen sie wirklich ihre aktuelle Nutzergruppe auflösen\nund ihr aktuelles Guthaben auf die neue Nutzergruppe übertragen?")
          != 0) {
        return;
      }
    } else {
      JOptionPane.showMessageDialog(
          getTopComponent(),
          "Wollen sie wirklich ihre aktuelle Nutzergruppe verlassen?\nDas Guthaben wird nicht übertragen, da noch weitere Nutzer\nin ihrer Nutzergruppe sind.");
    }
    try {
      controller.changeUserGroup();
      JOptionPane.showMessageDialog(
          getTopComponent(),
          "Sie sind erfolgreich der Nutzergruppe von " + getUsername() + " beigetreiten");
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

  private double requestSoli() {
    String txt =
        JOptionPane.showInputDialog(
            getTopComponent(), "bitte geben sie den neuen Solidarzuschlag ein[%]");
    try {
      return Double.parseDouble(txt.replaceAll("[^\\d,.]", "").replace(",", ".")) / 100.;
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(
          getTopComponent(), "der eingegebene Wert kann nicht als zahl interpretiert werden");
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
    return "Nutzergruppe wechseln";
  }
}
