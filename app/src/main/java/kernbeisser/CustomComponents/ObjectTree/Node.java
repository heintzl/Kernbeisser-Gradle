package kernbeisser.CustomComponents.ObjectTree;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;

public interface Node<T> extends TreeNode {
  T getValue();

  List<Node<T>> getNodes();

  Node<? extends T> getParent();

  @Override
  default TreeNode getChildAt(int childIndex) {
    return getNodes().get(childIndex);
  }

  @Override
  default int getChildCount() {
    return getNodes().size();
  }

  @Override
  default int getIndex(TreeNode node) {
    if (!(node instanceof Node)) return -1;
    return getNodes().indexOf(node);
  }

  @Override
  default boolean getAllowsChildren() {
    return true;
  }

  @Override
  default boolean isLeaf() {
    return getChildCount() == 0;
  }

  @Override
  default Enumeration<Node<T>> children() {
    return Collections.enumeration(getNodes());
  }
}
