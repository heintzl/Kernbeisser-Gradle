package kernbeisser.Windows.EditUserGroup;

import static kernbeisser.Useful.Tools.optional;

import java.awt.event.ActionEvent;
import java.util.Collections;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
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
              "Möchtest du wirklich deine Nutzergruppe verlassen?\n"
                  + "Guthaben und Solidaraufschlag werden nicht übernommen...")
          == 0) {
        controller.leaveUserGroup();
        JOptionPane.showMessageDialog(
            getTopComponent(),
            "Du hast deine Nutzergruppe erfolgreich verlassen\n"
                + "und bist nun alleine in einer Nutzergruppe.");
      }
    }
  }

  private void changeUserGroup(ActionEvent event) {

    if (controller.getMemberCount() < 2) {
      if (JOptionPane.showConfirmDialog(
              getTopComponent(),
              "Du bist derzeit alleine in einer Nutzergruppe.\n"
                  + "Willst du wirklich deine aktuelle Nutzergruppe auflösen\n"
                  + "und ihr aktuelles Guthaben auf die neue Nutzergruppe übertragen?")
          != 0) {
        return;
      }
    } else {
      JOptionPane.showMessageDialog(
          getTopComponent(),
          "Willst du wirklich deine aktuelle Nutzergruppe verlassen?\n"
              + "Das Guthaben wird nicht übertragen, da noch weitere Nutzer\n"
              + "in deiner Nutzergruppe sind.");
    }
    try {
      controller.changeUserGroup();
      JOptionPane.showMessageDialog(
          getTopComponent(),
          "Du bist erfolgreich der Nutzergruppe von " + getUsername() + " beigetreten");
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
    return "Nutzergruppe wechseln";
  }
}
