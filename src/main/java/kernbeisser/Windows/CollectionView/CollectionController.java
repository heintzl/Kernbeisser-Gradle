package kernbeisser.Windows.CollectionView;

import java.util.ArrayList;
import java.util.Collection;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Exeptions.NoSelectionException;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Windows.MVC.Controller;

public class CollectionController<T> extends Controller<CollectionView<T>, CollectionModel<T>> {

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
    try {
      T object = getView().getSelectedAvailableObject().orElseThrow(NoSelectionException::new);
      model.getLoaded().add(object);
      refresh();
    } catch (NoSelectionException ignored) {
    }
  }

  public void selectChosen() {
    try {
      model
          .getLoaded()
          .remove(getView().getSelectedChosenObject().orElseThrow(NoSelectionException::new));
      refresh();
    } catch (NoSelectionException e) {
      getView().messageSelectObjectFirst();
    }
  }

  private void refresh() {
    Collection<T> source = new ArrayList<>(model.getSource());
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
}
