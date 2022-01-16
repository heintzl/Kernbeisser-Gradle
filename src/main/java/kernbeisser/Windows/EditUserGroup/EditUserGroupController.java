package kernbeisser.Windows.EditUserGroup;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.*;
import kernbeisser.CustomComponents.Dialogs.LogInDialog;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.CannotLogInException;
import kernbeisser.Exeptions.MissingFullMemberException;
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

  private String getUserGroupText(UserGroup userGroup) {
    String userGroupText =
        userGroup.getMembers().stream().map(User::getFullName).collect(Collectors.joining(", "));
    return Pattern.compile("^(.+), ([^,]+)$").matcher(userGroupText).replaceAll("$1 und $2");
  }

  private void select(UserGroup userGroup) {
    getView()
        .setUsername(
            Tools.optional(() -> getUserGroupText(userGroup)).orElse("[Keine Leseberechtigung]"));
  }

  @Override
  public @NotNull EditUserGroupModel getModel() {
    return model;
  }

  @Override
  public void fillView(EditUserGroupView editUserGroupView) {
    getView().setCurrentUserGroup(model.getUser().getUserGroup());
  }

  public boolean leaveUserGroup() {
    try {
      Users.leaveUserGroup(model.getUser());
      pushViewRefresh();
      return true;
    } catch (MissingFullMemberException e) {
      return false;
    }
  }

  public boolean changeUserGroup() throws CannotLogInException, MissingFullMemberException {
    boolean success;
    Optional<UserGroup> targetGroup = userGroupSearchBoxController.getSelectedObject();
    if (targetGroup.isPresent()
        && LogInDialog.showLogInRequest(
            getView().getTopComponent(), model.getLogIns(targetGroup.get().getMembers()))) {
      success = model.changeUserGroup(model.getUser().getId(), targetGroup.get().getId());
    } else throw new CannotLogInException();
    pushViewRefresh();
    return success;
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
