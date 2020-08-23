package kernbeisser.Windows.EditUserSetting;

import java.awt.*;
import javax.swing.*;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Theme;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.TabbedPanel.TabbedPaneModel;
import org.jetbrains.annotations.NotNull;

public class EditUserSettingController
    implements Controller<EditUserSettingView, EditUserSettingModel> {
  private EditUserSettingView view;
  private final EditUserSettingModel model;

  public EditUserSettingController(User user) {
    model = new EditUserSettingModel(user);
  }

  @NotNull
  @Override
  public EditUserSettingModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setThemes(Theme.values());
    view.setSelectedTheme(UserSetting.THEME.getEnumValue(Theme.class, model.getUser()));
    view.setFontSize(UserSetting.FONT_SCALE_FACTOR.getFloatValue(model.getUser()));
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public void fontChanged() {
    Font before = UIManager.getFont("Label.font");
    view.setExampleTextFont(
        new Font(
            before.getName(),
            before.getStyle(),
            Math.round(before.getSize() * view.getFontSize())));
  }

  void commit() {
    model.setFontScale(view.getFontSize());
    model.setTheme(view.getTheme());
    view.back();
  }

  public void refreshTheme() {
    try {
      UIManager.setLookAndFeel(view.getTheme().getLookAndFeel());
    } catch (UnsupportedLookAndFeelException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
    SwingUtilities.updateComponentTreeUI(
        TabbedPaneModel.DEFAULT_TABBED_PANE.getView().getTopComponent());
  }
}
