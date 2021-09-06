package kernbeisser.Windows.EditUserSetting;

import java.awt.*;
import javax.swing.*;
import kernbeisser.Enums.Theme;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class EditUserSettingView implements IView<EditUserSettingController> {

  private JButton commit;
  private JComboBox<Theme> themes;
  private JSlider fontSize;
  private JPanel main;
  private JLabel exampleText;
  private JCheckBox openMultipleShoppingMasks;
  @Linked private EditUserSettingController controller;

  public void setThemes(Theme[] values) {
    themes.removeAllItems();
    for (Theme value : values) {
      themes.addItem(value);
    }
  }

  @Override
  public void initialize(EditUserSettingController controller) {
    fontSize.addChangeListener(e -> controller.fontChanged());
    commit.addActionListener(e -> controller.commit());
    themes.addActionListener(e -> controller.refreshTheme());
    openMultipleShoppingMasks.addActionListener(controller);
  }

  float getFontSize() {
    return fontSize.getValue() / 100.f;
  }

  void setExampleTextFont(Font font) {
    exampleText.setFont(font);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public String getTitle() {
    return "Benutzerspezifische Einstellungen";
  }

  public Theme getTheme() {
    return themes.getItemAt(themes.getSelectedIndex());
  }

  public void setSelectedTheme(Theme enumValue) {
    themes.setSelectedItem(enumValue);
  }

  public void setFontSize(float v) {
    fontSize.setValue(Math.round(v * 100));
  }

  public boolean isAllowMultipleShoppingMasksSelected() {
    return openMultipleShoppingMasks.isSelected();
  }

  public void setAllowMultipleShoppingMasks(boolean b) {
    openMultipleShoppingMasks.setSelected(b);
  }
}
