package kernbeisser.Windows.EditArticle;

import java.awt.Dimension;
import java.util.Collection;
import javax.swing.*;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckBox;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingField;
import kernbeisser.CustomComponents.AccessChecking.ObjectForm;
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
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class EditArticleView implements IView<EditArticleController> {
  private JButton commit;
  private JButton cancel;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article, String> itemName;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox<Article, Supplier>
      supplier;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article, Double> netPrice;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article, Double> deposit;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article, Integer>
      kbItemNumber;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article, Integer>
      supplierItemNumber;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article, Double>
      crateDeposit;
  private kernbeisser.CustomComponents.PermissionButton search;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox<Article, PriceList>
      priceList;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article, Integer> amount;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article, Double>
      containerSize;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox<Article, MetricUnits>
      metricUnits;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article, Long> barcode;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckBox<Article> showInShoppingMask;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckBox<Article> weighable;
  private JTextArea extraInfo;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox<Article, VAT> vat;
  private JPanel main;
  private AccessCheckingComboBox<Article, SurchargeGroup> surchargeGroup;
  private AccessCheckingComboBox<Article, ShopRange> shopRange;

  private ObjectForm<Article> articleObjectForm;

  @Linked private EditArticleController controller;

  private void createUIComponents() {
    itemName =
        new AccessCheckingField<>(Article::getName, Article::setName, controller::validateName);
    amount =
        new AccessCheckingField<>(
            Article::getAmount,
            Article::setAmount,
            AccessCheckingField.combine(controller::displayAmount, controller::validateAmount));
    netPrice =
        new AccessCheckingField<>(
            Article::getNetPrice, Article::setNetPrice, AccessCheckingField.DOUBLE_FORMER);
    deposit =
        new AccessCheckingField<>(
            Article::getSingleDeposit,
            Article::setSingleDeposit,
            AccessCheckingField.DOUBLE_FORMER);
    kbItemNumber =
        new AccessCheckingField<>(
            Article::getKbNumber, Article::setKbNumber, controller::validateKBNumber);
    supplierItemNumber =
        new AccessCheckingField<>(
            Article::getSuppliersItemNumber,
            Article::setSuppliersItemNumber,
            controller::validateSuppliersItemNumber);
    crateDeposit =
        new AccessCheckingField<>(
            Article::getContainerDeposit,
            Article::setContainerDeposit,
            AccessCheckingField.DOUBLE_FORMER);
    containerSize =
        new AccessCheckingField<>(
            Article::getContainerSize,
            Article::setContainerSize,
            AccessCheckingField.DOUBLE_FORMER);
    supplier = new AccessCheckingComboBox<>(Article::getSupplier, Article::setSupplier);
    priceList = new AccessCheckingComboBox<>(Article::getPriceList, Article::setPriceList);
    metricUnits = new AccessCheckingComboBox<>(Article::getMetricUnits, Article::setMetricUnits);
    barcode =
        new AccessCheckingField<>(
            Article::getBarcode, Article::setBarcode, controller::validateBarcode);
    showInShoppingMask = new AccessCheckBox<>(Article::isShowInShop, Article::setShowInShop);
    weighable = new AccessCheckBox<>(Article::isWeighable, Article::setWeighable);
    vat = new AccessCheckingComboBox<>(Article::getVat, Article::setVat);
    surchargeGroup =
        new AccessCheckingComboBox<>(
            e -> {
              controller.loadSurchargeGroupsFor(getSelectedSupplier());
              return e.getSurchargeGroup();
            },
            Article::setSurchargeGroup);
    shopRange =
        new AccessCheckingComboBox<Article, ShopRange>(
            Article::getShopRange, Article::setShopRange);
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
  public void initialize(EditArticleController controller) {
    cancel.addActionListener((e) -> back());
    commit.addActionListener((e) -> controller.doAction());
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
            amount,
            barcode,
            showInShoppingMask,
            weighable,
            surchargeGroup,
            shopRange,
            vat);
    supplier.addActionListener(
        e -> {
          try {
            controller.loadSurchargeGroupsFor(supplier.getSelected());
          } catch (NullPointerException nullPointerException) {
            controller.loadSurchargeGroupsFor(Supplier.getKKSupplier());
          }
        });
  }

  void setActionTitle(String s) {
    commit.setText(s);
  }

  void setActionIcon(Icon i) {
    commit.setIcon(i);
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
    if (PermissionKey.ARTICLE_BARCODE_WRITE.userHas()
        && JOptionPane.showConfirmDialog(
                getTopComponent(), "Soll der Barcode auf " + s + " gesetzt werden?")
            == 0) {
      barcode.setText(s);
    }
  }

  public boolean isSameArticle(Article nearest) {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Es wurde ein Artikel gefunden der einen sehr identischen Namen hat:\n"
                + nearest.toString()
                + "\nWollen sie trozedem einen neuen Artikel erstellen?")
        == 0;
  }

  public Supplier getSelectedSupplier() {
    return supplier.getSelected();
  }

  public void suppliersItemNumberNotAvailable() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Die gewählte Lieferantennummer ist bereits für diesen Lieferant vergeben!");
  }
}
