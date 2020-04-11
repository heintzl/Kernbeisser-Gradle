package kernbeisser.Windows.EditItem;

import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.ContainerDefinition;
import kernbeisser.Enums.Key;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.JFrameWindow;
import kernbeisser.Windows.Window;
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
    private kernbeisser.CustomComponents.PermissionComboBox priceList;
    private IntegerParseField amount;
    private DoubleParseField containerSize;
    private kernbeisser.CustomComponents.PermissionComboBox metricUnits;
    private kernbeisser.CustomComponents.PermissionComboBox containerDefinition;
    private kernbeisser.CustomComponents.TextFields.PermissionField barcode;
    private kernbeisser.CustomComponents.PermissionCheckBox showInShoppingMask;
    private kernbeisser.CustomComponents.PermissionCheckBox weighable;
    private JTextArea extraInfo;
    private kernbeisser.CustomComponents.PermissionComboBox vat;
    private JPanel main;

    private final EditItemController controller;

    public EditItemView(EditItemController controller) {
        this.controller = controller;
    }


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
        vat.setSelectedItem(article.getVAT());
        supplierItemNumber.setText(String.valueOf(article.getSuppliersItemNumber()));
        crateDeposit.setText(String.valueOf(article.getCrateDeposit() ));
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
        out.setNetPrice((int) (netPrice.getSafeValue() * 100));
        out.setSingleDeposit((int) (deposit.getSafeValue() * 100));
        out.setKbNumber(kbItemNumber.getSafeValue());
        out.setVAT((VAT) vat.getSelectedItem());
        out.setSuppliersItemNumber(supplierItemNumber.getSafeValue());
        out.setSuppliersItemNumber(supplierItemNumber.getSafeValue());
        out.setCrateDeposit((int) (crateDeposit.getSafeValue() * 100));
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

    void kbNumberAlreadyExists() {
        JOptionPane.showMessageDialog(getTopComponent(), "Die Kernbeisser-Nummer ist bereits vergeben");
    }

    void barcodeAlreadyExists() {
        JOptionPane.showMessageDialog(getTopComponent(), "Der Barcode ist bereits vergeben");
    }

    @Override
    public void initialize(EditItemController controller) {
        cancel.addActionListener((e) -> back());
        commit.addActionListener((e) -> controller.doAction());
        itemName.setRequiredKeys(Key.ARTICLE_NAME_READ, Key.ARTICLE_NAME_WRITE);
        amount.setRequiredKeys(Key.ARTICLE_AMOUNT_READ, Key.ARTICLE_AMOUNT_WRITE);
        netPrice.setRequiredKeys(Key.ARTICLE_NET_PRICE_READ, Key.ARTICLE_NET_PRICE_WRITE);
        supplier.setRequiredReadKeys(Key.ARTICLE_SUPPLIER_READ,Key.SUPPLIER_NAME_READ);
        supplier.setRequiredWriteKeys(Key.ARTICLE_SUPPLIER_WRITE);
        netPrice.setRequiredKeys(Key.ARTICLE_NET_PRICE_READ,Key.ARTICLE_NET_PRICE_WRITE);
        deposit.setRequiredKeys(Key.ARTICLE_SINGLE_DEPOSIT_READ, Key.ARTICLE_SINGLE_DEPOSIT_WRITE);
        kbItemNumber.setRequiredKeys(Key.ARTICLE_KB_NUMBER_READ, Key.ARTICLE_KB_NUMBER_READ);
        supplierItemNumber.setRequiredKeys(Key.ARTICLE_SUPPLIERS_ITEM_NUMBER_READ,Key.ARTICLE_SUPPLIERS_ITEM_NUMBER_WRITE);
        crateDeposit.setRequiredKeys(Key.ARTICLE_CRATE_DEPOSIT_READ, Key.ARTICLE_CRATE_DEPOSIT_WRITE);
        priceList.setRequiredReadKeys(Key.ARTICLE_PRICE_LIST_READ,Key.PRICELIST_NAME_READ);
        priceList.setRequiredWriteKeys(Key.ARTICLE_PRICE_LIST_WRITE);
        search.setRequiredWriteKeys(Key.ARTICLE_PRICE_LIST_WRITE);
        vat.setRequiredKeys(Key.ARTICLE_VAT_READ,Key.ARTICLE_VAT_WRITE);
        metricUnits.setRequiredKeys(Key.ARTICLE_METRIC_UNITS_READ, Key.ARTICLE_METRIC_UNITS_WRITE);
        barcode.setRequiredKeys(Key.ARTICLE_BARCODE_READ,Key.ARTICLE_BARCODE_WRITE);
        containerDefinition.setRequiredKeys(Key.ARTICLE_CONTAINER_DEF_READ,Key.ARTICLE_CONTAINER_DEF_WRITE);
        containerSize.setRequiredKeys(Key.ARTICLE_CONTAINER_SIZE_READ,Key.ARTICLE_CONTAINER_SIZE_WRITE);
        showInShoppingMask.setRequiredReadKeys(Key.ARTICLE_SHOW_IN_SHOP_READ,Key.ARTICLE_SHOW_IN_SHOP_WRITE);
        weighable.setRequiredReadKeys(Key.ARTICLE_WEIGHABLE_READ,Key.ARTICLE_WEIGHABLE_WRITE);
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

}
