package kernbeisser.Forms.ObjectForm.Components;

import java.util.function.Consumer;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.ObjectForm.ObjectFormComponents.ObjectFormComponent;
import kernbeisser.Forms.ObjectForm.Properties.BoundedReadProperty;
import kernbeisser.Forms.ObjectForm.Properties.Predictable;
import kernbeisser.Security.Proxy;
import kernbeisser.Security.Utils.Getter;

public class DataListener<P, V>
    implements ObjectFormComponent<P>, BoundedReadProperty<P, V>, Predictable<P> {

  private final Getter<P, V> getter;
  private final Consumer<V> consumer;

  public DataListener(Getter<P, V> getter, Consumer<V> consumer) {
    this.getter = getter;
    this.consumer = consumer;
  }

  @Override
  public void setReadable(boolean v) {}

  @Override
  public boolean isPropertyReadable(P parent) {
    return Proxy.hasPermission(getter, parent);
  }

  @Override
  public boolean isPropertyWriteable(P parent) {
    return Proxy.hasPermission(getter, parent);
  }

  @Override
  public void setData(V v) {
    consumer.accept(v);
  }

  @Override
  public V get(P p) throws PermissionKeyRequiredException {
    return getter.get(p);
  }
}
