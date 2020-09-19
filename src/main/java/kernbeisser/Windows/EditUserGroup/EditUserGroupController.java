package kernbeisser.Windows.EditUserGroup;

import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Enums.PermissionKey;
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
    userGroupSearchBoxController = new SearchBoxController<>(UserGroup::defaultSearch);
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
    view.setCurrentUserGroup(Users.leaveUserGroup(model.getUser()).getUserGroup());
  }

  public void changeUserGroup() {
    view.setCurrentUserGroup(
        Users.switchUserGroup(
                model.getUser(), User.getByUsername(view.getUsername()).getUserGroup())
            .getUserGroup());
  }
}
