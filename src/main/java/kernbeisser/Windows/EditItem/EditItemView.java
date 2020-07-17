package kernbeisser.Windows.EditItem;

import kernbeisser.CustomComponents.AccessChecking.AccessCheckBox;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingField;
import kernbeisser.CustomComponents.AccessChecking.ObjectForm;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.CustomComponents.Verifier.*;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleBase;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.ContainerDefinition;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;

public class EditItemView implements View<EditItemController> {
    private JButton commit;
    private JButton cancel;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article,String> itemName;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox<Article,Supplier> supplier;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article,Double> netPrice;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article,Double> deposit;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article,Integer> kbItemNumber;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article,Integer> supplierItemNumber;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article,Double> crateDeposit;
    private kernbeisser.CustomComponents.PermissionButton search;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox<Article,PriceList> priceList;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article,Double> amount;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article,Double> containerSize;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox<Article,MetricUnits> metricUnits;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox<Article,ContainerDefinition> containerDefinition;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<Article,Long> barcode;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckBox<Article> showInShoppingMask;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckBox<Article> weighable;
    private JTextArea extraInfo;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox<Article,VAT> vat;
    private JPanel main;

    private ObjectForm<Article> articleObjectForm;

    private void createUIComponents() {
        itemName = new AccessCheckingField<>(ArticleBase::getName,ArticleBase::setName,AccessCheckingField.NOT_NULL);
        amount = new AccessCheckingField<>(e -> e.getAmount() * e.getMetricUnits().getBaseFactor(),
                                           (a, b) -> a.setAmount((int) (b * a.getMetricUnits().getBaseFactor())),
                                           AccessCheckingField.DOUBLE_FORMER);
        netPrice = new AccessCheckingField<>(ArticleBase::getNetPrice,ArticleBase::setNetPrice,AccessCheckingField.DOUBLE_FORMER);
        deposit = new AccessCheckingField<>(ArticleBase::getSingleDeposit,ArticleBase::setSingleDeposit,AccessCheckingField.DOUBLE_FORMER);
        kbItemNumber = new AccessCheckingField<>(Article::getKbNumber,Article::setKbNumber,AccessCheckingField.INT_FORMER);
        supplierItemNumber = new AccessCheckingField<>(ArticleBase::getSuppliersItemNumber,ArticleBase::setSuppliersItemNumber,AccessCheckingField.INT_FORMER);
        crateDeposit = new AccessCheckingField<>(ArticleBase::getContainerDeposit,ArticleBase::setContainerDeposit,AccessCheckingField.DOUBLE_FORMER);
        containerSize = new AccessCheckingField<>(ArticleBase::getContainerSize,ArticleBase::setContainerSize,AccessCheckingField.DOUBLE_FORMER);
        supplier = new AccessCheckingComboBox<>(ArticleBase::getSupplier,ArticleBase::setSupplier);
        priceList = new AccessCheckingComboBox<>(Article::getPriceList,Article::setPriceList);
        metricUnits = new AccessCheckingComboBox<>(ArticleBase::getMetricUnits,ArticleBase::setMetricUnits);
        containerDefinition = new AccessCheckingComboBox<>(Article::getContainerDef,Article::setContainerDef);
        barcode = new AccessCheckingField<>(ArticleBase::getBarcode,ArticleBase::setBarcode,AccessCheckingField.LONG_FORMER);
        showInShoppingMask = new AccessCheckBox<>(Article::isShowInShop,Article::setShowInShop);
        weighable = new AccessCheckBox<>(Article::isWeighable,Article::setWeighable);
        vat = new AccessCheckingComboBox<>(ArticleBase::getVat,ArticleBase::setVat);
    }

    void setUnits(MetricUnits[] metricUnits) {
        this.metricUnits.removeAllItems();
        for (MetricUnits u : metricUnits) {
            this.metricUnits.addItem(u);
        }
    }

    void setVATs(VAT[] vaTs) {
        vat.removeAllItems();
        for (VAT vaT : vaTs) {
            vat.addItem(vaT);
        }
    }

    void setSuppliers(Collection<Supplier> suppliers) {
        supplier.removeAllItems();
        suppliers.forEach(supplier::addItem);
    }

    void setPriceLists(Collection<PriceList> priceLists) {
        priceList.removeAllItems();
        priceLists.forEach(priceList::addItem);
    }

    void setContainerDefinitions(ContainerDefinition[] containerDefinitions) {
        containerDefinition.removeAllItems();
        for (ContainerDefinition definition : containerDefinitions) {
            containerDefinition.addItem(definition);
        }
    }


    public ObjectForm<Article> getArticleObjectForm() {
        return articleObjectForm;
    }


    boolean kbNumberAlreadyExists() {
        return 0 == JOptionPane.showConfirmDialog(getTopComponent(), "Die Artikelnummer ist bereits vergeben soll die nächste freie Ausgewählt werden?");
    }

    void barcodeAlreadyExists() {
        JOptionPane.showMessageDialog(getTopComponent(), "Der Barcode ist bereits vergeben");
    }

    @Override
    public void initialize(EditItemController controller) {
        cancel.addActionListener((e) -> back());
        commit.addActionListener((e) -> controller.doAction());
        itemName.setInputVerifier(new NotNullVerifier());
        amount.setInputVerifier(DoubleVerifier.from(0,Integer.MAX_VALUE));
        netPrice.setInputVerifier(DoubleVerifier.from(0.,999999));
        deposit.setInputVerifier(DoubleVerifier.from(0,0.1,5,300));
        kbItemNumber.setInputVerifier(new KBNumberVerifier());
        supplierItemNumber.setInputVerifier(IntegerVerifier.from(0,999999));
        crateDeposit.setInputVerifier(DoubleVerifier.from(0.,0.99,5,20));
        containerSize.setInputVerifier(DoubleVerifier.from(0,0.1,40,1000));
        articleObjectForm = new ObjectForm<>(controller.getModel().getSource(),
                                             itemName,
                                             supplier,
                                             netPrice,
                                             deposit,
                                             kbItemNumber,
                                             supplierItemNumber,
                                             crateDeposit,
                                             priceList,
                                             amount,
                                             containerSize,
                                             metricUnits,
                                             containerDefinition,
                                             barcode,
                                             showInShoppingMask,
                                             weighable
        );
    }

    boolean validate(){
        return Tools.verify(
                itemName,
                supplier,
                netPrice,
                deposit,
                kbItemNumber,
                supplierItemNumber,
                crateDeposit,
                search,
                priceList,
                amount,
                containerSize,
                metricUnits,
                containerDefinition,
                barcode,
                showInShoppingMask,
                weighable,
                extraInfo,
                vat
        );
    }

    void setActionTitle(String s){
        commit.setText(s);
    }

    void setActionIcon(Icon i){
        commit.setIcon(i);
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

    public void setKbNumber(int nextUnusedArticleNumber) {
        kbItemNumber.setText(nextUnusedArticleNumber+"");
        kbItemNumber.inputChanged();
    }

    public void nameAlreadyExists() {
        JOptionPane.showMessageDialog(getTopComponent(), "Der gewählte Name ist bereits vergeben!\nBitte wählen sie einen anderen");
    }

    public void invalidInput() {
        JOptionPane.showMessageDialog(getTopComponent(),"Bitte füllen sie alle Werte korrekt aus");
    }
}
