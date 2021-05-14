package kernbeisser.Windows.CollectionView;

import java.util.Collection;
import java.util.Optional;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
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

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  Optional<T> getSelectedChosenObject() {
    return chosen.getSelectedObject();
  }

  Optional<T> getSelectedAvailableObject() {
    return available.getSelectedObject();
  }

  void setColumns(Column<T>[] columns) {
    available.setColumns(columns);
    chosen.setColumns(columns);
  }

  void setAvailable(Collection<T> collection) {
    available.setObjects(collection);
  }

  private void createUIComponents() {
    available = new ObjectTable<T>();
    chosen = new ObjectTable<T>();
  }

  public void setChosen(Collection<T> loaded) {
    chosen.setObjects(loaded);
  }

  public CollectionView<T> asInjectedComponent() {
    actionBar.setVisible(false);
    return this;
  }

  public void messageSelectObjectFirst() {
    message("Bitte wähle zunächst ein Object aus");
  }
}
