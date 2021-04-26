package kernbeisser.Security.Utils;

import java.io.Serializable;

public interface AccessBiFunction<T1, T2, R> extends Serializable {
  R function(T1 t1, T2 t2) throws Throwable;
}
