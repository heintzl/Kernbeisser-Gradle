package kernbeisser.Windows.EditSurchargeTable;

import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeTable;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.util.Collection;

class EditSurchargeTableView extends Window {
    private final EditSurchargeTableController controller;
    private JButton commit;
    private JButton cancel;
    private JComboBox<Supplier> supplier;
    private JTextField name;
    private IntegerParseField from;
    private IntegerParseField to;
    private kernbeisser.CustomComponents.TextFields.DoubleParseField surcharge;
    private JPanel main;

    EditSurchargeTableView(EditSurchargeTableController controller, Window current) {
        super(current);
        this.controller = controller;
        commit.addActionListener((e) -> controller.commit());
        cancel.addActionListener((e) -> back());
        add(main);
        pack();
        setLocationRelativeTo(null);
        windowInitialized();
    }

    void setSuppliers(Collection<Supplier> suppliers) {
        supplier.removeAllItems();
        suppliers.forEach(supplier::addItem);
    }

    void paste(SurchargeTable table) {
        name.setText(table.getName());
        from.setText(String.valueOf(table.getFrom()));
        to.setText(String.valueOf(table.getTo()));
        supplier.setSelectedItem(table.getSupplier());
        surcharge.setText(String.valueOf(table.getSurcharge()));
    }

    SurchargeTable collect(SurchargeTable table) {
        table.setSupplier((Supplier) supplier.getSelectedItem());
        table.setSurcharge(surcharge.getSafeValue());
        table.setFrom(from.getSafeValue());
        table.setTo(to.getSafeValue());
        table.setName(name.getText());
        return table;
    }

}
