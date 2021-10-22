package kernbeisser.Windows.EditSurchargeGroups;

import java.awt.*;
import java.util.Collection;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.CustomComponents.ObjectTree.ObjectTree;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Enums.Setting;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingComboBox;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingField;
import kernbeisser.Forms.ObjectForm.Components.DataAnchor;
import kernbeisser.Forms.ObjectForm.Components.DataListener;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Security.StaticMethodTransformer.StaticAccessPoint;
import kernbeisser.Useful.DocumentChangeListener;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
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
  private JButton selectNoParent;
  private JButton calculateSurcharge;
  private ObjectTable<Article> surchargeGroupArticles;

  @Getter private ObjectForm<SurchargeGroup> objectForm;

  @Linked private EditSurchargeGroupController controller;

  @Override
  public void initialize(EditSurchargeGroupController controller) {
    supplier.addActionListener(e -> controller.loadForCurrentSupplier());
    objectForm =
        new ObjectForm<>(
            name,
            superGroup,
            surcharge,
            new DataAnchor<>(
                SurchargeGroup::setSupplier, () -> (Supplier) supplier.getSelectedItem()),
            new DataListener<>(SurchargeGroup::getArticles, surchargeGroupArticles::setObjects));
    objectForm.setObjectDistinction("Die Zuschlagsgruppe");
    objectForm.registerObjectValidator(controller::validate);
    selectNoParent.addActionListener(e -> superGroup.setSelectedItem(null));
    add.addActionListener(e -> controller.addSurchargeGroup());
    edit.addActionListener(e -> controller.editSurchargeGroup());
    delete.addActionListener(e -> controller.removeSurchargeGroup());
    name.getDocument()
        .addDocumentListener((DocumentChangeListener) e1 -> controller.surchargeGroupChanged());
    surcharge
        .getDocument()
        .addDocumentListener((DocumentChangeListener) e1 -> controller.surchargeGroupChanged());
    edit.setIcon(
        IconFontSwing.buildIcon(
            FontAwesome.PENCIL, Tools.scaleWithLabelScalingFactor(14), new Color(0x0095FF)));
    superGroup.addActionListener(e -> controller.surchargeGroupChanged());
    commit.addActionListener(e -> back());
    superGroup.setAllowNull(true);
    objectTree.addSelectionListener(controller::surchargeGroupSelected);
    if (Setting.IS_DEFAULT_SURCHARGES.getBooleanValue()) {
      calculateSurcharge.setVisible(true);
      calculateSurcharge.addActionListener(e -> controller.calculateSurchargeValues());
    }
  }

  void setAllSuperGroups(Collection<SurchargeGroup> superGroups) {
    superGroup.removeAllItems();
    superGroups.forEach(superGroup::addItem);
  }

  void setSurchargeGroups(Node<SurchargeGroup> surchargeGroups) {
    objectTree.load(surchargeGroups);
  }

  void setSuppliers(Iterable<Supplier> suppliers) {
    supplier.removeAllItems();
    suppliers.forEach(supplier::addItem);
  }

  void setSupplier(Supplier sup) {
    supplier.setSelectedItem(sup);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public String getTitle() {
    return "Zuschlagsgruppen bearbeiten";
  }

  @Override
  @StaticAccessPoint
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
    superGroup =
        new AccessCheckingComboBox<>(
            SurchargeGroup::getParent, SurchargeGroup::setParent, controller::getSurchargeGroups);
    surchargeGroupArticles =
        new ObjectTable<>(
            Columns.create("Artikelname", Article::getName),
            Columns.create("Kbnr.", Article::getKbNumber),
            Columns.create("Lieferantennr.", Article::getSuppliersItemNumber));
  }

  public Supplier getSelectedSupplier() {
    return (Supplier) supplier.getSelectedItem();
  }

  public void constraintViolationException() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Die Zuschlagsgruppe kann nicht gelöscht werden,\nda noch Artikel auf sie verweisen!");
  }

  public boolean shouldBecomeAutoLinked() {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Sollen die Artikel automatisch einer anderen Zuschlagsgruppe zugeteilt werden um anschließend den Vorgang zu wiederholen?")
        == 0;
  }

  boolean shouldDelete() {
    return JOptionPane.showConfirmDialog(
            getTopComponent(), "Soll die Zuschlagsgruppe wirklich gelöscht werden?")
        == 0;
  }

  public void setEditAvailable(boolean b) {
    edit.setEnabled(b);
  }
}
