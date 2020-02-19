package kernbeisser.Windows.ShoppingMask;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartView;
import kernbeisser.DBEntities.Item;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.User;
import kernbeisser.Useful.Tools;
import kernbeisser.Useful.Translator;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import java.awt.*;
import java.util.Collection;

public class ShoppingMaskUIView extends Window implements View {
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
    private JTextField kbNumber;
    private JTextField suppliersItemNumber;
    private JTextField articleName;
    private JTextField price;
    private JTextField packAmount;
    private JTextField amount;
    private JTextField deposit;
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
    private JLabel packUnit;
    private JTextField variablePercentage;
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
    private ButtonGroup optGrpArticleType;
    private char currentArticleType;

    public ShoppingMaskUIView(Window window, ShoppingMaskUIController controller,ShoppingCartController shoppingCartController) {
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
        articleTypeChange('a');
        pack();
        setLocationRelativeTo(window);
    }

    /*    void loadItemStats(Item i){
            if(i!=null){
                selectedItem.setText(i.getName());
                unit.setText(new Translator().translate(i.getUnit()));
                price.setText(i.calculatePrice()/100f+"\u20AC");
                itemAmountInfo.setText("Menge: "+i.getAmount());
                itemNameInfo.setText("Artikelname: "+i.getName());
                itemNumberInfo.setText("Artikelnummer: "+i.getKbNumber());
                itemPriceInfo.setText("Preis: "+i.calculatePrice()/100f+"\u20AC");
                editBarcodeField.setText(i.getBarcode()+"");
            }else {
                selectedItem.setText("Kein Ergebniss");
                price.setText("0.00\u20AC");
                unit.setText("");
                itemAmountInfo.setText("Menge: ");
                itemNameInfo.setText("Artikelname: ");
                itemNumberInfo.setText("Artikelnummer: ");
                itemPriceInfo.setText("Preis: ");
                editBarcodeField.setText("");
            }
        }
    */
    private void doCancel() {

    }

    private void doCheckout() {
    }

    private void openSearchWindow() {
        JOptionPane.showMessageDialog(null, "Hier erscheint dann mal das Suchfenster");
    }

    private void addToCart() {
        controller.addToShoppingCart();
    }

    public void setKbNumber(String value) {
        this.kbNumber.setText(value);
        this.kbNumber.setEnabled(value != "--");
    }

    public void setSuppliersItemNumber(String value) {
        this.suppliersItemNumber.setText(value);
        this.kbNumber.setEnabled(value != "--");
    }

    public void setArticleName(String value) {
        this.articleName.setText(value);
    }

    public void setAmount(String value) {
        this.amount.setText(value);
        this.kbNumber.setEnabled(value != "--");
    }

    public void setPrice(String value) {
        this.price.setText(value);
        this.kbNumber.setEnabled(value != "--");
    }

    public void setPackAmount(String value) {
        this.packAmount.setText(value);
        this.kbNumber.setEnabled(value != "--");
    }

    public void setDeposit(String value) {
        this.deposit.setText(value);
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

    public void setPackUnit(String value) {
        this.packUnit.setText(value);
    }

    public Controller getController() {
        return controller;
    }

    void loadUserInfo(User user) {
        customerName.setText(user.getFirstName() + " " + user.getSurname());
        customerLoginName.setText(user.getUsername());
        customerCredit.setText(user.getUserGroup().getValue() / 100f + "\u20AC");
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
            priceUnit.setVisible("pbc".indexOf(type) != -1);
            setPriceUnit("€");
            amount.setVisible(type == 'a');
            setAmountUnit("");
            packAmount.setVisible(type == 'a');
            setPackUnit("");
            packUnit.setVisible(type == 'a');
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

    public int getDiscount() {
        int d = 0;
        // TODO Add discount logic
        return d;
    }

    private void createUIComponents() {
        shoppingCartView = cartController.getView();
    }
}