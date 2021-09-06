package kernbeisser.Forms.FormImplemetations.Article;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.util.Collection;
import javax.swing.*;
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
            extraInfo);
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
        "Es wurde ein Artikel gefunden der einen sehr identischen Namen hat:\n"
            + nearest.toString()
            + "\nWollen sie trozedem einen neuen Artikel erstellen?")
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

}
