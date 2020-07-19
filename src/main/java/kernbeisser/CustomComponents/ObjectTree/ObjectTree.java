package kernbeisser.CustomComponents.ObjectTree;

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

public class ObjectTree<T> extends JTree {
  private final Collection<T> startValues;
  private ChildFactory<T> childFactory;
  private final String name;
  private final ArrayList<NodeSelectionListener<T>> selectionListeners = new ArrayList<>();

  public ObjectTree(ChildFactory<T> factory, String name, Collection<T> startValues) {
    this.startValues = startValues;
    this.childFactory = factory;
    this.name = name;
    refresh();
    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    addTreeSelectionListener(
        e -> {
          T t = getSelected();
          if (t != null) {
            for (NodeSelectionListener<T> listener : selectionListeners) {
              listener.select(t);
            }
          }
        });
  }

  public T getSelected() {
    Object o = getLastSelectedPathComponent();
    if (!(o instanceof ObjectNode)) {
      return null;
    }
    ObjectNode<T> node = (ObjectNode<T>) o;
    return node.getValue();
  }

  public void addSelectionListener(NodeSelectionListener<T> listener) {
    selectionListeners.add(listener);
  }

  public void refresh() {
    setModel(create(startValues));
  }

  private DefaultTreeModel create(Collection<T> x) {
    if (x.size() == 0) {
      return null;
    }
    ObjectNode<Void> main = new ObjectNode<>(null, name);
    for (T t : x) {
      main.add(extract(t));
    }
    return new DefaultTreeModel(main);
  }

  private ObjectNode<T> extract(T x) {
    ObjectNode<T> out = new ObjectNode<>(x, childFactory.getName(x));
    for (T t : childFactory.produce(x)) {
      out.add(extract(t));
    }
    return out;
  }

  public Collection<T> getStartValues() {
    return startValues;
  }

  public void setChildFactory(ChildFactory<T> childFactory) {
    this.childFactory = childFactory;
  }
}
