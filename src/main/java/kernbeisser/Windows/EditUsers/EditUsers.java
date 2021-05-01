package kernbeisser.Windows.EditUsers;

import java.awt.*;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.Filters.UserFilter;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.FormImplemetations.User.UserController;
import kernbeisser.Forms.ObjectView.ObjectViewController;
import kernbeisser.Forms.ObjectView.ObjectViewView;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Users;
import kernbeisser.Windows.EditUserGroup.EditUserGroupController;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.ViewContainers.SubWindow;

public class EditUsers extends ObjectViewController<User> {

  UserFilter userFilter =
      new UserFilter(() -> getSearchBoxController().invokeSearch(), UserFilter.FILTER_ACTIVE);

  @Key(PermissionKey.ACTION_OPEN_EDIT_USERS)
  public EditUsers() {
    super("Benutzer bearbeiten", new UserController(), false);
    setSearchBoxController(
        userFilter::searchable,
        Column.create("Vorname", User::getFirstName),
        Column.create("Nachname", User::getSurname),
        Column.create("Benutzername", User::getUsername),
        Column.create(
            "Guthaben",
            u -> String.format("%.2f€", u.getUserGroup().getValue()),
            SwingConstants.RIGHT,
            Column.NUMBER_SORTER));
    addRadioButtons(userFilter.createFilterOptionButtons());
  }

  @Key(PermissionKey.ACTION_OPEN_ADMIN_TOOLS)
  private void addAdministrationTools() {
    JButton resetPassword = new JButton("Passwort zurücksetzen");
    resetPassword.setIcon(IconFontSwing.buildIcon(FontAwesome.USER_SECRET, 20, Color.DARK_GRAY));
    resetPassword.setToolTipText(
        "Erzeugt für den Benutzer ein zufälliges Passwort. Dieses muss beim nächsten Anmelden geändert werden.");
    addButton(resetPassword, this::resetPassword);
    JButton editUserGroup = new JButton("Benutzergruppe bearbeiten");
    editUserGroup.setIcon(IconFontSwing.buildIcon(FontAwesome.USERS, 20, Color.BLUE));
    editUserGroup.setToolTipText(
        "Ermöglicht es, die Benutzergruppe für einen Benutzer zu wechseln, ohne dass der Wechsel mit Passwort bestätigt werden muss");
    addButton(editUserGroup, this::openUserGroupEditor);
  }

  public void resetPassword(User user) {
    if (verifyPasswordChange(user.getUsername())) {
      showPasswordToken(Users.resetPassword(user), user);
    }
  }

  public void openUserGroupEditor(User user) {
    new EditUserGroupController(user, LogInModel.getLoggedIn())
        .withCloseEvent(() -> fillView(getView()))
        .openIn(new SubWindow(getView().traceViewContainer()));
  }

  public void showPasswordToken(String resetPassword, User user) {
    Object message =
        new Object[] {
          "Das generierte Passwort ist Folgendes:\n",
          new JTextField(resetPassword) {
            {
              setEditable(false);
            }
          },
          "Bitte logge dich möglichst zeitnah ein,\num das Passwort zu ändern."
        };
    JOptionPane.showMessageDialog(
        getView().getTopComponent(),
        message,
        "Generiertes Password für " + user.getFullName(),
        JOptionPane.INFORMATION_MESSAGE);
  }

  public boolean verifyPasswordChange(String username) {
    return JOptionPane.showConfirmDialog(
            getView().getTopComponent(),
            "Soll das Passwort von "
                + username
                + " wirklich zurückgesetzt werden?\nDieser Vorgang kann nicht rückgängig gemacht werden.")
        == 0;
  }

  @Override
  public void fillView(ObjectViewView<User> userObjectViewView) {
    super.fillView(userObjectViewView);
    try {
      addAdministrationTools();
    } catch (PermissionKeyRequiredException ignored) {
    }
  }
}
