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
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.util.Collection;

public class EditItemView extends Window implements View {
    private JButton commit;
    private JButton cancel;
    private kernbeisser.CustomComponents.TextFields.PermissionField itemName;
    private JComboBox<Supplier> supplier;
    private DoubleParseField netPrice;
    private DoubleParseField deposit;
    private IntegerParseField kbItemNumber;
    private IntegerParseField supplierItemNumber;
    private DoubleParseField crateDeposit;
    private JButton search;
    private JComboBox<PriceList> priceList;
    private IntegerParseField amount;
    private DoubleParseField containerSize;
    private JComboBox<MetricUnits> metricUnits;
    private JComboBox<ContainerDefinition> containerDefinition;
    private kernbeisser.CustomComponents.TextFields.PermissionField barcode;
    private JCheckBox showInShoppingMask;
    private JCheckBox weighable;
    private JTextArea extraInfo;
    private JComboBox<VAT> vat;
    private JPanel main;

    public EditItemView(EditItemController controller, Window current) {
        super(current);
        cancel.addActionListener((e) -> back());
        commit.addActionListener((e) -> controller.doAction());
        add(main);
        pack();
        setLocationRelativeTo(null);
        amount.setRequiredKeys(Key.ITEM_AMOUNT_READ, Key.ITEM_AMOUNT_WRITE);
        netPrice.setRequiredKeys(Key.ITEM_NET_PRICE_READ, Key.ITEM_NET_PRICE_WRITE);
        deposit.setRequiredKeys(Key.ITEM_SINGLE_DEPOSIT_READ, Key.ITEM_SINGLE_DEPOSIT_WRITE);
        kbItemNumber.setRequiredKeys(Key.ITEM_KB_NUMBER_READ, Key.ITEM_KB_NUMBER_READ);
        supplierItemNumber.setRequiredKeys(Key.ITEM_SUPPLIERS_ITEM_NUMBER_READ, Key.ITEM_SUPPLIERS_ITEM_NUMBER_WRITE);
        crateDeposit.setRequiredKeys(Key.ITEM_CRATE_DEPOSIT_READ, Key.ITEM_CRATE_DEPOSIT_WRITE);
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
        netPrice.setText(String.valueOf(article.getNetPrice() / 100f));
        deposit.setText(String.valueOf(article.getSingleDeposit() / 100f));
        kbItemNumber.setText(String.valueOf(article.getKbNumber()));
        vat.setSelectedItem(article.isVatLow() ? VAT.LOW : VAT.HIGH);
        supplierItemNumber.setText(String.valueOf(article.getSuppliersItemNumber()));
        crateDeposit.setText(String.valueOf(article.getCrateDeposit() / 100f));
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
        out.setNetPrice((int) (netPrice.getValue() * 100));
        out.setSingleDeposit((int) (deposit.getValue() * 100));
        out.setKbNumber(kbItemNumber.getValue());
        out.setVatLow(vat.getSelectedItem() == VAT.LOW);
        out.setSuppliersItemNumber(supplierItemNumber.getValue());
        out.setSuppliersItemNumber(supplierItemNumber.getValue());
        out.setCrateDeposit((int) (crateDeposit.getValue() * 100));
        out.setContainerSize(containerSize.getValue());
        out.setAmount(amount.getValue());
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
        JOptionPane.showMessageDialog(this, "Die Kernbeisser-Nummer ist bereits vergeben");
    }

    void barcodeAlreadyExists() {
        JOptionPane.showMessageDialog(this, "Der Barcode ist bereits vergeben");
    }

}
