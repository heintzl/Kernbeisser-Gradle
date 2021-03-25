package kernbeisser.Windows.CollectionView;

import java.util.Collection;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Windows.MVC.Controller;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class CollectionController<T> extends Controller<CollectionView<T>, CollectionModel<T>> {

  public CollectionController(Collection<T> edit, Source<T> available, Column<T>[] columns) {
    super(new CollectionModel<>(edit, available, columns));
  }

  @NotNull
  @Override
  public CollectionModel<T> getModel() {
    return model;
  }

  @Override
  public void fillView(CollectionView<T> tCollectionView) {
    var view = getView();
    view.setAvailable(model.getSource());
    getView().setColumns(model.getColumns());
    getView().setEditable(model.isModifiable());
    refresh();
  }

  public void selectAvailable() {
    T object = getView().getSelectedAvailableObject();
    if (object == null) {
      return;
    }
    model.getLoaded().add(object);
    refresh();
  }

  public void selectChosen() {
    model.getLoaded().remove(getView().getSelectedChosenObject());
    refresh();
  }

  private void refresh() {
    Collection<T> source = model.getSource();
    source.removeAll(model.getLoaded());
    getView().setChosen(model.getLoaded());
    getView().setAvailable(source);
  }

  public void selectAllAvailable() {
    model.getLoaded().addAll(model.getSource());
    refresh();
  }

  public void selectAllChosen() {
    model.getLoaded().clear();
    refresh();
  }
}
