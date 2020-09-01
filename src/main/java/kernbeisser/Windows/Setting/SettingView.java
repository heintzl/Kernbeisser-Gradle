package kernbeisser.Windows.Setting;

import java.util.Collection;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.SettingValue;
import kernbeisser.Enums.Setting;
import kernbeisser.Windows.MVC.IView;
import org.jetbrains.annotations.NotNull;

public class SettingView implements IView<SettingController> {
  private ObjectTable<SettingValue> settingValues;
  private JButton cancel;
  private JPanel main;
  private JButton applyChange;
  private JTextField value;
  private JLabel setting;
  private JButton resetSettings;

  @Override
  public void initialize(SettingController controller) {
    resetSettings.addActionListener(e -> controller.resetAllSettings());
    resetSettings.setIcon(
        IconFontSwing.buildIcon(FontAwesome.TRASH, 20, resetSettings.getForeground()));
    resetSettings.setHorizontalTextPosition(SwingConstants.LEFT);
    settingValues.setColumns(
        Column.create("Setting", SettingValue::getSetting),
        Column.create("Wert", SettingValue::getValue),
        Column.create("Standart", e -> e.getSetting().getDefaultValue()));
    settingValues.addSelectionListener(controller::select);
    applyChange.addActionListener(e -> controller.apply());
    cancel.addActionListener(e -> controller.cancel());
  }

  void setValues(Collection<SettingValue> values) {
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

  SettingValue getSelectedValue() {
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
            "Der eingegebene Wert ist anders als erwartet\nErwartet: "
                + type
                + "\nSoll der Wert trotzdem verändert werden?(dies kann Fehler hervorrufen)")
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
  public IconCode getTabIcon() {
    return FontAwesome.WRENCH;
  }
}
