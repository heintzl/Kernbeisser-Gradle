package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.Exeptions.CannotParseException;


public interface BoundedWriteProperty<P,V> extends Writeable<V>, Setter<P,V>{
  default void set(P p) throws CannotParseException { set(p,getData()); }
  void setPropertyEditable(boolean v);
  void setInvalidInput();
}
