package kernbeisser.Windows.ShoppingMask;

import javax.swing.*;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartView;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import kernbeisser.Price.PriceCalculator;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.View;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ShoppingMaskUIView extends JPanel implements View {
    //TODO: create Enum
    static final int ARTICLE_NUMBER = 0;
    static final int BAKED_GOODS = 1;
    static final int CUSTOM_PRODUCT = 2;
    static final int DEPOSIT = 3;
    static final int RETURN_DEPOSIT = 4;
    static final int PRODUCE = 5;

    private ShoppingMaskUIController controller;
    private ShoppingCartController cartController;

    private JLabel customerName;
    private JPanel MainPanel;
    private JPanel westUpperPanel;
    private JPanel shoppingItemPanel;
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
    private ButtonGroup optGrpArticleType;
    private char currentArticleType;

    public ShoppingMaskUIView(ShoppingMaskUIController controller, ShoppingCartController shoppingCartController) {
        this.cartController = shoppingCartController;
        this.controller = controller;
        add(MainPanel);
        checkout.addActionListener(e -> doCheckout());
        cancelSalesSession.addActionListener(e -> doCancel());
        searchArticle.setIcon(IconFontSwing.buildIcon(FontAwesome.SEARCH, 20, new Color(49, 114, 128)));
        searchArticle.addActionListener(e -> openSearchWindow());
        addPrice.setIcon(IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, 20, new Color(49, 114, 128)));
        addPrice.addActionListener(e -> addToCart());
        addDeposit.setIcon(IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, 20, new Color(49, 114, 128)));
        addDeposit.addActionListener(e -> addToCart());
        addAmount.setIcon(IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, 20, new Color(49, 114, 128)));
        addAmount.addActionListener(e -> addToCart());
        editUser.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 20, new Color(49, 114, 128)));
        editUser.addActionListener(e -> editUserAction());
        optProduce.addItemListener(e -> articleTypeChange('p'));
        optBakedGoods.addItemListener(e -> articleTypeChange('b'));
        optArticleNo.addItemListener(e -> articleTypeChange('a'));
        optCustomProduct.addItemListener(e -> articleTypeChange('c'));
        optDeposit.addItemListener(e -> articleTypeChange('d'));
        optDepositReturn.addItemListener(e -> articleTypeChange('r'));
        kbNumber.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                controller.searchByKbNumber();
            }
        });
        kbNumber.addActionListener(e -> controller.addToShoppingCart());
        suppliersItemNumber.addActionListener(e -> controller.addToShoppingCart());
        suppliersItemNumber.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                controller.searchBySupplierItemsNumber();
            }
        });
        checkout.addActionListener(e -> controller.startPay());
        articleTypeChange('a');
        optTaxLow.setText(VAT.LOW.getName());
        optTaxStandard.setText(VAT.HIGH.getName());
    }

    private void doCancel() {

    }

    private void doCheckout() {
    }

    private void openSearchWindow() {
        controller.openSearchWindow();
    }

    private void addToCart() {
        controller.addToShoppingCart();
    }
    private void editUserAction() {controller.editUserAction();}

    public void setKbNumber(String value) {
        this.kbNumber.setText(value);
        this.kbNumber.setEnabled(!value.equals("--"));
    }

    public void setSuppliersItemNumber(String value) {
        this.suppliersItemNumber.setText(value);
        this.kbNumber.setEnabled(!value.equals("--"));
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

    public void setPriceUnit(String value) {
        this.priceUnit.setText(value);
    }

    public void setAmountUnit(String value) {
        this.amountUnit.setText(value);
    }

    public void setArticleUnit(String value) {
        this.articleUnit.setText(value);
    }

    public Controller getController() {
        return controller;
    }

    void loadUserInfo(SaleSession saleSession) {
        customerName.setText(saleSession.getCustomer().getFirstName() + " " + saleSession.getCustomer().getSurname());
        customerLoginName.setText(saleSession.getCustomer().getUsername());
        customerCredit.setText(saleSession.getCustomer().getUserGroup().getValue() + "\u20AC");
        salesPerson1.setText(saleSession.getSeller().getUsername());
        salesPerson2.setText("saleSession.getSeller().getUsername()");
    }

    private void articleTypeChange(char type) {
        if (currentArticleType != type) {
            currentArticleType = type;
            addAmount.setVisible(type == 'a');
            addPrice.setVisible("pbc".indexOf(type) != -1);
            addDeposit.setVisible("dr".indexOf(type) != -1);
            kbNumber.setVisible(type == 'a');
            setKbNumber("");
            suppliersItemNumber.setVisible(type == 'a');
            setSuppliersItemNumber("");
            price.setVisible("pbac".indexOf(type) != -1);
            price.setEnabled("pbc".indexOf(type) != -1);
            setPrice("");
            priceUnit.setVisible("pbac".indexOf(type) != -1);
            setPriceUnit("€");
            amount.setVisible("ac".indexOf(type) != -1);
            amount.setText("1");
            setAmountUnit("");
            articleAmount.setVisible(type == 'a');
            setArticleUnit("");
            articleUnit.setVisible(type == 'a');
            deposit.setEnabled("cdr".indexOf(type) != -1);
            deposit.setVisible("acdr".indexOf(type) != -1);
            depositUnit.setVisible("acdr".indexOf(type) != -1);
            setOptTaxLow();
            optTaxLow.setEnabled(type == 'c');
            optTaxStandard.setEnabled(type == 'c');
            if (type == 'p') {
                setArticleName("Obst & Gemüse");
                price.requestFocusInWindow();
                shoppingItemPanel.getRootPane().setDefaultButton(addPrice);
            } else if (type == 'b') {
                setArticleName("Backwaren");
                price.requestFocusInWindow();
                shoppingItemPanel.getRootPane().setDefaultButton(addPrice);
            } else if (type == 'd') {
                setArticleName("Pfand-Behälter");
                deposit.requestFocusInWindow();
                shoppingItemPanel.getRootPane().setDefaultButton(addDeposit);
            } else if (type == 'r') {
                setArticleName("Pfand zurück");
                deposit.requestFocusInWindow();
                shoppingItemPanel.getRootPane().setDefaultButton(addDeposit);
            } else if (type == 'a') {
                setArticleName("");
                kbNumber.requestFocusInWindow();
                shoppingItemPanel.getRootPane().setDefaultButton(addAmount);
            } else if (type == 'c') {
                setArticleName("");
                articleName.requestFocusInWindow();
                shoppingItemPanel.getRootPane().setDefaultButton(addPrice);
            }
            articleName.setEnabled(type == 'c');
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
            return PriceCalculator.CONTAINER_DISCOUNT;
        }
        return 0;
    }

    public void setDiscount() {
        if (!rememberReductionSetting.isSelected()) {
            priceStandard.setSelected(true);
        }
    }

    private void createUIComponents() {
        shoppingCartView = cartController.getView();
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

    void noArticleFound() {
        JOptionPane.showConfirmDialog(this,
                                      "Es konnte kein Artikel mit den angegeben Artikelnummer / Lieferantennummer gefunden werden");
    }

    public double getAmount() {
        return amount.getSafeValue();
    }

    public void setAmount(String value) {
        this.amount.setText(value);
        this.kbNumber.setEnabled(!value.equals("--"));
    }

    void loadItemStats(Article article) {
        articleUnit.setText(MetricUnits.STACK.getShortName());
        kbNumber.setText(article.getKbNumber() + "");
        suppliersItemNumber.setText(article.getSuppliersItemNumber() + "");
        articleName.setText(
                article.getName().length() > 16
                ? new StringBuilder(article.getName()).replace(16, article.getName().length(), "...").toString()
                : article.getName());
        articleAmount.setText(article.getMetricUnits().fromUnit(article.getAmount()) + "");
        articleUnit.setText(article.getMetricUnits().getShortName());
        price.setText(String.format("%.2f", controller.getPrice(article)));
        priceUnit.setText(article.isWeighAble() ? "€/kg" : "€");
        amountUnit.setText(article.isWeighAble() ? "g" : "stk.");
        articleAmount.setVisible(!article.isWeighAble());
        articleAmountLabel.setForeground(article.isWeighAble() ? Color.WHITE : Color.BLACK);
        articleUnit.setVisible(!article.isWeighAble());
        optTaxLow.setSelected(article.getVAT().getValue()==0.07);
        optTaxStandard.setSelected(article.getVAT().getValue()!=0.07);
    }

    void defaultSettings() {
        price.setText("0.00");
        depositUnit.setText("€");
        priceUnit.setText("€");
        amount.setText("");
        amount.setEnabled(true);
        articleName.setText("Kein Artikel gefunden!");
        amountUnit.setText("");
        articleUnit.setText("");
    }

    int getArticleAmount() {
        return articleAmount.getSafeValue();
    }

    public void setArticleAmount(String value) {
        this.articleAmount.setText(value);
        this.kbNumber.setEnabled(!value.equals("--"));
    }

    public String getItemName() {
        return articleName.getText();
    }

    public int getDeposit() {
        return (int) (deposit.getSafeValue() * 100);
    }

    public void setDeposit(String value) {
        this.deposit.setText(value);
    }

    VAT getSelectedVAT() {
        return optTaxLow.isSelected() ? VAT.LOW : VAT.HIGH;
    }
}