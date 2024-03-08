package kernbeisser.Forms.ObjectForm.Properties;

import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;

public interface Writeable<V> {
  V getData() throws CannotParseException;
}
