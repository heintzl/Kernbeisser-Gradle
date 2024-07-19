package kernbeisser.Forms.FormImplemetations.User;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.util.function.Supplier;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.Permission_;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Enums.Setting;
import kernbeisser.Forms.FormController;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Useful.Tools;
import kernbeisser.Useful.Users;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import rs.groump.AccessDeniedException;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class UserController extends FormController<UserView, UserModel, User> {

  @Getter private boolean trialMemberMode = false;

  public UserController() {
    super(new UserModel());
  }

  public static UserController getBeginnerUserController() {
    UserController controller = new UserController();
    controller.trialMemberMode = true;
    return controller;
  }

  @Override
  public @NotNull UserModel getModel() {
    return model;
  }

  public void validateUser(User user, Mode mode) throws CannotParseException {
    if (mode != Mode.REMOVE) {
      try {
        int shares = user.getShares();
        boolean fullMember = user.isFullMember();
        boolean trialMember = user.isTrialMember();
        if (shares > 0
            && !fullMember
            && getView().askForAddPermissionFullMember(shares, trialMember)) {
          user.getPermissions().add(PermissionConstants.FULL_MEMBER.getPermission());
          if (trialMember) {
            user.getPermissions().remove(PermissionConstants.TRIAL_MEMBER.getPermission());
          }
        } else {
          if (shares == 0 && fullMember && getView().askForRemovePermissionFullMember()) {
            user.getPermissions().remove(PermissionConstants.FULL_MEMBER.getPermission());
          }
        }
      } catch (AccessDeniedException ignored) {
      }
    }
    if (mode == Mode.ADD) {
      String passwordToken = Users.generateToken();
      user.setPassword(
          BCrypt.withDefaults()
              .hashToString(Setting.HASH_COSTS.getIntValue(), passwordToken.toCharArray()));
      user.setForcePasswordChange(true);
      user.getPermissions().add(PermissionConstants.BASIC_ACCESS.getPermission());
      if (this.isTrialMemberMode()) {
        user.getPermissions().add(PermissionConstants.TRIAL_MEMBER.getPermission());
      }
      user.setNewUserGroup();
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

  public void validatePermissions(User user, Mode mode) throws CannotParseException {
    if (mode != Mode.REMOVE) {
      if (model.invalidMembershipRoles(user)) {
        getView().invalidMembership();
        throw new CannotParseException();
      }
    }
  }

  @Override
  public void fillView(UserView userView) {}

  void refreshUsername() {
    UserView view = getView();
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

  @Key(PermissionKey.ACTION_ADD_TRIAL_MEMBER)
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
    } catch (AccessDeniedException e) {
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
    UserGroup userGroup = user.getUserGroup();
    double userValue = userGroup.getValue();
    if (!user.isTestOnly() && userGroup.getMembers().size() > 1) {
      getView().messageUserIsInGroup();
      return;
    }
    if (userValue != 0.0 && !(user.isTestOnly() && user.getUserGroup().getMembers().size() != 1)) {
      getView().messageUserBalanceExists(userValue);
      return;
    }
    getView().messageDeleteSuccess(user.delete());
  }

  public Source<Permission> getPermissionSource() {
    return () ->
        QueryBuilder.selectAll(Permission.class)
            .where(
                Permission_.name
                    .in(
                        PermissionConstants.APPLICATION.nameId(),
                        PermissionConstants.IMPORT.nameId(),
                        PermissionConstants.IN_RELATION_TO_OWN_USER.nameId())
                    .not())
            .getResultList();
  }
}
