package kernbeisser.Windows.EditUserGroup;

import javax.swing.*;
import kernbeisser.CustomComponents.Dialogs.LogInDialog;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.CannotLogInException;
import kernbeisser.Tasks.Users;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class EditUserGroupController extends Controller<EditUserGroupView, EditUserGroupModel> {

  @Linked private final SearchBoxController<UserGroup> userGroupSearchBoxController;

  public EditUserGroupController(User user) {
    super(new EditUserGroupModel(user));
    userGroupSearchBoxController =
        new SearchBoxController<>(
            UserGroup::defaultSearch,
            Column.create("Mitglieder", UserGroup::getMemberString, SwingConstants.LEFT),
            Column.create(
                "Solidarzuschlag",
                e -> String.format("%.2f%%", e.getSolidaritySurcharge() * 100),
                SwingConstants.RIGHT));
    userGroupSearchBoxController.addSelectionListener(this::select);
  }

  private void select(UserGroup userGroup) {
    getView().setUsername(userGroup.getMembers().iterator().next().getUsername());
  }

  @Override
  public @NotNull EditUserGroupModel getModel() {
    return model;
  }

  @Override
  public void fillView(EditUserGroupView editUserGroupView) {
    getView().setCurrentUserGroup(model.getUser().getUserGroup());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[] {};
  }

  public void leaveUserGroup() {
    Users.leaveUserGroup(model.getUser());
    pushViewRefresh();
  }

  public void changeUserGroup() throws CannotLogInException {
    if (LogInDialog.showLogInRequest(
        getView().getTopComponent(),
        User.getByUsername(getView().getUsername()).getUserGroup().getMembers())) {
      model.changeUserGroup(
          model.getUser().getId(),
          User.getByUsername(getView().getUsername()).getUserGroup().getId());
    } else throw new CannotLogInException();
    pushViewRefresh();
  }

  private void pushViewRefresh() {
    model.refreshData();
    getView().setCurrentUserGroup(model.getUser().getUserGroup());
    userGroupSearchBoxController.search();
  }

  public void editSoli(double newValue) {
    model.changeSoli(newValue);
    pushViewRefresh();
  }

  public int getMemberCount() {
    return model.getUser().getUserGroup().getMembers().size();
  }
}
