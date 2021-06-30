package kernbeisser.Windows.CollectionView;

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Windows.MVC.Controller;

public class CollectionController<T> extends Controller<CollectionView<T>, CollectionModel<T>> {

  Collection<Runnable> selectionListeners = new ArrayList<>();

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
    model.getLoaded().addAll(getView().getSelectedAvailableObjects());
    refresh();
  }

  public void selectChosen() {
    model.getLoaded().removeAll(getView().getSelectedChosenObjects());
    refresh();
  }

  private void refresh() {
    Collection<T> source = new ArrayList<>(model.getSource());
    source.removeAll(model.getLoaded());
    getView().setChosen(model.getLoaded());
    getView().setAvailable(source);
    selectionListeners.forEach(Runnable::run);
  }

  public void selectAllAvailable() {
    model.getLoaded().addAll(getView().getAllAvailableObjects());
    getView().clearSeachBox();
    refresh();
  }

  public void selectAllChosen() {
    model.getLoaded().removeAll(getView().getAllChosenObjects());
    getView().clearSeachBox();
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

  public void addSelectionListener(Runnable listener) {
    selectionListeners.add(listener);
  }
}
