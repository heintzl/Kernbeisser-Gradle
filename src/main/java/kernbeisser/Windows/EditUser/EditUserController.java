package kernbeisser.Windows.EditUser;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Security.Proxy;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class EditUserController extends Controller<EditUserView, EditUserModel> {
  public EditUserController(User user, Mode mode) {
    super(new EditUserModel(user == null ? Proxy.getSecureInstance(new User()) : user, mode));
    if (user != null && mode == Mode.REMOVE) {
      Tools.delete(user);
    }
  }

  @Override
  public @NotNull EditUserModel getModel() {
    return model;
  }

  private User validateUser(User user) throws CannotParseException {
    if (model.getMode() == Mode.ADD) {
      user.setPassword(
          BCrypt.withDefaults()
              .hashToString(Setting.HASH_COSTS.getIntValue(), "start".toCharArray()));
      user.setForcePasswordChange(true);
      user.setUserGroup(new UserGroup());
      Tools.persist(user.getUserGroup());
    }
    return user;
  }

  @Override
  public void fillView(EditUserView editUserView) {
    var view = getView();
    view.getObjectForm().setSource(getModel().getUser());
    view.getObjectForm().setObjectValidator(this::validateUser);
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    switch (getModel().getMode()) {
      case ADD:
        return new PermissionKey[] {PermissionKey.ADD_USER};
      case EDIT:
        return new PermissionKey[] {PermissionKey.EDIT_USER};
      case REMOVE:
        return new PermissionKey[] {PermissionKey.REMOVE_USER};
    }
    throw new UnsupportedOperationException("undefined mode");
  }

  void doAction() {
    var view = getView();
    if (view.getObjectForm().applyMode(model.getMode())) {
      view.back();
    }
  }

  void refreshUsername() {
    if (model.getMode() == Mode.ADD) {
      var view = getView();
      String firstName = view.getFirstName();
      String surName = view.getSurname();
      if (surName != null && firstName != null) {
        getView()
            .setUsername(
                model
                    .generateUsername(
                        firstName.toLowerCase().replace(" ", ""),
                        surName.toLowerCase())
                    .replace(" ", ""));
      }
    }
  }

  public String validateUsername(String s) throws CannotParseException {
    var view = getView();
    switch (model.getMode()) {
      case EDIT:
        if (model.getUser().getUsername().equals(s)) {
          return s;
        }
      case ADD:
        if (model.usernameExists(s)) {
          view.usernameAlreadyExists();
          throw new CannotParseException("username already exists");
        } else return s;
      default:
        throw new UnsupportedOperationException(
            model.getMode() + "is not a supported mode for this form");
    }
  }
}
