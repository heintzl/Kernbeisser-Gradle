package kernbeisser.Forms.FormImplemetations.User;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.util.function.Supplier;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.FormController;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import kernbeisser.Useful.Users;
import lombok.Getter;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class UserController extends FormController<UserView, UserModel, User> {

  @Getter private boolean beginner = false;

  public UserController() {
    super(new UserModel());
  }

  public static UserController getBeginnerUserController() {
    var controller = new UserController();
    controller.beginner = true;
    return controller;
  }

  @Override
  public @NotNull UserModel getModel() {
    return model;
  }

  public void validateUser(User user, Mode mode) throws CannotParseException {
    if (mode != Mode.REMOVE) {
      int shares = user.getShares();
      boolean fullMember =
          user.getPermissions().contains(PermissionConstants.FULL_MEMBER.getPermission());
      if (shares > 0 && !fullMember && getView().askForAddPermissionFullMember(shares)) {
        user.getPermissions().add(PermissionConstants.FULL_MEMBER.getPermission());
      } else {
        if (shares == 0 && fullMember && getView().askForRemovePermissionFullMember()) {
          user.getPermissions().remove(PermissionConstants.FULL_MEMBER.getPermission());
        }
      }
    }
    if (mode == Mode.ADD) {
      String passwordToken = Users.generateToken();
      user.setPassword(
          BCrypt.withDefaults()
              .hashToString(Setting.HASH_COSTS.getIntValue(), passwordToken.toCharArray()));
      user.setForcePasswordChange(true);
      user.setUserGroup(new UserGroup());
      Tools.persist(user.getUserGroup());
      getView().showPasswordToken(passwordToken, user);
    }
  }

  public void validateFullname(User user, Mode mode) throws CannotParseException {
    if (mode != Mode.REMOVE) {
      if (model.fullNameExists(user)) {
        getView().wrongFullname(user.getFullName());
        throw new CannotParseException();
      }
    }
  }

  @Override
  public void fillView(UserView userView) {}

  void refreshUsername() {
    var view = getView();
    String originalUserName = getObjectContainer().getOriginal().getUsername();
    if (originalUserName == null || originalUserName.isEmpty()) {
      String firstName = view.getFirstName();
      String surName = view.getSurname();
      if (surName != null && firstName != null) {
        getView()
            .setUsername(
                model
                    .generateUsername(
                        firstName.toLowerCase().replace(" ", ""), surName.toLowerCase())
                    .replace(" ", ""));
      }
    }
  }

  public boolean isUsernameUnique(String username) {
    return model.usernameExists(username);
  }

  @Key(PermissionKey.REMOVE_USER)
  private void generalRemovePermission() {}

  @Key(PermissionKey.ACTION_ADD_BEGINNER)
  private void beginnerRemovePermission() {}

  @Override
  @Key(PermissionKey.ADD_USER)
  public void addPermission() {}

  @Override
  @Key(PermissionKey.EDIT_USER)
  public void editPermission() {}

  @Override
  public void removePermission() {
    try {
      generalRemovePermission();
    } catch (PermissionKeyRequiredException e) {
      beginnerRemovePermission();
    }
  }

  @Override
  public ObjectForm<User> getObjectContainer() {
    return getView().getObjectForm();
  }

  @Override
  public Supplier<User> defaultFactory() {
    return User::new;
  }

  @Override
  public void remove(User user) {
    if (!getView().confirmDelete()) {
      return;
    }
    if (!user.canDelete()) {
      getView().messageDeleteSuccess(false);
      return;
    }
    var userGroup = user.getUserGroup();
    double userValue = userGroup.getValue();
    if (userGroup.getMembers().size() > 1) {
      getView().messageUserIsInGroup();
      return;
    }
    if (userValue != 0.0) {
      getView().messageUserBalanceExists(userValue);
      return;
    }
    ;
    getView().messageDeleteSuccess(user.delete());
  }
}
