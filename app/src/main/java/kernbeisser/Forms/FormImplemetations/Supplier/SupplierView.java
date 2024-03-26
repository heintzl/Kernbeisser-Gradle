package kernbeisser.Forms.FormImplemetations.Supplier;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
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

  {
    // GUI initializer generated by IntelliJ IDEA GUI Designer
    // >>> IMPORTANT!! <<<
    // DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR
   * call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    createUIComponents();
    main = new JPanel();
    main.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    main.add(
        panel1,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
    panel1.add(
        panel2,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    panel2.setBorder(
        BorderFactory.createTitledBorder(
            null,
            "Info",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            null,
            null));
    panel2.add(
        name,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer1 = new Spacer();
    panel2.add(
        spacer1,
        new GridConstraints(
            6,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_VERTICAL,
            1,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    final JLabel label1 = new JLabel();
    label1.setText("Name");
    panel2.add(
        label1,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label2 = new JLabel();
    label2.setText("Kurzname");
    panel2.add(
        label2,
        new GridConstraints(
            2,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    panel2.add(
        shortName,
        new GridConstraints(
            3,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JLabel label3 = new JLabel();
    label3.setText("Zuschlag[%]");
    panel2.add(
        label3,
        new GridConstraints(
            4,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    panel2.add(
        surcharge,
        new GridConstraints(
            5,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridLayoutManager(13, 1, new Insets(0, 0, 0, 0), -1, -1));
    panel1.add(
        panel3,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    panel3.setBorder(
        BorderFactory.createTitledBorder(
            null,
            "Kontakt",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            null,
            null));
    panel3.add(
        location,
        new GridConstraints(
            3,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer2 = new Spacer();
    panel3.add(
        spacer2,
        new GridConstraints(
            12,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_VERTICAL,
            1,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    final JLabel label4 = new JLabel();
    label4.setText("Straße");
    panel3.add(
        label4,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label5 = new JLabel();
    label5.setText("Betreuer");
    panel3.add(
        label5,
        new GridConstraints(
            4,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    panel3.add(
        keeper,
        new GridConstraints(
            5,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JLabel label6 = new JLabel();
    label6.setText("Telefonnummer");
    panel3.add(
        label6,
        new GridConstraints(
            6,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    panel3.add(
        phoneNumber,
        new GridConstraints(
            7,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JLabel label7 = new JLabel();
    label7.setText("Fax");
    panel3.add(
        label7,
        new GridConstraints(
            10,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    panel3.add(
        fax,
        new GridConstraints(
            11,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    panel3.add(
        street,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JLabel label8 = new JLabel();
    label8.setText("Ort");
    panel3.add(
        label8,
        new GridConstraints(
            2,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    panel3.add(
        email,
        new GridConstraints(
            9,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JLabel label9 = new JLabel();
    label9.setText("E-Mail");
    panel3.add(
        label9,
        new GridConstraints(
            8,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return main;
  }
}