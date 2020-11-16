package kernbeisser.Windows.EditSurchargeTable;

import java.util.Collection;
import javax.swing.*;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingField;
import kernbeisser.CustomComponents.AccessChecking.ObjectForm;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

class EditSurchargeTableView implements IView<EditSurchargeTableController> {
  private JButton commit;
  private JButton cancel;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox<
          SurchargeGroup, Supplier>
      supplier;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<SurchargeGroup, String>
      name;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<SurchargeGroup, Integer>
      from;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<SurchargeGroup, Integer>
      to;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<SurchargeGroup, Double>
      surcharge;
  private JPanel main;

  private ObjectForm<SurchargeGroup> objectForm;
  @Linked private EditSurchargeTableController controller;

  void setSuppliers(Collection<Supplier> suppliers) {
    supplier.removeAllItems();
    suppliers.forEach(supplier::addItem);
  }

  public ObjectForm<SurchargeGroup> getObjectForm() {
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
        new AccessCheckingComboBox<>(SurchargeGroup::getSupplier, SurchargeGroup::setSupplier);
    surcharge =
        new AccessCheckingField<>(
            SurchargeGroup::getSurcharge,
            SurchargeGroup::setSurcharge,
            AccessCheckingField.DOUBLE_FORMER);
  }
}
