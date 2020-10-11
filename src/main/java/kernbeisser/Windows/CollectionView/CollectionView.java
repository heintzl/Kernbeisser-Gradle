package kernbeisser.Windows.CollectionView;

import java.util.Collection;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectSelectionListener;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Windows.MVC.IView;
import org.apache.commons.collections4.IterableUtils;
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

  @Override
  public void initialize(CollectionController<T> controller) {
    available.addSelectionListener(
        new ObjectSelectionListener<T>() {
          T last;

          @Override
          public void selected(T e) {
            if (e.equals(last)) {
              controller.selectAvailable();
            } else {
              last = e;
            }
          }
        });
    chosen.addSelectionListener(
        new ObjectSelectionListener<T>() {
          T last;

          @Override
          public void selected(T e) {
            if (e.equals(last)) {
              controller.selectChosen();
            } else {
              last = e;
            }
          }
        });
    add.addActionListener(e -> controller.selectAvailable());
    addAll.addActionListener(e -> controller.selectAllAvailable());
    remove.addActionListener(e -> controller.selectChosen());
    removeAll.addActionListener(e -> controller.selectAllChosen());
    cancel.addActionListener(e -> back());
    commit.addActionListener(e -> back());
  }

  void setEditable(boolean editable) {
    availableSec.setVisible(editable);
    moveSec.setVisible(editable);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  T getSelectedChosenObject() {
    return chosen.getSelectedObject();
  }

  T getSelectedAvailableObject() {
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

  public Collection<T> getAllChosen() {
    return IterableUtils.toList(chosen);
  }
}
