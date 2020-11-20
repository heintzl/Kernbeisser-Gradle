package kernbeisser.Windows.EditSurchargeTable;

import java.util.Collection;
import javax.swing.*;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingField;
import kernbeisser.CustomComponents.AccessChecking.ObjectForm;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeTable;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

class EditSurchargeTableView implements IView<EditSurchargeTableController> {
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

  private ObjectForm<SurchargeTable> objectForm;
  @Linked private EditSurchargeTableController controller;

  void setSuppliers(Collection<Supplier> suppliers) {
    supplier.removeAllItems();
    suppliers.forEach(supplier::addItem);
  }

  public ObjectForm<SurchargeTable> getObjectForm() {
    return objectForm;
  }

  @Override
  public void initialize(EditSurchargeTableController controller) {
    objectForm = new ObjectForm<>(supplier, name, from, to, surcharge);
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
            SurchargeTable::getDescription,
            SurchargeTable::setDescription,
            AccessCheckingField.NONE);
    from =
        new AccessCheckingField<>(
            SurchargeTable::getFrom_number,
            SurchargeTable::setFrom_number,
            AccessCheckingField.INT_FORMER);
    to =
        new AccessCheckingField<>(
            SurchargeTable::getTo_number,
            SurchargeTable::setTo_number,
            AccessCheckingField.INT_FORMER);
    surcharge =
        new AccessCheckingField<>(
            SurchargeTable::getSurcharge,
            SurchargeTable::setSurcharge,
            AccessCheckingField.DOUBLE_FORMER);
  }
}
