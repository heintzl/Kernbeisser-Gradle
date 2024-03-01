package kernbeisser.CustomComponents.ObjectTree;

import java.util.List;
import java.util.function.Function;
import kernbeisser.Useful.Tools;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CachedNode<T> implements Node<T> {

  private final Node<T> parent;
  private final Function<T, List<T>> childFactory;
  private final T value;
  private List<Node<T>> nodes;

  @Override
  public List<Node<T>> getNodes() {
    if (nodes == null) {
      nodes = Tools.transform(childFactory.apply(value), this::asNode);
    }
    return nodes;
  }

  public CachedNode(@Nullable Node<T> parent, @NotNull T value, Function<T, List<T>> childFactory) {
    this.parent = parent;
    this.childFactory = childFactory;
    this.value = value;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public T getValue() {
    return value;
  }

  @Override
  public Node<? extends T> getParent() {
    return parent;
  }

  private Node<T> asNode(T value) {
    return new CachedNode<>(this, value, childFactory);
  }
}
