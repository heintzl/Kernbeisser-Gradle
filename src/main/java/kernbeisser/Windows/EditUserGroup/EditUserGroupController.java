package kernbeisser.Windows.EditUserGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.*;
import kernbeisser.CustomComponents.Dialogs.LogInDialog;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.CannotLogInException;
import kernbeisser.Security.Key;
import kernbeisser.Tasks.Users;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class EditUserGroupController extends Controller<EditUserGroupView, EditUserGroupModel> {

  @Linked private final SearchBoxController<UserGroup> userGroupSearchBoxController;

  public EditUserGroupController(User user) {
    this(user, null);
  }

  @Key(PermissionKey.ACTION_OPEN_EDIT_USER_GROUP)
  public EditUserGroupController(User user, User caller) {
    super(new EditUserGroupModel(user, caller));
    userGroupSearchBoxController =
        new SearchBoxController<>(
            (s, m) ->
                UserGroup.defaultSearch(s, m).stream()
                    .filter(
                        new Predicate<UserGroup>() {
                          final UserGroup kernbeisserUserGroup =
                              User.getKernbeisserUser().getUserGroup();

                          @Override
                          public boolean test(UserGroup userGroup) {
                            return !kernbeisserUserGroup.equals(userGroup);
                          }
                        })
                    .collect(Collectors.toCollection(ArrayList::new)),
            Columns.create("Mitglieder", UserGroup::getMemberString, SwingConstants.LEFT),
            Columns.create(
                "Solidarzuschlag",
                e -> String.format("%.2f%%", e.getSolidaritySurcharge() * 100),
                SwingConstants.RIGHT));
    userGroupSearchBoxController.addSelectionListener(this::select);
  }

  private void select(UserGroup userGroup) {
    getView()
        .setUsername(
            Tools.optional(userGroup::getMembers)
                .map(Collection::iterator)
                .map(Iterator::next)
                .map(User::getUsername)
                .orElse("[Keine Leseberehtigung]"));
  }

  @Override
  public @NotNull EditUserGroupModel getModel() {
    return model;
  }

  @Override
  public void fillView(EditUserGroupView editUserGroupView) {
    getView().setCurrentUserGroup(model.getUser().getUserGroup());
  }

  public void leaveUserGroup() {
    Users.leaveUserGroup(model.getUser());
    pushViewRefresh();
  }

  public void changeUserGroup() throws CannotLogInException {
    if (LogInDialog.showLogInRequest(getView().getTopComponent(), model.getLogIns())) {
      model.changeUserGroup(
          model.getUser().getId(),
          User.getByUsername(getView().getUsername()).getUserGroup().getId());
    } else throw new CannotLogInException();
    pushViewRefresh();
  }

  private void pushViewRefresh() {
    model.refreshData();
    getView().setCurrentUserGroup(model.getUser().getUserGroup());
    userGroupSearchBoxController.invokeSearch();
  }

  public void editSoli(double newValue) {
    model.changeSoli(newValue);
    pushViewRefresh();
  }

  public int getMemberCount() {
    return model.getUser().getUserGroup().getMembers().size();
  }
}
