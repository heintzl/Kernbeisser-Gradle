package kernbeisser.Windows.ShoppingMask;

import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.CustomComponents.FocusTraversal.FocusTraversal;
import kernbeisser.CustomComponents.KeyCapture;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartView;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.Vector;

import static java.text.MessageFormat.format;

public class ShoppingMaskUIView implements View<ShoppingMaskUIController> {
    //TODO: create Enum
    static final int ARTICLE_NUMBER = 0;
    static final int BAKED_GOODS = 1;
    static final int CUSTOM_PRODUCT = 2;
    static final int DEPOSIT = 3;
    static final int RETURN_DEPOSIT = 4;
    static final int PRODUCE = 5;
    static final String stornoMessageTitle = "Storno";

    private ShoppingMaskUIController controller;
    private ShoppingCartController cartController;

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
    private kernbeisser.CustomComponents.TextFields.IntegerParseField articleAmount;
    private kernbeisser.CustomComponents.TextFields.DoubleParseField amount;
    private kernbeisser.CustomComponents.TextFields.DoubleParseField deposit;
    private JPanel westPanel;
    private JRadioButton optTaxLow;
    private JRadioButton optTaxStandard;
    private JPanel eastPanel;
    private JPanel eastUpperPanel;
    private JLabel customerCredit;
    private JLabel customerLoginName;
    private JRadioButton priceStandard;
    private JRadioButton pricePreordered;
    private JRadioButton price50Percent;
    private JRadioButton priceVariablePercentage;
    private JLabel priceUnit;
    private JLabel amountUnit;
    private JLabel articleUnit;
    private kernbeisser.CustomComponents.TextFields.IntegerParseField variablePercentage;
    private JCheckBox rememberReductionSetting;
    private JButton editUser;
    private JButton addPrice;
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
    private JLabel articleAmountLabel;
    private JPanel productTypePanel;
    private JPanel reductionPanel;
    private JButton emptyShoppingCart;
    private ButtonGroup optGrpArticleType;
    private ButtonGroup optGrpReduction;

    private char currentArticleType;
    private boolean isWeighable;
    static Vector<Component> traversalOrder = new Vector<Component>(1);
    static FocusTraversal traversalPolicy;
    private BarcodeCapture barcodeCapture;
    private KeyCapture keyCapture;

    public ShoppingMaskUIView(ShoppingMaskUIController controller, ShoppingCartController shoppingCartController) {
        this.cartController = shoppingCartController;
        this.controller = controller;
        articleTypeChange('a');
        traversalOrder.add(kbNumber);
        traversalOrder.add(articleName);
        traversalOrder.add(price);
        traversalOrder.add(amount);
        traversalOrder.add(suppliersItemNumber);
        traversalOrder.add(deposit);
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
    }

    private void doCancel() {
        controller.emptyShoppingCart();
        back();}

    private void doCheckout() {
        controller.startPay();
    }

    private void openSearchWindow() {
        controller.openSearchWindow();
    }

    public void addToCart() {
        if (controller.addToShoppingCart()) {articleTypeInitialize(currentArticleType);};
    }
    private void editUserAction() {controller.editUserAction();}

    private void createUIComponents() {
        shoppingCartView = cartController.getView();
    }

    void loadUserInfo(SaleSession saleSession) {
        customerName.setText(saleSession.getCustomer().getFirstName() + " " + saleSession.getCustomer().getSurname());
        customerLoginName.setText(saleSession.getCustomer().getUsername());
        customerCredit.setText(format("{0, number, 0.00}\u20AC", saleSession.getCustomer().getUserGroup().getValue()));
        salesPerson1.setText(saleSession.getSeller().getUsername());
        salesPerson2.setText(saleSession.getSecondSeller() != null ? saleSession.getSecondSeller().getUsername() : "");
    }

    private void articleTypeChange(char type) {
        if (currentArticleType != type) {
            articleTypeInitialize(type);
        }
    }

