package kernbeisser.Windows.EditUserSetting;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Theme;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import kernbeisser.Useful.UiTools;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.TabbedPane.TabbedPaneModel;
import org.jetbrains.annotations.NotNull;

public class EditUserSettingController extends Controller<EditUserSettingView, EditUserSettingModel>
    implements ActionListener {

  @Key(PermissionKey.ACTION_OPEN_EDIT_USER_SETTING)
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
    editUserSettingView.setAllowMultipleShoppingMasks(
        UserSetting.ALLOW_MULTIPLE_SHOPPING_MASK_INSTANCES.getBooleanValue(model.getUser()));
  }

  public void fontChanged() {
    Font before = UIManager.getFont("Label.font");
    getView()
        .setExampleTextFont(
            new Font(
                before.getName(),
                before.getStyle(),
                Math.round(UiTools.DEFAULT_LABEL_SIZE * getView().getFontSize())));
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
    SwingUtilities.updateComponentTreeUI(TabbedPaneModel.getMainPanel().getView().getContent());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("allowMultipleShoppingMasks")) {
      model.setOpenMultipleShoppingMasks(getView().isAllowMultipleShoppingMasksSelected());
    }
  }
}
