package kernbeisser.Windows.EditItem;

import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.CustomComponents.TextFields.LongParseField;
import kernbeisser.DBEntities.Item;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.ContainerDefinition;
import kernbeisser.Enums.Unit;
import kernbeisser.Enums.VAT;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.util.Collection;

public class EditItemView extends Window implements View {
    private JButton commit;
    private JButton cancel;
    private JTextField itemName;
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
    private JComboBox<Unit> unit;
    private JComboBox<ContainerDefinition> containerDefinition;
    private JTextField barcode;
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
    }


    private void createUIComponents() {
        barcode = new LongParseField();
        amount = new IntegerParseField();
        netPrice = new DoubleParseField();
        deposit = new DoubleParseField();
        kbItemNumber = new IntegerParseField();
        supplierItemNumber = new IntegerParseField();
        crateDeposit = new DoubleParseField();
        containerSize = new DoubleParseField();
    }

    void setUnits(Unit[] units) {
        unit.removeAllItems();
        for (Unit u : units) {
            unit.addItem(u);
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

    void pasteItem(Item item) {
        itemName.setText(item.getName());
        netPrice.setText(String.valueOf(item.getNetPrice() / 100f));
        deposit.setText(String.valueOf(item.getSingleDeposit() / 100f));
        kbItemNumber.setText(String.valueOf(item.getKbNumber()));
        vat.setSelectedItem(item.isVatLow() ? VAT.LOW : VAT.HIGH);
        supplierItemNumber.setText(String.valueOf(item.getSuppliersItemNumber()));
        crateDeposit.setText(String.valueOf(item.getCrateDeposit() / 100f));
        containerSize.setText(String.valueOf(item.getContainerSize()));
        amount.setText(String.valueOf(item.getAmount()));
        barcode.setText(String.valueOf(item.getBarcode()));
        showInShoppingMask.setSelected(item.isShowInShop());
        weighable.setSelected(item.isWeighAble());
        extraInfo.setText(item.getInfo());
        priceList.setSelectedItem(item.getPriceList());
        supplier.setSelectedItem(item.getSupplier());
        containerDefinition.setSelectedItem(item.getContainerDef());
    }

    Item collectItem(Item out) {
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
