package kernbeisser.Windows.EditUsers;

import java.awt.*;
import java.util.stream.Collectors;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.SearchBox.Filters.UserFilter;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.FormImplemetations.User.UserController;
import kernbeisser.Forms.FormImplemetations.User.UserView;
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
  boolean hasAdminTools = false;

  @Key(PermissionKey.ACTION_OPEN_EDIT_USERS)
  public EditUsers() {
    super("Benutzer bearbeiten", new UserController(), false);
    setSearchBoxController(
        userFilter::searchable,
        Columns.create("Vorname", User::getFirstName).withDefaultFilter(),
        Columns.create("Nachname", User::getSurname).withDefaultFilter(),
        Columns.create("Benutzername", User::getUsername),
        Columns.create("Dienste", User::getJobsAsString).withDefaultFilter(),
        Columns.create("Berechtigungen", this::formatPermissions).withDefaultFilter());
    addComponents(userFilter.createFilterOptionButtons());
  }

  @Key(PermissionKey.ACTION_OPEN_ADMIN_TOOLS)
  private void addAdministrationTools() {
    if (hasAdminTools) return;
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
    hasAdminTools = true;
  }

  private String formatPermissions(User u) {
    return u.getPermissionsAsAvailable().stream()
        .filter(
            p ->
                (!p.getName()
                    .matches("@KEY_PERMISSION|@IN_RELATION_TO_OWN_USER|@IMPORT|@APPLICATION")))
        .map(Permission::getNeatName)
        .collect(Collectors.joining(", "));
  }

  public void resetPassword(User user) {
    if (verifyPasswordChange(user.getUsername())) {
      UserView.showPasswordToken(Users.resetPassword(user), user, getView().getTopComponent());
    }
  }

  public void openUserGroupEditor(User user) {
    new EditUserGroupController(user, LogInModel.getLoggedInFromDB())
        .withCloseEvent(() -> fillView(getView()))
        .openIn(new SubWindow(getView().traceViewContainer()));
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
