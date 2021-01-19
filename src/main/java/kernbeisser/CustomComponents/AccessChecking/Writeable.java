package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.Exeptions.CannotParseException;

public interface Writeable<V> {
  V getData() throws CannotParseException;
}
