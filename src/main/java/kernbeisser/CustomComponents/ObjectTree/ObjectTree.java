package kernbeisser.CustomComponents.ObjectTree;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;
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

  public void addSelectionChangeListener(Consumer<Node<T>> modelConsumer) {
    getSelectionModel()
        .addTreeSelectionListener(
            e -> {
              modelConsumer.accept(getSelected());
            });
  }

  public void selectionComponent(Component component) {
    component.setEnabled(!getSelectionModel().isSelectionEmpty());
    getSelectionModel()
        .addTreeSelectionListener(
            e -> component.setEnabled(!getSelectionModel().isSelectionEmpty()));
  }

  public void selectionComponents(Component... components) {
    for (Component component : components)
      component.setEnabled(!getSelectionModel().isSelectionEmpty());
    getSelectionModel()
        .addTreeSelectionListener(
            e -> {
              for (Component component : components)
                component.setEnabled(!getSelectionModel().isSelectionEmpty());
            });
  }
}
