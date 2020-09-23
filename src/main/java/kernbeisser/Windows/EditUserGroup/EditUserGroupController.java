package kernbeisser.Windows.EditUserGroup;

import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.CannotLogInException;
import kernbeisser.Tasks.Users;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class EditUserGroupController implements IController<EditUserGroupView, EditUserGroupModel> {
  private EditUserGroupView view;
  private final EditUserGroupModel model;

  @Linked private final SearchBoxController<UserGroup> userGroupSearchBoxController;

  public EditUserGroupController(User user) {
    model = new EditUserGroupModel(user);
    userGroupSearchBoxController =
        new SearchBoxController<>(
            UserGroup::defaultSearch,
            Column.create("Mitglieder", UserGroup::getMemberString, SwingConstants.LEFT),
            Column.create(
                "Guthaben", e -> String.format("%.2f€", e.getValue()), SwingConstants.RIGHT),
            Column.create(
                "Solidarzuschlag",
                e -> String.format("%.2f%%", e.getSolidaritySurcharge() * 100),
                SwingConstants.RIGHT));
    userGroupSearchBoxController.addSelectionListener(this::select);
  }

  private void select(UserGroup userGroup) {
    view.setUsername(userGroup.getMembers().iterator().next().getUsername());
  }

  @Override
  public @NotNull EditUserGroupModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setCurrentUserGroup(model.getUser().getUserGroup());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public void leaveUserGroup() {
    Users.leaveUserGroup(model.getUser());
    pushViewRefresh();
  }

  public void changeUserGroup(String password) throws CannotLogInException {
    model.changeUserGroup(
        model.getUser().getId(),
        User.getByUsername(view.getUsername()).getUserGroup().getId(),
        password);
    pushViewRefresh();
  }

  private void pushViewRefresh() {
    model.refreshData();
    view.setCurrentUserGroup(model.getUser().getUserGroup());
    userGroupSearchBoxController.search();
  }

  public int getMemberCount() {
    return model.getUser().getUserGroup().getMembers().size();
  }
}
