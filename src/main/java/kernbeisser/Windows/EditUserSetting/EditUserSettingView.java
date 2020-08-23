package kernbeisser.Windows.EditUserSetting;

import java.awt.*;
import javax.swing.*;
import kernbeisser.Enums.Theme;
import kernbeisser.Windows.MVC.View;
import org.jetbrains.annotations.NotNull;

public class EditUserSettingView implements View<EditUserSettingController> {
  private JButton commit;
  private JComboBox<Theme> themes;
  private JSlider fontSize;
  private JPanel main;
  private JLabel exampleText;

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

  public Theme getTheme() {
    return themes.getItemAt(themes.getSelectedIndex());
  }

  public void setSelectedTheme(Theme enumValue) {
    themes.setSelectedItem(enumValue);
  }

  public void setFontSize(float v) {
    fontSize.setValue(Math.round(v * 100));
  }
}
