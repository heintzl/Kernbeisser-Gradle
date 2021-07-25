package kernbeisser.Windows.Supply;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import javax.persistence.NoResultException;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.Setting;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingField;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class SupplyView implements IView<SupplyController> {

  private JPanel main;
  private kernbeisser.CustomComponents.TextFields.IntegerParseField suppliersNumber;
  private JComboBox<Supplier> supplier;
  private JButton add;
  private ObjectTable<ShoppingItem> shoppingItems;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingField<Article, Double>
      containerSize;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingField<Article, Double> netPrice;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingField<Article, String> name;
  private DoubleParseField amount;
  private JButton commit;
  private JButton cancel;
  @Getter private JPanel printButtonPanel;

  @Linked private SupplyController controller;

  private ObjectForm<Article> objectForm;

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
    amount.addActionListener(e -> addItem());
    suppliersNumber.addActionListener(e -> addItem());
    amount.addActionListener(e -> addItem());
    shoppingItems.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
              shoppingItems
                  .getSelectedObject()
                  .ifPresent(
                      selection -> {
                        controller.remove(selection);
                        shoppingItems.remove(selection);
                      });
            }
          }
        });
    objectForm = new ObjectForm<>(name, netPrice, containerSize);
    cancel.addActionListener(e -> back());
    commit.addActionListener(e -> controller.commit());
  }

  public ObjectForm<Article> getObjectForm() {
    return objectForm;
  }

  void addItem() {
    try {
      try {
        shoppingItems.add(controller.addItem(getAmount()));
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
  }

  void setSuppliers(Collection<Supplier> suppliers) {
    supplier.removeAllItems();
    for (Supplier s : suppliers) {
      supplier.addItem(s);
    }
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
    Icon selected = IconFontSwing.buildIcon(FontAwesome.CHECK_SQUARE, 20, new Color(0x38FF00));
    Icon unselected = IconFontSwing.buildIcon(FontAwesome.SQUARE, 20, new Color(0xC7C7C7));
    shoppingItems =
        new ObjectTable<>(
            Column.create("Lieferant", ShoppingItem::getSupplier),
            Column.create("Lief.Art.Nr.", ShoppingItem::getSuppliersItemNumber),
            Column.create("Gebinde-Anzahl", p -> p.getItemMultiplier() / p.getContainerSize()),
            Column.create("Name", ShoppingItem::getName),
            Column.create("Netto-Einzelpreis", e -> String.format("%.2f€", e.getItemNetPrice())),
            Column.create("Gebindegröße", ShoppingItem::getContainerSize),
            Column.create(
                "Gebinde-Preis",
                e -> String.format("%.2f", e.getItemNetPrice() * e.getContainerSize())),
            Column.createIcon(
                "Ausdrucken",
                e -> controller.becomePrinted(e) ? selected : unselected,
                controller::togglePrint,
                null,
                (int) (100 * Setting.LABEL_SCALE_FACTOR.getDoubleValue())));
  }

  public void invalidInput() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Bitte überpüfe die rot markierten Felder nach Fehlern!");
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
}
