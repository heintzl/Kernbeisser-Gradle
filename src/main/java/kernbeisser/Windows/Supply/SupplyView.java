package kernbeisser.Windows.Supply;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Windows.MVC.IView;
import org.jetbrains.annotations.NotNull;

public class SupplyView implements IView<SupplyController> {

  private JPanel main;
  private kernbeisser.CustomComponents.TextFields.IntegerParseField suppliersNumber;
  private JComboBox<Supplier> supplier;
  private JButton add;
  private ObjectTable<ShoppingItem> shoppingItems;
  private DoubleParseField containerSize;
  private DoubleParseField netPrice;
  private JTextField name;

  @Override
  public void initialize(SupplyController controller) {}

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  void loadItem(ShoppingItem item) {
    containerSize.setText(String.valueOf(item.getContainerSize()));
    netPrice.setText(String.valueOf(item.getItemNetPrice()));
  }

  public void setShoppingItems(ObjectTable<ShoppingItem> shoppingItems) {
    this.shoppingItems = shoppingItems;
  }

  private void createUIComponents() {
    shoppingItems =
        new ObjectTable<ShoppingItem>(
            Column.create("Lieferant", ShoppingItem::getName),
            Column.create("Lief.Art.Nr.", ShoppingItem::getSuppliersItemNumber),
            Column.create("Gebinde-Anzahl", p -> p.getItemMultiplier() / p.getContainerSize()),
            Column.create("Name", ShoppingItem::getName),
            Column.create("Netto-Einzelpreis", e -> String.format("%.2f€", e.getItemNetPrice())),
            Column.create("Gebindegröße", ShoppingItem::getContainerSize));
  }
}
