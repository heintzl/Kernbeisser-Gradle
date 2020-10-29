package kernbeisser.Windows.ChangePassword;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class ChangePasswordController extends Controller<ChangePasswordView, ChangePasswordModel> {

  public ChangePasswordController(User user, boolean verifyWithOldPassword) {
    super(new ChangePasswordModel(user, verifyWithOldPassword));
  }

  @NotNull
  @Override
  public ChangePasswordModel getModel() {
    return model;
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
    return getView().getNewPassword().equals(getView().getRepeatedPassword());
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
    show(getPasswordStrength(getView().getNewPassword()));
  }

  void show(int passwordStrength) {
    switch (passwordStrength) {
      case 0:
        getView().setPasswordStrength(PasswordStrength.TO_LOW);
        break;
      case 1:
        getView().setPasswordStrength(PasswordStrength.LOW);
        break;
      case 2:
        getView().setPasswordStrength(PasswordStrength.NORMAL);
        break;
      case 3:
        getView().setPasswordStrength(PasswordStrength.GOOD);
        break;
      case 4:
        getView().setPasswordStrength(PasswordStrength.OPTIMAL);
        break;
      case 5:
        getView().setPasswordStrength(PasswordStrength.LEGENDARY);
        break;
    }
  }

  boolean checkPassword() {
    if (!((!model.verifyWithOldPassword()) || validOldPassword(getView().getCurrentPassword()))) {
      getView().currentPasswordEnteredWrong();
      return false;
    }
    if (!comparePasswords()) {
      getView().passwordsDontMatch();
      return false;
    } else {
      getView().passwordsMatch();
      int passwordStrength = getPasswordStrength(getView().getNewPassword());
      if (lengthValid(getView().getNewPassword())) {
        if (strengthValid(passwordStrength)) {
          show(passwordStrength);
        } else {
          getView().setPasswordStrength(PasswordStrength.TO_LOW);
          return false;
        }
      } else {
        getView().setPasswordStrength(PasswordStrength.LENGTH_TO_SMALL);
        return false;
      }
      return true;
    }
  }

  void changePassword() {
    if (checkPassword()) {
      model.changePassword(
          BCrypt.withDefaults()
              .hashToString(
                  Setting.HASH_COSTS.getIntValue(), getView().getNewPassword().toCharArray()));
      getView().passwordChanged();
      getView().back();
    }
  }

  @Override
  public void fillView(ChangePasswordView changePasswordView) {
    changePasswordView.setVerifyWithOldEnable(model.verifyWithOldPassword());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
