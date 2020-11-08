package kernbeisser.Windows.Supply;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import javax.persistence.NoResultException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
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
  private DoubleParseField amount;

  @Linked private SupplyController controller;

  @Override
  public void initialize(SupplyController controller) {
    suppliersNumber.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            controller.searchShoppingItem(getSelected(), suppliersNumber.getSafeValue());
          }
        });
    add.addActionListener(e -> addItem());
    suppliersNumber.addActionListener(e -> addItem());
    amount.addActionListener(e -> addItem());
  }

  void addItem() {
    try {
      ShoppingItem item = controller.addItem(getSelected(), getSuppliersItemNumber(), getAmount());
      shoppingItems.add(item);
      suppliersNumber.setText("");
      suppliersNumber.requestFocusInWindow();
      amount.setText("1");
    } catch (NoResultException e) {
      JOptionPane.showMessageDialog(getTopComponent(), "Der Artikel kann nicht gefunden werden.");
    }
  }

  Supplier getSelected() {
    return (Supplier) supplier.getSelectedItem();
  }

  double getAmount() {
    return amount.getSafeValue();
  }

  int getSuppliersItemNumber() {
    return suppliersNumber.getSafeValue();
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  void loadItem(ShoppingItem item) {
    if (item == null) {
      containerSize.setText("0.0");
      name.setText("Kein Atikel gefunden");
      netPrice.setText("0.00");
    } else {
      containerSize.setText(String.valueOf(item.getContainerSize()));
      netPrice.setText(String.valueOf(item.getItemNetPrice()));
      name.setText(item.getName());
    }
  }

  public void setShoppingItems(ObjectTable<ShoppingItem> shoppingItems) {
    this.shoppingItems = shoppingItems;
  }

  void setSuppliers(Collection<Supplier> suppliers) {
    supplier.removeAllItems();
    for (Supplier s : suppliers) {
      supplier.addItem(s);
    }
  }

  private void createUIComponents() {
    shoppingItems =
        new ObjectTable<ShoppingItem>(
            Column.create("Lieferant", ShoppingItem::getSupplier),
            Column.create("Lief.Art.Nr.", ShoppingItem::getSuppliersItemNumber),
            Column.create("Gebinde-Anzahl", p -> p.getItemMultiplier() / p.getContainerSize()),
            Column.create("Name", ShoppingItem::getName),
            Column.create("Netto-Einzelpreis", e -> String.format("%.2f€", e.getItemNetPrice())),
            Column.create("Gebindegröße", ShoppingItem::getContainerSize));
  }
}
