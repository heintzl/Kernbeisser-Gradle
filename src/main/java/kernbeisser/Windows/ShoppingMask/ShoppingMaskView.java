package kernbeisser.Windows.ShoppingMask;

import static java.text.MessageFormat.format;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.FocusTraversal.FocusTraversal;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartView;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.DBConnection.DBConnection;
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
  private JButton editPost;
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

  public void setEditPostVisible(boolean visible) {
    editPost.setVisible(visible);
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
      if (pricePreordered.isSelected()) {
        suppliersItemNumber.requestFocusInWindow();
      } else {
        kbNumber.requestFocusInWindow();
      }
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
    kbNumber.addActionListener(this::articleSelectedListener);

    DBConnection.getAll(Supplier.class).stream()
        .sorted(Comparator.comparing(Supplier::getName))
        .forEach(s -> supplier.addItem(s));
    supplier.addActionListener(e -> supplierChange());

    suppliersItemNumber.addActionListener(this::articleSelectedListener);
    suppliersItemNumber.addKeyListener(
        new KeyAdapter() {
          private String lastSearch = "";

          @Override
          public void keyReleased(KeyEvent e) {
            String number = suppliersItemNumber.getText();
            if (number.isEmpty() || number.equals(lastSearch)) {
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
    rememberReductionSetting.addActionListener(e -> setDiscount());
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
    editPost.addActionListener(e -> controller.openPost(false));

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

  private void articleSelectedListener(ActionEvent e) {
    if (isWeighable && !isPreordered) {
      itemMultiplier.setText("");
    } else {
      if (itemMultiplier.getText().isEmpty()) {
        itemMultiplier.setText("1");
      }
    }
    itemMultiplier.selectAll();
    itemMultiplier.requestFocusInWindow();
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
    mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    mainPanel.setMinimumSize(new Dimension(-1, -1));
    mainPanel.setPreferredSize(new Dimension(-1, -1));
    final JSplitPane splitPane1 = new JSplitPane();
    splitPane1.setContinuousLayout(false);
    splitPane1.setDividerLocation(400);
    splitPane1.setDividerSize(5);
    mainPanel.add(
        splitPane1,
        new GridConstraints(
            0,
            0,
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
    westPanel = new JPanel();
    westPanel.setLayout(new BorderLayout(5, 0));
    westPanel.setBackground(new Color(-1));
    westPanel.setFocusCycleRoot(false);
    westPanel.setFocusTraversalPolicyProvider(true);
    westPanel.setInheritsPopupMenu(false);
    westPanel.setMaximumSize(new Dimension(-1, -1));
    westPanel.setMinimumSize(new Dimension(-1, -1));
    westPanel.setPreferredSize(new Dimension(-1, -1));
    splitPane1.setLeftComponent(westPanel);
    westUpperPanel = new JPanel();
    westUpperPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 22));
    westUpperPanel.setDoubleBuffered(false);
    westUpperPanel.setMinimumSize(new Dimension(251, 55));
    westUpperPanel.setPreferredSize(new Dimension(251, 55));
    westPanel.add(westUpperPanel, BorderLayout.NORTH);
    final JLabel label1 = new JLabel();
    Font label1Font = this.$$$getFont$$$(null, -1, 22, label1.getFont());
    if (label1Font != null) label1.setFont(label1Font);
    label1.setText("Einkauf für");
    westUpperPanel.add(label1);
    customerName = new JLabel();
    Font customerNameFont = this.$$$getFont$$$(null, Font.ITALIC, 22, customerName.getFont());
    if (customerNameFont != null) customerName.setFont(customerNameFont);
    customerName.setText("Einkäufer");
    westUpperPanel.add(customerName);
    editUser = new JButton();
    editUser.setBorderPainted(false);
    editUser.setContentAreaFilled(false);
    editUser.setInheritsPopupMenu(true);
    editUser.setLabel("");
    editUser.setMinimumSize(new Dimension(40, 30));
    editUser.setPreferredSize(new Dimension(40, 30));
    editUser.setText("");
    westUpperPanel.add(editUser);
    ShoppingItemPanel = new JPanel();
    ShoppingItemPanel.setLayout(new GridBagLayout());
    ShoppingItemPanel.setAutoscrolls(false);
    ShoppingItemPanel.setDoubleBuffered(true);
    ShoppingItemPanel.setEnabled(true);
    ShoppingItemPanel.setForeground(new Color(-1));
    ShoppingItemPanel.setMaximumSize(new Dimension(-1, -1));
    ShoppingItemPanel.setMinimumSize(new Dimension(-1, -1));
    ShoppingItemPanel.setPreferredSize(new Dimension(-1, -1));
    ShoppingItemPanel.setVisible(true);
    ShoppingItemPanel.putClientProperty("html.disable", Boolean.FALSE);
    westPanel.add(ShoppingItemPanel, BorderLayout.CENTER);
    ShoppingItemPanel.setBorder(
        BorderFactory.createTitledBorder(
            null,
            "Einkaufs-Artikel",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.ABOVE_TOP,
            this.$$$getFont$$$(null, Font.BOLD | Font.ITALIC, -1, ShoppingItemPanel.getFont()),
            new Color(-16752083)));
    final JLabel label2 = new JLabel();
    Font label2Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label2.getFont());
    if (label2Font != null) label2.setFont(label2Font);
    label2.setText("Lief.-Artikelnr.:");
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(6, 10, 6, 0);
    ShoppingItemPanel.add(label2, gbc);
    suppliersItemNumber = new IntegerParseField();
    Font suppliersItemNumberFont =
        this.$$$getFont$$$(null, Font.PLAIN, 16, suppliersItemNumber.getFont());
    if (suppliersItemNumberFont != null) suppliersItemNumber.setFont(suppliersItemNumberFont);
    suppliersItemNumber.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 5, 0, 5);
    ShoppingItemPanel.add(suppliersItemNumber, gbc);
    final JLabel label3 = new JLabel();
    Font label3Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label3.getFont());
    if (label3Font != null) label3.setFont(label3Font);
    label3.setText("Artikel:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(6, 10, 6, 0);
    ShoppingItemPanel.add(label3, gbc);
    articleName = new JTextField();
    Font articleNameFont = this.$$$getFont$$$(null, Font.PLAIN, 16, articleName.getFont());
    if (articleNameFont != null) articleName.setFont(articleNameFont);
    articleName.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 4;
    gbc.gridwidth = 4;
    gbc.weightx = 0.3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 5, 0, 5);
    ShoppingItemPanel.add(articleName, gbc);
    final JLabel label4 = new JLabel();
    Font label4Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label4.getFont());
    if (label4Font != null) label4.setFont(label4Font);
    label4.setText("Verkaufspreis:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 8;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(6, 10, 6, 0);
    ShoppingItemPanel.add(label4, gbc);
    retailPrice = new DoubleParseField();
    Font retailPriceFont = this.$$$getFont$$$(null, Font.PLAIN, 16, retailPrice.getFont());
    if (retailPriceFont != null) retailPrice.setFont(retailPriceFont);
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 8;
    gbc.gridwidth = 2;
    gbc.weightx = 0.3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 5, 0, 5);
    ShoppingItemPanel.add(retailPrice, gbc);
    final JLabel label5 = new JLabel();
    Font label5Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label5.getFont());
    if (label5Font != null) label5.setFont(label5Font);
    label5.setText("Menge:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 9;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(6, 10, 6, 0);
    ShoppingItemPanel.add(label5, gbc);
    itemMultiplier = new DoubleParseField();
    Font itemMultiplierFont = this.$$$getFont$$$(null, Font.PLAIN, 16, itemMultiplier.getFont());
    if (itemMultiplierFont != null) itemMultiplier.setFont(itemMultiplierFont);
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 9;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 5, 0, 5);
    ShoppingItemPanel.add(itemMultiplier, gbc);
    containerSizeLabel = new JLabel();
    Font containerSizeLabelFont =
        this.$$$getFont$$$(null, Font.PLAIN, 16, containerSizeLabel.getFont());
    if (containerSizeLabelFont != null) containerSizeLabel.setFont(containerSizeLabelFont);
    containerSizeLabel.setText("Gebindegröße:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 10;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(6, 10, 6, 0);
    ShoppingItemPanel.add(containerSizeLabel, gbc);
    final JLabel label6 = new JLabel();
    Font label6Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label6.getFont());
    if (label6Font != null) label6.setFont(label6Font);
    label6.setText("Pfand:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 11;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(6, 10, 6, 0);
    ShoppingItemPanel.add(label6, gbc);
    deposit = new DoubleParseField();
    Font depositFont = this.$$$getFont$$$(null, Font.PLAIN, 16, deposit.getFont());
    if (depositFont != null) deposit.setFont(depositFont);
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 11;
    gbc.gridwidth = 2;
    gbc.weightx = 0.3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 5, 0, 5);
    ShoppingItemPanel.add(deposit, gbc);
    retailPriceUnit = new JLabel();
    retailPriceUnit.setEnabled(true);
    Font retailPriceUnitFont = this.$$$getFont$$$(null, Font.PLAIN, 16, retailPriceUnit.getFont());
    if (retailPriceUnitFont != null) retailPriceUnit.setFont(retailPriceUnitFont);
    retailPriceUnit.setText("€/kg");
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 8;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 0, 0, 5);
    ShoppingItemPanel.add(retailPriceUnit, gbc);
    itemMultiplierUnit = new JLabel();
    itemMultiplierUnit.setBackground(new Color(-1));
    Font itemMultiplierUnitFont =
        this.$$$getFont$$$(null, Font.PLAIN, 16, itemMultiplierUnit.getFont());
    if (itemMultiplierUnitFont != null) itemMultiplierUnit.setFont(itemMultiplierUnitFont);
    itemMultiplierUnit.setText("g");
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 9;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 0, 0, 5);
    ShoppingItemPanel.add(itemMultiplierUnit, gbc);
    containerUnit = new JLabel();
    containerUnit.setBackground(new Color(-1));
    Font containerUnitFont = this.$$$getFont$$$(null, Font.PLAIN, 16, containerUnit.getFont());
    if (containerUnitFont != null) containerUnit.setFont(containerUnitFont);
    containerUnit.setText("Stk");
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 10;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 0, 0, 5);
    ShoppingItemPanel.add(containerUnit, gbc);
    depositUnit = new JLabel();
    depositUnit.setBackground(new Color(-1));
    Font depositUnitFont = this.$$$getFont$$$(null, Font.PLAIN, 16, depositUnit.getFont());
    if (depositUnitFont != null) depositUnit.setFont(depositUnitFont);
    depositUnit.setText("€");
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 11;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 0, 0, 5);
    ShoppingItemPanel.add(depositUnit, gbc);
    addDeposit = new JButton();
    addDeposit.setBorderPainted(false);
    addDeposit.setContentAreaFilled(false);
    addDeposit.setInheritsPopupMenu(true);
    addDeposit.setLabel("");
    addDeposit.setText("");
    addDeposit.setToolTipText("Pfand verbuchen");
    addDeposit.setVerifyInputWhenFocusTarget(true);
    addDeposit.setVisible(true);
    gbc = new GridBagConstraints();
    gbc.gridx = 7;
    gbc.gridy = 11;
    gbc.gridwidth = 2;
    ShoppingItemPanel.add(addDeposit, gbc);
    addAmount = new JButton();
    addAmount.setBorderPainted(true);
    addAmount.setContentAreaFilled(false);
    addAmount.setEnabled(true);
    addAmount.setInheritsPopupMenu(true);
    addAmount.setLabel("");
    addAmount.setText("");
    addAmount.setToolTipText("Artikel einkaufen");
    addAmount.setVisible(true);
    gbc = new GridBagConstraints();
    gbc.gridx = 7;
    gbc.gridy = 9;
    gbc.gridwidth = 2;
    ShoppingItemPanel.add(addAmount, gbc);
    addPrice = new JButton();
    addPrice.setBorderPainted(true);
    addPrice.setContentAreaFilled(false);
    addPrice.setInheritsPopupMenu(true);
    addPrice.setLabel("");
    addPrice.setText("");
    addPrice.setToolTipText("Artikel einkaufen");
    gbc = new GridBagConstraints();
    gbc.gridx = 7;
    gbc.gridy = 8;
    gbc.gridwidth = 2;
    ShoppingItemPanel.add(addPrice, gbc);
    searchArticle = new JButton();
    searchArticle.setBorderPainted(true);
    searchArticle.setContentAreaFilled(false);
    searchArticle.setInheritsPopupMenu(true);
    searchArticle.setLabel("");
    searchArticle.setText("");
    searchArticle.setToolTipText("Artikel suchen");
    gbc = new GridBagConstraints();
    gbc.gridx = 7;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    ShoppingItemPanel.add(searchArticle, gbc);
    productTypePanel = new JPanel();
    productTypePanel.setLayout(new GridBagLayout());
    productTypePanel.setAutoscrolls(false);
    productTypePanel.setFocusCycleRoot(false);
    productTypePanel.setFocusTraversalPolicyProvider(true);
    productTypePanel.setFocusable(true);
    productTypePanel.setMaximumSize(new Dimension(2147483647, 185));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 9;
    gbc.weightx = 0.3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.insets = new Insets(5, 5, 15, 0);
    ShoppingItemPanel.add(productTypePanel, gbc);
    optProduce = new JRadioButton();
    optProduce.setFocusCycleRoot(true);
    optProduce.setFocusTraversalPolicyProvider(true);
    Font optProduceFont = this.$$$getFont$$$(null, Font.PLAIN, 16, optProduce.getFont());
    if (optProduceFont != null) optProduce.setFont(optProduceFont);
    optProduce.setInheritsPopupMenu(true);
    optProduce.setLabel("Obst & Gemüse");
    optProduce.setText("Obst & Gemüse");
    optProduce.setMnemonic('O');
    optProduce.setDisplayedMnemonicIndex(0);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    productTypePanel.add(optProduce, gbc);
    optBakedGoods = new JRadioButton();
    optBakedGoods.setFocusTraversalPolicyProvider(true);
    Font optBakedGoodsFont = this.$$$getFont$$$(null, Font.PLAIN, 16, optBakedGoods.getFont());
    if (optBakedGoodsFont != null) optBakedGoods.setFont(optBakedGoodsFont);
    optBakedGoods.setLabel("Backwaren");
    optBakedGoods.setText("Backwaren");
    optBakedGoods.setMnemonic('B');
    optBakedGoods.setDisplayedMnemonicIndex(0);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    productTypePanel.add(optBakedGoods, gbc);
    optArticleNo = new JRadioButton();
    optArticleNo.setFocusCycleRoot(true);
    Font optArticleNoFont = this.$$$getFont$$$(null, Font.PLAIN, 16, optArticleNo.getFont());
    if (optArticleNoFont != null) optArticleNo.setFont(optArticleNoFont);
    optArticleNo.setLabel("Artikelnr./Barcode");
    optArticleNo.setSelected(true);
    optArticleNo.setText("Artikelnr./Barcode");
    optArticleNo.setMnemonic('A');
    optArticleNo.setDisplayedMnemonicIndex(0);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    productTypePanel.add(optArticleNo, gbc);
    optDeposit = new JRadioButton();
    optDeposit.setFocusTraversalPolicyProvider(false);
    Font optDepositFont = this.$$$getFont$$$(null, Font.PLAIN, 16, optDeposit.getFont());
    if (optDepositFont != null) optDeposit.setFont(optDepositFont);
    optDeposit.setLabel("Pfand ausleihen");
    optDeposit.setText("Pfand ausleihen");
    optDeposit.setMnemonic('P');
    optDeposit.setDisplayedMnemonicIndex(0);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    productTypePanel.add(optDeposit, gbc);
    optDepositReturn = new JRadioButton();
    optDepositReturn.setFocusTraversalPolicyProvider(false);
    Font optDepositReturnFont =
        this.$$$getFont$$$(null, Font.PLAIN, 16, optDepositReturn.getFont());
    if (optDepositReturnFont != null) optDepositReturn.setFont(optDepositReturnFont);
    optDepositReturn.setLabel("Pfand zurück");
    optDepositReturn.setText("Pfand zurück");
    optDepositReturn.setMnemonic('Z');
    optDepositReturn.setDisplayedMnemonicIndex(6);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    productTypePanel.add(optDepositReturn, gbc);
    optCustomProduct = new JRadioButton();
    optCustomProduct.setFocusTraversalPolicyProvider(false);
    Font optCustomProductFont =
        this.$$$getFont$$$(null, Font.PLAIN, 16, optCustomProduct.getFont());
    if (optCustomProductFont != null) optCustomProduct.setFont(optCustomProductFont);
    optCustomProduct.setLabel("Selbstdefinierter Artikel");
    optCustomProduct.setText("Selbstdefinierter Artikel");
    optCustomProduct.setMnemonic('F');
    optCustomProduct.setDisplayedMnemonicIndex(8);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    productTypePanel.add(optCustomProduct, gbc);
    final JLabel label7 = new JLabel();
    Font label7Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label7.getFont());
    if (label7Font != null) label7.setFont(label7Font);
    label7.setText("KB-Artikelnr.:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(6, 10, 6, 0);
    ShoppingItemPanel.add(label7, gbc);
    kbNumber = new IntegerParseField();
    Font kbNumberFont = this.$$$getFont$$$(null, Font.PLAIN, 16, kbNumber.getFont());
    if (kbNumberFont != null) kbNumber.setFont(kbNumberFont);
    kbNumber.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 5, 0, 5);
    ShoppingItemPanel.add(kbNumber, gbc);
    reductionPanel = new JPanel();
    reductionPanel.setLayout(new GridBagLayout());
    reductionPanel.setEnabled(true);
    reductionPanel.setFocusCycleRoot(false);
    reductionPanel.setFocusTraversalPolicyProvider(false);
    reductionPanel.setFocusable(false);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 12;
    gbc.gridwidth = 9;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(15, 5, 0, 0);
    ShoppingItemPanel.add(reductionPanel, gbc);
    priceStandard = new JRadioButton();
    priceStandard.setFocusCycleRoot(false);
    priceStandard.setFocusable(true);
    Font priceStandardFont = this.$$$getFont$$$(null, Font.PLAIN, 16, priceStandard.getFont());
    if (priceStandardFont != null) priceStandard.setFont(priceStandardFont);
    priceStandard.setSelected(true);
    priceStandard.setText("Normalpreis");
    priceStandard.setMnemonic('N');
    priceStandard.setDisplayedMnemonicIndex(0);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 5;
    gbc.anchor = GridBagConstraints.WEST;
    reductionPanel.add(priceStandard, gbc);
    pricePreordered = new JRadioButton();
    Font pricePreorderedFont = this.$$$getFont$$$(null, Font.PLAIN, 16, pricePreordered.getFont());
    if (pricePreorderedFont != null) pricePreordered.setFont(pricePreorderedFont);
    pricePreordered.setSelected(false);
    pricePreordered.setText("Vorbestellung");
    pricePreordered.setMnemonic('V');
    pricePreordered.setDisplayedMnemonicIndex(0);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 5;
    gbc.anchor = GridBagConstraints.WEST;
    reductionPanel.add(pricePreordered, gbc);
    price50Percent = new JRadioButton();
    Font price50PercentFont = this.$$$getFont$$$(null, Font.PLAIN, 16, price50Percent.getFont());
    if (price50PercentFont != null) price50Percent.setFont(price50PercentFont);
    price50Percent.setText("50 %");
    price50Percent.setMnemonic('5');
    price50Percent.setDisplayedMnemonicIndex(0);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 5;
    gbc.anchor = GridBagConstraints.WEST;
    reductionPanel.add(price50Percent, gbc);
    priceVariablePercentage = new JRadioButton();
    Font priceVariablePercentageFont =
        this.$$$getFont$$$(null, Font.PLAIN, 16, priceVariablePercentage.getFont());
    if (priceVariablePercentageFont != null)
      priceVariablePercentage.setFont(priceVariablePercentageFont);
    priceVariablePercentage.setText("Rabatt:");
    priceVariablePercentage.setMnemonic('R');
    priceVariablePercentage.setDisplayedMnemonicIndex(0);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 5;
    gbc.anchor = GridBagConstraints.WEST;
    reductionPanel.add(priceVariablePercentage, gbc);
    final JLabel label8 = new JLabel();
    Font label8Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label8.getFont());
    if (label8Font != null) label8.setFont(label8Font);
    label8.setText("Rabatt");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 5;
    gbc.anchor = GridBagConstraints.WEST;
    reductionPanel.add(label8, gbc);
    rememberReductionSetting = new JCheckBox();
    rememberReductionSetting.setEnabled(true);
    Font rememberReductionSettingFont =
        this.$$$getFont$$$(null, Font.PLAIN, 16, rememberReductionSetting.getFont());
    if (rememberReductionSettingFont != null)
      rememberReductionSetting.setFont(rememberReductionSettingFont);
    rememberReductionSetting.setHideActionText(true);
    rememberReductionSetting.setHorizontalAlignment(4);
    rememberReductionSetting.setSelected(false);
    rememberReductionSetting.setText("Rabatt-Einstellung merken");
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(0, 0, 0, 5);
    reductionPanel.add(rememberReductionSetting, gbc);
    variablePercentage = new IntegerParseField();
    Font variablePercentageFont =
        this.$$$getFont$$$(null, Font.PLAIN, 16, variablePercentage.getFont());
    if (variablePercentageFont != null) variablePercentage.setFont(variablePercentageFont);
    variablePercentage.setPreferredSize(new Dimension(20, 32));
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 4;
    gbc.weightx = 0.1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 0, 0, 5);
    reductionPanel.add(variablePercentage, gbc);
    final JLabel label9 = new JLabel();
    Font label9Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label9.getFont());
    if (label9Font != null) label9.setFont(label9Font);
    label9.setText("%");
    gbc = new GridBagConstraints();
    gbc.gridx = 6;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 0, 0, 3);
    reductionPanel.add(label9, gbc);
    final JPanel spacer1 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 7;
    gbc.gridy = 4;
    gbc.weighty = 0.1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    reductionPanel.add(spacer1, gbc);
    netPrice = new DoubleParseField();
    Font netPriceFont = this.$$$getFont$$$(null, Font.PLAIN, 16, netPrice.getFont());
    if (netPriceFont != null) netPrice.setFont(netPriceFont);
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 7;
    gbc.gridwidth = 2;
    gbc.weightx = 0.3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 5, 0, 5);
    ShoppingItemPanel.add(netPrice, gbc);
    final JLabel label10 = new JLabel();
    Font label10Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label10.getFont());
    if (label10Font != null) label10.setFont(label10Font);
    label10.setText("Nettopreis:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 7;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(6, 10, 6, 0);
    ShoppingItemPanel.add(label10, gbc);
    netPriceUnit = new JLabel();
    netPriceUnit.setEnabled(true);
    Font netPriceUnitFont = this.$$$getFont$$$(null, Font.PLAIN, 16, netPriceUnit.getFont());
    if (netPriceUnitFont != null) netPriceUnit.setFont(netPriceUnitFont);
    netPriceUnit.setMaximumSize(new Dimension(100, 22));
    netPriceUnit.setMinimumSize(new Dimension(100, 22));
    netPriceUnit.setText("€/kg");
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 7;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 0, 0, 5);
    ShoppingItemPanel.add(netPriceUnit, gbc);
    addNetPrice = new JButton();
    addNetPrice.setBorderPainted(true);
    addNetPrice.setContentAreaFilled(false);
    addNetPrice.setInheritsPopupMenu(true);
    addNetPrice.setLabel("");
    addNetPrice.setText("");
    addNetPrice.setToolTipText("Artikel einkaufen");
    gbc = new GridBagConstraints();
    gbc.gridx = 7;
    gbc.gridy = 7;
    gbc.gridwidth = 2;
    ShoppingItemPanel.add(addNetPrice, gbc);
    containerSize = new DoubleParseField();
    Font containerSizeFont = this.$$$getFont$$$(null, Font.PLAIN, 16, containerSize.getFont());
    if (containerSizeFont != null) containerSize.setFont(containerSizeFont);
    containerSize.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 10;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 5, 0, 5);
    ShoppingItemPanel.add(containerSize, gbc);
    final JLabel label11 = new JLabel();
    Font label11Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label11.getFont());
    if (label11Font != null) label11.setFont(label11Font);
    label11.setText("MWSt.:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(3, 10, 3, 0);
    ShoppingItemPanel.add(label11, gbc);
    vat = new JComboBox();
    vat.setEditable(false);
    Font vatFont = this.$$$getFont$$$(null, -1, 14, vat.getFont());
    if (vatFont != null) vat.setFont(vatFont);
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 6;
    gbc.gridwidth = 4;
    gbc.weightx = 0.3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 5, 0, 5);
    ShoppingItemPanel.add(vat, gbc);
    supplier = new JComboBox();
    supplier.setEditable(false);
    Font supplierFont = this.$$$getFont$$$(null, -1, 14, supplier.getFont());
    if (supplierFont != null) supplier.setFont(supplierFont);
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 2;
    gbc.gridwidth = 4;
    gbc.weightx = 0.3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 5, 0, 5);
    ShoppingItemPanel.add(supplier, gbc);
    final JLabel label12 = new JLabel();
    Font label12Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label12.getFont());
    if (label12Font != null) label12.setFont(label12Font);
    label12.setText("Lieferant:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(6, 10, 6, 0);
    ShoppingItemPanel.add(label12, gbc);
    final JPanel spacer2 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 13;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.VERTICAL;
    ShoppingItemPanel.add(spacer2, gbc);
    final JLabel label13 = new JLabel();
    Font label13Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label13.getFont());
    if (label13Font != null) label13.setFont(label13Font);
    label13.setText("Hersteller:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(6, 10, 6, 0);
    ShoppingItemPanel.add(label13, gbc);
    producer = new JTextField();
    producer.setEditable(false);
    producer.setEnabled(false);
    Font producerFont = this.$$$getFont$$$(null, Font.PLAIN, 16, producer.getFont());
    if (producerFont != null) producer.setFont(producerFont);
    producer.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 5;
    gbc.gridwidth = 4;
    gbc.weightx = 0.3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 5, 0, 5);
    ShoppingItemPanel.add(producer, gbc);
    eastPanel = new JPanel();
    eastPanel.setLayout(new BorderLayout(0, 0));
    eastPanel.setMinimumSize(new Dimension(-1, -1));
    eastPanel.setPreferredSize(new Dimension(-1, -1));
    splitPane1.setRightComponent(eastPanel);
    eastUpperPanel = new JPanel();
    eastUpperPanel.setLayout(new GridBagLayout());
    eastUpperPanel.setMinimumSize(new Dimension(300, 55));
    eastUpperPanel.setPreferredSize(new Dimension(300, 55));
    eastPanel.add(eastUpperPanel, BorderLayout.NORTH);
    final JLabel label14 = new JLabel();
    Font label14Font = this.$$$getFont$$$(null, Font.BOLD, 16, label14.getFont());
    if (label14Font != null) label14.setFont(label14Font);
    label14.setText("Dienst-Info");
    gbc = new GridBagConstraints();
    gbc.gridx = 10;
    gbc.gridy = 0;
    gbc.gridheight = 4;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    eastUpperPanel.add(label14, gbc);
    salesPersonInfo = new JLabel();
    Font salesPersonInfoFont = this.$$$getFont$$$(null, Font.PLAIN, 16, salesPersonInfo.getFont());
    if (salesPersonInfoFont != null) salesPersonInfo.setFont(salesPersonInfoFont);
    salesPersonInfo.setMaximumSize(new Dimension(-1, 22));
    salesPersonInfo.setMinimumSize(new Dimension(50, 15));
    salesPersonInfo.setOpaque(false);
    salesPersonInfo.setPreferredSize(new Dimension(300, 22));
    salesPersonInfo.setText("(Selbsteingabe) / LD1");
    gbc = new GridBagConstraints();
    gbc.gridx = 10;
    gbc.gridy = 4;
    gbc.gridwidth = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 0, 5, 15);
    eastUpperPanel.add(salesPersonInfo, gbc);
    final JSeparator separator1 = new JSeparator();
    Font separator1Font = this.$$$getFont$$$(null, -1, -1, separator1.getFont());
    if (separator1Font != null) separator1.setFont(separator1Font);
    separator1.setForeground(new Color(-16752083));
    separator1.setMinimumSize(new Dimension(1, 2));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 9;
    gbc.gridheight = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 5, 0, 10);
    eastUpperPanel.add(separator1, gbc);
    final JSeparator separator2 = new JSeparator();
    Font separator2Font = this.$$$getFont$$$(null, -1, -1, separator2.getFont());
    if (separator2Font != null) separator2.setFont(separator2Font);
    separator2.setForeground(new Color(-16752083));
    separator2.setMinimumSize(new Dimension(1, 2));
    gbc = new GridBagConstraints();
    gbc.gridx = 11;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.gridheight = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 5, 0, 15);
    eastUpperPanel.add(separator2, gbc);
    final JLabel label15 = new JLabel();
    Font label15Font = this.$$$getFont$$$(null, Font.BOLD, 16, label15.getFont());
    if (label15Font != null) label15.setFont(label15Font);
    label15.setText("Kunden-Info");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 4;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 10, 0, 0);
    eastUpperPanel.add(label15, gbc);
    customerCredit = new JLabel();
    Font customerCreditFont = this.$$$getFont$$$(null, Font.PLAIN, 16, customerCredit.getFont());
    if (customerCreditFont != null) customerCredit.setFont(customerCreditFont);
    customerCredit.setMaximumSize(new Dimension(-1, 22));
    customerCredit.setMinimumSize(new Dimension(50, 15));
    customerCredit.setPreferredSize(new Dimension(-1, 22));
    customerCredit.setText("0,00€");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.gridwidth = 3;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 5, 5, 10);
    eastUpperPanel.add(customerCredit, gbc);
    final JLabel label16 = new JLabel();
    Font label16Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label16.getFont());
    if (label16Font != null) label16.setFont(label16Font);
    label16.setMaximumSize(new Dimension(-1, 22));
    label16.setMinimumSize(new Dimension(64, 15));
    label16.setPreferredSize(new Dimension(89, 22));
    label16.setText("Guthaben:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 10, 5, 0);
    eastUpperPanel.add(label16, gbc);
    final JLabel label17 = new JLabel();
    Font label17Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label17.getFont());
    if (label17Font != null) label17.setFont(label17Font);
    label17.setMaximumSize(new Dimension(-1, 22));
    label17.setMinimumSize(new Dimension(71, 15));
    label17.setPreferredSize(new Dimension(120, 22));
    label17.setText("Soli-Aufschlag:");
    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 10, 5, 0);
    eastUpperPanel.add(label17, gbc);
    solidarity = new JLabel();
    Font solidarityFont = this.$$$getFont$$$(null, Font.PLAIN, 16, solidarity.getFont());
    if (solidarityFont != null) solidarity.setFont(solidarityFont);
    solidarity.setMaximumSize(new Dimension(-1, 22));
    solidarity.setMinimumSize(new Dimension(50, 15));
    solidarity.setPreferredSize(new Dimension(-1, 22));
    solidarity.setText("0,00€");
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 4;
    gbc.gridwidth = 3;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 5, 5, 10);
    eastUpperPanel.add(solidarity, gbc);
    final JPanel spacer3 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 8;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    eastUpperPanel.add(spacer3, gbc);
    shoppingCartPanel = new JPanel();
    shoppingCartPanel.setLayout(new BorderLayout(0, 0));
    shoppingCartPanel.setAutoscrolls(true);
    shoppingCartPanel.setEnabled(true);
    shoppingCartPanel.setMaximumSize(new Dimension(-1, -1));
    shoppingCartPanel.setMinimumSize(new Dimension(-1, -1));
    shoppingCartPanel.setPreferredSize(new Dimension(-1, -1));
    eastPanel.add(shoppingCartPanel, BorderLayout.CENTER);
    shoppingCartPanel.setBorder(
        BorderFactory.createTitledBorder(
            null,
            "Einkauf",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.ABOVE_TOP,
            this.$$$getFont$$$(null, Font.BOLD | Font.ITALIC, -1, shoppingCartPanel.getFont()),
            new Color(-16752083)));
    shoppingListPanel = new JPanel();
    shoppingListPanel.setLayout(new BorderLayout(0, 0));
    shoppingListPanel.setBackground(new Color(-1));
    shoppingListPanel.setMaximumSize(new Dimension(-1, -1));
    shoppingListPanel.setMinimumSize(new Dimension(-1, -1));
    shoppingListPanel.setOpaque(false);
    shoppingListPanel.setPreferredSize(new Dimension(-1, -1));
    shoppingListPanel.setRequestFocusEnabled(true);
    shoppingCartPanel.add(shoppingListPanel, BorderLayout.CENTER);
    shoppingListPanel.add(shoppingCartView.$$$getRootComponent$$$(), BorderLayout.CENTER);
    shoppingActionPanel = new JPanel();
    shoppingActionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    shoppingActionPanel.setAutoscrolls(true);
    shoppingActionPanel.setMaximumSize(new Dimension(-1, 40));
    shoppingActionPanel.setMinimumSize(new Dimension(-1, 40));
    shoppingActionPanel.setPreferredSize(new Dimension(-1, 40));
    shoppingCartPanel.add(shoppingActionPanel, BorderLayout.SOUTH);
    editPost = new JButton();
    editPost.setText("Popup bearbeiten");
    shoppingActionPanel.add(editPost);
    cancelSalesSession = new JButton();
    cancelSalesSession.setText("Einkauf abbrechen");
    shoppingActionPanel.add(cancelSalesSession);
    emptyShoppingCart = new JButton();
    emptyShoppingCart.setText("Alle Artikel löschen");
    shoppingActionPanel.add(emptyShoppingCart);
    checkout = new JButton();
    checkout.setInheritsPopupMenu(true);
    checkout.setLabel("Zur Kasse ...");
    checkout.setText("Zur Kasse ...");
    shoppingActionPanel.add(checkout);
    optGrpArticleType = new ButtonGroup();
    optGrpArticleType.add(optProduce);
    optGrpArticleType.add(optDepositReturn);
    optGrpArticleType.add(optArticleNo);
    optGrpArticleType.add(optCustomProduct);
    optGrpArticleType.add(optBakedGoods);
    optGrpArticleType.add(optDeposit);
    ButtonGroup buttonGroup;
    buttonGroup = new ButtonGroup();
    buttonGroup.add(pricePreordered);
    buttonGroup.add(pricePreordered);
    buttonGroup.add(priceStandard);
    buttonGroup.add(price50Percent);
    buttonGroup.add(priceVariablePercentage);
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
    return mainPanel;
  }
}
