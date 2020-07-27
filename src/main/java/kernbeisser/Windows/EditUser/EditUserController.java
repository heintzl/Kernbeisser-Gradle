package kernbeisser.Windows.EditUser;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Security.Proxy;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

public class EditUserController implements Controller<EditUserView, EditUserModel> {
  private final EditUserView view;
  private final EditUserModel model;

  public EditUserController(User user, Mode mode) {
    model = new EditUserModel(user == null ? Proxy.getSecureInstance(new User()) : user, mode);
    if (mode == Mode.REMOVE) {
      model.doAction(model.getUser());
      view = null;
    } else {
      this.view = new EditUserView();
    }
  }

  private void changePassword(String to) {
    model
        .getUser()
        .setPassword(
            BCrypt.withDefaults().hashToString(Setting.HASH_COSTS.getIntValue(), to.toCharArray()));
  }

  void requestChangePassword() {
    String password = view.requestPassword();
    if (password == null) {
      return;
    }
    if (password.length() < 4) {
      view.passwordToShort();
      requestChangePassword();
    } else {
      changePassword(password);
      view.passwordChanged();
    }
  }

  @Override
  public @NotNull EditUserModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {}

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[] {
      PermissionKey.USER_USERNAME_READ,
    };
  }

  @Override
  public @NotNull EditUserView getView() {
    return view;
  }

  void doAction() {
    User data;
    try {
      data = view.getObjectForm().getData();
    } catch (CannotParseException e) {
      view.invalidInput();
      view.getObjectForm().markErrors();
      return;
    }
    switch (model.getMode()) {
      case ADD:
        if (!view.getObjectForm().isValid()) {
          view.invalidInput();
          view.getObjectForm().markErrors();
          return;
        }
        if (model.usernameExists(data.getUsername())) {
          view.usernameAlreadyExists();
          return;
        }
        try {
          if (data.getPassword() == null) {
            requestChangePassword();
            data.setPassword(model.getUser().getPassword());
          }
        } catch (/*Access Denied exception TODO:*/ Exception e) {
          e.printStackTrace();
        }
        break;
    }
    if (model.doAction(data)) {
      view.back();
    }
  }

  void refreshUsername() {
    if (model.getMode() == Mode.ADD) {
      User data = view.getObjectForm().getDataIgnoreWrongInput();
      if (data.getSurname() != null && data.getFirstName() != null) {
        view.setUsername(
            model
                .generateUsername(
                    data.getFirstName().toLowerCase().replace(" ", ""),
                    data.getSurname().toLowerCase())
                .replace(" ", ""));
      }
    }
  }
}
