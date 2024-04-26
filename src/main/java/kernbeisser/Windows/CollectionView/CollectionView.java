package kernbeisser.Windows.CollectionView;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.util.Collection;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ObjectTable.RegexFilter;
import kernbeisser.CustomComponents.ObjectTable.RowFilter;
import kernbeisser.Useful.DocumentChangeListener;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class CollectionView<T> implements IView<CollectionController<T>> {

  private JButton commit;
  private JButton cancel;
  @Getter private ObjectTable<T> available;
  @Getter private ObjectTable<T> chosen;
  private JPanel main;
  private JButton add;
  private JButton addAll;
  private JButton removeAll;
  private JButton remove;
  private JPanel moveSec;
  private JScrollPane availableSec;
  private JPanel actionBar;
  private JPanel searchPanelChosen;
  private JPanel searchPanelAvailable;
  private JPanel additionalControls;
  private JTextField searchAvailable;
  private JTextField searchChosen;
  private JLabel iconAvailable;
  private JLabel iconChosen;
  @Linked private CollectionController<T> controller;
  public static final int NONE = 0;
  public static final int AVAILABLE = 1;
  public static final int CHOSEN = 2;
  public static final int BOTH = 3;

  @Override
  public void initialize(CollectionController<T> controller) {
    available.addDoubleClickListener(e -> controller.selectAvailable());
    chosen.addDoubleClickListener(e -> controller.selectChosen());
    add.addActionListener(e -> controller.selectAvailable());
    add.setToolTipText("markierte von rechts nach links");
    addAll.addActionListener(e -> controller.selectAllAvailable());
    addAll.setToolTipText("alle gefilterten von rechts nach links");
    remove.addActionListener(e -> controller.selectChosen());
    remove.setToolTipText("markierte von links nach rechts");
    removeAll.addActionListener(e -> controller.selectAllChosen());
    removeAll.setToolTipText(" alle gefilterten von links nach rechts");
    cancel.addActionListener(e -> back());
    commit.addActionListener(e -> controller.exitWithSave());
    searchPanelAvailable.setVisible(false);
    searchPanelChosen.setVisible(false);
  }

  void setEditable(boolean editable) {
    availableSec.setVisible(editable);
    moveSec.setVisible(editable);
  }

  public void setRowFilter(RowFilter<T> rowfilter, int scope) {
    if ((scope & 1) == 1) {
      available.setRowFilter(rowfilter);
    }
    if ((scope & 2) == 2) {
      chosen.setRowFilter(rowfilter);
    }
  }

  public void addSearchbox(int scope) {
    Icon searchIcon = IconFontSwing.buildIcon(FontAwesome.SEARCH, 15, new Color(0x757EFF));
    iconAvailable.setIcon(searchIcon);
    iconChosen.setIcon(searchIcon);
    if ((scope & 1) == 1) {
      addSearchbox(searchAvailable, available);
    }
    if ((scope & 2) == 2) {
      addSearchbox(searchChosen, chosen);
    }
  }

  private void addSearchbox(JTextField textField, ObjectTable<T> objectTable) {
    textField.getParent().setVisible(true);
    textField
        .getDocument()
        .addDocumentListener(
            (DocumentChangeListener)
                e ->
                    objectTable.setSwingRowFilter(
                        new RegexFilter(
                            Pattern.compile(textField.getText(), Pattern.CASE_INSENSITIVE))));
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  Collection<T> getSelectedChosenObjects() {
    return chosen.getSelectedObjects();
  }

  Collection<T> getSelectedAvailableObjects() {
    return available.getSelectedObjects();
  }

  Collection<T> getAllChosenObjects() {
    return chosen.getFilteredObjects();
  }

  Collection<T> getAllAvailableObjects() {
    return available.getFilteredObjects();
  }

  void setColumns(Column<T>[] columns) {
    available.setColumns(columns);
    chosen.setColumns(columns);
  }

  void setAvailable(Collection<T> collection) {
    available.setObjects(collection);
  }

  private void createUIComponents() {
    available = new ObjectTable<>();
    chosen = new ObjectTable<>();
  }

  public void setChosen(Collection<T> loaded) {
    chosen.setObjects(loaded);
  }

  public CollectionView<T> asInjectedComponent() {
    actionBar.setVisible(false);
    return this;
  }

  boolean confirmCancel() {
    return JOptionPane.showConfirmDialog(
            getContent(),
            "Soll die Eingabe beendet werden? Alle Änderungen werden verworfen.",
            "Eingabe abbrechen",
            JOptionPane.OK_CANCEL_OPTION)
        == JOptionPane.OK_OPTION;
  }

  public void clearSearchBox() {
    searchAvailable.setText("");
    searchChosen.setText("");
  }

  public void addAdditionalControl(JComponent component) {
    additionalControls.setVisible(true);
    additionalControls.add(component);
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
    main.setLayout(new GridLayoutManager(3, 3, new Insets(5, 5, 5, 5), -1, -1));
    availableSec = new JScrollPane();
    main.add(
        availableSec,
        new GridConstraints(
            1,
            2,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    availableSec.setBorder(
        BorderFactory.createTitledBorder(
            null,
            "Verfügbar",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            null,
            null));
    availableSec.setViewportView(available);
    final JScrollPane scrollPane1 = new JScrollPane();
    main.add(
        scrollPane1,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    scrollPane1.setBorder(
        BorderFactory.createTitledBorder(
            null,
            "Ausgewählt",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            null,
            null));
    scrollPane1.setViewportView(chosen);
    actionBar = new JPanel();
    actionBar.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
    main.add(
        actionBar,
        new GridConstraints(
            2,
            0,
            1,
            3,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    commit = new JButton();
    commit.setText("Speichern");
    actionBar.add(
        commit,
        new GridConstraints(
            0,
            3,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer1 = new Spacer();
    actionBar.add(
        spacer1,
        new GridConstraints(
            0,
            2,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            1,
            null,
            null,
            null,
            0,
            false));
    cancel = new JButton();
    cancel.setText("Abbrechen");
    actionBar.add(
        cancel,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    additionalControls = new JPanel();
    additionalControls.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    additionalControls.setVisible(false);
    actionBar.add(
        additionalControls,
        new GridConstraints(
            0,
            1,
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
    moveSec = new JPanel();
    moveSec.setLayout(new GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
    main.add(
        moveSec,
        new GridConstraints(
            1,
            1,
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
    remove = new JButton();
    remove.setText(">");
    moveSec.add(
        remove,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer2 = new Spacer();
    moveSec.add(
        spacer2,
        new GridConstraints(
            5,
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
    add = new JButton();
    add.setText("<");
    moveSec.add(
        add,
        new GridConstraints(
            2,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    addAll = new JButton();
    addAll.setText("<<");
    moveSec.add(
        addAll,
        new GridConstraints(
            3,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    removeAll = new JButton();
    removeAll.setText(">>");
    moveSec.add(
        removeAll,
        new GridConstraints(
            4,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer3 = new Spacer();
    moveSec.add(
        spacer3,
        new GridConstraints(
            0,
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
    searchPanelChosen = new JPanel();
    searchPanelChosen.setLayout(new GridLayoutManager(1, 2, new Insets(0, 2, 0, 5), -1, -1));
    searchPanelChosen.setVisible(true);
    main.add(
        searchPanelChosen,
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
    searchChosen = new JTextField();
    searchPanelChosen.add(
        searchChosen,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            new Dimension(150, -1),
            null,
            0,
            false));
    iconChosen = new JLabel();
    iconChosen.setInheritsPopupMenu(true);
    iconChosen.setRequestFocusEnabled(false);
    iconChosen.setText("");
    searchPanelChosen.add(
        iconChosen,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_EAST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    searchPanelAvailable = new JPanel();
    searchPanelAvailable.setLayout(new GridLayoutManager(1, 2, new Insets(0, 2, 0, 5), -1, -1));
    searchPanelAvailable.setVisible(true);
    main.add(
        searchPanelAvailable,
        new GridConstraints(
            0,
            2,
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
    searchAvailable = new JTextField();
    searchPanelAvailable.add(
        searchAvailable,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    iconAvailable = new JLabel();
    iconAvailable.setText("");
    searchPanelAvailable.add(
        iconAvailable,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_EAST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    iconChosen.setLabelFor(searchChosen);
  }

  /** @noinspection ALL */
  public JComponent $$$getRootComponent$$$() {
    return main;
  }
}
