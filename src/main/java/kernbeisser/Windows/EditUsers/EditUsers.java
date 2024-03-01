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
import kernbeisser.Exeptions.MissingFullMemberException;
import kernbeisser.Forms.FormImplemetations.User.UserController;
import kernbeisser.Forms.FormImplemetations.User.UserView;
import kernbeisser.Forms.ObjectView.ObjectViewController;
import kernbeisser.Forms.ObjectView.ObjectViewView;
import kernbeisser.Reports.TrialMemberReport;
import kernbeisser.Useful.Tools;
import kernbeisser.Useful.Users;
import kernbeisser.Windows.EditUserGroup.EditUserGroupController;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.ViewContainers.SubWindow;
import rs.groump.AccessDeniedException;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class EditUsers extends ObjectViewController<User> {

  UserFilter userFilter =
      new UserFilter(() -> getSearchBoxController().invokeSearch(), UserFilter.FILTER_ACTIVE);
  boolean hasAdminTools = false;
  boolean hasTrialMemberReport = false;

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
    addComponents(userFilter.createFilterUIComponents());
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
    JButton createTestUser = new JButton("Testuser erstellen");
    createTestUser.setIcon(IconFontSwing.buildIcon(FontAwesome.USER_SECRET, 20, Color.BLUE));
    createTestUser.setToolTipText(
        "Erstellt eine Kopie des markierten Benutzers mit seinen Berechtigungen. Der User befindet sich anschließend in deiner Benutzergruppe");
    addButton(createTestUser, this::createTestUser);
    hasAdminTools = true;
  }

  @Key(PermissionKey.ACTION_ADD_TRIAL_MEMBER)
  private void addTrialMemberReport() {
    if (hasTrialMemberReport) return;
    JButton trialMemberReport =
        new JButton("Probemitglieder-Liste") {
          @Override
          public void setEnabled(boolean b) {
            super.setEnabled(true);
          }
        };
    trialMemberReport.setIcon(
        IconFontSwing.buildIcon(FontAwesome.LIST, 20, new Color(255, 115, 0)));
    trialMemberReport.setToolTipText("Erzeugt die Liste der Probemitglieder");
    addButton(trialMemberReport, e -> showTrialMemberReport());
    hasTrialMemberReport = true;
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
    new EditUserGroupController(user, LogInModel.getLoggedIn())
        .withCloseEvent(() -> fillView(getView()))
        .openIn(new SubWindow(getView().traceViewContainer()));
  }

  private void createTestUser(User user) {
    try {
      JOptionPane.showMessageDialog(
          getView().getContent(),
          "Der User "
              + kernbeisser.Tasks.Users.createTestUserFrom(user).getUsername()
              + " wurde erfolgreich erstellt. Passwort ist das selbe, wie deins.",
          "Testuser erstellen",
          JOptionPane.INFORMATION_MESSAGE);
    } catch (MissingFullMemberException e) {
      JOptionPane.showMessageDialog(
          getView().getContent(),
          "Das dürfen leider nur Vollmitglieder",
          "Testuser erstellen",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  public boolean verifyPasswordChange(String username) {
    return JOptionPane.showConfirmDialog(
            getView().getTopComponent(),
            "Soll das Passwort von "
                + username
                + " wirklich zurückgesetzt werden?\nDieser Vorgang kann nicht rückgängig gemacht werden.")
        == 0;
  }

  public void showTrialMemberReport() {
    new TrialMemberReport()
        .sendToPrinter("Probemitgliederliste wird gedruckt", Tools::showUnexpectedErrorWarning);
  }

  @Override
  public void fillView(ObjectViewView<User> userObjectViewView) {
    super.fillView(userObjectViewView);
    try {
      addAdministrationTools();
      addTrialMemberReport();
    } catch (AccessDeniedException ignored) {
    }
  }
}
