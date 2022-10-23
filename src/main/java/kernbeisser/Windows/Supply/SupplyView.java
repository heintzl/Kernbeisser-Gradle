package kernbeisser.Windows.Supply;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import javax.persistence.NoResultException;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingField;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Useful.DocumentChangeListener;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class SupplyView implements IView<SupplyController> {

  private JPanel main;
  private IntegerParseField suppliersNumber;
  private JComboBox<Supplier> supplier;
  private JButton add;
  private ObjectTable<ShoppingItem> shoppingItems;
  private AccessCheckingField<Article, Double> containerSize;
  private AccessCheckingField<Article, Double> netPrice;
  private AccessCheckingField<Article, String> name;
  private DoubleParseField amount;
  private JButton commit;
  private JButton cancel;
  private JButton importSupplyFile;
  @Getter private JPanel printButtonPanel;
  private JLabel total;
  private JLabel produce;

  @Linked private SupplyController controller;

  private ObjectForm<Article> objectForm;

  @Override
  public void initialize(SupplyController controller) {
    suppliersNumber
        .getDocument()
        .addDocumentListener(
            (DocumentChangeListener)
                e -> controller.searchShoppingItem(getSelected(), suppliersNumber.getSafeValue()));
    add.addActionListener(e -> addItem());
    amount.addActionListener(e -> addItem());
    suppliersNumber.addActionListener(e -> addItem());
    shoppingItems.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
              shoppingItems
                  .getSelectedObjects()
                  .forEach(
                      selection -> {
                        controller.remove(selection);
                        shoppingItems.remove(selection);
                      });
            }
          }
        });
    objectForm = new ObjectForm<>(name, netPrice, containerSize);
    importSupplyFile.addActionListener(e -> controller.openImportSupplyFile());
    cancel.addActionListener(e -> back());
    commit.addActionListener(e -> controller.commit());
  }

  public ObjectForm<Article> getObjectForm() {
    return objectForm;
  }

  void addItem() {
    try {
      try {
        controller.addItem(getAmount());
      } catch (NullPointerException e) {
        throw new NoResultException();
      }
      suppliersNumber.setText("");
      suppliersNumber.requestFocusInWindow();
      amount.setText("1");
    } catch (NoResultException e) {
      JOptionPane.showMessageDialog(getTopComponent(), "Der Artikel kann nicht gefunden werden.");
    } catch (CannotParseException e) {
      invalidInput();
    }
  }

  public void setAmount(String amount) {
    this.amount.setText(amount);
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

  void noArticleFound() {
    containerSize.setText("0.0");
    name.setText("Keinen Artikel gefunden");
    netPrice.setText("0.00");
    setAddAvailable(false);
  }

  void setAddAvailable(boolean b) {
    add.setEnabled(b);
  }

  public void setShoppingItems(Collection<ShoppingItem> shoppingItems) {
    this.shoppingItems.setObjects(shoppingItems);
  }

  void setSuppliers(Collection<Supplier> suppliers) {
    supplier.removeAllItems();
    for (Supplier s : suppliers) {
      supplier.addItem(s);
    }
  }

  void setTotal(double d) {
    total.setText(String.format("%.2f€", d));
  }

  void setProduce(double d) {
    produce.setText(String.format("%.2f€", d));
  }

  private void createUIComponents() {
    name = new AccessCheckingField<>(Article::getName, Article::setName, AccessCheckingField.NONE);
    netPrice =
        new AccessCheckingField<>(
            Article::getNetPrice, Article::setNetPrice, AccessCheckingField.DOUBLE_FORMER);
    containerSize =
        new AccessCheckingField<>(
            Article::getContainerSize,
            Article::setContainerSize,
            AccessCheckingField.DOUBLE_FORMER);
    Icon selected = IconFontSwing.buildIcon(FontAwesome.CHECK_SQUARE_O, 18, new Color(0x3D3D3D));
    Icon unselected = IconFontSwing.buildIcon(FontAwesome.SQUARE_O, 18, new Color(0x313131));
    shoppingItems =
        new ObjectTable<>(
            Columns.create("Lieferant", ShoppingItem::getSupplier)
                .withColumnAdjustor(e -> e.setPreferredWidth(200)),
            Columns.create("Lief.Art.Nr.", ShoppingItem::getSuppliersItemNumber)
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Anzahl", ShoppingItem::getDisplayContainerCount)
                .withLeftClickConsumer(controller::editItemMultiplier)
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Name", ShoppingItem::getName)
                .withColumnAdjustor(e -> e.setPreferredWidth(400)),
            Columns.<ShoppingItem>create(
                    "Netto-Einzelpreis", e -> String.format("%.2f€", e.getItemNetPrice()))
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Gebindegröße", ShoppingItem::getContainerSize)
                .withSorter(Column.NUMBER_SORTER),
            Columns.<ShoppingItem>createIconColumn(
                    "Auswiegware", e -> (e.isWeighAble() ? selected : unselected))
                .withHorizontalAlignment(SwingConstants.CENTER),
            Columns.<ShoppingItem>create(
                    "Gebinde-Preis",
                    e -> String.format("%.2f", e.getItemNetPrice() * e.getContainerSize()))
                .withSorter(Column.NUMBER_SORTER),
            Columns.<ShoppingItem>create(
                    "Gesamtpreis",
                    e ->
                        String.format(
                            "%.2f€",
                            Math.abs(
                                e.getItemNetPrice()
                                    * (e.isWeighAble()
                                        ? (e.getItemMultiplier() / 1000.)
                                        : e.getItemMultiplier()))))
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Ausdrucken", controller::getPrintNumber)
                .withLeftClickConsumer(controller::editPrintPool)
                .withRightClickConsumer(controller::increaseItemPrintNumber)
                .withSorter(Column.NUMBER_SORTER));
//    shoppingItems.addDoubleClickListener(controller::editArticle); TODO activate, when refresh logic is done
  }

  public void invalidInput() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Bitte überprüfe die rot markierten Felder nach Fehlern!");
  }

  public boolean commitClose() {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Bist du sicher, dass du das Fenster schließen\nund die jetzige Eingabe beenden willst?")
        == 0;
  }

  @Override
  public String getTitle() {
    return "Lieferung eingeben";
  }

  public void refreshRow(ShoppingItem item) {
    shoppingItems.replace(item, item);
  }

  public void repaintTable() {
    suppliersNumber.requestFocusInWindow();
    shoppingItems.repaint();
  }

  public boolean shouldPrintLabels() {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Soll ich mir die ausgewählten Etiketten für den Ausdruck merken?",
            "Später drucken",
            JOptionPane.YES_NO_OPTION)
        == 0;
  }

  public boolean confirmChanges() {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Sollen die Änderungen am Artikel übernommen werden?",
            "Artikel geändert",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE)
        == 0;
  }
}
