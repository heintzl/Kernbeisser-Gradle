package kernbeisser.Windows.CollectionView;

import java.util.Collection;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Security.IterableProtection.ProtectedIterable;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;

public class CollectionModel<T> implements IModel<CollectionController<T>> {

  @Getter private final Collection<T> loaded;
  private final Source<T> available;
  @Getter private final Column<T>[] columns;
  @Getter private final boolean modifiable;
  @Getter private final boolean readable;

  public CollectionModel(Collection<T> loaded, Source<T> available, Column<T>[] columns) {
    this.columns = columns;
    this.loaded = loaded;
    this.available = available;
    if (loaded instanceof ProtectedIterable) {
      modifiable = ((ProtectedIterable) loaded).isModifiable();
      readable = ((ProtectedIterable) loaded).isReadable();
    } else {
      modifiable = true;
      readable = true;
    }
  }

  public Collection<T> getSource() {
    return available.query();
  }
}
