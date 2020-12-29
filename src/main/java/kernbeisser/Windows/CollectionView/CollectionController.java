package kernbeisser.Windows.CollectionView;

import java.util.Collection;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.Controller;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class CollectionController<T> extends Controller<CollectionView<T>, CollectionModel<T>> {

  public CollectionController(
      Collection<T> edit, Collection<T> available, boolean editable, Column<T>[] columns) {
    super(new CollectionModel<>(edit, available, editable, columns));
  }

  @NotNull
  @Override
  public CollectionModel<T> getModel() {
    return model;
  }

  @Override
  public void fillView(CollectionView<T> tCollectionView) {
    var view = getView();
    view.setAvailable(model.getAvailable());
    model.getAvailable().removeAll(model.getLoaded());
    view.setChosen(model.getLoaded());
    getView().setColumns(model.getColumns());
    getView().setEditable(model.isEditable());
  }



  public void selectAvailable() {
    T object = getView().getSelectedAvailableObject();
    if (object == null) {
      return;
    }
    model.getLoaded().add(object);
    model.getAvailable().remove(object);
    refresh();
  }

  public void selectChosen() {
    T object = getView().getSelectedChosenObject();
    if (object == null) {
      return;
    }
    model.getAvailable().add(object);
    model.getLoaded().remove(object);
    refresh();
  }

  private void refresh() {
    getView().setAvailable(model.getAvailable());
    getView().setChosen(model.getLoaded());
  }

  public void selectAllAvailable() {
    model.getLoaded().addAll(model.getAvailable());
    model.getAvailable().clear();
    refresh();
  }

  public void selectAllChosen() {
    model.getAvailable().addAll(model.getLoaded());
    model.getLoaded().clear();
    refresh();
  }
}
