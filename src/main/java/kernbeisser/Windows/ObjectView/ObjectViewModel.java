package kernbeisser.Windows.ObjectView;

import java.util.Collection;
import kernbeisser.Enums.Mode;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Windows.CloseEvent;
import kernbeisser.Windows.MVC.IModel;
import kernbeisser.Windows.MaskLoader;
import kernbeisser.Windows.Searchable;
import kernbeisser.Windows.ViewContainer;
import kernbeisser.Windows.ViewContainers.SubWindow;

public class ObjectViewModel<T> implements IModel<ObjectViewController<T>> {
  private final MaskLoader<T> maskLoader;
  private final Searchable<T> itemSupplier;

  private final boolean copyValuesToAdd;

  ObjectViewModel(MaskLoader<T> maskLoader, Searchable<T> itemSupplier, boolean copyValuesToAdd) {
    this.maskLoader = maskLoader;
    this.itemSupplier = itemSupplier;
    this.copyValuesToAdd = copyValuesToAdd;
  }

  ViewContainer openEdit(ViewContainer window, T selected, CloseEvent closeEvent) {
    return maskLoader
        .accept(selected, Mode.EDIT)
        .withCloseEvent(closeEvent)
        .openIn(new SubWindow(window));
  }

  Collection<T> getItems(String search, int max) {
    return itemSupplier.search(search, max);
  }

  ViewContainer openAdd(ViewContainer window, T selected, CloseEvent closeEvent) {
    return maskLoader
        .accept(copyValuesToAdd ? selected : null, Mode.ADD)
        .withCloseEvent(closeEvent)
        .openIn(new SubWindow(window));
  }

  boolean isAvailable(Mode mode){
    try {
      maskLoader.accept(null,mode);
      return true;
    }catch (PermissionKeyRequiredException e){
      return false;
    }
  }

  void remove(T selected) {
    maskLoader.accept(selected, Mode.REMOVE);
  }
}
