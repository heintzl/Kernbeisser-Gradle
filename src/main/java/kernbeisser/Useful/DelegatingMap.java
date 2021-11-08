package kernbeisser.Useful;

import java.util.Map;
import lombok.experimental.Delegate;

public abstract class DelegatingMap<K, V> implements Map<K, V> {
  @Delegate private final Map<K, V> target;

  protected DelegatingMap(Map<K, V> target) {
    this.target = target;
  }
}
