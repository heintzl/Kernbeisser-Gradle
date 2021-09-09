package kernbeisser.Windows.CollectionView;

import java.awt.*;
import java.util.Collection;
import java.util.regex.Pattern;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ObjectTable.RegexFilter;
import kernbeisser.CustomComponents.ObjectTable.RowFilter;
import kernbeisser.Useful.DocumentChangeListener;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class CollectionView<T> implements IView<CollectionController<T>> {

  private JButton commit;
  private JButton cancel;
  private ObjectTable<T> available;
  private ObjectTable<T> chosen;
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
    addAll.addActionListener(e -> controller.selectAllAvailable());
    remove.addActionListener(e -> controller.selectChosen());
    removeAll.addActionListener(e -> controller.selectAllChosen());
    cancel.addActionListener(e -> back());
    commit.addActionListener(e -> controller.exitWithSave());
    searchPanelAvailable.setVisible(false);
    searchPanelChosen.setVisible(false);
  }

  void setEditable(boolean editable) {
    availableSec.setVisible(editable);
    moveSec.setVisible(editable);
  }

  public void addRowFilter(RowFilter<T> rowfilter, int scope) {
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
}
