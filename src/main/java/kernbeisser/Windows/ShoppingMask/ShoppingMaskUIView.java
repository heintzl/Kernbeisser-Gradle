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
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.InvalidVATValueException;
import kernbeisser.Exeptions.UndefinedInputException;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class ShoppingMaskUIView implements IView<ShoppingMaskUIController> {
  // TODO: create Enum
  static final int ARTICLE_NUMBER = 0;
  static final int BAKED_GOODS = 1;
  static final int CUSTOM_PRODUCT = 2;
  static final int DEPOSIT = 3;
  static final int RETURN_DEPOSIT = 4;
  static final int PRODUCE = 5;
  static final String stornoMessageTitle = "Storno";

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

  @Linked private ShoppingMaskUIController controller;
  @Linked private ShoppingCartController cartController;

  private char currentArticleType;
  private boolean isWeighable;
  static Vector<Component> traversalOrder = new Vector<Component>(1);
  static FocusTraversal traversalPolicy;
  @Getter private boolean isPreordered = false;
  private BarcodeCapture barcodeCapture;
  private KeyCapture keyCapture;
  private ShoppingItem currentItem;

  private void createUIComponents() {
    shoppingCartView = cartController.getView();
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

  void loadUserInfo(SaleSession saleSession) {
    // TODO display customerName instead of LoginName
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
    updateAllControls(currentArticleType);
    if (getOption() == CUSTOM_PRODUCT) {
      String savedPrice = isPreordered ? netPrice.getText() : price.getText();
      loadItemStats(controller.createCustomItem(getSupplier()));
      if (isPreordered) {
        netPrice.setText(savedPrice);
        recalculatePrice();
      } else {
        price.setText(savedPrice);
      }
    }
    articleNameOrVatChange();
  }

  private void articleNameOrVatChange() {
    updateAllControls(currentArticleType);

    if (articleName.isEnabled()) {
      if (isEmptyArticleName()) {
        articleName.requestFocusInWindow();
      } else if (!isValidVat()) {
        vat.requestFocusInWindow();
      } else if (isPreordered) {
        netPrice.selectAll();
        netPrice.requestFocusInWindow();
      } else {
        price.selectAll();
        price.requestFocusInWindow();
      }
    }
  }

  private void priceEntered() {
    updateAmountControl(currentArticleType, isPreordered);
    if (amount.isEnabled()) {
      amount.selectAll();
      amount.requestFocusInWindow();
    } else {
      addToCart();
    }
  }

  private boolean isEmptyArticleName() {
    return articleName.getText().equals("");
  }

  private boolean isSupplierSet() {
    return supplier.getSelectedItem() != null;
  }

  private boolean isValidVat() {
    return vat.getSelectedIndex() > -1;
  }

  private void updateKbNumberControl(char type) {
    kbNumber.setEnabled(type == 'a');
  }

  private void updateSupplierControl(char type, boolean preordered) {
    if (type == 'a' || (type == 'c' && preordered)) {
      supplier.setEnabled(true);
    } else {
      supplier.setEnabled(false);
    }
  }

  private void updateSupplierNumberControl(char type) {
    if (type == 'a' && isSupplierSet()) {
      suppliersItemNumber.setEnabled(true);
    } else {
      suppliersItemNumber.setEnabled(false);
    }
  }

  private void updateArticleNameControl(char type, boolean preordered) {
    if (type == 'c' && (isSupplierSet() || !preordered)) {
      articleName.setEnabled(true);
    } else {
      articleName.setEnabled(false);
    }
  }

  private void updateVATControl(char type, boolean preordered) {
    if (type == 'c' && (isSupplierSet() || !preordered)) {
      vat.setEnabled(true);
    } else {
      vat.setEnabled(false);
    }
  }

  private void updateNetPriceControl(char type, boolean preordered) {
    if (preordered) {
      if (type == 'c') {
        netPrice.setEnabled(isSupplierSet() && !isEmptyArticleName() && isValidVat());
      } else {
        netPrice.setEnabled(!isEmptyArticleName() && isValidVat());
      }
    } else {
      netPrice.setEnabled(false);
    }
  }

  private void updatePriceControl(char type, boolean preordered) {
    if (preordered) {
      price.setEnabled(false);
    } else {
      if (type == 'c' || type == 'p' || type == 'b') {
        price.setEnabled(!isEmptyArticleName() && isValidVat());
      } else {
        price.setEnabled(false);
      }
    }
  }

  private void updateAmountControl(char type, boolean preordered) {
    if (type == 'a') {
      amount.setEnabled(true);
    } else if (type == 'c') {
      boolean isPriceBaseFieldsSet = !isEmptyArticleName() && isValidVat();
      if (preordered) {
        amount.setEnabled(isPriceBaseFieldsSet && isSupplierSet() && getNetPrice() > 0.0);
      } else {
        amount.setEnabled(isPriceBaseFieldsSet && getPrice() > 0.0);
      }
    } else {
      amount.setEnabled(false);
    }
  }

  private void updateDepositControl(char type) {
    if (type == 'd' || type == 'r') {
      deposit.setEnabled(true);
    } else {
      deposit.setEnabled(false);
    }
  }

  private void updateAllControls(char type) {
    updateKbNumberControl(type);
    updateSupplierControl(type, isPreordered);
    updateSupplierNumberControl(type);
    updateArticleNameControl(type, isPreordered);
    updateVATControl(type, isPreordered);
    updateNetPriceControl(type, isPreordered);
    updatePriceControl(type, isPreordered);
    updateAmountControl(type, isPreordered);
    updateDepositControl(type);
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

    addAmount.setVisible(type == 'a');
    addPrice.setVisible(!isPreordered && "pbc".indexOf(type) != -1);
    addNetPrice.setVisible(isPreordered && "pbc".indexOf(type) != -1);
    addDeposit.setVisible("dr".indexOf(type) != -1);

    setSupplier(null);
    setSuppliersItemNumber("");

    setKbNumber("");

    price.setText("");
    priceUnit.setText("€");
    netPrice.setText("");
    netPriceUnit.setText("€");

    amount.setText("1");
    amountUnit.setText("");

    containerUnit.setText("");

    deposit.setText("");

    if (type == 'd' || type == 'r') {
      setVat(VAT.HIGH);
    }
    variablePercentage.setEnabled(
        priceVariablePercentage.isEnabled() && priceVariablePercentage.isSelected());

    if (type == 'p') {
      loadItemStats(Objects.requireNonNull(ShoppingItem.createProduce(0.0, isPreordered)));
      this.articleName.setText("Obst & Gemüse");
      price.selectAll();
      price.requestFocusInWindow();
    } else if (type == 'b') {
      loadItemStats(Objects.requireNonNull(ShoppingItem.createBakeryProduct(0.0, isPreordered)));
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
    updateAllControls(type);
  }

  private void setPriceOptions(char type) {
    if (type == 'd' || type == 'r') {
      pricePreordered.setEnabled(false);
      isPreordered = false;
    } else {
      pricePreordered.setEnabled(true);
    }
    if (type == 'a') {
      price50Percent.setEnabled(true);
      priceVariablePercentage.setEnabled(true);
    } else {
      priceStandard.setSelected(!isPreordered);
      pricePreordered.setSelected(isPreordered);
      price50Percent.setEnabled(false);
      priceVariablePercentage.setEnabled(false);
    }
  }

  void loadItemStats(ShoppingItem shoppingItem) {
    currentItem = shoppingItem;
    setSupplier(shoppingItem.getSupplier());
    setKbNumber(
        shoppingItem.getKbNumber() != 0 ? Integer.toString(shoppingItem.getKbNumber()) : "");
    setSuppliersItemNumber(Integer.toString(shoppingItem.getSuppliersItemNumber()));
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
            (isPreordered
                ? shoppingItem.calculateItemRetailPrice(shoppingItem.getItemNetPrice())
                    * shoppingItem.getContainerSize()
                : shoppingItem.getItemRetailPrice())));
    priceUnit.setText(isPreordered ? "€/Geb." : shoppingItem.isWeighAble() ? "€/kg" : "€");
    netPrice.setText(
        String.format(
            "%.2f",
            shoppingItem.getItemNetPrice()
                * (isPreordered ? shoppingItem.getContainerSize() : 1.0)));
    netPriceUnit.setText(priceUnit.getText());
    amountUnit.setText(
        isPreordered
            ? "Geb."
            : shoppingItem.isWeighAble() ? shoppingItem.getPriceUnits().getShortName() : "stk.");
    isWeighable = shoppingItem.isWeighAble();
    containerSize.setText(
        new DecimalFormat("##.###")
            .format(shoppingItem.getContainerSize() * (isWeighable ? 1000 : 1)));
    containerUnit.setText(
        (isWeighable ? shoppingItem.getPriceUnits() : MetricUnits.PIECE).getShortName());
    try {
      if (shoppingItem.getVat() > 0) setVat(shoppingItem.getVat());
    } catch (InvalidVATValueException e) {
      e.printStackTrace();
      vat.setSelectedIndex(-1);
    }
    deposit.setText(String.format("%.2f", shoppingItem.getSingleDeposit()));
    updateAllControls(currentArticleType);
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
    depositUnit.setText("€");
    priceUnit.setText("€");
    amount.setText("1");
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
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                initValue);
    if (response != null) {
      response = response.trim();
    }
    return response;
  }

  public boolean confirmClose() {
    return JOptionPane.showConfirmDialog(
            getTopComponent(), "Soll der Einkauf wirklich abgebrochen werden?")
        == 0;
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

  // Getters and Setters BEGIN
  public String getItemName() {
    return articleName.getText();
  }

  public double getDeposit() {
    return deposit.getSafeValue();
  }

  public void setKbNumber(String value) {
    this.kbNumber.setText(value);
  }

  void setSuppliersItemNumber(String value) {
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

  int getDiscount() {
    if (priceStandard.isSelected()) {
      return 0;
    }
    if (price50Percent.isSelected()) {
      return 50;
    }
    if (priceVariablePercentage.isSelected()) {
      return variablePercentage.getSafeValue();
    }
    return 0;
  }

  void setDiscount() {
    if (!rememberReductionSetting.isSelected()) {
      priceStandard.setSelected(true);
    }
  }

  public int getOption() {
    // TODO make enum
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

  void setSupplier(Supplier s) {
    supplier.getModel().setSelectedItem(s);
  }

  VAT getVat() {
    return (VAT) vat.getSelectedItem();
  }

  void setVat(VAT vatEnum) {
    vat.getModel().setSelectedItem(vatEnum);
  }

  void setVat(double vatValue) throws InvalidVATValueException {
    boolean found = false;
    for (VAT vatEnum : VAT.values()) {
      if (vatEnum.getValue() == vatValue) {
        setVat(vatEnum);
        found = true;
        break;
      }
    }
    if (!found) {
      throw new InvalidVATValueException(vatValue);
    }
  }
  // Getters and Setters END

  Dimension getShoppingListSize() {
    return shoppingListPanel.getSize();
  }

  public IController getController() {
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

    optProduce.addItemListener(e -> articleTypeChange('p'));
    optBakedGoods.addItemListener(e -> articleTypeChange('b'));
    optArticleNo.addItemListener(e -> articleTypeChange('a'));
    optCustomProduct.addItemListener(e -> articleTypeChange('c'));
    optDeposit.addItemListener(e -> articleTypeChange('d'));
    optDepositReturn.addItemListener(e -> articleTypeChange('r'));

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

    Supplier.getAll(null).forEach(s -> supplier.addItem(s));
    supplier.addActionListener(e -> supplierChange());

    suppliersItemNumber.addActionListener(e -> addToCart());
    suppliersItemNumber.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            controller.searchBySupplierItemsNumber();
          }
        });
    suppliersItemNumber.setToolTipText(
        "für die Suche nach der Lieferantennummer muss erst ein Lieferant ausgewählt werden");

    articleName.addActionListener(e -> articleNameOrVatChange());
    vat.addActionListener(e -> articleNameOrVatChange());

    netPrice.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            if (netPrice.isEnabled()) recalculatePrice();
          }
        });
    netPrice.addActionListener(e -> priceEntered());

    price.addActionListener(e -> priceEntered());
    deposit.addActionListener(e -> addToCart());
    amount.addActionListener(e -> addToCart());
    for (VAT val : VAT.values()) {
      vat.addItem(val);
    }

    containerSize.setEnabled(false);

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
    price50Percent.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) addToCart();
          }
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

    editUser.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 20, new Color(49, 114, 128)));
    editUser.addActionListener(e -> editUserAction());

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

    // TODO make enum
    articleTypeChange('a');
  }

  private void enablePreordered() {
    isPreordered = true;
    articleTypeInitialize(currentArticleType);
    articleNameOrVatChange();
  }

  private void disablePreordered() {
    if (isPreordered) {
      isPreordered = false;
      articleTypeInitialize(currentArticleType);
      articleNameOrVatChange();
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
    return Setting.OPEN_MULTIPLE_SHOPPING_MASK.getBooleanValue();
  }

  @Override
  public boolean processKeyboardInput(KeyEvent e) {
    return barcodeCapture.processKeyEvent(e) || keyCapture.processKeyEvent(e);
  }
}
