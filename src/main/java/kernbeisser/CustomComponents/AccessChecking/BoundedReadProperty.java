package kernbeisser.CustomComponents.AccessChecking;

public interface BoundedReadProperty<P,V> extends Readable<V>, Getter<P,V>{
  default void setValue(P p)  { setData(get(p)); }
  void setReadable(boolean v);
}
