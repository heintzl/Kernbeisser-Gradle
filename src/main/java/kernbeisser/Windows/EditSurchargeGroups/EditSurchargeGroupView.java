package kernbeisser.Windows.EditSurchargeGroups;

import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingComboBox;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingField;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingLabel;
import kernbeisser.CustomComponents.AccessChecking.ObjectForm;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.CustomComponents.ObjectTree.ObjectTree;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Windows.MVC.IView;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class EditSurchargeGroupView implements IView<EditSurchargeGroupController> {

  private JPanel main;
  private ObjectTree<SurchargeGroup> objectTree;
  private JComboBox<Supplier> supplier;
  private JButton commit;
  private AccessCheckingField<SurchargeGroup, String> name;
  private AccessCheckingComboBox<SurchargeGroup, SurchargeGroup> superGroup;
  private AccessCheckingField<SurchargeGroup, Double> surcharge;
  private JButton delete;
  private JButton edit;
  private JButton add;
  private AccessCheckingComboBox<SurchargeGroup, Supplier> surchargeGroupSupplier;
  private JButton selectNoParent;
  private AccessCheckingLabel<SurchargeGroup> exactSurcharge;

  @Getter private ObjectForm<SurchargeGroup> objectForm;

  @Override
  public void initialize(EditSurchargeGroupController controller) {
    supplier.addActionListener(e -> controller.loadForCurrentSupplier());
    objectForm = new ObjectForm<>(name, superGroup, surcharge);
    selectNoParent.addActionListener(e -> superGroup.setSelectedItem(null));
    add.addActionListener(e -> controller.addSurchargeGroup());
    edit.addActionListener(e -> controller.editSurchargeGroup());
    delete.addActionListener(e -> controller.removeSurchargeGroup());
    commit.addActionListener(e -> back());
    superGroup.setAllowNull(true);
    objectTree.addSelectionListener(controller::surchargeGroupSelected);
  }

  void setAllSuperGroups(Collection<SurchargeGroup> superGroups) {
    superGroup.removeAllItems();
    superGroups.forEach(superGroup::addItem);
  }

  void setSurchargeGroups(Node<SurchargeGroup> surchargeGroups) {
    objectTree.load(surchargeGroups);
  }

  void setSuppliers(Iterable<Supplier> suppliers) {
    surchargeGroupSupplier.removeAllItems();
    supplier.removeAllItems();
    suppliers.forEach(
        e -> {
          supplier.addItem(e);
          surchargeGroupSupplier.addItem(e);
        });
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public String getTitle() {
    return "Zuschlagesgruppen bearbeiten";
  }

  @Override
  public IconCode getTabIcon() {
    return FontAwesome.PERCENT;
  }

  private void createUIComponents() {
    objectTree = new ObjectTree<>();
    name =
        new AccessCheckingField<>(
            SurchargeGroup::getName, SurchargeGroup::setName, AccessCheckingField.NOT_NULL);
    surcharge =
        new AccessCheckingField<>(
            e -> e.getSurcharge() * 100,
            (e, v) -> e.setSurcharge(v / 100),
            AccessCheckingField.DOUBLE_FORMER);
    superGroup = new AccessCheckingComboBox<>(SurchargeGroup::getParent, SurchargeGroup::setParent);
    surchargeGroupSupplier =
        new AccessCheckingComboBox<>(SurchargeGroup::getSupplier, SurchargeGroup::setSupplier);
  }

  public Supplier getSelectedSupplier() {
    return (Supplier) supplier.getSelectedItem();
  }

  public void constraintViolationException() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Die Zuschlagsgruppe kann nicht gelöscht werden,\nda noch Artikel auf sie verweissen!");
  }

  boolean shouldDelete() {
    return JOptionPane.showConfirmDialog(
            getTopComponent(), "Soll die Zuschlagsgruppe wirklich gelöscht werde?")
        == 0;
  }
}