    private void articleTypeInitialize(char type) {
        currentArticleType = type;
        isWeighable = false;
        addAmount.setVisible(type == 'a');
        addPrice.setVisible("pbc".indexOf(type) != -1);
        addDeposit.setVisible("dr".indexOf(type) != -1);
        kbNumber.setVisible(type == 'a');
        setKbNumber("");
        suppliersItemNumber.setVisible(type == 'a');
        setSuppliersItemNumber("");
        price.setEnabled("dra".indexOf(type) == -1);
        price.setVisible("dr".indexOf(type) == -1);
        setPrice("");
        priceUnit.setVisible("pbac".indexOf(type) != -1);
        setPriceUnit("€");
        amount.setVisible("ac".indexOf(type) != -1);
        amount.setText("1");
        setAmountUnit("");
        articleAmount.setVisible(type == 'a');
        setArticleUnit("");
        articleUnit.setVisible(type == 'a');
        deposit.setEnabled("dr".indexOf(type) != -1);
        deposit.setVisible("adr".indexOf(type) != -1);
        depositUnit.setVisible("adr".indexOf(type) != -1);
        if ("dr".indexOf(type) != -1) {
            setOptTaxStandard();
        } else {
            setOptTaxLow();
        }
        optTaxLow.setEnabled(type == 'c');
        optTaxStandard.setEnabled(type == 'c');
        if(type== 'a') {
            priceStandard.setEnabled(true);
            price50Percent.setEnabled(true);
            priceVariablePercentage.setEnabled(true);
            pricePreordered.setEnabled(true);
        } else {
            priceStandard.setSelected(true);
            priceStandard.setEnabled(false);
            price50Percent.setEnabled(false);
            priceVariablePercentage.setEnabled(false);
            pricePreordered.setEnabled(false);
        }

        variablePercentage.setEnabled(priceVariablePercentage.isEnabled() && priceVariablePercentage.isSelected());
        if (type == 'p') {
            setArticleName("Obst & Gemüse");
            price.requestFocusInWindow();
        } else if (type == 'b') {
            setArticleName("Backwaren");
            price.requestFocusInWindow();
        } else if (type == 'd') {
            setArticleName("Pfand-Behälter");
            deposit.requestFocusInWindow();
        } else if (type == 'r') {
            setArticleName("Pfand zurück");
            deposit.requestFocusInWindow();
        } else if (type == 'a') {
            setArticleName("");
            kbNumber.requestFocusInWindow();
        } else if (type == 'c') {
            setArticleName("");
            articleName.requestFocusInWindow();
        }
        articleName.setEnabled(type == 'c');
    }

    void loadItemStats(Article article) {
        articleUnit.setText(MetricUnits.PIECE.getShortName());
        kbNumber.setText(article.getKbNumber() + "");
        suppliersItemNumber.setText(article.getSuppliersItemNumber() + "");
        articleName.setText(
                article.getName().length() > 40
                ? new StringBuilder(article.getName()).replace(36, article.getName().length(), "...").toString()
                : article.getName());
        articleAmount.setText(article.getAmount() + "");
        articleUnit.setText(article.getMetricUnits().getShortName());
        price.setText(String.format("%.2f", controller.getPrice(article)));
        priceUnit.setText(article.isWeighAble() ? "€/kg" : "€");
        amountUnit.setText(article.isWeighAble() ? "g" : "stk.");
        isWeighable = article.isWeighAble();
        articleAmount.setVisible(!article.isWeighAble());
        articleAmountLabel.setForeground(article.isWeighAble() ? Color.WHITE : Color.BLACK);
        articleUnit.setVisible(!article.isWeighAble());
        optTaxLow.setSelected(article.getVat().getValue() == 0.07);
        optTaxStandard.setSelected(article.getVat().getValue() != 0.07);
    }

    void defaultSettings() {
        price.setText("0.00");
        depositUnit.setText("€");
        priceUnit.setText("€");
        amount.setText("1");
        amount.setEnabled(true);
        articleName.setText("Kein Artikel gefunden!");
        amountUnit.setText("");
        articleUnit.setText("");
    }

    void noArticleFound() {
        java.awt.Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(mainPanel,
                                      "Es konnte kein Artikel mit den angegeben Artikelnummer / Lieferantennummer gefunden werden");
    }

