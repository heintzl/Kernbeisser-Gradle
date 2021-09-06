package kernbeisser.Windows.Setting;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
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
        Columns.create("Einstellung", Setting::toString, SwingConstants.LEFT),
        Columns.create("Wert", Setting::getValue, SwingConstants.LEFT),
        Columns.create("Standard", Setting::getDefaultValue, SwingConstants.LEFT));
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

  Optional<Setting> getSelectedValue() {
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

  public static boolean confirmAccounting() {
    return JOptionPane.showConfirmDialog(
        null,
        "Dieser Wert darf nur in Absprache mit der Buchhaltung geändert werden!\n"
            + "Bist Du 100% sicher, dass Du den Wert anpassen musst?",
        "Riskante Anpassung",
        JOptionPane.YES_NO_OPTION)
        == JOptionPane.YES_OPTION;
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
