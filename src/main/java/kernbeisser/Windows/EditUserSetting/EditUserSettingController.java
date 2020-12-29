package kernbeisser.Windows.EditUserSetting;

import java.awt.*;
import javax.swing.*;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Theme;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.TabbedPane.TabbedPaneModel;
import org.jetbrains.annotations.NotNull;

public class EditUserSettingController
    extends Controller<EditUserSettingView, EditUserSettingModel> {

  public EditUserSettingController(User user) {
    super(new EditUserSettingModel(user));
  }

  @NotNull
  @Override
  public EditUserSettingModel getModel() {
    return model;
  }

  @Override
  public void fillView(EditUserSettingView editUserSettingView) {
    editUserSettingView.setThemes(Theme.values());
    editUserSettingView.setSelectedTheme(
        UserSetting.THEME.getEnumValue(Theme.class, model.getUser()));
    editUserSettingView.setFontSize(UserSetting.FONT_SCALE_FACTOR.getFloatValue(model.getUser()));
  }



  public void fontChanged() {
    Font before = UIManager.getFont("Label.font");
    getView()
        .setExampleTextFont(
            new Font(
                before.getName(),
                before.getStyle(),
                Math.round(before.getSize() * getView().getFontSize())));
  }

  void commit() {
    model.setFontScale(getView().getFontSize());
    model.setTheme(getView().getTheme());
    getView().back();
  }

  public void refreshTheme() {
    try {
      UIManager.setLookAndFeel(getView().getTheme().getLookAndFeel());
    } catch (UnsupportedLookAndFeelException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
    SwingUtilities.updateComponentTreeUI(TabbedPaneModel.MAIN_PANEL.getView().getContent());
  }
}
