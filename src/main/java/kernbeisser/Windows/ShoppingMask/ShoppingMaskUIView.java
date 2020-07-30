package kernbeisser.Windows.ShoppingMask;

import static java.text.MessageFormat.format;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Vector;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.CustomComponents.FocusTraversal.FocusTraversal;
import kernbeisser.CustomComponents.KeyCapture;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartView;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.InvalidVATValueException;
import kernbeisser.Exeptions.UndefinedInputException;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.View;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class ShoppingMaskUIView implements View<ShoppingMaskUIController> {
  // TODO: create Enum
  static final int ARTICLE_NUMBER = 0;
  static final int BAKED_GOODS = 1;
  static final int CUSTOM_PRODUCT = 2;
  static final int DEPOSIT = 3;
  static final int RETURN_DEPOSIT = 4;
  static final int PRODUCE = 5;
  static final String stornoMessageTitle = "Storno";

  private final ShoppingMaskUIController controller;
  private final ShoppingCartController cartController;

  private JLabel customerName;
  private JPanel mainPanel;
  private JPanel westUpperPanel;
  private JPanel ShoppingItemPanel;
  private JRadioButton optProduce;
  private JRadioButton optBakedGoods;
  private JRadioButton optArticleNo;
  private JRadioButton optCustomProduct;
  private JRadioButton optDeposit;
  private JRadioButton optDepositReturn;
  private kernbeisser.CustomComponents.TextFields.IntegerParseField kbNumber;
  private kernbeisser.CustomComponents.TextFields.IntegerParseField suppliersItemNumber;
  private JTextField articleName;
  private kernbeisser.CustomComponents.TextFields.DoubleParseField price;
  private kernbeisser.CustomComponents.TextFields.DoubleParseField netPrice;
  private kernbeisser.CustomComponents.TextFields.DoubleParseField containerSize;
  private kernbeisser.CustomComponents.TextFields.DoubleParseField amount;
  private kernbeisser.CustomComponents.TextFields.DoubleParseField deposit;
  private JPanel westPanel;
  private JPanel eastPanel;
  private JPanel eastUpperPanel;
  private JLabel customerCredit;
  private JLabel customerLoginName;
  private JRadioButton priceStandard;
  private JRadioButton pricePreordered;
  private JRadioButton price50Percent;
  private JRadioButton priceVariablePercentage;
  private JLabel priceUnit;
  private JLabel netPriceUnit;
  private JLabel amountUnit;
  private JLabel containerUnit;
  private kernbeisser.CustomComponents.TextFields.IntegerParseField variablePercentage;
  private JCheckBox rememberReductionSetting;
  private JButton editUser;
  private JButton addPrice;
  private JButton addNetPrice;
  private JButton addDeposit;
  private JButton addAmount;
  private JPanel shoppingCartPanel;
  private JPanel shoppingListPanel;
  private JPanel shoppingActionPanel;
  private JButton checkout;
  private JButton cancelSalesSession;
  private JButton searchArticle;
  private JLabel salesPerson1;
  private JLabel salesPerson2;
  private JLabel depositUnit;
  private ShoppingCartView shoppingCartView;
  private JLabel containerSizeLabel;
  private JPanel productTypePanel;
  private JPanel reductionPanel;
  private JComboBox supplier;
  private JButton emptyShoppingCart;
  private JComboBox vat;
  private ButtonGroup optGrpArticleType;
  private ButtonGroup optGrpReduction;

  private char currentArticleType;
  private boolean isWeighable;
  static Vector<Component> traversalOrder = new Vector<Component>(1);
  static FocusTraversal traversalPolicy;
  @Getter private boolean preordered = false;
  private BarcodeCapture barcodeCapture;
  private KeyCapture keyCapture;
  @Getter private ShoppingItem currentItem;

  public ShoppingMaskUIView(
      ShoppingMaskUIController controller, ShoppingCartController shoppingCartController) {
    this.cartController = shoppingCartController;
    this.controller = controller;
  }

  private boolean isEmptyArticleName() {
    return articleName.getText().equals("");
  }

  private void doCancel() {
    controller.emptyShoppingCart();
    back();
  }

  private void doCheckout() {
    controller.startPay();
  }

  private void openSearchWindow() {
    controller.openSearchWindow();
  }

  public void addToCart() {
    if (controller.addToShoppingCart()) {
      articleTypeInitialize(currentArticleType);
    }
  }

  private void editUserAction() {
    controller.editUserAction();
  }

  private void createUIComponents() {
    shoppingCartView = cartController.getView();
  }

  void loadUserInfo(SaleSession saleSession) {
    customerName.setText(
        saleSession.getCustomer().getFirstName() + " " + saleSession.getCustomer().getSurname());
    customerLoginName.setText(saleSession.getCustomer().getUsername());
    customerCredit.setText(
        format("{0, number, 0.00}\u20AC", saleSession.getCustomer().getUserGroup().getValue()));
    salesPerson1.setText(saleSession.getSeller().getUsername());
    salesPerson2.setText(
        saleSession.getSecondSeller() != null ? saleSession.getSecondSeller().getUsername() : "");
  }

  private void supplierChange() {
    boolean knownSupplier = supplier.getSelectedItem() != null;
    if (getOption() == ARTICLE_NUMBER) {
      suppliersItemNumber.setEnabled(knownSupplier);
    }

    if (getOption() == CUSTOM_PRODUCT) {
      articleName.setEnabled(knownSupplier || !preordered);
      vat.setEnabled(knownSupplier || !preordered);
      loadItemStats(controller.createCustomItem((Supplier) supplier.getSelectedItem()));
      amount.setEnabled(knownSupplier);
      netPrice.setEnabled(knownSupplier && !isEmptyArticleName());
    }
  }

  private void articleNameChange() {
    boolean validVat = vat.getSelectedIndex() > -1;
    netPrice.setEnabled(preordered && !isEmptyArticleName() && validVat);
    price.setEnabled(!preordered && !isEmptyArticleName() && validVat);
    if (articleName.isEnabled() && !isEmptyArticleName()) {
      if (vat.getSelectedIndex() == -1) {
        vat.requestFocusInWindow();
      } else {
        if (preordered) {
          netPrice.selectAll();
          netPrice.requestFocusInWindow();
        } else {
          price.selectAll();
          price.requestFocusInWindow();
        }
      }
    } else if (articleName.isEnabled()) {
      articleName.requestFocusInWindow();
    }
  }

  private void articleTypeChange(char type) {
    if (currentArticleType != type) {
      articleTypeInitialize(type);
    }
  }

  private void articleTypeInitialize(char type) {
    currentArticleType = type;
    setPriceOptions(type);
    isWeighable = false;
    netPrice.setEnabled(false);

    addAmount.setVisible(type == 'a');
    addPrice.setVisible(!preordered && "pbc".indexOf(type) != -1);
    addNetPrice.setVisible(preordered && "pbc".indexOf(type) != -1);
    addDeposit.setVisible("dr".indexOf(type) != -1);

    supplier.getModel().setSelectedItem(null);
    supplier.setEnabled(preordered || "ac".indexOf(type) != -1);
    suppliersItemNumber.setVisible(type == 'a');
    setSuppliersItemNumber("");
    supplierChange();

    kbNumber.setVisible(type == 'a');
    setKbNumber("");

    price.setVisible("dr".indexOf(type) == -1);
    price.setText("");
    priceUnit.setVisible("pbac".indexOf(type) != -1);
    priceUnit.setText("€");
    netPrice.setVisible(preordered || price.isVisible());
    netPrice.setText("");
    netPriceUnit.setVisible(priceUnit.isVisible());
    netPriceUnit.setText("€");

    amount.setVisible("ac".indexOf(type) != -1);
    amount.setText("1");
    this.amountUnit.setText("");

    containerSize.setVisible(type == 'a');
    this.containerUnit.setText("");
    containerUnit.setVisible(type == 'a');

    deposit.setEnabled("dr".indexOf(type) != -1);
    deposit.setVisible("adr".indexOf(type) != -1);
    depositUnit.setVisible("adr".indexOf(type) != -1);

    if ("dr".indexOf(type) != -1) {
      setVat(VAT.HIGH);
    }
    vat.setEnabled(type == 'c');

    variablePercentage.setEnabled(
        priceVariablePercentage.isEnabled() && priceVariablePercentage.isSelected());

    if (type == 'p') {
      loadItemStats(Objects.requireNonNull(ShoppingItem.createProduce(0.0, preordered)));
      this.articleName.setText("Obst & Gemüse");
      price.selectAll();
      price.requestFocusInWindow();
    } else if (type == 'b') {
      loadItemStats(Objects.requireNonNull(ShoppingItem.createBakeryProduct(0.0, preordered)));
      price.selectAll();
      this.articleName.setText("Backwaren");
      price.requestFocusInWindow();
    } else if (type == 'd') {
      this.articleName.setText("Pfand-Behälter");
      deposit.requestFocusInWindow();
    } else if (type == 'r') {
      this.articleName.setText("Pfand zurück");
      deposit.requestFocusInWindow();
    } else if (type == 'a') {
      this.articleName.setText("");
      kbNumber.requestFocusInWindow();
    } else if (type == 'c') {
      this.articleName.setText("");
      this.vat.setSelectedIndex(-1);
      if (articleName.isEnabled()) {
        articleName.requestFocusInWindow();
      } else {
        supplier.requestFocusInWindow();
      }
    }
    articleName.setEnabled(type == 'c' && !preordered);
    vat.setEnabled(type == 'c' && !preordered);
    price.setEnabled(!preordered && "dra".indexOf(type) == -1 && !isEmptyArticleName());
  }

  private void setPriceOptions(char type) {
    if ("dr".indexOf(type) == -1) {
      pricePreordered.setEnabled(true);
    } else {
      pricePreordered.setEnabled(false);
      preordered = false;
    }
    if (type == 'a') {
      price50Percent.setEnabled(true);
      priceVariablePercentage.setEnabled(true);
    } else {
      priceStandard.setSelected(!preordered);
      pricePreordered.setSelected(preordered);
      price50Percent.setEnabled(false);
      priceVariablePercentage.setEnabled(false);
    }
  }

  void loadItemStats(ShoppingItem shoppingItem) {
    currentItem = shoppingItem;
    supplier.getModel().setSelectedItem(shoppingItem.getSupplier());
    kbNumber.setText(shoppingItem.getKbNumber() != 0 ? shoppingItem.getKbNumber() + "" : "");
    suppliersItemNumber.setText(shoppingItem.getSuppliersItemNumber() + "");
    if (shoppingItem.getName() != null) {
      articleName.setText(
          shoppingItem.getName().length() > 40
              ? new StringBuilder(shoppingItem.getName())
                  .replace(36, shoppingItem.getName().length(), "...")
                  .toString()
              : shoppingItem.getName());
    }
    price.setText(
        String.format(
            "%.2f",
            (preordered
                ? shoppingItem.calculateItemRetailPrice(shoppingItem.getItemNetPrice())
                    * shoppingItem.getContainerSize()
                : shoppingItem.getItemRetailPrice())));
    priceUnit.setText(preordered ? "€/Geb." : shoppingItem.isWeighAble() ? "€/kg" : "€");
    netPrice.setText(
        String.format(
            "%.2f",
            shoppingItem.getItemNetPrice() * (preordered ? shoppingItem.getContainerSize() : 1.0)));
    netPrice.setEnabled(preordered && !isEmptyArticleName());
    netPriceUnit.setText(priceUnit.getText());
    amountUnit.setText(
        preordered
            ? "Geb."
            : shoppingItem.isWeighAble() ? shoppingItem.getMetricUnits().getShortName() : "stk.");
    isWeighable = shoppingItem.isWeighAble();
    containerSize.setText(
        new DecimalFormat("##.###")
            .format(shoppingItem.getContainerSize() * (isWeighable ? 1000 : 1)));
    containerUnit.setText(
        (isWeighable ? shoppingItem.getMetricUnits() : MetricUnits.PIECE).getShortName());
    try {
      if (shoppingItem.getVat() > 0) setVat(shoppingItem.getVat());
    } catch (InvalidVATValueException e) {
      e.printStackTrace();
      vat.setSelectedIndex(-1);
    }
    deposit.setText(String.format("%.2f", shoppingItem.getSingleDeposit()));
  }

  private void recalculatePrice() {
    if (currentItem.getName() == null) {
      try {
        currentItem = controller.extractShoppingItemFromUI();
      } catch (UndefinedInputException e) {
        e.printStackTrace();
      }
    }
    price.setText(
        String.format("%.2f", currentItem.calculateItemRetailPrice(netPrice.getSafeValue())));
  }

  void defaultSettings() {
    price.setText("0.00");
    netPrice.setText("0.00");
    netPrice.setEnabled(false);
    depositUnit.setText("€");
    priceUnit.setText("€");
    amount.setText("1");
    amount.setEnabled(true);
    articleName.setText("Kein Artikel gefunden!");
    amountUnit.setText("");
    containerUnit.setText("");
    currentItem = null;
  }

  void messageNoArticleFound() {
    java.awt.Toolkit.getDefaultToolkit().beep();
    JOptionPane.showMessageDialog(
        mainPanel,
        "Es konnte kein Artikel mit den angegeben Artikelnummer / Lieferantennummer gefunden werden");
  }

  void messageInvalidDiscount() {
    java.awt.Toolkit.getDefaultToolkit().beep();
    JOptionPane.showMessageDialog(mainPanel, "Rabatt muss zwischen 0 und 100 % liegen");
    variablePercentage.setText("");
  }

  public void messageBarcodeNotFound(long barcode) {
    java.awt.Toolkit.getDefaultToolkit().beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Konnte keinen Artikel mit Barcode \"" + barcode + "\" finden",
        "Artikel nicht gefunden",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void messageInvalidBarcode(String barcode) {
    java.awt.Toolkit.getDefaultToolkit().beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Ungültiger Barcode: " + barcode,
        "Barcode Fehler",
        JOptionPane.WARNING_MESSAGE);
  }

  public void messageDepositStorno() {
    java.awt.Toolkit.getDefaultToolkit().beep();
    JOptionPane.showMessageDialog(
        getContent(), "Pfand kann nicht storniert werden!", "Storno", JOptionPane.WARNING_MESSAGE);
    deposit.setText("");
  }

  public void messageCartIsEmpty() {
    java.awt.Toolkit.getDefaultToolkit().beep();
    JOptionPane.showMessageDialog(
        getContent(), "Es gibt nichts zu bezahlen!", "Storno", JOptionPane.WARNING_MESSAGE);
  }

  public String inputStornoRetailPrice(double itemRetailPrice, boolean retry) {
    String initValue = MessageFormat.format("{0, number, 0.00}", itemRetailPrice).trim();
    String message = "";
    String response = "";
    if (retry) { // item is piece, first try
      message =
          "Die Eingabe ist ungültig. Bitte hier einen gültigen Einzelpreis angeben, für den Fall, dass er sich seit dem ursprünglichen Einkauf geändert hat:";
    } else { // item is piece later try
      message =
          "Negative Menge: Soll der Artikel wirklich storniert werden? Dann kann hier der Einzelpreis angepasst werden, für den Fall, dass er sich seit dem ursprünglichen Einkauf geändert hat:";
    }
    java.awt.Toolkit.getDefaultToolkit().beep();
    response =
        (String)
            JOptionPane.showInputDialog(
                getContent(),
                message,
                stornoMessageTitle,
                JOptionPane.YES_NO_OPTION,
                null,
                null,
                initValue);
    if (response != null) {
      response = response.trim();
    }
    return response;
  }

  public int confirmStorno() {
    java.awt.Toolkit.getDefaultToolkit().beep();
    return JOptionPane.showConfirmDialog(
        getContent(),
        "Soll die Ware wirklich storniert werden?",
        stornoMessageTitle,
        JOptionPane.YES_NO_OPTION);
  }

  public int confirmRoundedMultiplier(int roundedMultiplier) {
    java.awt.Toolkit.getDefaultToolkit().beep();
    return JOptionPane.showConfirmDialog(
        getContent(),
        "Die Menge an Artikeln muss ganzzahlig sein. Soll die Menge auf "
            + roundedMultiplier
            + "gerundet werden?",
        "Ungültige Mengenangabe",
        JOptionPane.YES_NO_OPTION);
  }

  public String getItemName() {
    return articleName.getText();
  }

  public double getDeposit() {
    return deposit.getSafeValue();
  }

  public void setKbNumber(String value) {
    this.kbNumber.setText(value);
  }

  public void setSuppliersItemNumber(String value) {
    this.suppliersItemNumber.setText(value);
  }

  public void setOptArticleNo() {
    this.optArticleNo.setSelected(true);
  }

  public double getAmount() {
    return amount.getSafeValue();
  }

  private void setAmount(String value) {
    if (amount.isEnabled() && amount.isVisible()) {
      this.amount.setText(value);
    }
  }

  public int getDiscount() {
    if (priceStandard.isSelected()) {
      return 0;
    }
    if (price50Percent.isSelected()) {
      return 50;
    }
    if (priceVariablePercentage.isSelected()) {
      return variablePercentage.getSafeValue();
    }
    if (pricePreordered.isSelected()) {
      // TODO
    }
    return 0;
  }

  public void setDiscount() {
    if (!rememberReductionSetting.isSelected() && !preordered) {
      priceStandard.setSelected(true);
    }
  }

  public int getOption() {
    if (optArticleNo.isSelected()) {
      return ARTICLE_NUMBER;
    }
    if (optBakedGoods.isSelected()) {
      return BAKED_GOODS;
    }
    if (optCustomProduct.isSelected()) {
      return CUSTOM_PRODUCT;
    }
    if (optDeposit.isSelected()) {
      return DEPOSIT;
    }
    if (optDepositReturn.isSelected()) {
      return RETURN_DEPOSIT;
    }
    if (optProduce.isSelected()) {
      return PRODUCE;
    }
    return -1;
  }

  double getNetPrice() {
    return netPrice.getSafeValue();
  }

  double getPrice() {
    return price.getSafeValue();
  }

  int getKBArticleNumber() {
    return kbNumber.getSafeValue();
  }

  int getSuppliersNumber() {
    return suppliersItemNumber.getSafeValue();
  }

  Supplier getSupplier() {
    return (Supplier) supplier.getSelectedItem();
  }

  VAT getVat() {
    return (VAT) vat.getSelectedItem();
  }

  private void setVat(VAT vatValue) {
    vat.getModel().setSelectedItem(vatValue);
  }

  private void setVat(double vatValue) throws InvalidVATValueException {
    boolean found = false;
    for (VAT vatEnum : VAT.values()) {
      if (vatEnum.getValue() == vatValue) {
        vat.getModel().setSelectedItem(vatEnum);
        found = true;
        break;
      }
    }
    if (!found) {
      throw new InvalidVATValueException(vatValue);
    }
  }

  Dimension getShoppingListSize() {
    return shoppingListPanel.getSize();
  }

  public Controller getController() {
    return controller;
  }

  @Override
  public void initialize(ShoppingMaskUIController controller) {

    checkout.addActionListener(e -> doCheckout());
    emptyShoppingCart.addActionListener(e -> controller.emptyShoppingCart());
    cancelSalesSession.addActionListener(e -> doCancel());

    searchArticle.setIcon(IconFontSwing.buildIcon(FontAwesome.SEARCH, 20, new Color(49, 114, 128)));
    searchArticle.addActionListener(e -> openSearchWindow());
    addPrice.setIcon(
        IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, 20, new Color(49, 114, 128)));
    addPrice.addActionListener(e -> addToCart());
    addNetPrice.setIcon(
        IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, 20, new Color(49, 114, 128)));
    addNetPrice.addActionListener(e -> addToCart());
    addDeposit.setIcon(
        IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, 20, new Color(49, 114, 128)));
    addDeposit.addActionListener(e -> addToCart());
    addAmount.setIcon(
        IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, 20, new Color(49, 114, 128)));
    addAmount.addActionListener(e -> addToCart());

    articleName.addActionListener(e -> articleNameChange());
    vat.addActionListener(e -> articleNameChange());
    price.addActionListener(e -> addToCart());
    deposit.addActionListener(e -> addToCart());
    amount.addActionListener(e -> addToCart());
    editUser.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 20, new Color(49, 114, 128)));
    editUser.addActionListener(e -> editUserAction());

    optProduce.addItemListener(e -> articleTypeChange('p'));
    optBakedGoods.addItemListener(e -> articleTypeChange('b'));
    optArticleNo.addItemListener(e -> articleTypeChange('a'));
    optCustomProduct.addItemListener(e -> articleTypeChange('c'));
    optDeposit.addItemListener(e -> articleTypeChange('d'));
    optDepositReturn.addItemListener(e -> articleTypeChange('r'));

    priceStandard.addItemListener(
        e -> {
          variablePercentage.setEnabled(false);
          disablePreordered();
        });
    price50Percent.addItemListener(
        e -> {
          variablePercentage.setEnabled(false);
          disablePreordered();
        });
    pricePreordered.addItemListener(
        e -> {
          variablePercentage.setEnabled(false);
          enablePreordered();
        });
    priceVariablePercentage.addItemListener(
        e -> {
          variablePercentage.setEnabled(true);
          variablePercentage.requestFocusInWindow();
          disablePreordered();
        });
    variablePercentage.addActionListener(e -> addToCart());

    kbNumber.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            controller.searchByKbNumber();
          }
        });
    kbNumber.addActionListener(
        e -> {
          if (isWeighable) {
            amount.setText("");
          }
          amount.selectAll();
          amount.requestFocusInWindow();
        });

    suppliersItemNumber.addActionListener(e -> addToCart());
    suppliersItemNumber.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            controller.searchBySupplierItemsNumber();
          }
        });

    netPrice.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            if (netPrice.isEnabled()) recalculatePrice();
          }
        });
    netPrice.addActionListener(e -> addToCart());

    Supplier.getAll(null).forEach(s -> supplier.addItem(s));
    supplier.addActionListener(e -> supplierChange());

    for (VAT val : VAT.values()) {
      vat.addItem(val);
    }
    ;

    containerSize.setEnabled(false);

    traversalOrder.add(kbNumber);
    traversalOrder.add(articleName);
    traversalOrder.add(vat);
    traversalOrder.add(netPrice);
    traversalOrder.add(price);
    traversalOrder.add(amount);
    traversalOrder.add(suppliersItemNumber);
    traversalOrder.add(deposit);
    traversalOrder.add(supplier);
    traversalPolicy = new FocusTraversal(traversalOrder);

    westPanel.setFocusTraversalPolicy(traversalPolicy);
    barcodeCapture = new BarcodeCapture(c -> controller.processBarcode(c));

    keyCapture = new KeyCapture();
    keyCapture.add(KeyEvent.VK_F2, () -> setAmount("2"));
    keyCapture.add(KeyEvent.VK_F3, () -> setAmount("3"));
    keyCapture.add(KeyEvent.VK_F4, () -> setAmount("4"));
    keyCapture.add(KeyEvent.VK_F5, () -> setAmount("5"));
    keyCapture.add(KeyEvent.VK_F6, () -> setAmount("6"));
    keyCapture.add(KeyEvent.VK_F7, () -> setAmount("8"));
    keyCapture.add(KeyEvent.VK_F8, () -> setAmount("10"));
    keyCapture.add(KeyEvent.VK_INSERT, () -> optProduce.doClick());
    keyCapture.add(KeyEvent.VK_PAGE_UP, () -> optBakedGoods.doClick());
    keyCapture.add(KeyEvent.VK_END, () -> optArticleNo.doClick());

    articleTypeChange('a');
  }

  private void enablePreordered() {
    preordered = true;
    articleTypeInitialize(currentArticleType);
    articleNameChange();
  }

  private void disablePreordered() {
    if (preordered) {
      preordered = false;
      articleTypeInitialize(currentArticleType);
      articleNameChange();
    }
  }

  @Override
  public @NotNull JComponent getContent() {
    return mainPanel;
  }

  @Override
  public IconCode getTabIcon() {
    return FontAwesome.SHOPPING_CART;
  }

  @Override
  public boolean isStackable() {
    return true;
  }

  @Override
  public boolean processKeyboardInput(KeyEvent e) {
    return barcodeCapture.processKeyEvent(e) || keyCapture.processKeyEvent(e);
  }
}
