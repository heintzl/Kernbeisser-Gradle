package kernbeisser.Windows.EditUserGroup;

import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class EditUserGroupView implements IView<EditUserGroupController> {
  private ObjectTable<User> currentUserGroup;
  private JButton leaveUserGroup;
  private JButton changeUserGroup;
  private JTextField username;
  private SearchBoxView<UserGroup> userGroupSelection;
  private JButton cancel;
  private JPanel main;
  private JLabel solidaritySurcharge;
  private JLabel value;
  private JLabel interestThisYear;

  @Linked private EditUserGroupController controller;

  @Linked private SearchBoxController<UserGroup> searchBoxController;

  private void createUIComponents() {
    userGroupSelection = searchBoxController.getView();
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
    solidaritySurcharge.setText(toEuro(userGroup.getSolidaritySurcharge()));
  }

  @Override
  public void initialize(EditUserGroupController controller) {
    cancel.addActionListener(e -> back());
    leaveUserGroup.addActionListener(e -> controller.leaveUserGroup());
    changeUserGroup.addActionListener(e -> controller.changeUserGroup());
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }
}
