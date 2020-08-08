package kernbeisser.Windows.Setting;

import kernbeisser.DBEntities.SettingValue;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Main;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class SettingController implements Controller<SettingView, SettingModel> {
  private final SettingModel model;
  private SettingView view;

  public SettingController() {
    model = new SettingModel();
  }

  @NotNull
  @Override
  public SettingModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setEditEnable(false);
    getView().setValues(SettingValue.getAll(null));
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public void apply() {
    switch (Setting.getExpectedType(model.getSelectedSettingValue().getSetting()).getSimpleName()) {
      case "Integer":
        try {
          Integer.parseInt(view.getValue());
        } catch (NumberFormatException e) {
          if (!view.commitType("ganze Zahl(-2147483648 bis +2147483647)")) {
            return;
          }
        }
        break;
      case "Long":
        try {
          Long.parseLong(view.getValue());
        } catch (NumberFormatException e) {
          if (!view.commitType(
              "ganze Zahl(-9,223,372,036,854,775,808 bis +9,223,372,036,854,775,807)")) {
            return;
          }
        }
        break;
      case "Double":
        try {
          Double.parseDouble(view.getValue());
        } catch (NumberFormatException e) {
          if (!view.commitType(
              "Kommazahl(-4.94065645841246544e-324d bis +1.79769313486231570e+308d)")) {
            return;
          }
        }
        break;
      case "Float":
        try {
          Float.parseFloat(view.getValue());
        } catch (NumberFormatException e) {
          if (!view.commitType("Kommazahl(1.40129846432481707e-45 bis 3.40282346638528860e+38)")) {
            return;
          }
        }
        break;
      case "Boolean":
        if (view.getValue().equals("false") || view.getValue().equals("true")) {
          break;
        } else {
          if (!view.commitType("Boolean wert(ja = true, nein = false)")) {
            return;
          }
        }
        break;
    }
    model.edit(view.getValue());
    view.setValues(SettingValue.getAll(null));
    Main.logger.info(
        "User["
            + LogInModel.getLoggedIn().getId()
            + "] set "
            + model.getSelectedSettingValue().toString()
            + " to '"
            + view.getValue()
            + "'");
  }

  public void cancel() {
    view.back();
  }

  public void resetAllSettings() {
    if (view.commitResetSettings()) {
      for (Setting value : Setting.values()) {
        if (value != Setting.DB_INITIALIZED) {
          value.setValue(value.getDefaultValue());
        }
      }
      getView().setValues(SettingValue.getAll(null));
      Main.logger.info(
          "User[" + LogInModel.getLoggedIn().getId() + "] set all settings to default");
    }
  }

  public void select(SettingValue settingValue) {
    view.setValue(settingValue.getValue());
    view.setSelectedSetting(settingValue.getSetting());
    view.setEditEnable(true);
    model.setSelectedValue(settingValue);
  }
}
