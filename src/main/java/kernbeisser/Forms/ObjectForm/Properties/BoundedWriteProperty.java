package kernbeisser.Forms.ObjectForm.Properties;

import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Security.Utils.Setter;

public interface BoundedWriteProperty<P, V> extends Writeable<V>, Setter<P, V> {
  default void set(P p) throws CannotParseException {
    set(p, getData());
  }

  void setPropertyEditable(boolean v);

  void setInvalidInput();
}