    public void messageBarcodeNotFound(long barcode) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog( getContent(), "Konnte keinen Artikel mit Barcode \"" + barcode + "\" finden", "Artikel nicht gefunden", JOptionPane.INFORMATION_MESSAGE);
    }

    public void messageInvalidBarcode(String barcode) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(getContent(), "Ungültiger Barcode: " + barcode,"Barcode Fehler", JOptionPane.WARNING_MESSAGE);
    }

    public void messageDepositStorno() {
        java.awt.Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(getContent(), "Pfand kann nicht storniert werden!","Storno" , JOptionPane.WARNING_MESSAGE);
        deposit.setText("");
    }

    public String inputStornoRetailPrice(double itemRetailPrice, boolean retry) {
        String initValue = MessageFormat.format("{0, number, 0.00}", itemRetailPrice).trim();
        String message = "";
        String response = "";
        if (retry) { // item is piece, first try
            message = "Die Eingabe ist ungültig. Bitte hier einen gültigen Einzelpreis angeben, für den Fall, dass er sich seit dem ursprünglichen Einkauf geändert hat:";
        } else { //item is piece later try
            message = "Negative Menge: Soll der Artikel wirklich storniert werden? Dann kann hier der Einzelpreis angepasst werden, für den Fall, dass er sich seit dem ursprünglichen Einkauf geändert hat:";
        }
        java.awt.Toolkit.getDefaultToolkit().beep();
        response = (String) JOptionPane.showInputDialog(
                getContent(),
                message,
                stornoMessageTitle,
                JOptionPane.YES_NO_OPTION,
                null,
                null,
                initValue
        );
        if (response != null) {
            response = response.trim();
        }
        return response;
    }

    public int confirmStorno() {
        java.awt.Toolkit.getDefaultToolkit().beep();
        return JOptionPane.showConfirmDialog(
                getContent(),"Soll die Ware wirklich storniert werden?", stornoMessageTitle, JOptionPane.YES_NO_OPTION
        );
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

    public void setArticleName(String value) {
        this.articleName.setText(value);
    }

    public void setOptTaxLow() {
        this.optTaxLow.setSelected(true);
    }

    public void setOptTaxStandard() {
        this.optTaxStandard.setSelected(true);
    }

    public void setOptArticleNo() {
        this.optArticleNo.setSelected(true);
    }

    public void setPriceUnit(String value) {
        this.priceUnit.setText(value);
    }

    public void setAmountUnit(String value) {
        this.amountUnit.setText(value);
    }

    public void setArticleUnit(String value) {
        this.articleUnit.setText(value);
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
        if (!rememberReductionSetting.isSelected()) {
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

    double getPriceVATIncluded() {
        return price.getSafeValue();
    }

    public void setPrice(String value) {
        this.price.setText(value);
        this.kbNumber.setEnabled(!value.equals("--"));
    }

    int getKBArticleNumber() {
        return kbNumber.getSafeValue();
    }

    int getSuppliersNumber() {
        return suppliersItemNumber.getSafeValue();
    }

    VAT getSelectedVAT() {
        return optTaxLow.isSelected() ? VAT.LOW : VAT.HIGH;
    }

    Dimension getShoppingListSize() {return shoppingListPanel.getSize();}
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
        addPrice.setIcon(IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, 20, new Color(49, 114, 128)));
        addPrice.addActionListener(e -> addToCart());
        addDeposit.setIcon(IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, 20, new Color(49, 114, 128)));
        addDeposit.addActionListener(e -> addToCart());
        addAmount.setIcon(IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, 20, new Color(49, 114, 128)));
        addAmount.addActionListener(e -> addToCart());
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
        priceStandard.addItemListener(e -> variablePercentage.setEnabled(false));
        price50Percent.addItemListener(e -> variablePercentage.setEnabled(false));
        priceVariablePercentage.addItemListener(e -> {variablePercentage.setEnabled(true); variablePercentage.requestFocusInWindow();});
        kbNumber.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                controller.searchByKbNumber();
            }
        });
        kbNumber.addActionListener(e -> {if(isWeighable) {amount.setText("");}; amount.selectAll(); amount.requestFocusInWindow();});
        suppliersItemNumber.addActionListener(e -> addToCart());
        suppliersItemNumber.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                controller.searchBySupplierItemsNumber();
            }
        });
        articleTypeChange('a');
        optTaxLow.setText(VAT.LOW.getName());
        optTaxStandard.setText(VAT.HIGH.getName());
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
        return barcodeCapture.processKeyEvent(e)?true:keyCapture.processKeyEvent(e);
    }

}