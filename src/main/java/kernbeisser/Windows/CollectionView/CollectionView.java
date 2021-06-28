package kernbeisser.Windows.CollectionView;

import java.util.Collection;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.Searchable;
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
  @Linked private CollectionController<T> controller;

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
  }

  void setEditable(boolean editable) {
    availableSec.setVisible(editable);
    moveSec.setVisible(editable);
  }

  void addSearchbox(Searchable<T> searchable) {
    available.addSearchbox(searchable, searchPanelAvailable);
    chosen.addSearchbox(searchable, searchPanelChosen);
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

  public void clearSeachBox() {
    available.clearSearchBox();
    chosen.clearSearchBox();
  }

  public void addAdditionalControl(JComponent component) {
    additionalControls.setVisible(true);
    additionalControls.add(component);
  }
}
