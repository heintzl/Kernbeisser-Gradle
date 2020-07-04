package kernbeisser.Windows.EditItem;

import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.CustomComponents.Verifier.*;
import kernbeisser.DBEntities.Article;
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
    private kernbeisser.CustomComponents.TextFields.PermissionField itemName;
    private kernbeisser.CustomComponents.PermissionComboBox supplier;
    private DoubleParseField netPrice;
    private DoubleParseField deposit;
    private IntegerParseField kbItemNumber;
    private IntegerParseField supplierItemNumber;
    private DoubleParseField crateDeposit;
    private kernbeisser.CustomComponents.PermissionButton search;
    private kernbeisser.CustomComponents.PermissionComboBox<PriceList> priceList;
    private IntegerParseField amount;
    private DoubleParseField containerSize;
    private kernbeisser.CustomComponents.PermissionComboBox<MetricUnits> metricUnits;
    private kernbeisser.CustomComponents.PermissionComboBox<ContainerDefinition> containerDefinition;
    private kernbeisser.CustomComponents.TextFields.PermissionField barcode;
    private kernbeisser.CustomComponents.PermissionCheckBox showInShoppingMask;
    private kernbeisser.CustomComponents.PermissionCheckBox weighable;
    private JTextArea extraInfo;
    private kernbeisser.CustomComponents.PermissionComboBox<VAT> vat;
    private JPanel main;

    private void createUIComponents() {
        amount = new IntegerParseField();
        netPrice = new DoubleParseField();
        deposit = new DoubleParseField();
        kbItemNumber = new IntegerParseField();
        supplierItemNumber = new IntegerParseField();
        crateDeposit = new DoubleParseField();
        containerSize = new DoubleParseField();
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

    void pasteItem(Article article) {
        itemName.setText(article.getName());
        netPrice.setText(String.valueOf(article.getNetPrice()));
        deposit.setText(String.valueOf(article.getSingleDeposit()));
        kbItemNumber.setText(String.valueOf(article.getKbNumber()));
        vat.setSelectedItem(article.getVat());
        supplierItemNumber.setText(String.valueOf(article.getSuppliersItemNumber()));
        crateDeposit.setText(String.valueOf(article.getContainerDeposit() ));
        containerSize.setText(String.valueOf(article.getContainerSize()));
        amount.setText(String.valueOf(article.getAmount()));
        barcode.setText(String.valueOf(article.getBarcode()));
        showInShoppingMask.setSelected(article.isShowInShop());
        weighable.setSelected(article.isWeighable());
        extraInfo.setText(article.getInfo());
        priceList.setSelectedItem(article.getPriceList());
        supplier.setSelectedItem(article.getSupplier());
        containerDefinition.setSelectedItem(article.getContainerDef());
    }

    Article collectItem(Article out) {
        out.setName(itemName.getText());
        out.setNetPrice(netPrice.getSafeValue());
        out.setSingleDeposit(deposit.getSafeValue());
        out.setKbNumber(kbItemNumber.getSafeValue());
        out.setVat((VAT) vat.getSelectedItem());
        out.setSuppliersItemNumber(supplierItemNumber.getSafeValue());
        out.setSuppliersItemNumber(supplierItemNumber.getSafeValue());
        out.setContainerDeposit(crateDeposit.getSafeValue());
        out.setContainerSize(containerSize.getSafeValue());
        out.setAmount(amount.getSafeValue());
        try {
            out.setBarcode(Long.parseLong(barcode.getText()));
        } catch (NumberFormatException e) {
            out.setBarcode(null);
        }
        out.setShowInShop(showInShoppingMask.isSelected());
        out.setWeighable(weighable.isSelected());
        out.setInfo(extraInfo.getText());
        out.setPriceList((PriceList) priceList.getSelectedItem());
        out.setSupplier((Supplier) supplier.getSelectedItem());
        out.setContainerDef((ContainerDefinition) containerDefinition.getSelectedItem());
        return out;
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
        itemName.setRequiredKeys(PermissionKey.ARTICLE_BASE_NAME_READ, PermissionKey.ARTICLE_BASE_NAME_WRITE);
        itemName.setInputVerifier(new NotNullVerifier());
        amount.setRequiredKeys(PermissionKey.ARTICLE_BASE_AMOUNT_READ, PermissionKey.ARTICLE_BASE_AMOUNT_WRITE);
        amount.setInputVerifier(IntegerVerifier.from(0,Integer.MAX_VALUE));
        netPrice.setInputVerifier(DoubleVerifier.from(0.,999999));
        supplier.setRequiredReadKeys(PermissionKey.ARTICLE_BASE_SUPPLIER_READ, PermissionKey.SUPPLIER_NAME_READ);
        supplier.setRequiredWriteKeys(PermissionKey.ARTICLE_BASE_SUPPLIER_WRITE);
        deposit.setRequiredKeys(PermissionKey.ARTICLE_BASE_SINGLE_DEPOSIT_READ, PermissionKey.ARTICLE_BASE_SINGLE_DEPOSIT_WRITE);
        deposit.setInputVerifier(DoubleVerifier.from(0,0.1,5,300));
        kbItemNumber.setInputVerifier(new KBNumberVerifier());
        supplierItemNumber.setRequiredKeys(PermissionKey.ARTICLE_BASE_SUPPLIERS_ITEM_NUMBER_READ, PermissionKey.ARTICLE_BASE_SUPPLIERS_ITEM_NUMBER_WRITE);
        supplierItemNumber.setInputVerifier(IntegerVerifier.from(0,999999));
        crateDeposit.setInputVerifier(DoubleVerifier.from(0.,0.99,5,20));
        priceList.setRequiredWriteKeys(PermissionKey.ARTICLE_PRICE_LIST_WRITE);
        search.setRequiredWriteKeys(PermissionKey.ARTICLE_PRICE_LIST_WRITE);
        containerDefinition.setRequiredKeys(PermissionKey.ARTICLE_CONTAINER_DEF_READ, PermissionKey.ARTICLE_CONTAINER_DEF_WRITE);
        containerSize.setInputVerifier(DoubleVerifier.from(0,0.1,40,1000));
        showInShoppingMask.setRequiredReadKeys(PermissionKey.ARTICLE_SHOW_IN_SHOP_READ, PermissionKey.ARTICLE_SHOW_IN_SHOP_WRITE);
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
    }

    public void nameAlreadyExists() {
        JOptionPane.showMessageDialog(getTopComponent(), "Der gewählte Name ist bereits vergeben!\nBitte wählen sie einen anderen");
    }
}
