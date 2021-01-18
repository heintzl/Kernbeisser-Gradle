package kernbeisser.Windows.Setting;

import java.util.Collection;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Enums.Setting;
import kernbeisser.Security.StaticMethodTransformer.StaticAccessPoint;
import kernbeisser.Windows.MVC.IView;
import org.jetbrains.annotations.NotNull;

public class SettingView implements IView<SettingController> {
  private ObjectTable<Setting> settingValues;
  private JButton cancel;
  private JPanel main;
  private JButton applyChange;
  private JTextField value;
  private JLabel setting;
  private JButton resetSettings;
  private SettingController controller;

  @Override
  public void initialize(SettingController controller) {
    resetSettings.addActionListener(e -> controller.resetAllSettings());
    resetSettings.setIcon(
        IconFontSwing.buildIcon(FontAwesome.TRASH, 20, resetSettings.getForeground()));
    resetSettings.setHorizontalTextPosition(SwingConstants.LEFT);
    settingValues.setColumns(
        Column.create("Einstellung", Setting::toString, SwingConstants.LEFT),
        Column.create("Wert", Setting::getValue, SwingConstants.LEFT),
        Column.create("Standard", Setting::getDefaultValue, SwingConstants.LEFT));
    settingValues.addSelectionListener(controller::select);
    applyChange.addActionListener(e -> controller.apply());
    cancel.addActionListener(e -> controller.cancel());
  }

  void setValues(Collection<Setting> values) {
    settingValues.setObjects(values);
  }

  void setValue(String s) {
    value.setText(s);
  }

  String getValue() {
    return value.getText();
  }

  void setSelectedSetting(Setting selectedSetting) {
    setting.setText(selectedSetting.toString());
  }

  Setting getSelectedValue() {
    return settingValues.getSelectedObject();
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    settingValues = new ObjectTable<>();
  }

  public void setEditEnable(boolean b) {
    applyChange.setEnabled(b);
  }

  boolean commitType(String type) {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Der eingegebene Wert ist anders als erwartet\n"
                + "Erwartet: "
                + type
                + "\n"
                + "Soll der Wert trotzdem verändert werden? (Dies kann Fehler hervorrufen)")
        == 0;
  }

  public boolean commitResetSettings() {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Sollen wirklich alle Einstellungen außer "
                + Setting.DB_INITIALIZED.name()
                + " zurückgesetzt werden?")
        == 0;
  }

  @Override
  @StaticAccessPoint
  public IconCode getTabIcon() {
    return FontAwesome.WRENCH;
  }

  @Override
  public String getTitle() {
    return "Einstellungen";
  }
}
