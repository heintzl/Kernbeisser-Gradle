package kernbeisser.Windows.Setting;

import java.util.Arrays;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Main;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class SettingController extends Controller<SettingView, SettingModel> {
  public SettingController() {
    super(new SettingModel());
  }

  @NotNull
  @Override
  public SettingModel getModel() {
    return model;
  }

  @Override
  public void fillView(SettingView settingView) {
    getView().setEditEnable(false);
    getView().setValues(Arrays.asList(Setting.values()));
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public void apply() {
    switch (Setting.getExpectedType(model.getSelectedSettingValue()).getSimpleName()) {
      case "Integer":
        try {
          Integer.parseInt(getView().getValue());
        } catch (NumberFormatException e) {
          if (!getView().commitType("ganze Zahl(-2147483648 bis +2147483647)")) {
            return;
          }
        }
        break;
      case "Long":
        try {
          Long.parseLong(getView().getValue());
        } catch (NumberFormatException e) {
          if (!getView()
              .commitType(
                  "ganze Zahl(-9,223,372,036,854,775,808 bis +9,223,372,036,854,775,807)")) {
            return;
          }
        }
        break;
      case "Double":
        try {
          Double.parseDouble(getView().getValue());
        } catch (NumberFormatException e) {
          if (!getView()
              .commitType("Kommazahl(-4.94065645841246544e-324d bis +1.79769313486231570e+308d)")) {
            return;
          }
        }
        break;
      case "Float":
        try {
          Float.parseFloat(getView().getValue());
        } catch (NumberFormatException e) {
          if (!getView()
              .commitType("Kommazahl(1.40129846432481707e-45 bis 3.40282346638528860e+38)")) {
            return;
          }
        }
        break;
      case "Boolean":
        if (getView().getValue().equals("false") || getView().getValue().equals("true")) {
          break;
        } else {
          if (!getView().commitType("Boolean wert(ja = true, nein = false)")) {
            return;
          }
        }
        break;
    }
    model.edit(getView().getValue());
    getView().setValues(Arrays.asList(Setting.values()));
    Main.logger.info(
        "User["
            + LogInModel.getLoggedIn().getId()
            + "] set "
            + model.getSelectedSettingValue().toString()
            + " to '"
            + getView().getValue()
            + "'");
  }

  public void cancel() {
    getView().back();
  }

  public void resetAllSettings() {
    if (getView().commitResetSettings()) {
      for (Setting value : Setting.values()) {
        if (value != Setting.DB_INITIALIZED) {
          value.changeValue(value.getDefaultValue());
        }
      }
      getView().setValues(Arrays.asList(Setting.values()));
      Main.logger.info(
          "User[" + LogInModel.getLoggedIn().getId() + "] set all settings to default");
    }
  }

  public void select(Setting settingValue) {
    getView().setValue(settingValue.getValue());
    getView().setSelectedSetting(settingValue);
    getView().setEditEnable(true);
    model.setSelectedValue(settingValue);
  }
}
