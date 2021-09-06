package kernbeisser.Windows.EditSurchargeGroups;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.Insets;
import java.util.Collection;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
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

  @Getter
  private ObjectForm<SurchargeGroup> objectForm;

  @Linked
  private EditSurchargeGroupController controller;

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
    main.setLayout(new GridLayoutManager(1, 2, new Insets(2, 2, 2, 2), -1, -1));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
    main.add(panel1,
        new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
            null, 0, false));
    final JScrollPane scrollPane1 = new JScrollPane();
    panel1.add(scrollPane1,
        new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null,
            null, null, 0, false));
    scrollPane1.setViewportView(objectTree);
    supplier = new JComboBox();
    panel1.add(supplier, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST,
        GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JLabel label1 = new JLabel();
    label1.setText("Lieferant");
    panel1.add(label1,
        new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
            false));
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
    main.add(panel2,
        new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
            null, 0, false));
    commit = new JButton();
    commit.setText("Fertig");
    panel2.add(commit, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer1 = new Spacer();
    panel2.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null,
        0, false));
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridLayoutManager(8, 2, new Insets(0, 0, 0, 0), -1, -1));
    panel2.add(panel3,
        new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
            null, 0, false));
    final JLabel label2 = new JLabel();
    label2.setText("Name");
    panel3.add(label2,
        new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
            false));
    panel3.add(name, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
        null, 0, false));
    panel3.add(superGroup, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
        null, 0, false));
    final JLabel label3 = new JLabel();
    label3.setText("Übergruppe");
    panel3.add(label3,
        new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
            false));
    final JLabel label4 = new JLabel();
    label4.setText("Zuschlag");
    panel3.add(label4,
        new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
            false));
    panel3.add(surcharge, new GridConstraints(5, 0, 1, 2, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
        null, 0, false));
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
    panel3.add(panel4,
        new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null,
            null, 0, false));
    final Spacer spacer2 = new Spacer();
    panel4.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null,
        0, false));
    edit = new JButton();
    edit.setText("Bearbeiten");
    panel4.add(edit, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    add = new JButton();
    add.setText("Hinzufügen");
    panel4.add(add, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    delete = new JButton();
    delete.setText("Löschen");
    panel4.add(delete, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    calculateSurcharge = new JButton();
    calculateSurcharge.setEnabled(true);
    calculateSurcharge.setText("Aufschläge ermitteln");
    calculateSurcharge.setVisible(false);
    panel4.add(calculateSurcharge, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    selectNoParent = new JButton();
    selectNoParent.setText("Keine Übergruppe");
    panel3.add(selectNoParent, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JScrollPane scrollPane2 = new JScrollPane();
    panel3.add(scrollPane2,
        new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null,
            null, null, 0, false));
    scrollPane2.setViewportView(surchargeGroupArticles);
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return main;
  }
}
