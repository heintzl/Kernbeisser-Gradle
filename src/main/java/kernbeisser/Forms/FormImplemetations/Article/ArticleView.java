package kernbeisser.Forms.FormImplemetations.Article;

import java.awt.*;
import java.util.Collection;
import javax.swing.*;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBoxRenderer;
import kernbeisser.CustomComponents.PermissionButton;
import kernbeisser.CustomComponents.Verifier.DoubleVerifier;
import kernbeisser.CustomComponents.Verifier.IntegerVerifier;
import kernbeisser.CustomComponents.Verifier.KBNumberVerifier;
import kernbeisser.CustomComponents.Verifier.NotNullVerifier;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Enums.VAT;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckBox;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingComboBox;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingField;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class ArticleView implements IView<ArticleController> {

    private AccessCheckingField<Article, String> itemName;
    private AccessCheckingComboBox<Article, Supplier> supplier;
    private AccessCheckingField<Article, Double> netPrice;
    private AccessCheckingField<Article, Double> deposit;
    private AccessCheckingField<Article, Integer> kbItemNumber;
    private AccessCheckingField<Article, Integer> supplierItemNumber;
    private AccessCheckingField<Article, Double> crateDeposit;
    private PermissionButton searchPriceList;
    private AccessCheckingComboBox<Article, PriceList> priceList;
    private AccessCheckingField<Article, Integer> amount;
    private AccessCheckingField<Article, Double> containerSize;
    private AccessCheckingComboBox<Article, MetricUnits> metricUnits;
    private AccessCheckingField<Article, Long> barcode;
    private AccessCheckBox<Article> showInShoppingMask;
    private AccessCheckBox<Article> weighable;
    private AccessCheckingField<Article, String> extraInfo;
    private AccessCheckingComboBox<Article, VAT> vat;
    private JPanel main;
    private AccessCheckingComboBox<Article, SurchargeGroup> surchargeGroup;
    private AccessCheckingComboBox<Article, ShopRange> shopRange;
    private PermissionButton searchSurchargeGroup;
    private AccessCheckingField<Article, Double> catalogPriceFactor;
    private AccessCheckingField<Article, Integer> labelCount;
    private AccessCheckBox<Article> labelPerUnit;
    private AccessCheckingField<Article, String> producer;

    private ObjectForm<Article> articleObjectForm;

    @Linked
    private ArticleController controller;

    private void createUIComponents() {
        itemName =
                new AccessCheckingField<>(Article::getName, Article::setName, AccessCheckingField.NONE);
        amount =
                new AccessCheckingField<>(
                        Article::getAmount, Article::setAmount, AccessCheckingField.UNSIGNED_INT_FORMER);
        netPrice =
                new AccessCheckingField<>(
                        Article::getNetPrice,
                        Article::setNetPrice,
                        AccessCheckingField.UNSIGNED_CURRENCY_FORMER);
        deposit =
                new AccessCheckingField<>(
                        Article::getSingleDeposit,
                        Article::setSingleDeposit,
                        AccessCheckingField.UNSIGNED_CURRENCY_FORMER);
        kbItemNumber =
                new AccessCheckingField<>(
                        Article::getKbNumber, Article::setKbNumber, AccessCheckingField.UNSIGNED_INT_FORMER);
        supplierItemNumber =
                new AccessCheckingField<>(
                        Article::getSuppliersItemNumber,
                        Article::setSuppliersItemNumber,
                        AccessCheckingField.UNSIGNED_INT_FORMER);
        crateDeposit =
                new AccessCheckingField<>(
                        Article::getContainerDeposit,
                        Article::setContainerDeposit,
                        AccessCheckingField.UNSIGNED_CURRENCY_FORMER);
        containerSize =
                new AccessCheckingField<>(
                        Article::getContainerSize,
                        Article::setContainerSize,
                        AccessCheckingField.UNSIGNED_DOUBLE_FORMER);
        supplier =
                new AccessCheckingComboBox<>(
                        Article::getSupplier, Article::setSupplier, controller::getSuppliers);
        priceList =
                new AccessCheckingComboBox<>(
                        Article::getPriceList, Article::setPriceList, controller::getPriceLists);
        metricUnits =
                new AccessCheckingComboBox<>(
                        Article::getMetricUnits, Article::setMetricUnits, controller::getMetricUnits);
        barcode =
                new AccessCheckingField<>(Article::getBarcode, Article::setBarcode, controller::parseLong);
        showInShoppingMask = new AccessCheckBox<>(Article::isShowInShop, Article::setShowInShop);
        weighable = new AccessCheckBox<>(Article::isWeighable, Article::setWeighable);
        vat = new AccessCheckingComboBox<>(Article::getVat, Article::setVat, controller::getVats);
        surchargeGroup =
                new AccessCheckingComboBox<>(
                        Article::getSurchargeGroup,
                        Article::setSurchargeGroup,
                        () ->
                                controller.getAllForSuppler(
                                        supplier.getSelected().orElse(Supplier.getKKSupplier())));
        shopRange =
                new AccessCheckingComboBox<Article, ShopRange>(
                        Article::getShopRange, Article::setShopRange, controller::getAllShopRages);
        extraInfo =
                new AccessCheckingField<>(Article::getInfo, Article::setInfo, AccessCheckingField.NONE);
        catalogPriceFactor =
                new AccessCheckingField<>(
                        Article::getCatalogPriceFactor,
                        Article::setCatalogPriceFactor,
                        AccessCheckingField.UNSIGNED_DOUBLE_FORMER);
        labelCount =
                new AccessCheckingField<>(
                        Article::getLabelCount,
                        Article::setLabelCount,
                        AccessCheckingField.UNSIGNED_INT_FORMER);
        labelPerUnit = new AccessCheckBox<>(Article::isLabelPerUnit, Article::setLabelPerUnit);
        producer =
                new AccessCheckingField<>(
                        Article::getProducer, Article::setProducer, AccessCheckingField.NONE);
    }

    void setUnits(MetricUnits[] metricUnits) {
        this.metricUnits.setItems(metricUnits);
    }

    void setVATs(VAT[] vaTs) {
        vat.removeAllItems();
        for (VAT vaT : vaTs) {
            vat.addItem(vaT);
        }
    }

    void setSuppliers(Collection<Supplier> suppliers) {
        supplier.setItems(suppliers);
    }

    void setShopRanges(Collection<ShopRange> shopRanges) {
        shopRange.setItems(shopRanges);
    }

    void setSurchargeGroup(Collection<SurchargeGroup> surchargeGroups) {
        surchargeGroup.setItems(surchargeGroups);
    }

    void setPriceLists(Collection<PriceList> priceLists) {
        priceList.setItems(priceLists);
    }

    public ObjectForm<Article> getArticleObjectForm() {
        return articleObjectForm;
    }

    boolean kbNumberAlreadyExists() {
        return 0
                == JOptionPane.showConfirmDialog(
                getTopComponent(),
                "Die Artikelnummer ist bereits vergeben. Soll die nächste freie gewählt werden?");
    }

    void barcodeAlreadyExists() {
        JOptionPane.showMessageDialog(getTopComponent(), "Der Barcode ist bereits vergeben.");
    }

    @Override
    public void initialize(ArticleController controller) {
        itemName.setInputVerifier(new NotNullVerifier());
        amount.setInputVerifier(DoubleVerifier.from(0, Integer.MAX_VALUE));
        netPrice.setInputVerifier(DoubleVerifier.from(0., 999999));
        deposit.setInputVerifier(DoubleVerifier.from(0, 0.1, 5, 300));
        kbItemNumber.setInputVerifier(new KBNumberVerifier());
        supplierItemNumber.setInputVerifier(IntegerVerifier.from(0, 999999));
        crateDeposit.setInputVerifier(DoubleVerifier.from(0., 0.99, 5, 20));
        containerSize.setInputVerifier(DoubleVerifier.from(0, 0.1, 40, 1000));
        articleObjectForm =
                new ObjectForm<>(
                        itemName,
                        supplier,
                        netPrice,
                        deposit,
                        kbItemNumber,
                        supplierItemNumber,
                        crateDeposit,
                        priceList,
                        containerSize,
                        metricUnits,
                        barcode,
                        showInShoppingMask,
                        weighable,
                        surchargeGroup,
                        shopRange,
                        vat,
                        amount,
                        extraInfo,
                        catalogPriceFactor,
                        labelCount,
                        labelPerUnit,
                        producer);
        articleObjectForm.setObjectDistinction("Der Artikel");
        articleObjectForm.registerUniqueCheck(
                barcode, controller::barcodeExists, this::barcodeAlreadyExists);
        articleObjectForm.registerObjectValidator(controller::validateKbNumber);
        articleObjectForm.registerUniqueCheck(
                itemName, controller::nameExists, this::nameAlreadyExists);
        articleObjectForm.registerObjectValidator(controller::checkSuppliersItemNumber);
        articleObjectForm.registerObjectValidator(controller::validateArticle);
        supplier.addActionListener(
                e -> {
                    try {
                        controller.loadSurchargeGroupsFor(
                                supplier.getSelected().orElse(Supplier.getKKSupplier()));
                    } catch (NullPointerException nullPointerException) {
                        controller.loadSurchargeGroupsFor(Supplier.getKKSupplier());
                    }
                });
        surchargeGroup.setRenderer(
                new AdvancedComboBoxRenderer<SurchargeGroup>(
                        e -> String.format("%s[%.2f%%]", e.getName(), e.getSurcharge())));
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

    public void setKbNumber(int nextUnusedArticleNumber) {
        kbItemNumber.setText(nextUnusedArticleNumber + "");
    }

    public void nameAlreadyExists() {
        JOptionPane.showMessageDialog(
                getTopComponent(),
                "Der gewählte Name ist bereits vergeben!\n" + "Bitte wähle einen anderen.");
    }

    public void invalidInput() {
        JOptionPane.showMessageDialog(getTopComponent(), "Bitte fülle alle Werte korrekt aus");
    }

    @Override
    public @NotNull Dimension getSize() {
        return new Dimension(1200, 1000);
    }

    public MetricUnits getMetricUnits() {
        return (MetricUnits) metricUnits.getSelectedItem();
    }

    public void setBarcode(String s) {
        if (Tools.canInvoke(this::checkArticleBarcodeWritePermission)
                && JOptionPane.showConfirmDialog(
                getTopComponent(), "Soll der Barcode auf " + s + " gesetzt werden?")
                == 0) {
            barcode.setText(s);
        }
    }

    @Key(PermissionKey.ARTICLE_BARCODE_WRITE)
    private void checkArticleBarcodeWritePermission() {
    }

    public boolean isSameArticle(Article nearest) {
        return JOptionPane.showConfirmDialog(
                getTopComponent(),
                "Es wurde ein anderer Artikel gefunden, der einen sehr ähnlichen Namen hat:\n"
                        + nearest.toString()
                        + "\nSoll der Artikel trotzdem erstellt werden?")
                == 0;
    }

    public void messageSuppliersItemNumberAlreadyTaken() {
        supplierItemNumber.setInvalidInput();
        JOptionPane.showMessageDialog(
                getTopComponent(),
                "Die gewählte Lieferantennummer ist bereits für diesen Lieferant vergeben!");
    }

    public void messageUnitRequired() {
        JOptionPane.showMessageDialog(getContent(), "Bitte setzten sie die Einheit.");
    }

    public boolean messageCommitDelete(Article article) {
        return JOptionPane.showConfirmDialog(
                getTopComponent(),
                String.format("Soll der Artikel %s wirklich gelöscht werden?", article.getName()),
                "Artikel löschen?",
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
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        main = new JPanel();
        main.setLayout(new GridLayoutManager(2, 3, new Insets(10, 10, 10, 10), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Artikeldaten");
        main.add(label1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel1, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(24, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setPreferredSize(new Dimension(250, 550));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Name");
        panel2.add(label2, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(23, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panel2.add(itemName, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Lieferant");
        panel2.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(supplier, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Netto-Preis[€]");
        panel2.add(label4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(netPrice, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Einzelpfand[€]");
        panel2.add(label5, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(deposit, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Barcode");
        panel2.add(label6, new GridConstraints(10, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(barcode, new GridConstraints(11, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(8, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.add(amount, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Packungs-Menge");
        panel3.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel3.add(metricUnits, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Einheit");
        panel3.add(label8, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(shopRange, new GridConstraints(20, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Mehrwertsteuer");
        panel2.add(label9, new GridConstraints(4, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(vat, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Kistenpfand[€]");
        panel2.add(label10, new GridConstraints(6, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(crateDeposit, new GridConstraints(7, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Laden-Artikelnummer");
        panel2.add(label11, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(kbItemNumber, new GridConstraints(13, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Lieferanten-Artikelnummer");
        panel2.add(label12, new GridConstraints(12, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(supplierItemNumber, new GridConstraints(13, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("Preisliste");
        panel2.add(label13, new GridConstraints(15, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        panel2.add(panel4, new GridConstraints(16, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        searchPriceList = new PermissionButton();
        searchPriceList.setEnabled(false);
        searchPriceList.setHorizontalAlignment(0);
        searchPriceList.setText("Suchen");
        panel4.add(searchPriceList, BorderLayout.EAST);
        panel4.add(priceList, BorderLayout.CENTER);
        final JLabel label14 = new JLabel();
        label14.setText("Zuschlagsgruppe");
        panel2.add(label14, new GridConstraints(15, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText("Zusatz Informationen");
        panel2.add(label15, new GridConstraints(17, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(extraInfo, new GridConstraints(18, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label16 = new JLabel();
        label16.setText("Gebindegröße");
        panel2.add(label16, new GridConstraints(8, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(containerSize, new GridConstraints(9, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label17 = new JLabel();
        label17.setText("Sortimentstatus");
        panel2.add(label17, new GridConstraints(19, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        weighable.setText("Auswiegware");
        panel2.add(weighable, new GridConstraints(14, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showInShoppingMask.setText("Einblenden in der Einkaufsmaske");
        panel2.add(showInShoppingMask, new GridConstraints(14, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new BorderLayout(0, 0));
        panel2.add(panel5, new GridConstraints(16, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.add(surchargeGroup, BorderLayout.CENTER);
        searchSurchargeGroup = new PermissionButton();
        searchSurchargeGroup.setEnabled(false);
        searchSurchargeGroup.setHorizontalAlignment(0);
        searchSurchargeGroup.setText("Suchen");
        panel5.add(searchSurchargeGroup, BorderLayout.EAST);
        final JLabel label18 = new JLabel();
        label18.setText("Preisfaktor Lieferschein");
        panel2.add(label18, new GridConstraints(21, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(catalogPriceFactor, new GridConstraints(22, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.add(labelCount, new GridConstraints(22, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label19 = new JLabel();
        label19.setText("Etiketten bei Lieferung");
        panel2.add(label19, new GridConstraints(21, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelPerUnit.setText("zusätzlich 1 pro Gebinde");
        panel2.add(labelPerUnit, new GridConstraints(22, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label20 = new JLabel();
        label20.setText("Hersteller");
        panel2.add(label20, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(producer, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main;
    }
}
