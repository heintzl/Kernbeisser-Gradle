package kernbeisser.Forms.ObjectForm.Components;

import java.util.function.Consumer;
import kernbeisser.Forms.ObjectForm.ObjectFormComponents.ObjectFormComponent;
import kernbeisser.Forms.ObjectForm.Properties.BoundedReadProperty;
import kernbeisser.Forms.ObjectForm.Properties.PredictableModifiable;
import kernbeisser.Security.Utils.Getter;
import rs.groump.Access;
import rs.groump.AccessDeniedException;

public class DataListener<P, V>
    implements ObjectFormComponent<P>, BoundedReadProperty<P, V>, PredictableModifiable<P> {

  private final Getter<P, V> getter;
  private final Consumer<V> consumer;

  public DataListener(Getter<P, V> getter, Consumer<V> consumer) {
    this.getter = getter;
    this.consumer = consumer;
  }

  @Override
  public void setReadable(boolean v) {}

  @Override
  public boolean isPropertyModifiable(P parent) {
    return Access.hasPermission(getter, parent);
  }

  @Override
  public void setData(V v) {
    consumer.accept(v);
  }

  @Override
  public V get(P p) throws AccessDeniedException {
    return getter.get(p);
  }
}
