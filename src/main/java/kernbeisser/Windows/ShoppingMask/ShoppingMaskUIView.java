package kernbeisser.Windows.ShoppingMask;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Item;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.User;
import kernbeisser.Useful.Tools;
import kernbeisser.Useful.Translator;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import java.awt.*;

public class ShoppingMaskUIView extends JPanel implements View {
    private ObjectTable<ShoppingItem> shoppingCartTable;
    private ShoppingMaskUIController controller;
    private Window window;
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
    private ButtonGroup optGrpArticleType;
    private char currentArticleType;

    public ShoppingMaskUIView(Window window, ShoppingMaskUIController controller) {
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
        shoppingCartTable = new ObjectTable<>(
                Column.create("Name", ShoppingItem::getName),
                Column.create("Anzahl", ShoppingItem::getItemAmount),
                Column.create("Preis", e -> e.getRawPrice() / 100f + "\u20AC" + (e.getDiscount() == 0 ? "" : (" (" + e.getDiscount() + "% Rabatt)")))
        );
        shoppingCartPanel.add(new JScrollPane(shoppingCartTable));
        shoppingCartTable.repaintUI();
        articleTypeChange('a');
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
        JOptionPane.showMessageDialog(null, "-> Einkauf");
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

    public Window getWindow() {
        return window;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        MainPanel = new JPanel();
        MainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JSplitPane splitPane1 = new JSplitPane();
        MainPanel.add(splitPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        westPanel = new JPanel();
        westPanel.setLayout(new BorderLayout(5, 0));
        westPanel.setBackground(new Color(-1));
        splitPane1.setLeftComponent(westPanel);
        westUpperPanel = new JPanel();
        westUpperPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 22));
        westUpperPanel.setBackground(new Color(-1));
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
        ShoppingItemPanel = new JPanel();
        ShoppingItemPanel.setLayout(new GridBagLayout());
        ShoppingItemPanel.setAutoscrolls(true);
        ShoppingItemPanel.setBackground(new Color(-1));
        ShoppingItemPanel.setEnabled(true);
        ShoppingItemPanel.setForeground(new Color(-1));
        ShoppingItemPanel.setVisible(true);
        westPanel.add(ShoppingItemPanel, BorderLayout.CENTER);
        ShoppingItemPanel.setBorder(BorderFactory.createTitledBorder(null, "Einkaufs-Artikel", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.ABOVE_TOP, this.$$$getFont$$$(null, Font.BOLD | Font.ITALIC, -1, ShoppingItemPanel.getFont()), new Color(-16752083)));
        optProduce = new JRadioButton();
        optProduce.setBackground(new Color(-1));
        Font optProduceFont = this.$$$getFont$$$(null, Font.PLAIN, 16, optProduce.getFont());
        if (optProduceFont != null) optProduce.setFont(optProduceFont);
        optProduce.setText("Obst  Gemüse");
        optProduce.setMnemonic(' ');
        optProduce.setDisplayedMnemonicIndex(5);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        ShoppingItemPanel.add(optProduce, gbc);
        optBakedGoods = new JRadioButton();
        optBakedGoods.setBackground(new Color(-1));
        Font optBakedGoodsFont = this.$$$getFont$$$(null, Font.PLAIN, 16, optBakedGoods.getFont());
        if (optBakedGoodsFont != null) optBakedGoods.setFont(optBakedGoodsFont);
        optBakedGoods.setText("Backwaren");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        ShoppingItemPanel.add(optBakedGoods, gbc);
        optArticleNo = new JRadioButton();
        optArticleNo.setBackground(new Color(-1));
        Font optArticleNoFont = this.$$$getFont$$$(null, Font.PLAIN, 16, optArticleNo.getFont());
        if (optArticleNoFont != null) optArticleNo.setFont(optArticleNoFont);
        optArticleNo.setSelected(true);
        optArticleNo.setText("Artikelnr./Barcode");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        ShoppingItemPanel.add(optArticleNo, gbc);
        optCustomProduct = new JRadioButton();
        optCustomProduct.setBackground(new Color(-1));
        Font optCustomProductFont = this.$$$getFont$$$(null, Font.PLAIN, 16, optCustomProduct.getFont());
        if (optCustomProductFont != null) optCustomProduct.setFont(optCustomProductFont);
        optCustomProduct.setText("Freier Artikel");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        ShoppingItemPanel.add(optCustomProduct, gbc);
        optDeposit = new JRadioButton();
        optDeposit.setBackground(new Color(-1));
        Font optDepositFont = this.$$$getFont$$$(null, Font.PLAIN, 16, optDeposit.getFont());
        if (optDepositFont != null) optDeposit.setFont(optDepositFont);
        optDeposit.setText("Pfand ausleihen");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        ShoppingItemPanel.add(optDeposit, gbc);
        optDepositReturn = new JRadioButton();
        optDepositReturn.setBackground(new Color(-1));
        Font optDepositReturnFont = this.$$$getFont$$$(null, Font.PLAIN, 16, optDepositReturn.getFont());
        if (optDepositReturnFont != null) optDepositReturn.setFont(optDepositReturnFont);
        optDepositReturn.setText("Pfand zurück");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 0);
        ShoppingItemPanel.add(optDepositReturn, gbc);
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("KB-Artikelnr.:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(30, 10, 3, 0);
        ShoppingItemPanel.add(label2, gbc);
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setText("Lief.-Artikelnr.:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 10, 3, 0);
        ShoppingItemPanel.add(label3, gbc);
        kbNumber = new JTextField();
        kbNumber.setBackground(new Color(-1));
        Font kbNumberFont = this.$$$getFont$$$(null, Font.PLAIN, 16, kbNumber.getFont());
        if (kbNumberFont != null) kbNumber.setFont(kbNumberFont);
        kbNumber.setForeground(new Color(-1));
        kbNumber.setText("kbNumber");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(30, 0, 0, 5);
        ShoppingItemPanel.add(kbNumber, gbc);
        suppliersItemNumber = new JTextField();
        suppliersItemNumber.setBackground(new Color(-1));
        Font suppliersItemNumberFont = this.$$$getFont$$$(null, Font.PLAIN, 16, suppliersItemNumber.getFont());
        if (suppliersItemNumberFont != null) suppliersItemNumber.setFont(suppliersItemNumberFont);
        suppliersItemNumber.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 7;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 5);
        ShoppingItemPanel.add(suppliersItemNumber, gbc);
        final JLabel label4 = new JLabel();
        Font label4Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        label4.setText("Artikel:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 10, 3, 0);
        ShoppingItemPanel.add(label4, gbc);
        articleName = new JTextField();
        articleName.setBackground(new Color(-1));
        Font articleNameFont = this.$$$getFont$$$(null, Font.PLAIN, 16, articleName.getFont());
        if (articleNameFont != null) articleName.setFont(articleNameFont);
        articleName.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 8;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 5);
        ShoppingItemPanel.add(articleName, gbc);
        final JLabel label5 = new JLabel();
        Font label5Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label5.getFont());
        if (label5Font != null) label5.setFont(label5Font);
        label5.setText("Preis:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 10, 6, 0);
        ShoppingItemPanel.add(label5, gbc);
        price = new JTextField();
        price.setBackground(new Color(-1));
        Font priceFont = this.$$$getFont$$$(null, Font.PLAIN, 16, price.getFont());
        if (priceFont != null) price.setFont(priceFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 5);
        ShoppingItemPanel.add(price, gbc);
        final JLabel label6 = new JLabel();
        Font label6Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label6.getFont());
        if (label6Font != null) label6.setFont(label6Font);
        label6.setText("Menge:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 10, 6, 0);
        ShoppingItemPanel.add(label6, gbc);
        packAmount = new JTextField();
        packAmount.setBackground(new Color(-1));
        Font packAmountFont = this.$$$getFont$$$(null, Font.PLAIN, 16, packAmount.getFont());
        if (packAmountFont != null) packAmount.setFont(packAmountFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 5);
        ShoppingItemPanel.add(packAmount, gbc);
        amount = new JTextField();
        amount.setBackground(new Color(-1));
        Font amountFont = this.$$$getFont$$$(null, Font.PLAIN, 16, amount.getFont());
        if (amountFont != null) amount.setFont(amountFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 5);
        ShoppingItemPanel.add(amount, gbc);
        final JLabel label7 = new JLabel();
        Font label7Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label7.getFont());
        if (label7Font != null) label7.setFont(label7Font);
        label7.setText("Gebindegröße:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 10, 6, 0);
        ShoppingItemPanel.add(label7, gbc);
        final JLabel label8 = new JLabel();
        Font label8Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label8.getFont());
        if (label8Font != null) label8.setFont(label8Font);
        label8.setText("Pfand:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 10, 6, 0);
        ShoppingItemPanel.add(label8, gbc);
        deposit = new JTextField();
        deposit.setBackground(new Color(-1));
        Font depositFont = this.$$$getFont$$$(null, Font.PLAIN, 16, deposit.getFont());
        if (depositFont != null) deposit.setFont(depositFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 5);
        ShoppingItemPanel.add(deposit, gbc);
        final JLabel label9 = new JLabel();
        Font label9Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label9.getFont());
        if (label9Font != null) label9.setFont(label9Font);
        label9.setText("MWSt.:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 10, 3, 0);
        ShoppingItemPanel.add(label9, gbc);
        optTaxLow = new JRadioButton();
        optTaxLow.setBackground(new Color(-1));
        Font optTaxLowFont = this.$$$getFont$$$(null, Font.PLAIN, 16, optTaxLow.getFont());
        if (optTaxLowFont != null) optTaxLow.setFont(optTaxLowFont);
        optTaxLow.setText("7%");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        ShoppingItemPanel.add(optTaxLow, gbc);
        priceStandard = new JRadioButton();
        priceStandard.setBackground(new Color(-1));
        Font priceStandardFont = this.$$$getFont$$$(null, Font.PLAIN, 16, priceStandard.getFont());
        if (priceStandardFont != null) priceStandard.setFont(priceStandardFont);
        priceStandard.setSelected(true);
        priceStandard.setText("Normalpreis");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.gridwidth = 8;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        ShoppingItemPanel.add(priceStandard, gbc);
        pricePreordered = new JRadioButton();
        pricePreordered.setBackground(new Color(-1));
        Font pricePreorderedFont = this.$$$getFont$$$(null, Font.PLAIN, 16, pricePreordered.getFont());
        if (pricePreorderedFont != null) pricePreordered.setFont(pricePreorderedFont);
        pricePreordered.setSelected(false);
        pricePreordered.setText("Vorbestellung");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.gridwidth = 8;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        ShoppingItemPanel.add(pricePreordered, gbc);
        price50Percent = new JRadioButton();
        price50Percent.setBackground(new Color(-1));
        Font price50PercentFont = this.$$$getFont$$$(null, Font.PLAIN, 16, price50Percent.getFont());
        if (price50PercentFont != null) price50Percent.setFont(price50PercentFont);
        price50Percent.setText("50 %");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 17;
        gbc.gridwidth = 8;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        ShoppingItemPanel.add(price50Percent, gbc);
        priceVariablePercentage = new JRadioButton();
        priceVariablePercentage.setBackground(new Color(-1));
        Font priceVariablePercentageFont = this.$$$getFont$$$(null, Font.PLAIN, 16, priceVariablePercentage.getFont());
        if (priceVariablePercentageFont != null) priceVariablePercentage.setFont(priceVariablePercentageFont);
        priceVariablePercentage.setText("Rabatt:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 18;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        ShoppingItemPanel.add(priceVariablePercentage, gbc);
        priceUnit = new JLabel();
        priceUnit.setBackground(new Color(-1));
        Font priceUnitFont = this.$$$getFont$$$(null, Font.PLAIN, 16, priceUnit.getFont());
        if (priceUnitFont != null) priceUnit.setFont(priceUnitFont);
        priceUnit.setText("€/kg");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 5);
        ShoppingItemPanel.add(priceUnit, gbc);
        amountUnit = new JLabel();
        amountUnit.setBackground(new Color(-1));
        Font amountUnitFont = this.$$$getFont$$$(null, Font.PLAIN, 16, amountUnit.getFont());
        if (amountUnitFont != null) amountUnit.setFont(amountUnitFont);
        amountUnit.setText("g");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 5);
        ShoppingItemPanel.add(amountUnit, gbc);
        packUnit = new JLabel();
        packUnit.setBackground(new Color(-1));
        Font packUnitFont = this.$$$getFont$$$(null, Font.PLAIN, 14, packUnit.getFont());
        if (packUnitFont != null) packUnit.setFont(packUnitFont);
        packUnit.setText("Stk");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 5);
        ShoppingItemPanel.add(packUnit, gbc);
        depositUnit = new JLabel();
        depositUnit.setBackground(new Color(-1));
        Font depositUnitFont = this.$$$getFont$$$(null, Font.PLAIN, 14, depositUnit.getFont());
        if (depositUnitFont != null) depositUnit.setFont(depositUnitFont);
        depositUnit.setText("€");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 5);
        ShoppingItemPanel.add(depositUnit, gbc);
        final JLabel label10 = new JLabel();
        Font label10Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label10.getFont());
        if (label10Font != null) label10.setFont(label10Font);
        label10.setText("Rabatt");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(30, 10, 5, 0);
        ShoppingItemPanel.add(label10, gbc);
        rememberReductionSetting = new JCheckBox();
        rememberReductionSetting.setBackground(new Color(-1));
        Font rememberReductionSettingFont = this.$$$getFont$$$(null, Font.PLAIN, 16, rememberReductionSetting.getFont());
        if (rememberReductionSettingFont != null) rememberReductionSetting.setFont(rememberReductionSettingFont);
        rememberReductionSetting.setSelected(true);
        rememberReductionSetting.setText("Einstellung merken");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 14;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(30, 0, 5, 15);
        ShoppingItemPanel.add(rememberReductionSetting, gbc);
        addDeposit = new JButton();
        addDeposit.setBorderPainted(false);
        addDeposit.setContentAreaFilled(false);
        addDeposit.setIcon(new ImageIcon(getClass().getResource("/Images/Icons/cart-icon32.png")));
        addDeposit.setInheritsPopupMenu(true);
        addDeposit.setLabel("");
        addDeposit.setText("");
        addDeposit.setToolTipText("Pfand verbuchen");
        addDeposit.setVerifyInputWhenFocusTarget(true);
        addDeposit.setVisible(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 12;
        ShoppingItemPanel.add(addDeposit, gbc);
        addAmount = new JButton();
        addAmount.setBorderPainted(false);
        addAmount.setContentAreaFilled(false);
        addAmount.setEnabled(true);
        addAmount.setIcon(new ImageIcon(getClass().getResource("/Images/Icons/cart-icon32.png")));
        addAmount.setInheritsPopupMenu(true);
        addAmount.setLabel("");
        addAmount.setText("");
        addAmount.setToolTipText("Artikel einkaufen");
        addAmount.setVisible(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 10;
        ShoppingItemPanel.add(addAmount, gbc);
        addPrice = new JButton();
        addPrice.setBorderPainted(false);
        addPrice.setContentAreaFilled(false);
        addPrice.setIcon(new ImageIcon(getClass().getResource("/Images/Icons/cart-icon32.png")));
        addPrice.setInheritsPopupMenu(true);
        addPrice.setLabel("");
        addPrice.setText("");
        addPrice.setToolTipText("Artikel einkaufen");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 9;
        ShoppingItemPanel.add(addPrice, gbc);
        variablePercentage = new JTextField();
        Font variablePercentageFont = this.$$$getFont$$$(null, Font.PLAIN, 16, variablePercentage.getFont());
        if (variablePercentageFont != null) variablePercentage.setFont(variablePercentageFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 18;
        gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 5);
        ShoppingItemPanel.add(variablePercentage, gbc);
        final JLabel label11 = new JLabel();
        Font label11Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label11.getFont());
        if (label11Font != null) label11.setFont(label11Font);
        label11.setText("%");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 18;
        gbc.anchor = GridBagConstraints.WEST;
        ShoppingItemPanel.add(label11, gbc);
        final JSeparator separator1 = new JSeparator();
        Font separator1Font = this.$$$getFont$$$(null, -1, -1, separator1.getFont());
        if (separator1Font != null) separator1.setFont(separator1Font);
        separator1.setForeground(new Color(-16752083));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 14;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(30, 0, 5, 0);
        ShoppingItemPanel.add(separator1, gbc);
        optTaxStandard = new JRadioButton();
        optTaxStandard.setBackground(new Color(-1));
        Font optTaxStandardFont = this.$$$getFont$$$(null, Font.PLAIN, 16, optTaxStandard.getFont());
        if (optTaxStandardFont != null) optTaxStandard.setFont(optTaxStandardFont);
        optTaxStandard.setText("19%");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 13;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        ShoppingItemPanel.add(optTaxStandard, gbc);
        searchArticle = new JButton();
        searchArticle.setBorderPainted(false);
        searchArticle.setContentAreaFilled(false);
        searchArticle.setIcon(new ImageIcon(getClass().getResource("/Images/Icons/zoom-seach-icon32.png")));
        searchArticle.setInheritsPopupMenu(true);
        searchArticle.setLabel("");
        searchArticle.setText("");
        searchArticle.setToolTipText("Artikel suchen");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 8;
        ShoppingItemPanel.add(searchArticle, gbc);
        eastPanel = new JPanel();
        eastPanel.setLayout(new BorderLayout(0, 0));
        splitPane1.setRightComponent(eastPanel);
        eastUpperPanel = new JPanel();
        eastUpperPanel.setLayout(new GridBagLayout());
        eastUpperPanel.setBackground(new Color(-1));
        eastPanel.add(eastUpperPanel, BorderLayout.NORTH);
        final JLabel label12 = new JLabel();
        Font label12Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label12.getFont());
        if (label12Font != null) label12.setFont(label12Font);
        label12.setText("Login:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        eastUpperPanel.add(label12, gbc);
        customerLoginName = new JLabel();
        Font customerLoginNameFont = this.$$$getFont$$$(null, Font.PLAIN, 16, customerLoginName.getFont());
        if (customerLoginNameFont != null) customerLoginName.setFont(customerLoginNameFont);
        customerLoginName.setText("<<Login>>");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        eastUpperPanel.add(customerLoginName, gbc);
        final JLabel label13 = new JLabel();
        Font label13Font = this.$$$getFont$$$(null, Font.BOLD, 16, label13.getFont());
        if (label13Font != null) label13.setFont(label13Font);
        label13.setText("Dienst-Info");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        eastUpperPanel.add(label13, gbc);
        salesPerson1 = new JLabel();
        Font salesPerson1Font = this.$$$getFont$$$(null, Font.PLAIN, 16, salesPerson1.getFont());
        if (salesPerson1Font != null) salesPerson1.setFont(salesPerson1Font);
        salesPerson1.setText("(Selbsteingabe) / LD1");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 15);
        eastUpperPanel.add(salesPerson1, gbc);
        final JLabel label14 = new JLabel();
        Font label14Font = this.$$$getFont$$$(null, Font.PLAIN, 16, label14.getFont());
        if (label14Font != null) label14.setFont(label14Font);
        label14.setText("Guthaben:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 10, 0);
        eastUpperPanel.add(label14, gbc);
        customerCredit = new JLabel();
        Font customerCreditFont = this.$$$getFont$$$(null, Font.PLAIN, 16, customerCredit.getFont());
        if (customerCreditFont != null) customerCredit.setFont(customerCreditFont);
        customerCredit.setText("<<100000,00 €>>");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);
        eastUpperPanel.add(customerCredit, gbc);
        salesPerson2 = new JLabel();
        Font salesPerson2Font = this.$$$getFont$$$(null, Font.PLAIN, 16, salesPerson2.getFont());
        if (salesPerson2Font != null) salesPerson2.setFont(salesPerson2Font);
        salesPerson2.setText("optional: LD2");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 15);
        eastUpperPanel.add(salesPerson2, gbc);
        editUser = new JButton();
        editUser.setBorderPainted(false);
        editUser.setContentAreaFilled(false);
        editUser.setIcon(new ImageIcon(getClass().getResource("/Images/Icons/pencil-icon32.png")));
        editUser.setInheritsPopupMenu(true);
        editUser.setLabel("");
        editUser.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 10);
        eastUpperPanel.add(editUser, gbc);
        final JSeparator separator2 = new JSeparator();
        Font separator2Font = this.$$$getFont$$$(null, -1, -1, separator2.getFont());
        if (separator2Font != null) separator2.setFont(separator2Font);
        separator2.setForeground(new Color(-16752083));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        eastUpperPanel.add(separator2, gbc);
        final JSeparator separator3 = new JSeparator();
        Font separator3Font = this.$$$getFont$$$(null, -1, -1, separator3.getFont());
        if (separator3Font != null) separator3.setFont(separator3Font);
        separator3.setForeground(new Color(-16752083));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 15);
        eastUpperPanel.add(separator3, gbc);
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
        shoppingCartPanel = new JPanel();
        shoppingCartPanel.setLayout(new BorderLayout(0, 0));
        shoppingCartPanel.setAutoscrolls(true);
        shoppingCartPanel.setBackground(new Color(-1));
        shoppingCartPanel.setEnabled(true);
        eastPanel.add(shoppingCartPanel, BorderLayout.CENTER);
        shoppingCartPanel.setBorder(BorderFactory.createTitledBorder(null, "Einkauf", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.ABOVE_TOP, this.$$$getFont$$$(null, Font.BOLD | Font.ITALIC, -1, shoppingCartPanel.getFont()), new Color(-16752083)));
        shoppingListPanel = new JPanel();
        shoppingListPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        shoppingListPanel.setBackground(new Color(-1));
        shoppingCartPanel.add(shoppingListPanel, BorderLayout.CENTER);
        shoppingActionPanel = new JPanel();
        shoppingActionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        shoppingActionPanel.setAutoscrolls(true);
        shoppingActionPanel.setBackground(new Color(-1));
        shoppingCartPanel.add(shoppingActionPanel, BorderLayout.SOUTH);
        cancelSalesSession = new JButton();
        cancelSalesSession.setText("Einkauf abbrechen");
        shoppingActionPanel.add(cancelSalesSession);
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
        buttonGroup.add(optTaxStandard);
        buttonGroup.add(optTaxLow);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(pricePreordered);
        buttonGroup.add(pricePreordered);
        buttonGroup.add(priceStandard);
        buttonGroup.add(price50Percent);
        buttonGroup.add(priceVariablePercentage);
    }

    /**
     * @noinspection ALL
     */
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
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return MainPanel;
    }

}