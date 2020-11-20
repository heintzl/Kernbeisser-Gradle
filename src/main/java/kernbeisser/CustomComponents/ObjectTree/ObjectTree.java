package kernbeisser.CustomComponents.ObjectTree;

import java.util.ArrayList;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

public class ObjectTree<T> extends JTree {
  private Node<T> startValue;
  private final ArrayList<NodeSelectionListener<T>> selectionListeners = new ArrayList<>();

  {
    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    addTreeSelectionListener(
        e -> {
          Node<T> t = getSelected();
          if (t != null) {
            for (NodeSelectionListener<T> listener : selectionListeners) {
              listener.select(t);
            }
          }
        });
  }

  public void load(Node<T> startValue) {
    this.startValue = startValue;
    refresh();
  }

  public ObjectTree(Node<T> startValue) {
    load(startValue);
  }

  public ObjectTree() {}

  public void setStartValue(Node<T> startValue) {
    this.startValue = startValue;
  }

  public Node<T> getSelected() {
    return (Node<T>) getLastSelectedPathComponent();
  }

  public void addSelectionListener(NodeSelectionListener<T> listener) {
    selectionListeners.add(listener);
  }

  public void refresh() {
    setModel(create(startValue));
  }

  private DefaultTreeModel create(Node<T> x) {
    return new DefaultTreeModel(x);
  }
}
