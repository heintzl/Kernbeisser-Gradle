package kernbeisser.Windows.CollectionView;

import java.util.ArrayList;
import java.util.Collection;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;
import lombok.Setter;
import rs.groump.Access;

public class CollectionModel<T> implements IModel<CollectionController<T>> {

  @Getter private Collection<T> loaded;
  @Setter private Source<T> source;
  @Getter private final Column<T>[] columns;
  private Collection<T> originalContent;
  @Getter boolean saveChanges;

  public CollectionModel(Collection<T> loaded, Source<T> source, Column<T>[] columns) {
    this.columns = columns;
    this.source = source;
    this.saveChanges = false;
    setLoaded(loaded);
  }

  public boolean isUnChanged() {
    if (loaded.size() != originalContent.size()) return false;
    Collection<T> test = new ArrayList<>(loaded);
    test.removeAll(originalContent);
    return test.isEmpty();
  }

  public boolean isModifiable() {
    return Access.isIterableModifiable(loaded);
  }

  public Collection<T> getSource() {
    return source.query();
  }

  public void revert() {
    if (isModifiable()) {
      loaded.clear();
      loaded.addAll(originalContent);
    }
  }

  public void setLoaded(Collection<T> loaded) {
    this.loaded = loaded;
    originalContent = new ArrayList<>(loaded);
  }
}
