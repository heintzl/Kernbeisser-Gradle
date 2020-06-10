package kernbeisser.Windows.EditItem;

import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.CustomComponents.Verifier.*;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.ContainerDefinition;
import kernbeisser.Enums.Key;
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
        weighable.setSelected(article.isWeighAble());
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
        out.setWeighAble(weighable.isSelected());
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
        itemName.setRequiredKeys(Key.ARTICLE_NAME_READ, Key.ARTICLE_NAME_WRITE);
        itemName.setInputVerifier(new NotNullVerifier());
        amount.setRequiredKeys(Key.ARTICLE_AMOUNT_READ, Key.ARTICLE_AMOUNT_WRITE);
        amount.setInputVerifier(IntegerVerifier.from(0,Integer.MAX_VALUE));
        netPrice.setRequiredKeys(Key.ARTICLE_NET_PRICE_READ, Key.ARTICLE_NET_PRICE_WRITE);
        netPrice.setInputVerifier(DoubleVerifier.from(0.,999999));
        supplier.setRequiredReadKeys(Key.ARTICLE_SUPPLIER_READ,Key.SUPPLIER_NAME_READ);
        supplier.setRequiredWriteKeys(Key.ARTICLE_SUPPLIER_WRITE);
        deposit.setRequiredKeys(Key.ARTICLE_SINGLE_DEPOSIT_READ, Key.ARTICLE_SINGLE_DEPOSIT_WRITE);
        deposit.setInputVerifier(DoubleVerifier.from(0,0.1,5,300));
        kbItemNumber.setRequiredKeys(Key.ARTICLE_KB_NUMBER_READ, Key.ARTICLE_KB_NUMBER_READ);
        kbItemNumber.setInputVerifier(new KBNumberVerifier());
        supplierItemNumber.setRequiredKeys(Key.ARTICLE_SUPPLIERS_ITEM_NUMBER_READ,Key.ARTICLE_SUPPLIERS_ITEM_NUMBER_WRITE);
        supplierItemNumber.setInputVerifier(IntegerVerifier.from(0,999999));
        crateDeposit.setRequiredKeys(Key.ARTICLE_CRATE_DEPOSIT_READ, Key.ARTICLE_CRATE_DEPOSIT_WRITE);
        crateDeposit.setInputVerifier(DoubleVerifier.from(0.,0.99,5,20));
        priceList.setRequiredReadKeys(Key.ARTICLE_PRICE_LIST_READ,Key.PRICELIST_NAME_READ);
        priceList.setRequiredWriteKeys(Key.ARTICLE_PRICE_LIST_WRITE);
        search.setRequiredWriteKeys(Key.ARTICLE_PRICE_LIST_WRITE);
        vat.setRequiredKeys(Key.ARTICLE_VAT_READ,Key.ARTICLE_VAT_WRITE);
        metricUnits.setRequiredKeys(Key.ARTICLE_METRIC_UNITS_READ, Key.ARTICLE_METRIC_UNITS_WRITE);
        barcode.setRequiredKeys(Key.ARTICLE_BARCODE_READ,Key.ARTICLE_BARCODE_WRITE);
        containerDefinition.setRequiredKeys(Key.ARTICLE_CONTAINER_DEF_READ,Key.ARTICLE_CONTAINER_DEF_WRITE);
        containerSize.setRequiredKeys(Key.ARTICLE_CONTAINER_SIZE_READ,Key.ARTICLE_CONTAINER_SIZE_WRITE);
        containerSize.setInputVerifier(DoubleVerifier.from(0,0.1,40,1000));
        showInShoppingMask.setRequiredReadKeys(Key.ARTICLE_SHOW_IN_SHOP_READ,Key.ARTICLE_SHOW_IN_SHOP_WRITE);
        weighable.setRequiredReadKeys(Key.ARTICLE_WEIGHABLE_READ,Key.ARTICLE_WEIGHABLE_WRITE);
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
