package kernbeisser.Windows.CollectionView;

import java.util.Collection;
import java.util.HashSet;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

public class CollectionController<T> implements Controller<CollectionView<T>, CollectionModel<T>> {
  private final CollectionView<T> view;
  private final CollectionModel<T> model;

  public CollectionController(
      Collection<T> edit, Collection<T> available, boolean editable, Column<T>[] columns) {
    model = new CollectionModel<>(edit, available, editable, columns);
    view = new CollectionView<>();
  }

  @NotNull
  @Override
  public CollectionView<T> getView() {
    return view;
  }

  @NotNull
  @Override
  public CollectionModel<T> getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setAvailable(model.getAvailable());
    view.setChosen(model.getLoaded());
    model.getLoaded().forEach(view::removeAvailable);
    view.setColumns(model.getColumns());
    view.setEditable(model.isEditable());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  void commit() {
    model.getLoaded().clear();
    model.getLoaded().addAll(view.getAllChosen());
    view.back();
  }

  public void selectAvailable() {
    if (view.getSelectedAvailableObject() == null) {
      return;
    }
    view.addChosen(view.getSelectedAvailableObject());
    view.removeAvailable(view.getSelectedAvailableObject());
  }

  public void selectChosen() {
    if (view.getSelectedChosenObject() == null) {
      return;
    }
    view.addAvailable(view.getSelectedChosenObject());
    view.removeChosen(view.getSelectedChosenObject());
  }

  public void selectAllAvailable() {
    view.setChosen(new HashSet<>());
    view.setAvailable(new HashSet<>());
    model.getAvailable().forEach(view::addChosen);
  }

  public void selectAllChosen() {
    view.getAllChosen().forEach(view::addAvailable);
    view.setChosen(new HashSet<>());
  }
}
