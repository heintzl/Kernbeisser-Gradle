package kernbeisser.Windows.ChangePassword;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

public class ChangePasswordController
    implements Controller<ChangePasswordView, ChangePasswordModel> {
  private final ChangePasswordModel model;
  private final ChangePasswordView view;

  public ChangePasswordController(User user, boolean verifyWithOldPassword) {
    model = new ChangePasswordModel(user, verifyWithOldPassword);
    view = new ChangePasswordView();
  }

  @NotNull
  @Override
  public ChangePasswordView getView() {
    return view;
  }

  @NotNull
  @Override
  public ChangePasswordModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setVerifyWithOldEnable(model.verifyWithOldPassword());
  }

  private int getPasswordStrength(String password) {
    int security = 0;
    if (password.matches(".*[a-z].*")) {
      security++;
    }
    if (password.matches(".*[A-Z].*")) {
      security++;
    }
    if (password.matches(".*\\d.*")) {
      security++;
    }
    if (password.matches(".*[@#$%^&*].*")) {
      security++;
    }
    if (password.matches(".{8,}")) {
      security++;
    }
    return security;
  }

  private boolean comparePasswords() {
    return view.getNewPassword().equals(view.getRepeatedPassword());
  }

  private boolean lengthValid(String password) {
    return !(password.length() < Setting.MIN_PASSWORD_LENGTH.getIntValue());
  }

  private boolean strengthValid(int strength) {
    return !(Setting.MIN_REQUIRED_PASSWORD_STRENGTH.getIntValue() > strength);
  }

  private boolean validOldPassword(String password) {
    return model.checkPassword(password);
  }

  void refreshPasswordStrength() {
    show(getPasswordStrength(view.getNewPassword()));
  }

  void show(int passwordStrength) {
    switch (passwordStrength) {
      case 0:
        view.setPasswordStrength(Strength.TO_LOW);
        break;
      case 1:
        view.setPasswordStrength(Strength.LOW);
        break;
      case 2:
        view.setPasswordStrength(Strength.NORMAL);
        break;
      case 3:
        view.setPasswordStrength(Strength.GOOD);
        break;
      case 4:
        view.setPasswordStrength(Strength.OPTIMAL);
        break;
      case 5:
        view.setPasswordStrength(Strength.LEGENDARY);
        break;
    }
  }

  boolean checkPassword() {
    if (!((!model.verifyWithOldPassword()) || validOldPassword(view.getCurrentPassword()))) {
      view.currentPasswordEnteredWrong();
      return false;
    }
    if (!comparePasswords()) {
      view.passwordsDontMatch();
      return false;
    } else {
      view.passwordsMatch();
      int passwordStrength = getPasswordStrength(view.getNewPassword());
      if (lengthValid(view.getNewPassword())) {
        if (strengthValid(passwordStrength)) {
          show(passwordStrength);
        } else {
          view.setPasswordStrength(Strength.TO_LOW);
          return false;
        }
      } else {
        view.setPasswordStrength(Strength.LENGTH_TO_SMALL);
        return false;
      }
      return true;
    }
  }

  void changePassword() {
    if (checkPassword()) {
      model.changePassword(
          BCrypt.withDefaults()
              .hashToString(Setting.HASH_COSTS.getIntValue(), view.getNewPassword().toCharArray()));
      view.passwordChanged();
      view.back();
    }
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
