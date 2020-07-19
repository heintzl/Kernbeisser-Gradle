package kernbeisser.CustomComponents.ObjectTree;

import javax.swing.tree.DefaultMutableTreeNode;

public class ObjectNode<T> extends DefaultMutableTreeNode {
  private final T value;

  ObjectNode(T value, String name) {
    super(name);
    this.value = value;
  }

  public T getValue() {
    return value;
  }
}
