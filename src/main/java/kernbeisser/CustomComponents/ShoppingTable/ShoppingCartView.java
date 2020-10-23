package kernbeisser.CustomComponents.ShoppingTable;

import static java.text.MessageFormat.format;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.function.Predicate;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.RawPrice;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class ShoppingCartView implements IView<ShoppingCartController> {

  private JLabel sum;
  private JLabel value;
  private JPanel main;
  private ObjectTable<ShoppingItem> shoppingItems;
  private JLabel headerDelete;
  private JScrollPane tablePanel;
  private JLabel valueAfterLabel;
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

  String inputNoOfContainers(ShoppingItem item, boolean retry) {
    String initValue =
        MessageFormat.format(
                "{0,number,0}", Math.floor(item.getItemMultiplier() / item.getContainerSize()))
            .trim();
    String message =
        MessageFormat.format(
            retry
                ? "Eingabe kann nicht verarbeitet werden, bitte noch einmal versuchen. Für wie viele {0,number,0}er Pfand-Gebinde soll Pfand berechnet werden?"
                : "Die eingegebene Menge passt in ein oder mehrere {0,number,0}er Pfand-Gebinde. Für wie viele Gebinde soll Pfand berechnet werden?",
            item.getContainerSize());
    String response = JOptionPane.showInputDialog(getContent(), message, initValue);
    if (response != null) {
      response = response.trim();
    }
    return response;
  }

  private void createUIComponents() {
    shoppingItems =
        new ObjectTable<>(
            new Column<ShoppingItem>() {
              @Override
              public String getName() {
                return "Name";
              }

              @Override
              public Object getValue(ShoppingItem shoppingItem)
                  throws PermissionKeyRequiredException {
                return shoppingItem.getName()
                    + (shoppingItem.getShortName() != null
                        ? " [" + shoppingItem.getShortName() + "]"
                        : "");
              }

              @Override
              public TableCellRenderer getRenderer() {
                return DEFAULT_STRIPED_RENDERER;
              }

              @Override
              public void adjust(TableColumn column) {
                column.setMinWidth(500);
              }
            },
            Column.create("Inhalt", ShoppingItem::getUnitAmount, SwingConstants.RIGHT),
            Column.create(
                "Menge",
                e -> {
                  if (e.isContainerDiscount()) {
                    return e.getItemMultiplier() / e.getContainerSize() + " Gebinde";
                  }
                  return e.getPriceUnits() == MetricUnits.NONE
                      ? ""
                      : e.getItemMultiplier() + e.getPriceUnits().getShortName() + " ";
                },
                SwingConstants.RIGHT),
            Column.create(
                "Rabatt",
                e ->
                    e.isContainerDiscount()
                        ? "VB "
                        : (e.getDiscount() != 0 ? e.getDiscount() + "% " : " "),
                SwingConstants.RIGHT),
            Column.create(
                "Preis", e -> String.format("%.2f€ ", e.getRetailPrice()), SwingConstants.RIGHT));
    if (editable) {
      Predicate<ShoppingItem> predicate =
          item ->
              !(item.getName().equals(RawPrice.PRODUCE.getName())
                  || item.getName().equals(RawPrice.BAKERY.getName()));
      shoppingItems.addColumn(
          Column.createIcon(
              IconFontSwing.buildIcon(FontAwesome.PLUS, 20, new Color(0x0B315A)),
              controller::plus,
              predicate));
      shoppingItems.addColumn(
          Column.createIcon(
              IconFontSwing.buildIcon(FontAwesome.MINUS, 20, new Color(0x920101)),
              controller::minus,
              predicate));
      shoppingItems.addColumn(
          Column.createIcon(
              IconFontSwing.buildIcon(FontAwesome.TRASH, 20, Color.RED),
              controller::delete,
              predicate));
    }
    shoppingItems.getTableHeader().setBackground(Color.BLACK);
    shoppingItems.getTableHeader().setForeground(Color.LIGHT_GRAY);
    shoppingItems.getTableHeader().setFont(shoppingItems.getFont().deriveFont(22.0f));
    shoppingItems.setFont(shoppingItems.getFont().deriveFont(22.0f));
    shoppingItems.setRowHeight(36);
  }

  @Override
  public void initialize(ShoppingCartController controller) {
    tablePanel
        .getVerticalScrollBar()
        .addAdjustmentListener(
            new AdjustmentListener() {
              @Override
              public void adjustmentValueChanged(AdjustmentEvent e) {
                if (autoScrollDown) e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                autoScrollDown = false;
              }
            });
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  public void setValueAfterLabel(String text) {
    valueAfterLabel.setText(text);
  }
}
