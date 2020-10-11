package kernbeisser.Windows.CollectionView;

import java.util.Collection;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.IController;
import org.jetbrains.annotations.NotNull;

public class CollectionController<T> implements IController<CollectionView<T>, CollectionModel<T>> {
  private CollectionView<T> view;
  private final CollectionModel<T> model;

  public CollectionController(
      Collection<T> edit, Collection<T> available, boolean editable, Column<T>[] columns) {
    model = new CollectionModel<>(edit, available, editable, columns);
  }

  @NotNull
  @Override
  public CollectionModel<T> getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setAvailable(model.getAvailable());
    model.getAvailable().removeAll(model.getLoaded());
    view.setChosen(model.getLoaded());
    view.setColumns(model.getColumns());
    view.setEditable(model.isEditable());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public void selectAvailable() {
    T object = view.getSelectedAvailableObject();
    if (object == null) {
      return;
    }
    model.getLoaded().add(object);
    model.getAvailable().remove(object);
    refresh();
  }

  public void selectChosen() {
    T object = view.getSelectedChosenObject();
    if (object == null) {
      return;
    }
    model.getAvailable().add(object);
    model.getLoaded().remove(object);
    refresh();
  }

  private void refresh() {
    view.setAvailable(model.getAvailable());
    view.setChosen(model.getLoaded());
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
