package kernbeisser.Forms.ObjectForm.Properties;

import kernbeisser.Security.Utils.Getter;

public interface BoundedReadProperty<P, V> extends Readable<V>, Getter<P, V> {
  default void setValue(P p) {
    setData(get(p));
  }

  void setReadable(boolean v);
}
