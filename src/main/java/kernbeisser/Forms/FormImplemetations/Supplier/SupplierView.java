package kernbeisser.Forms.FormImplemetations.Supplier;

import javax.swing.*;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingField;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class SupplierView implements IView<SupplierController> {

  @Linked private SupplierController controller;

  private JPanel main;
  private AccessCheckingField<Supplier, String> location;
  private AccessCheckingField<Supplier, String> keeper;
  private AccessCheckingField<Supplier, String> phoneNumber;
  private AccessCheckingField<Supplier, String> fax;
  private AccessCheckingField<Supplier, String> name;
  private AccessCheckingField<Supplier, String> shortName;
  private AccessCheckingField<Supplier, Double> surcharge;
  private AccessCheckingField<Supplier, String> street;
  private AccessCheckingField<Supplier, String> email;

  @Getter private ObjectForm<Supplier> objectForm;

  @Override
  public void initialize(SupplierController controller) {
    objectForm =
        new ObjectForm<>(
            name, street, location, keeper, phoneNumber, fax, email, shortName, surcharge);
    objectForm.setObjectDistinction("Der Lieferant");
    objectForm.registerUniqueCheck(
        shortName, controller::isShortNameUnique, this::shortNameAlreadyExists);
    objectForm.registerUniqueCheck(name, controller::isNameUnique, this::nameAlreadyExists);
    objectForm.registerObjectValidator(controller::confirmSurcharge);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    street =
        new AccessCheckingField<>(
            Supplier::getStreet, Supplier::setStreet, AccessCheckingField.NONE);
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
        new AccessCheckingField<>(
            Supplier::getName, Supplier::setName, AccessCheckingField.NOT_NULL);
    shortName =
        new AccessCheckingField<>(
            Supplier::getShortName, Supplier::setShortName, AccessCheckingField.NOT_NULL);
    surcharge =
        new AccessCheckingField<>(
            (e) -> e.getDefaultSurcharge() * 100,
            (e, v) -> e.setDefaultSurcharge(v / 100),
            AccessCheckingField.DOUBLE_FORMER);
    email =
        new AccessCheckingField<>(
            Supplier::getEmail, Supplier::setEmail, AccessCheckingField.EMAIL_FORMER);
  }

  public void nameAlreadyExists() {
    JOptionPane.showMessageDialog(getTopComponent(), "Der gewählte Name ist schon vergeben.");
  }

  public void shortNameAlreadyExists() {
    JOptionPane.showMessageDialog(getTopComponent(), "Die Abkürzung ist schon vergeben.");
  }

  public boolean messageConfirmSurcharge(double defaultSurcharge) {
    return JOptionPane.showConfirmDialog(
            getContent(),
            String.format(
                "Ist der eingegebene Standartzuschlag von %.2f%% korrekt?", defaultSurcharge * 100),
            "Komischer Zuschlag!",
            JOptionPane.DEFAULT_OPTION)
        == 0;
  }

  public void messageSurchargeNotValid() {
    message("Der Zuschlag ist nicht zwischen 0 - 100%", "Zuschlag ist nicht korrekt!");
  }

  public void messageSelectSupplierFirst() {
    message(
        "Bitte wähle zuerst einen Lieferanten aus, der gelöscht werden soll!",
        "Kein Lieferanten ausgewählt");
  }

  public void messageConstraintViolation() {
    message(
        "Der Lieferant ist noch Artikeln zugewiesen und kann daher nicht gelöscht werden!",
        "Lieferant wird noch verwendet.");
  }
}
