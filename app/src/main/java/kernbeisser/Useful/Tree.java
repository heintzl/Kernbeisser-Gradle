package kernbeisser.Useful;

import java.util.HashMap;
import java.util.function.BiConsumer;

public class Tree<K> extends HashMap<K, Tree<K>> {

  @SafeVarargs
  private final void put(int offset, K... v) {
    Tree<K> tree = get(v[offset]);
    if (tree == null) {
      tree = new Tree<>();
      put(v[offset], tree);
    }
    if (offset < v.length - 1) {
      tree.put(offset + 1, v);
    }
  }

  public final void put(K... v) {
    put(0, v);
  }

  private void overAll(K parent, K key, BiConsumer<K, K> consumer) {
    consumer.accept(parent, key);
    forEach((k, v) -> v.overAll(key, k, consumer));
  }

  public void overAll(K key, BiConsumer<K, K> consumer) {
    forEach(
        (k, v) -> {
          v.overAll(key, k, consumer);
        });
  }
}
