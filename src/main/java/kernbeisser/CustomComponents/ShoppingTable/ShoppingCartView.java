package kernbeisser.CustomComponents.ShoppingTable;

import static java.lang.Math.round;
import static java.text.MessageFormat.format;

import java.awt.*;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.function.Predicate;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.RawPrice;
import kernbeisser.Enums.Setting;
import kernbeisser.Security.Utils.Getter;
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
    float scaleFactor = Setting.LABEL_SCALE_FACTOR.getFloatValue();
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
              if (autoScrollDown) e.getAdjustable().setValue(e.getAdjustable().getMaximum());
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
}
