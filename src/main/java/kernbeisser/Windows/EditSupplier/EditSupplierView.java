package kernbeisser.Windows.EditSupplier;

import javax.swing.*;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingField;
import kernbeisser.CustomComponents.AccessChecking.ObjectForm;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.MVC.View;
import org.jetbrains.annotations.NotNull;

public class EditSupplierView implements View<EditSupplierController> {

  @Linked private EditSupplierController controller;

  private JPanel main;
  private JButton commit;
  private JButton cancel;
  private AccessCheckingField<Supplier, String> location;
  private AccessCheckingField<Supplier, String> keeper;
  private AccessCheckingField<Supplier, String> phoneNumber;
  private AccessCheckingField<Supplier, String> fax;
  private AccessCheckingField<Supplier, String> name;
  private AccessCheckingField<Supplier, String> shortName;
  private AccessCheckingField<Supplier, Integer> surcharge;
  private AccessCheckingField<Supplier,String> street;

  private ObjectForm<Supplier> objectForm;

  @Override
  public void initialize(EditSupplierController controller) {
    objectForm =
        new ObjectForm<>(
            controller.getModel().getSupplier(),
                street,
                location,
            keeper,
            phoneNumber,
            fax,
            name,
            shortName,
            surcharge);
    commit.addActionListener(e -> controller.commit());
    cancel.addActionListener(e -> back());
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    street = new AccessCheckingField<>(Supplier::getStreet,Supplier::setStreet,AccessCheckingField.NONE);
    location =
        new AccessCheckingField<>(
            Supplier::getLocation, Supplier::setLocation, AccessCheckingField.NONE);
    keeper =
        new AccessCheckingField<>(
            Supplier::getKeeper, Supplier::setKeeper, AccessCheckingField.NONE);
    phoneNumber =
        new AccessCheckingField<>(
            Supplier::getPhoneNumber, Supplier::setPhoneNumber, AccessCheckingField.NONE);
    fax = new AccessCheckingField<>(Supplier::getFax, Supplier::setFax, AccessCheckingField.NONE);
    name =
        new AccessCheckingField<>(Supplier::getName, Supplier::setName, controller::validateName);
    shortName =
        new AccessCheckingField<>(
            Supplier::getShortName, Supplier::setShortName, controller::validateShortName);
    surcharge =
        new AccessCheckingField<>(
            Supplier::getSurcharge, Supplier::setSurcharge, AccessCheckingField.INT_FORMER);
  }

  public ObjectForm<Supplier> getObjectForm() {
    return objectForm;
  }

  public void nameAlreadyExists() {
    JOptionPane.showMessageDialog(getTopComponent(), "Der gewählte Name ist schon vergeben");
  }

  public void shortNameAlreadyExists() {
    JOptionPane.showMessageDialog(getTopComponent(), "Die Abkürzung ist schon vergeben");
  }
}
