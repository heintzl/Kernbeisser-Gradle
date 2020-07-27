package kernbeisser.Windows.EditSurchargeTable;

import java.util.Collection;
import javax.swing.*;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingField;
import kernbeisser.CustomComponents.AccessChecking.ObjectForm;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeTable;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

class EditSurchargeTableView implements View<EditSurchargeTableController> {
  private JButton commit;
  private JButton cancel;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox<
          SurchargeTable, Supplier>
      supplier;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<SurchargeTable, String>
      name;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<SurchargeTable, Integer>
      from;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<SurchargeTable, Integer>
      to;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<SurchargeTable, Double>
      surcharge;
  private JPanel main;

  private final EditSurchargeTableController controller;

  EditSurchargeTableView(EditSurchargeTableController controller) {
    this.controller = controller;
  }

  private ObjectForm<SurchargeTable> objectForm;

  void setSuppliers(Collection<Supplier> suppliers) {
    supplier.removeAllItems();
    suppliers.forEach(supplier::addItem);
  }

  public ObjectForm<SurchargeTable> getObjectForm() {
    return objectForm;
  }

  @Override
  public void initialize(EditSurchargeTableController controller) {
    objectForm =
        new ObjectForm<>(controller.getModel().getSource(), supplier, name, from, to, surcharge);
    commit.addActionListener((e) -> controller.commit());
    cancel.addActionListener((e) -> back());
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    supplier =
        new AccessCheckingComboBox<>(SurchargeTable::getSupplier, SurchargeTable::setSupplier);
    name =
        new AccessCheckingField<>(
            SurchargeTable::getName, SurchargeTable::setName, AccessCheckingField.NONE);
    from =
        new AccessCheckingField<>(
            SurchargeTable::getFrom, SurchargeTable::setFrom, AccessCheckingField.INT_FORMER);
    to =
        new AccessCheckingField<>(
            SurchargeTable::getTo, SurchargeTable::setTo, AccessCheckingField.INT_FORMER);
    surcharge =
        new AccessCheckingField<>(
            SurchargeTable::getSurcharge,
            SurchargeTable::setSurcharge,
            AccessCheckingField.DOUBLE_FORMER);
  }

  public void incorrectInput() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Bitte überprüfen sie die eingaben auf Fehler");
  }
}
