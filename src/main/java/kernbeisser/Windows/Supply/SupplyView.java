package kernbeisser.Windows.Supply;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Locale;
import javax.persistence.NoResultException;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
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
import kernbeisser.Useful.Tools;
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
    Icon selected = IconFontSwing.buildIcon(FontAwesome.CHECK_SQUARE, 20, new Color(0x38FF00));
    Icon unselected = IconFontSwing.buildIcon(FontAwesome.SQUARE, 20, new Color(0xC7C7C7));
    shoppingItems =
        new ObjectTable<>(
            Columns.create("Lieferant", ShoppingItem::getSupplier),
            Columns.create("Lief.Art.Nr.", ShoppingItem::getSuppliersItemNumber),
            Columns.create("Gebinde-Anzahl", ShoppingItem::getDisplayContainerCount),
            Columns.create("Name", ShoppingItem::getName),
            Columns.create("Netto-Einzelpreis", e -> String.format("%.2f€", e.getItemNetPrice())),
            Columns.create("Gebindegröße", ShoppingItem::getContainerSize),
            Columns.create(
                "Gebinde-Preis",
                e -> String.format("%.2f", e.getItemNetPrice() * e.getContainerSize())),
            Columns.create(
                "Gesamtpreis",
                e ->
                    String.format(
                        "%.2f€,",
                        Math.abs(
                            e.getItemNetPrice()
                                * (e.isWeighAble()
                                    ? (e.getItemMultiplier() / 1000.)
                                    : e.getItemMultiplier())))),
            Columns.createIconColumn(
                "Ausdrucken",
                e -> controller.becomePrinted(e) ? selected : unselected,
                controller::togglePrint,
                (e) -> {},
                Tools.scaleWithLabelScalingFactor(100)));
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
    main.setLayout(new GridLayoutManager(4, 6, new Insets(5, 5, 5, 5), -1, -1));
    final JScrollPane scrollPane1 = new JScrollPane();
    main.add(
        scrollPane1,
        new GridConstraints(
            1,
            0,
            1,
            6,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    scrollPane1.setViewportView(shoppingItems);
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(2, 7, new Insets(0, 0, 0, 0), -1, -1));
    main.add(
        panel1,
        new GridConstraints(
            0,
            0,
            1,
            6,
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
    label1.setText("Lieferantennr.");
    panel1.add(
        label1,
        new GridConstraints(
            0,
            1,
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
    final JLabel label2 = new JLabel();
    label2.setText("Lieferant");
    panel1.add(
        label2,
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
    supplier = new JComboBox();
    panel1.add(
        supplier,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label3 = new JLabel();
    label3.setText("Gebindegröße");
    panel1.add(
        label3,
        new GridConstraints(
            0,
            5,
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
    panel1.add(
        containerSize,
        new GridConstraints(
            1,
            5,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    suppliersNumber = new IntegerParseField();
    panel1.add(
        suppliersNumber,
        new GridConstraints(
            1,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            new Dimension(150, -1),
            null,
            0,
            false));
    final JLabel label4 = new JLabel();
    label4.setText("Artikelname");
    panel1.add(
        label4,
        new GridConstraints(
            0,
            3,
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
    panel1.add(
        name,
        new GridConstraints(
            1,
            3,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            new Dimension(150, -1),
            null,
            0,
            false));
    add = new JButton();
    add.setText("Hinzufügen");
    panel1.add(
        add,
        new GridConstraints(
            1,
            6,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    amount = new DoubleParseField();
    amount.setText("1");
    panel1.add(
        amount,
        new GridConstraints(
            1,
            2,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JLabel label5 = new JLabel();
    label5.setText("Gebindeanzahl");
    panel1.add(
        label5,
        new GridConstraints(
            0,
            2,
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
    panel1.add(
        netPrice,
        new GridConstraints(
            1,
            4,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JLabel label6 = new JLabel();
    label6.setText("Einzelpreis");
    panel1.add(
        label6,
        new GridConstraints(
            0,
            4,
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
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
    main.add(
        panel2,
        new GridConstraints(
            3,
            0,
            1,
            6,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    commit = new JButton();
    commit.setText("Eingabe abschließen");
    panel2.add(
        commit,
        new GridConstraints(
            0,
            4,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer1 = new Spacer();
    panel2.add(
        spacer1,
        new GridConstraints(
            0,
            2,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            1,
            null,
            null,
            null,
            0,
            false));
    cancel = new JButton();
    cancel.setText("Abbrechen");
    panel2.add(
        cancel,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    printButtonPanel = new JPanel();
    printButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel2.add(
        printButtonPanel,
        new GridConstraints(
            0,
            3,
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
    importSupplyFile = new JButton();
    importSupplyFile.setText("Lieferung Importieren");
    panel2.add(
        importSupplyFile,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label7 = new JLabel();
    Font label7Font = this.$$$getFont$$$(null, Font.BOLD, -1, label7.getFont());
    if (label7Font != null) label7.setFont(label7Font);
    label7.setText("Gesamtpreis: ");
    main.add(
        label7,
        new GridConstraints(
            2,
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
    final Spacer spacer2 = new Spacer();
    main.add(
        spacer2,
        new GridConstraints(
            2,
            4,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            1,
            null,
            null,
            null,
            0,
            false));
    total = new JLabel();
    Font totalFont = this.$$$getFont$$$(null, Font.BOLD, -1, total.getFont());
    if (totalFont != null) total.setFont(totalFont);
    total.setText("0.00€");
    main.add(
        total,
        new GridConstraints(
            2,
            1,
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
    final JLabel label8 = new JLabel();
    Font label8Font = this.$$$getFont$$$(null, Font.BOLD, -1, label8.getFont());
    if (label8Font != null) label8.setFont(label8Font);
    label8.setForeground(new Color(-16401641));
    label8.setText("Obst  Gemüse:");
    label8.setDisplayedMnemonic(' ');
    label8.setDisplayedMnemonicIndex(5);
    main.add(
        label8,
        new GridConstraints(
            2,
            2,
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
    produce = new JLabel();
    Font produceFont = this.$$$getFont$$$(null, Font.BOLD, -1, produce.getFont());
    if (produceFont != null) produce.setFont(produceFont);
    produce.setForeground(new Color(-16401641));
    produce.setText("0.00€");
    main.add(
        produce,
        new GridConstraints(
            2,
            3,
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
