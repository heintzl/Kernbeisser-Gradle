package kernbeisser.Forms.ObjectForm.Components;

import java.util.function.Supplier;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Forms.ObjectForm.ObjectFormComponents.ObjectFormComponent;
import kernbeisser.Forms.ObjectForm.Properties.BoundedWriteProperty;
import kernbeisser.Forms.ObjectForm.Properties.PredictableModifiable;
import kernbeisser.Security.Utils.Setter;
import rs.groump.AccessDeniedException;

public class DataAnchor<P, V>
    implements ObjectFormComponent<P>, BoundedWriteProperty<P, V>, PredictableModifiable<P> {

  private final Setter<P, V> setter;
  private final Supplier<V> valueSupplier;

  public DataAnchor(Setter<P, V> setter, Supplier<V> valueSupplier) {
    this.setter = setter;
    this.valueSupplier = valueSupplier;
  }

  @Override
  public void setPropertyModifiable(boolean v) {}

  @Override
  public void setInvalidInput() {}

  @Override
  public V getData() throws CannotParseException {
    return valueSupplier.get();
  }

  @Override
  public void set(P p, V t) throws AccessDeniedException {
    setter.set(p, t);
  }

  @Override
  public boolean isPropertyModifiable(P parent) {
    return true;
  }
}
