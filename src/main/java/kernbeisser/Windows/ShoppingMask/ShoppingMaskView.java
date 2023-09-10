package kernbeisser.Windows.ShoppingMask;

import static java.text.MessageFormat.format;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.Vector;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.FocusTraversal.FocusTraversal;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartView;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.*;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingField;
import kernbeisser.Security.StaticMethodTransformer.StaticAccessPoint;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class ShoppingMaskView implements IView<ShoppingMaskController> {

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
  private IntegerParseField kbNumber;
  private IntegerParseField suppliersItemNumber;
  private JTextField articleName;
  private DoubleParseField retailPrice;
  private DoubleParseField netPrice;
  private DoubleParseField containerSize;
  private DoubleParseField itemMultiplier;
  private DoubleParseField deposit;
  private JPanel westPanel;
  private JPanel eastPanel;
  private JPanel eastUpperPanel;
  private JLabel customerCredit;
  private JLabel customerInfoName;
  private JRadioButton priceStandard;
  private JRadioButton pricePreordered;
  private JRadioButton price50Percent;
  private JRadioButton priceVariablePercentage;
  private JLabel retailPriceUnit;
  private JLabel netPriceUnit;
  private JLabel itemMultiplierUnit;
  private JLabel containerUnit;
  private IntegerParseField variablePercentage;
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
  private JLabel salesPersonInfo;
  private JLabel depositUnit;
  private ShoppingCartView shoppingCartView;
  private JLabel containerSizeLabel;
  private JPanel productTypePanel;
  private JPanel reductionPanel;
  private JComboBox<Supplier> supplier;
  private JButton emptyShoppingCart;
  private JComboBox<VAT> vat;
  private JLabel solidarity;
  private JTextField producer;
  private ButtonGroup optGrpArticleType;
  private ButtonGroup optGrpReduction;

  @Linked private ShoppingMaskController controller;
  @Linked private ShoppingCartController cartController;

  private ArticleType currentArticleType;
  private boolean isWeighable;
  static Vector<Component> traversalOrder = new Vector<>(1);
  static FocusTraversal traversalPolicy;
  @Getter private boolean isPreordered = false;

  EnumSet<ArticleType> articleTypesWithSettablePrice;
  EnumSet<ArticleType> depositArticleTypes;

  private void createUIComponents() {
    shoppingCartView = cartController.getView();
  }

  private void doCancel() {
    back();
  }

  private void doCheckout() {
    controller.startPay();
  }

  void openSearchWindow() {
    controller.openSearchWindow();
  }

  public void addToCart() {
    if (itemMultiplier.getText().isEmpty() && !isWeighable) itemMultiplier.setText("1");
    if (controller.addToShoppingCart()) {
      articleTypeInitialize(currentArticleType);
    }
    itemMultiplier.setText("");
  }

  void loadUserInfo(SaleSession saleSession) {
    String customerDisplayName = saleSession.getCustomer().getFullName();
    UserGroup userGroup = saleSession.getCustomer().getUserGroup();
    customerName.setText(customerDisplayName);
    customerCredit.setText(format("{0, number, 0.00}\u20AC", userGroup.getValue()));
    solidarity.setText(format("{0, number, 0.0} %", userGroup.getSolidaritySurcharge() * 100));
    salesPersonInfo.setText(
        saleSession.getSeller().getFullName()
            + (saleSession.getSecondSeller() != null
                ? " / " + saleSession.getSecondSeller().getFullName()
                : ""));
  }

  private void supplierChange() {
    if (getSupplier() == null) {
      return;
    }
    updateAllControls(currentArticleType);
    if (getArticleType() == ArticleType.CUSTOM_PRODUCT) {
      String savedPrice = isPreordered ? netPrice.getText() : retailPrice.getText();
      loadArticleStats(controller.createCustomArticle(getSupplier()));
      if (isPreordered && !savedPrice.isEmpty()) {
        netPrice.setText(savedPrice);
        recalculateRetailPrice(false);
      } else {
        retailPrice.setText(savedPrice);
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
        retailPrice.selectAll();
        retailPrice.requestFocusInWindow();
      }
    }
  }

  private void priceEntered(int keyCode) {
    updateItemMultiplierControl(currentArticleType, isPreordered);
    if (itemMultiplier.isEnabled()) {
      itemMultiplier.selectAll();
      if (keyCode == KeyEvent.VK_ENTER) {
        itemMultiplier.requestFocusInWindow();
      }
    } else {
      if (keyCode == KeyEvent.VK_ENTER) {
        addToCart();
      }
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

  private void updateKbNumberControl(ArticleType type) {
    kbNumber.setEnabled(type == ArticleType.ARTICLE_NUMBER);
  }

  private void updateSupplierControl(ArticleType type, boolean preordered) {
    if (type == ArticleType.ARTICLE_NUMBER || (type == ArticleType.CUSTOM_PRODUCT && preordered)) {
      supplier.setEnabled(true);
    } else {
      supplier.setEnabled(false);
    }
  }

  private void updateSupplierNumberControl(ArticleType type) {
    if (type == ArticleType.ARTICLE_NUMBER && isSupplierSet()) {
      suppliersItemNumber.setEnabled(true);
    } else {
      suppliersItemNumber.setEnabled(false);
    }
  }

  private void updateArticleNameControl(ArticleType type, boolean preordered) {
    if (type == ArticleType.CUSTOM_PRODUCT && (isSupplierSet() || !preordered)) {
      articleName.setEnabled(true);
    } else {
      articleName.setEnabled(false);
    }
  }

  private void updateVATControl(ArticleType type, boolean preordered) {
    if (type == ArticleType.CUSTOM_PRODUCT && (isSupplierSet() || !preordered)) {
      vat.setEnabled(true);
    } else {
      vat.setEnabled(false);
    }
  }

  private void updateNetPriceControl(ArticleType type, boolean preordered) {
    if (preordered) {
      if (type == ArticleType.CUSTOM_PRODUCT) {
        netPrice.setEnabled(isSupplierSet() && !isEmptyArticleName() && isValidVat());
      } else {
        netPrice.setEnabled(!isEmptyArticleName() && isValidVat());
      }
    } else {
      netPrice.setEnabled(false);
    }
  }

  private void updatePriceControl(ArticleType type, boolean preordered) {
    if (preordered) {
      retailPrice.setEnabled(false);
    } else {
      if (articleTypesWithSettablePrice.contains(type)) {
        retailPrice.setEnabled(!isEmptyArticleName() && isValidVat());
      } else {
        retailPrice.setEnabled(false);
      }
    }
  }

  private void updateItemMultiplierControl(ArticleType type, boolean preordered) {
    if (type == ArticleType.ARTICLE_NUMBER) {
      itemMultiplier.setEnabled(true);
    } else if (type == ArticleType.CUSTOM_PRODUCT) {
      boolean isPriceBaseFieldsSet = !isEmptyArticleName() && isValidVat();
      if (preordered) {
        itemMultiplier.setEnabled(isPriceBaseFieldsSet && isSupplierSet() && getNetPrice() > 0.0);
      } else {
        itemMultiplier.setEnabled(isPriceBaseFieldsSet && getRetailPrice() > 0.0);
      }
    } else {
      itemMultiplier.setEnabled(false);
    }
  }

  private void updateDepositControl(ArticleType type) {
    if (depositArticleTypes.contains(type)) {
      deposit.setEnabled(true);
    } else {
      deposit.setEnabled(false);
    }
  }

  private void updateAllControls(ArticleType type) {
    updateKbNumberControl(type);
    updateSupplierControl(type, isPreordered);
    updateSupplierNumberControl(type);
    updateArticleNameControl(type, isPreordered);
    updateVATControl(type, isPreordered);
    updateNetPriceControl(type, isPreordered);
    updatePriceControl(type, isPreordered);
    updateItemMultiplierControl(type, isPreordered);
    updateDepositControl(type);
  }

  void articleTypeChange(ArticleType type) {
    if (currentArticleType != type) {
      articleTypeInitialize(type);
    }
  }

  private void articleTypeInitialize(ArticleType type) {
    currentArticleType = type;
    setPriceOptions(type);
    isWeighable = false;

    addAmount.setVisible(type == ArticleType.ARTICLE_NUMBER || type == ArticleType.CUSTOM_PRODUCT);
    addPrice.setVisible(
        !isPreordered
            && type != ArticleType.CUSTOM_PRODUCT
            && articleTypesWithSettablePrice.contains(type));
    addNetPrice.setVisible(isPreordered && articleTypesWithSettablePrice.contains(type));
    addDeposit.setVisible(depositArticleTypes.contains(type));

    if (!isPreordered) {
      setSupplier(null);
    }
    setSuppliersItemNumber("");

    setKbNumber("");
    vat.setSelectedIndex(-1);

    retailPrice.setText("");
    retailPriceUnit.setText("€");
    netPrice.setText("");
    netPriceUnit.setText("€");

    itemMultiplierUnit.setText("");

    containerSize.setText("");
    containerUnit.setText("");

    deposit.setText("");

    if (depositArticleTypes.contains(type)) {
      setVat(VAT.HIGH);
    }
    variablePercentage.setEnabled(
        priceVariablePercentage.isEnabled() && priceVariablePercentage.isSelected());

    if (type == ArticleType.PRODUCE || type == ArticleType.BAKED_GOODS) {
      RawPrice rawPrice = type == ArticleType.PRODUCE ? RawPrice.PRODUCE : RawPrice.BAKERY;
      loadArticleStats(Articles.getOrCreateRawPriceArticle(rawPrice));
      this.articleName.setText(rawPrice.getName());
      if (isPreordered) {
        netPrice.requestFocusInWindow();
        netPrice.selectAll();
      } else {
        retailPrice.requestFocusInWindow();
        retailPrice.selectAll();
      }
    } else if (type == ArticleType.DEPOSIT) {
      this.articleName.setText("Pfand-Behälter");
      deposit.requestFocusInWindow();
    } else if (type == ArticleType.RETURN_DEPOSIT) {
      this.articleName.setText("Pfand zurück");
      deposit.requestFocusInWindow();
    } else if (type == ArticleType.ARTICLE_NUMBER) {
      this.articleName.setText("");
      kbNumber.requestFocusInWindow();
    } else if (type == ArticleType.CUSTOM_PRODUCT) {
      this.articleName.setText("");
      this.vat.setSelectedIndex(-1);
      if (articleName.isEnabled()) {
        articleName.requestFocusInWindow();
      } else {
        supplier.requestFocusInWindow();
      }
      this.producer.setText("");
    }
    updateAllControls(type);
  }

  private void setPriceOptions(ArticleType type) {
    if (depositArticleTypes.contains(type)) {
      pricePreordered.setEnabled(false);
      isPreordered = false;
    } else {
      pricePreordered.setEnabled(true);
    }
    if (type == ArticleType.ARTICLE_NUMBER) {
      price50Percent.setEnabled(true);
      priceVariablePercentage.setEnabled(true);
    } else {
      priceStandard.setSelected(!isPreordered);
      pricePreordered.setSelected(isPreordered);
      price50Percent.setEnabled(false);
      priceVariablePercentage.setEnabled(false);
    }
  }

  private String intToStringNot0(int value) {
    if (value == 0) return "";
    return Integer.toString(value);
  }

  private String shorten(String string, int maxLength) {
    if (string.length() > maxLength) {
      return new StringBuilder(string).replace(maxLength - 4, string.length(), "...").toString();
    }
    return string;
  }

  public void loadArticleStats(Article article) {
    isWeighable = article.isWeighable();
    // TODO Why?
    if (article.getKbNumber() == ArticleConstants.CUSTOM_PRODUCT.getUniqueIdentifier()) {
      return;
    }
    setKbNumber(intToStringNot0(article.getKbNumber()));
    setSupplier(article.getSupplier());
    setSuppliersItemNumber(intToStringNot0(article.getSuppliersItemNumber()));
    if (article.getName() != null) {
      articleName.setText(shorten(article.getName(), 40));
      articleName.setCaretPosition(0);
    }
    if (article.getProducer() != null) {
      producer.setText(article.getProducer());
    }
    setVat(article.getVat());
    try {
      netPrice.setText(formattedPrice(controller.getUnitNetPrice(article, isPreordered)));
      retailPrice.setText(
          formattedPrice(
              controller.recalculateRetailPrice(article, getDiscount(), isPreordered, false)));
    } catch (NullPointerException e) {
      netPrice.setText("");
      retailPrice.setText("");
    }
    String priceUnit = "€";
    if (isPreordered) {
      String unit = MetricUnits.CONTAINER.getShortName();
      priceUnit += "/" + unit;
      itemMultiplierUnit.setText(unit);
    } else if (getArticleType() == ArticleType.ARTICLE_NUMBER
        || getArticleType() == ArticleType.CUSTOM_PRODUCT) {
      priceUnit += "/" + Articles.getPriceUnit(article).getShortName();
      itemMultiplierUnit.setText(Articles.getMultiplierUnit(article).getShortName());
    } else {
      itemMultiplierUnit.setText("");
    }
    netPriceUnit.setText(priceUnit);
    retailPriceUnit.setText(priceUnit);
    containerSize.setText(
        new DecimalFormat("##.###").format(article.getContainerSize() * (isWeighable ? 1000 : 1)));
    containerUnit.setText(Articles.getContainerUnits(article).getShortName());
    deposit.setText(String.format("%.2f", article.getSingleDeposit()));
    updateAllControls(currentArticleType);
  }

  private String formattedPrice(double value) {
    return AccessCheckingField.UNSIGNED_CURRENCY_FORMER.toString(value);
  }

  private void recalculateRetailPrice(boolean overWriteNetPrice) {
    if (getKBArticleNumber() > 0 || isPreordered) {
      Article article = controller.extractArticleFromUI();
      try {
        double retailPrice =
            controller.recalculateRetailPrice(
                article, getDiscount(), isPreordered(), overWriteNetPrice);
        if (retailPrice <= 0) {
          throw new NullPointerException();
        }
        this.retailPrice.setText(String.format("%.2f", retailPrice));
      } catch (NullPointerException e) {
        retailPrice.setText("");
      }
    }
  }

  void defaultSettings() {
    retailPrice.setText("0.00");
    netPrice.setText("0.00");
    depositUnit.setText("€");
    retailPriceUnit.setText("€");
    articleName.setText("Keinen Artikel gefunden!");
    itemMultiplierUnit.setText("");
    containerUnit.setText("");
  }

  void messageNoArticleFound() {
    Tools.beep();
    JOptionPane.showMessageDialog(
        mainPanel,
        "Es konnte kein Artikel mit der angegeben Artikelnummer / Lieferantennummer gefunden werden");
  }

  void messageInvalidDiscount() {
    Tools.beep();
    JOptionPane.showMessageDialog(mainPanel, "Rabatt muss zwischen 0 und 100 % liegen");
    variablePercentage.setText("");
  }

  public void messageInvalidBarcode(String barcode) {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Ungültiger Barcode: " + barcode,
        "Barcodefehler",
        JOptionPane.WARNING_MESSAGE);
  }

  public void messageDepositStorno() {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(), "Pfand kann nicht storniert werden!", "Storno", JOptionPane.WARNING_MESSAGE);
    deposit.setText("");
  }

  public void messageCartIsEmpty() {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(), "Es gibt nichts zu bezahlen!", "Leerer Einkauf", JOptionPane.WARNING_MESSAGE);
  }

  public void messageNoSupplier() {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Um nach der Lieferantennummer suchen zu können,\nmuss als erstes ein Lieferant ausgewählt werden.",
        "Lieferant nicht ausgewählt",
        JOptionPane.WARNING_MESSAGE);
  }

  public String inputStornoRetailPrice(double itemRetailPrice, boolean retry) {
    String initValue = MessageFormat.format("{0, number, 0.00}", itemRetailPrice).trim();
    String message;
    String response;
    if (retry) { // item is piece, first try
      message =
          "Die Eingabe ist ungültig. Bitte hier einen gültigen Einzelpreis angeben, für den Fall, dass er sich seit "
              + "dem ursprünglichen Einkauf geändert hat:";
    } else { // item is piece later try
      message =
          "Negative Menge: Soll der Artikel wirklich storniert werden? Dann kann hier der Einzelpreis angepasst"
              + " werden, für den Fall, dass er sich seit dem ursprünglichen Einkauf geändert hat:";
    }
    Tools.beep();
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
            getTopComponent(),
            "Soll der Einkauf wirklich abgebrochen werden?",
            "Einkauf abbrechen",
            JOptionPane.YES_NO_OPTION)
        == 0;
  }

  public boolean confirmStorno() {
    Tools.beep();
    return JOptionPane.showConfirmDialog(
            getContent(),
            "Soll die Ware wirklich storniert werden?",
            stornoMessageTitle,
            JOptionPane.YES_NO_OPTION)
        == 0;
  }

  public boolean confirmEmptyCart() {
    Tools.beep();
    return JOptionPane.showConfirmDialog(
            getContent(),
            "Sollen wirklich alle Artikel gelöscht werden?",
            "Alle Artikel löschen",
            JOptionPane.YES_NO_OPTION)
        == 0;
  }

  public boolean confirmPriceWarning() {
    Tools.beep();
    return JOptionPane.showConfirmDialog(
            getContent(),
            "Der Preis ist ganz schön hoch. Bist Du sicher, dass alle Eingaben stimmen?",
            "Teurer Einkauf",
            JOptionPane.YES_NO_OPTION)
        == 0;
  }

  public boolean confirmAmountWarning() {
    Tools.beep();
    return JOptionPane.showConfirmDialog(
            getContent(),
            "Die Menge ist ganz schön hoch. Bist Du sicher, dass alle Eingaben stimmen?",
            "Hohe Menge",
            JOptionPane.YES_NO_OPTION)
        == 0;
  }

  public void messageRoundedMultiplier(String roundedMultiplier) {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Die Menge an Artikeln muss ganzzahlig sein. Sie wird auf "
            + roundedMultiplier
            + " gerundet.",
        "Ungültige Mengenangabe",
        JOptionPane.WARNING_MESSAGE);
  }

  public boolean messageUnderMin() {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Mit diesem Artikel würde das Mindestguthaben unterschritten. Bitte Guthaben auffüllen, um "
            + "weiter einzukaufen!",
        "Zuviel eingekauft",
        JOptionPane.ERROR_MESSAGE);
    return false;
  }

  // Getters and Setters BEGIN
  public String getArticleName() {
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

  public double getItemMultiplier() {
    return itemMultiplier.getSafeValue();
  }

  void setItemMultiplier(String value) {
    if (itemMultiplier.isEnabled() && itemMultiplier.isVisible()) {
      this.itemMultiplier.setText(value);
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

  public ArticleType getArticleType() {
    if (optArticleNo.isSelected()) {
      return ArticleType.ARTICLE_NUMBER;
    }
    if (optBakedGoods.isSelected()) {
      return ArticleType.BAKED_GOODS;
    }
    if (optCustomProduct.isSelected()) {
      return ArticleType.CUSTOM_PRODUCT;
    }
    if (optDeposit.isSelected()) {
      return ArticleType.DEPOSIT;
    }
    if (optDepositReturn.isSelected()) {
      return ArticleType.RETURN_DEPOSIT;
    }
    if (optProduce.isSelected()) {
      return ArticleType.PRODUCE;
    }
    return ArticleType.INVALID;
  }

  double getNetPrice() {
    return netPrice.getSafeValue();
  }

  double getRetailPrice() {
    return retailPrice.getSafeValue();
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
  // Getters and Setters END

  public static void resizeFonts(JPanel p, float fontSize) {
    for (Component c : p.getComponents()) {
      if (c instanceof JPanel) {
        resizeFonts((JPanel) c, fontSize);
      } else {
        c.setFont(c.getFont().deriveFont(fontSize));
      }
    }
  }

  public ShoppingMaskController getController() {
    return controller;
  }

  @Override
  public void initialize(ShoppingMaskController controller) {
    float fontSize =
        UserSetting.FONT_SCALE_FACTOR.getFloatValue(LogInModel.getLoggedIn()) * 8f + 4f;
    resizeFonts(ShoppingItemPanel, fontSize);
    articleTypesWithSettablePrice =
        EnumSet.of(ArticleType.CUSTOM_PRODUCT, ArticleType.BAKED_GOODS, ArticleType.PRODUCE);
    depositArticleTypes = EnumSet.of(ArticleType.DEPOSIT, ArticleType.RETURN_DEPOSIT);
    checkout.addActionListener(e -> doCheckout());
    emptyShoppingCart.addActionListener(e -> controller.emptyShoppingCart());
    cancelSalesSession.addActionListener(e -> doCancel());
    float iconSize = fontSize * 1.25f;
    searchArticle.setIcon(
        IconFontSwing.buildIcon(FontAwesome.SEARCH, iconSize, new Color(49, 114, 128)));
    searchArticle.addActionListener(e -> openSearchWindow());
    addPrice.setIcon(
        IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, iconSize, new Color(49, 114, 128)));
    addPrice.addActionListener(e -> addToCart());
    addNetPrice.setIcon(
        IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, iconSize, new Color(49, 114, 128)));
    addNetPrice.addActionListener(e -> addToCart());
    addDeposit.setIcon(
        IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, iconSize, new Color(49, 114, 128)));
    addDeposit.addActionListener(e -> addToCart());
    addAmount.setIcon(
        IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, iconSize, new Color(49, 114, 128)));
    addAmount.addActionListener(e -> addToCart());

    optProduce.addItemListener(e -> articleTypeChange(ArticleType.PRODUCE));
    optBakedGoods.addItemListener(e -> articleTypeChange(ArticleType.BAKED_GOODS));
    optArticleNo.addItemListener(e -> articleTypeChange(ArticleType.ARTICLE_NUMBER));
    optCustomProduct.addItemListener(e -> articleTypeChange(ArticleType.CUSTOM_PRODUCT));
    optDeposit.addItemListener(e -> articleTypeChange(ArticleType.DEPOSIT));
    optDepositReturn.addItemListener(e -> articleTypeChange(ArticleType.RETURN_DEPOSIT));

    kbNumber.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            controller.searchByKbNumber();
          }
        });
    kbNumber.addActionListener(
        e -> {
          if (isWeighable && !isPreordered) {
            itemMultiplier.setText("");
          } else {
            if (itemMultiplier.getText().isEmpty()) {
              itemMultiplier.setText("1");
            }
          }
          itemMultiplier.selectAll();
          itemMultiplier.requestFocusInWindow();
        });

    Supplier.getAll(null).forEach(s -> supplier.addItem(s));
    supplier.addActionListener(e -> supplierChange());

    suppliersItemNumber.addActionListener(e -> addToCart());
    suppliersItemNumber.addKeyListener(
        new KeyAdapter() {
          private String lastSearch = "";

          @Override
          public void keyReleased(KeyEvent e) {
            if (suppliersItemNumber.getText().equals(lastSearch)) {
              return;
            }
            controller.searchBySupplierItemsNumber();
            lastSearch = suppliersItemNumber.getText();
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
            if (netPrice.isEnabled() && getNetPrice() > 0.0) {
              recalculateRetailPrice(true);
            }
            priceEntered(e.getKeyCode());
          }
        });
    retailPrice.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            priceEntered(e.getKeyCode());
          }
        });
    deposit.addActionListener(e -> addToCart());
    itemMultiplier.addActionListener(e -> addToCart());
    for (VAT val : VAT.values()) {
      vat.addItem(val);
    }

    containerSize.setEnabled(false);

    priceStandard.addItemListener(
        e -> {
          variablePercentage.setEnabled(false);
          disablePreordered();
          rememberReductionSetting.setSelected(false);
        });

    price50Percent.addItemListener(
        e -> {
          variablePercentage.setEnabled(false);
          disablePreordered();
          rememberReductionSetting.setSelected(false);
        });
    price50Percent.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              addToCart();
            }
          }
        });

    pricePreordered.addItemListener(
        e -> {
          variablePercentage.setEnabled(false);
          enablePreordered();
          setSupplier(Supplier.getKKSupplier());
          rememberReductionSetting.setSelected(true);
        });
    rememberReductionSetting.setToolTipText("Rabatt-Einstellungen für Folgeartikel merken");
    priceVariablePercentage.addItemListener(
        e -> {
          variablePercentage.setEnabled(true);
          variablePercentage.requestFocusInWindow();
          disablePreordered();
          rememberReductionSetting.setSelected(false);
        });
    variablePercentage.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              addToCart();
            } else {
              recalculateRetailPrice(false);
            }
          }
        });

    editUser.setIcon(IconFontSwing.buildIcon(FontAwesome.INFO, 20, new Color(49, 114, 128)));
    editUser.addActionListener(e -> controller.openUserInfo());

    traversalOrder.add(kbNumber);
    traversalOrder.add(articleName);
    traversalOrder.add(vat);
    traversalOrder.add(netPrice);
    traversalOrder.add(retailPrice);
    traversalOrder.add(itemMultiplier);
    traversalOrder.add(suppliersItemNumber);
    traversalOrder.add(deposit);
    traversalOrder.add(supplier);
    traversalPolicy = new FocusTraversal(traversalOrder);

    westPanel.setFocusTraversalPolicy(traversalPolicy);

    articleTypeChange(ArticleType.ARTICLE_NUMBER);

    SwingUtilities.invokeLater(
        () -> {
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          kbNumber.requestFocusInWindow();
        });
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
    } else {
      recalculateRetailPrice(false);
    }
  }

  @Override
  public @NotNull JComponent getContent() {
    return mainPanel;
  }

  @Override
  @StaticAccessPoint
  public IconCode getTabIcon() {
    return FontAwesome.SHOPPING_CART;
  }

  @Override
  public boolean isStackable() {
    return Setting.OPEN_MULTIPLE_SHOPPING_MASK.getBooleanValue();
  }

  public void setFocusOnKBNumber() {
    kbNumber.requestFocusInWindow();
  }

  @Override
  public Component getFocusOnInitialize() {
    return kbNumber;
  }

  public void setFocusOnAmount() {
    itemMultiplier.requestFocusInWindow();
  }

  @Override
  public String getTitle() {
    return "Einkauf für " + controller.getModel().getSaleSession().getCustomer().getFullName();
  }
}
