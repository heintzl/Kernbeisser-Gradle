package kernbeisser.CustomComponents.ShoppingTable;

import static java.text.MessageFormat.format;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.text.MessageFormat;
import java.util.Collection;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Exeptions.AccessDeniedException;
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
              public Object getValue(ShoppingItem shoppingItem) throws AccessDeniedException {
                return shoppingItem.getName() + "[" + shoppingItem.getShortName() + "]";
              }

              @Override
              public int getMinWidth() {
                return 500;
              }
            },
            Column.create("Inhalt", ShoppingItem::getAmount, SwingConstants.RIGHT),
            Column.create("Menge", ShoppingItem::getItemMultiplier, SwingConstants.RIGHT),
            Column.create("Rabatt", ShoppingItem::getDiscount, SwingConstants.RIGHT),
            Column.create("Preis", ShoppingItem::getItemRetailPrice, SwingConstants.RIGHT));
    shoppingItems.getTableHeader().setBackground(Color.BLACK);
    shoppingItems.getTableHeader().setForeground(Color.WHITE);
    shoppingItems.getTableHeader().setFont(shoppingItems.getFont().deriveFont(Font.BOLD));
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
}
