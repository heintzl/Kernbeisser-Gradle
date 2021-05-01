package kernbeisser.Security.Utils;

import java.io.Serializable;

public interface AccessBiConsumer<T1, T2> extends Serializable {
  void function(T1 t1, T2 t2) throws Throwable;

  static Serializable wrap(AccessBiConsumer<?, ?> accessBiConsumer) {
    return accessBiConsumer;
  }
}
