package kernbeisser.CustomComponents.ShoppingTable;

import static java.lang.Math.round;
import static java.text.MessageFormat.format;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.*;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Predicate;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.RawPrice;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Security.Utils.Getter;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class ShoppingCartView implements IView<ShoppingCartController> {

  private JLabel sum;
  private JLabel value;
  private JPanel main;
  private ObjectTable<ShoppingItem> shoppingItems;
  private JScrollPane tablePanel;
  private JLabel valueAfterLabel;
  private JLabel underMinWarning;
  private boolean autoScrollDown;

  @Linked private boolean editable;

  @Linked private ShoppingCartController controller;

  public void setObjects(Collection<ShoppingItem> items) {
    autoScrollDown = true;
    shoppingItems.setObjects(items);
  }

  public void setSum(double s) {
    sum.setText(format("{0,number,0.00}€", s));
  }

  public void setValue(double s) {
    value.setText(String.format("%.2f€", s));
  }

  String inputNoOfContainers(int containers, double containerSize, boolean retry) {
    String initValue = MessageFormat.format("{0,number,0}", containers).trim();
    String message =
        MessageFormat.format(
            retry
                ? "Eingabe kann nicht verarbeitet werden, bitte noch einmal versuchen. Für wie viele {0,number,0}er Pfand-Gebinde soll Pfand hinzugefügt werden?"
                : "Die eingegebene Menge passt in ein oder mehrere {0,number,0}er Pfand-Gebinde. Für wie viele Gebinde soll Pfand hinzugefügt werden?",
            containerSize);
    String response = JOptionPane.showInputDialog(getContent(), message, initValue);
    if (response != null) {
      response = response.trim();
    }
    return response;
  }

  public void setUnderMinWarningVisible(boolean visible) {
    underMinWarning.setVisible(visible);
  }

  private void createUIComponents() {
    float scaleFactor = UserSetting.FONT_SCALE_FACTOR.getFloatValue(LogInModel.getLoggedIn());
    float fontSize = 14.0f * scaleFactor;
    float tableIconSize = 13.0f * scaleFactor;
    int rowHeight = round(24 * scaleFactor);
    int depositKbNumber = ShoppingItem.createDeposit(0.0).getKbNumber();
    shoppingItems =
        new ObjectTable<>(
            Columns.create(
                    "Name",
                    (Getter<ShoppingItem, Object>)
                        shoppingItem ->
                            shoppingItem.getName()
                                + (shoppingItem.getSuppliersShortName() != null
                                    ? " [" + shoppingItem.getSuppliersShortName() + "]"
                                    : ""))
                .withColumnAdjustor(e -> e.setMinWidth(500)),
            Columns.create("Inhalt", ShoppingItem::getContentAmount, SwingConstants.RIGHT),
            Columns.create("Menge", ShoppingItem::getDisplayAmount, SwingConstants.RIGHT),
            Columns.create(
                "Rabatt",
                e ->
                    e.isContainerDiscount()
                        ? "VB "
                        : (e.getDiscount() != 0 ? e.getDiscount() + "% " : " "),
                SwingConstants.RIGHT),
            Columns.create(
                "Preis", e -> String.format("%.2f € ", e.getRetailPrice()), SwingConstants.RIGHT));
    if (editable) {
      Predicate<ShoppingItem> predicate =
          item ->
              !(item.getName().equals(RawPrice.PRODUCE.getName())
                  || item.getName().equals(RawPrice.BAKERY.getName())
                  || item.getKbNumber() == depositKbNumber
                  || item.getItemMultiplier() < 0);
      shoppingItems.addColumn(
          Columns.createIconColumn(
              IconFontSwing.buildIcon(FontAwesome.PLUS, tableIconSize, new Color(0x0B315A)),
              controller::plus,
              predicate));
      shoppingItems.addColumn(
          Columns.createIconColumn(
              IconFontSwing.buildIcon(FontAwesome.MINUS, tableIconSize, new Color(0x920101)),
              controller::minus,
              predicate));
      shoppingItems.addColumn(
          Columns.createIconColumn(
              IconFontSwing.buildIcon(FontAwesome.TRASH, tableIconSize, Color.RED),
              controller::delete,
              i -> i.getKbNumber() != depositKbNumber || i.getParentItem() == null));
    }
    shoppingItems.getTableHeader().setBackground(Color.BLACK);
    shoppingItems.getTableHeader().setForeground(Color.LIGHT_GRAY);
    shoppingItems.getTableHeader().setFont(shoppingItems.getFont().deriveFont(fontSize));
    shoppingItems.setFont(shoppingItems.getFont().deriveFont(fontSize));
    shoppingItems.setRowHeight(rowHeight);
  }

  @Override
  public void initialize(ShoppingCartController controller) {
    tablePanel
        .getVerticalScrollBar()
        .addAdjustmentListener(
            e -> {
              if (autoScrollDown) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
              }
              autoScrollDown = false;
            });
    CompoundBorder border =
        BorderFactory.createCompoundBorder(
            new LineBorder(Color.GRAY, 2, true), new EmptyBorder(2, 5, 2, 5));
    underMinWarning.setVisible(false);
    underMinWarning.setText("Bitte vor dem nächsten Einkauf Guthaben auffüllen!");
    underMinWarning.setFont(underMinWarning.getFont().deriveFont(Font.BOLD, 18f));
    underMinWarning.setForeground(Color.RED.darker());
    underMinWarning.setBorder(border);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  public void setValueAfterLabel(String text) {
    valueAfterLabel.setText(text);
  }

  public ObjectTable<ShoppingItem> getShoppingItemsTable() {
    return shoppingItems;
  }

  {
    // GUI initializer generated by IntelliJ IDEA GUI Designer
    // >>> IMPORTANT!! <<<
    // DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR
   * call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    createUIComponents();
    main = new JPanel();
    main.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(2, 2, new Insets(0, 10, 5, 10), -1, -1));
    main.add(
        panel1,
        new GridConstraints(
            2,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    valueAfterLabel = new JLabel();
    valueAfterLabel.setBackground(new Color(-16777216));
    valueAfterLabel.setEnabled(true);
    valueAfterLabel.setFocusable(true);
    Font valueAfterLabelFont = this.$$$getFont$$$("Arial", -1, 16, valueAfterLabel.getFont());
    if (valueAfterLabelFont != null) valueAfterLabel.setFont(valueAfterLabelFont);
    valueAfterLabel.setForeground(new Color(-16777216));
    valueAfterLabel.setText("Restguthaben:");
    panel1.add(
        valueAfterLabel,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    value = new JLabel();
    value.setBackground(new Color(-16777216));
    value.setEnabled(true);
    value.setFocusable(true);
    Font valueFont = this.$$$getFont$$$("Arial", -1, 16, value.getFont());
    if (valueFont != null) value.setFont(valueFont);
    value.setForeground(new Color(-16777216));
    value.setText("value");
    panel1.add(
        value,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_EAST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    underMinWarning = new JLabel();
    underMinWarning.setText("");
    panel1.add(
        underMinWarning,
        new GridConstraints(
            1,
            0,
            1,
            2,
            GridConstraints.ANCHOR_NORTH,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            new Dimension(150, 24),
            null,
            0,
            false));
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(1, 2, new Insets(5, 10, 5, 10), -1, -1));
    panel2.setBackground(new Color(-5197391));
    panel2.setEnabled(false);
    main.add(
        panel2,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JLabel label1 = new JLabel();
    label1.setBackground(new Color(-16777216));
    label1.setEnabled(true);
    label1.setFocusable(true);
    Font label1Font = this.$$$getFont$$$("Arial", -1, 16, label1.getFont());
    if (label1Font != null) label1.setFont(label1Font);
    label1.setForeground(new Color(-16777216));
    label1.setText("Summe:");
    panel2.add(
        label1,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    sum = new JLabel();
    sum.setBackground(new Color(-16777216));
    sum.setEnabled(true);
    sum.setFocusable(true);
    Font sumFont = this.$$$getFont$$$("Arial", -1, 16, sum.getFont());
    if (sumFont != null) sum.setFont(sumFont);
    sum.setForeground(new Color(-16777216));
    sum.setText("sum");
    panel2.add(
        sum,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_EAST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    tablePanel = new JScrollPane();
    tablePanel.setHorizontalScrollBarPolicy(30);
    main.add(
        tablePanel,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    tablePanel.setViewportView(shoppingItems);
  }

  /** @noinspection ALL */
  private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
    if (currentFont == null) return null;
    String resultName;
    if (fontName == null) {
      resultName = currentFont.getName();
    } else {
      Font testFont = new Font(fontName, Font.PLAIN, 10);
      if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
        resultName = fontName;
      } else {
        resultName = currentFont.getName();
      }
    }
    Font font =
        new Font(
            resultName,
            style >= 0 ? style : currentFont.getStyle(),
            size >= 0 ? size : currentFont.getSize());
    boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
    Font fontWithFallback =
        isMac
            ? new Font(font.getFamily(), font.getStyle(), font.getSize())
            : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
    return fontWithFallback instanceof FontUIResource
        ? fontWithFallback
        : new FontUIResource(fontWithFallback);
  }

  /** @noinspection ALL */
  public JComponent $$$getRootComponent$$$() {
    return main;
  }
}
