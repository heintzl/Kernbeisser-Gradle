package kernbeisser.Windows.CollectionView;

import java.util.Collection;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Security.Access.Access;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;
import lombok.Setter;

public class CollectionModel<T> implements IModel<CollectionController<T>> {

  @Getter @Setter private Collection<T> loaded;
  @Setter private Source<T> source;
  @Getter private final Column<T>[] columns;

  public CollectionModel(Collection<T> loaded, Source<T> source, Column<T>[] columns) {
    this.columns = columns;
    this.loaded = loaded;
    this.source = source;
  }

  public boolean isModifiable() {
    return Access.isIterableModifiable(loaded);
  }

  public Collection<T> getSource() {
    return source.query();
  }
}
