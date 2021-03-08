package kernbeisser.Windows.Setting;

import java.util.Arrays;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Main;
import kernbeisser.Security.Requires;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import lombok.var;
import org.jetbrains.annotations.NotNull;

@Requires(PermissionKey.ACTION_OPEN_APPLICATION_SETTINGS)
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
    var view = getView();
    view.setEditEnable(false);
    view.setValues(Arrays.asList(Setting.values()));
  }

  public void apply() {
    var view = getView();
    switch (Setting.getExpectedType(model.getSelectedSettingValue()).getSimpleName()) {
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
          if (!getView()
              .commitType(
                  "ganze Zahl(-9,223,372,036,854,775,808 bis +9,223,372,036,854,775,807)")) {
            return;
          }
        }
        break;
      case "Double":
        try {
          Double.parseDouble(view.getValue());
        } catch (NumberFormatException e) {
          if (!getView()
              .commitType("Kommazahl(-4.94065645841246544e-324d bis +1.79769313486231570e+308d)")) {
            return;
          }
        }
        break;
      case "Float":
        try {
          Float.parseFloat(view.getValue());
        } catch (NumberFormatException e) {
          if (!getView()
              .commitType("Kommazahl(1.40129846432481707e-45 bis 3.40282346638528860e+38)")) {
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
    view.setValues(Arrays.asList(Setting.values()));
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
    var view = getView();
    view.back();
  }

  public void resetAllSettings() {
    var view = getView();
    if (view.commitResetSettings()) {
      for (Setting value : Setting.values()) {
        if (value != Setting.DB_INITIALIZED) {
          value.changeValue(value.getDefaultValue());
        }
      }
      view.setValues(Arrays.asList(Setting.values()));
      Main.logger.info(
          "User[" + LogInModel.getLoggedIn().getId() + "] set all settings to default");
    }
  }

  public void select(Setting settingValue) {
    var view = getView();
    view.setValue(settingValue.getValue());
    view.setSelectedSetting(settingValue);
    view.setEditEnable(true);
    model.setSelectedValue(settingValue);
  }
}
