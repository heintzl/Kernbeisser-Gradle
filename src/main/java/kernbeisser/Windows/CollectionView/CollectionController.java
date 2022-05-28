package kernbeisser.Windows.CollectionView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Windows.MVC.Controller;

public class CollectionController<T> extends Controller<CollectionView<T>, CollectionModel<T>> {

  Collection<Runnable> collectionModifiedListeners = new ArrayList<>();

  Collection<BiConsumer<Collection<T>, Boolean>> objectsListeners = new ArrayList<>();

  @SafeVarargs
  public CollectionController(Collection<T> edit, Source<T> source, Column<T>... columns) {
    super(new CollectionModel<>(edit, source, columns));
  }

  public void exitWithSave() {
    model.saveChanges = true;
    getView().back();
  }

  @Override
  protected boolean commitClose() {
    if (model.saveChanges || model.isUnChanged()) return true;
    if (getView().confirmCancel()) {
      model.revert();
      return true;
    }
    return false;
  }

  @Override
  public void fillView(CollectionView<T> tCollectionView) {
    tCollectionView.setColumns(model.getColumns());
    tCollectionView.setEditable(model.isModifiable());
    refresh();
  }

  public void selectAvailable() {
    Collection<T> selectedObjects = getView().getSelectedAvailableObjects();
    model.getLoaded().addAll(selectedObjects);
    applyObjectsListeners(selectedObjects, true);
    refresh();
  }

  public void selectChosen() {
    Collection<T> selectedObjects = getView().getSelectedChosenObjects();
    model.getLoaded().removeAll(selectedObjects);
    applyObjectsListeners(selectedObjects, false);
    refresh();
  }

  private void refresh() {
    Collection<T> source = new ArrayList<>(model.getSource());
    source.removeAll(model.getLoaded());
    getView().setChosen(model.getLoaded());
    getView().setAvailable(source);
    collectionModifiedListeners.forEach(Runnable::run);
  }

  public void selectAllAvailable() {
    Collection<T> selectedObjects = getView().getAllAvailableObjects();
    model.getLoaded().addAll(selectedObjects);
    applyObjectsListeners(selectedObjects, true);
    getView().clearSearchBox();
    refresh();
  }

  public void selectAllChosen() {
    Collection<T> selectedObjects = getView().getAllChosenObjects();
    model.getLoaded().removeAll(selectedObjects);
    applyObjectsListeners(selectedObjects, false);
    getView().clearSearchBox();
    refresh();
  }

  public void setSource(Source<T> source) {
    getModel().setSource(source);
  }

  public void setLoaded(Collection<T> collection) {
    getModel().setLoaded(collection);
    getView().setEditable(model.isModifiable());
    refresh();
  }

  public void setLoadedAndSource(Collection<T> loaded, Source<T> source) {
    getModel().setSource(source);
    getModel().setLoaded(loaded);
    getView().setEditable(model.isModifiable());
    refresh();
  }

  public void addControls(JComponent... components) {
    for (JComponent c : components) {
      getView().addAdditionalControl(c);
    }
  }

  public void addCollectionModifiedListener(Runnable listener) {
    collectionModifiedListeners.add(listener);
  }

  public void addObjectsListener(BiConsumer<Collection<T>, Boolean> listener) {
    objectsListeners.add(listener);
  }

  private void applyObjectsListeners(Collection<T> objects, Boolean chosen) {
    for (BiConsumer<Collection<T>, Boolean> consumer : objectsListeners) {
      consumer.accept(objects, chosen);
    }
  }
}
