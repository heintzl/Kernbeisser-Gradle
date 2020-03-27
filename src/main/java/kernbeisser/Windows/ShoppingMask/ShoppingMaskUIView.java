package kernbeisser.Windows.ShoppingMask;

import javax.swing.*;

import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartView;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import kernbeisser.Price.PriceCalculator;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

public class ShoppingMaskUIView extends Window implements View {
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
    private ButtonGroup optGrpArticleType;
    private char currentArticleType;

    public ShoppingMaskUIView(Window window, ShoppingMaskUIController controller, ShoppingCartController shoppingCartController) {
        super(window);
        this.cartController = shoppingCartController;
        this.controller = controller;
        add(MainPanel);
        checkout.addActionListener(e -> doCheckout());
        cancelSalesSession.addActionListener(e -> doCancel());
        searchArticle.addActionListener(e -> openSearchWindow());
        addPrice.addActionListener(e -> addToCart());
        addDeposit.addActionListener(e -> addToCart());
        addAmount.addActionListener(e -> addToCart());
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
        pack();
        setLocationRelativeTo(window);
        optTaxLow.setText(VAT.LOW.getName());
        optTaxStandard.setText(VAT.HIGH.getName());
        windowInitialized();
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
            amount.setVisible(type == 'a');
            setAmountUnit("");
            articleAmount.setVisible(type == 'a');
            articleAmount.setEnabled(type == 'c');
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
            return variablePercentage.getValue();
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
        return price.getValue();
    }

    public void setPrice(String value) {
        this.price.setText(value);
        this.kbNumber.setEnabled(!value.equals("--"));
    }

    int getKBArticleNumber() {
        return kbNumber.getValue();
    }

    int getSuppliersNumber() {
        return suppliersItemNumber.getValue();
    }

    void noArticleFound() {
        JOptionPane.showConfirmDialog(this,
                                      "Es konnte kein Artikel mit den angegeben Artikelnummer / Lieferantennummer gefunden werden");
    }

    public double getAmount() {
        return amount.getValue();
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
        articleUnit.setVisible(!article.isWeighAble());
        articleAmountLabel.setVisible(!article.isWeighAble());
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
        return articleAmount.getValue();
    }

    public void setArticleAmount(String value) {
        this.articleAmount.setText(value);
        this.kbNumber.setEnabled(!value.equals("--"));
    }

    public String getItemName() {
        return articleName.getName();
    }

    public int getDeposit() {
        return (int) (deposit.getValue() * 100);
    }

    public void setDeposit(String value) {
        this.deposit.setText(value);
    }

    VAT getSelectedVAT() {
        return optTaxLow.isSelected() ? VAT.LOW : VAT.HIGH;
    }
}