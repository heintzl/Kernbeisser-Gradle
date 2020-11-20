package kernbeisser.CustomComponents.ObjectTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.tree.TreeNode;
import kernbeisser.Useful.Tools;

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

  static <T> Node<T> asNode(T start, Map<T, Collection<T>> map) {
    return new Node<T>() {
      @Override
      public T getValue() {
        return start;
      }

      @Override
      public List<Node<T>> getNodes() {
        Collection<T> result = map.get(start);
        return result == null
            ? new ArrayList<>()
            : new ArrayList<>(Tools.transform(result, e -> asNode(e, map)));
      }

      @Override
      public Node<? extends T> getParent() {
        AtomicReference<Node<T>> out = new AtomicReference<>();
        map.forEach(
            (v, k) -> {
              if (out.get() != null) return;
              if (k.contains(start)) out.set(asNode(v, map));
            });
        return out.get();
      }

      @Override
      public String toString() {
        return start.toString();
      }
    };
  }

  static <T> Collection<Node<T>> createMappingNode(
      Collection<T> collection, Function<T, T> parent) {
    Map<T, Collection<T>> childrenMap = Tools.reverseReference(Tools.createMap(collection, parent));
    return collection.stream()
        .filter(e -> parent.apply(e) == null)
        .map(e -> Node.asNode(e, childrenMap))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  static <T> Node<T> createHead(T head, List<Node<T>> collection) {
    return new Node<T>() {
      @Override
      public T getValue() {
        return head;
      }

      @Override
      public List<Node<T>> getNodes() {
        return collection;
      }

      @Override
      public Node<? extends T> getParent() {
        return null;
      }

      @Override
      public String toString() {
        return head.toString();
      }
    };
  }
}
