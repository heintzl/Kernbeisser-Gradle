package kernbeisser.Forms.ObjectView;

import java.util.Collection;
import kernbeisser.Enums.Mode;
import kernbeisser.Forms.FormController;
import kernbeisser.Security.PermissionSet;
import kernbeisser.Windows.MVC.IModel;
import kernbeisser.Windows.Searchable;
import lombok.Data;
import lombok.Setter;

@Data
public class ObjectViewModel<T> implements IModel<ObjectViewController<T>> {
  private final FormController<?, ?, T> form;
  private final Searchable<T> itemSupplier;

  private final boolean copyValuesToAdd;

  @Setter private Mode currentMode = null;

  ObjectViewModel(
      FormController<?, ?, T> maskLoader, Searchable<T> itemSupplier, boolean copyValuesToAdd) {
    this.form = maskLoader;
    this.itemSupplier = itemSupplier;
    this.copyValuesToAdd = copyValuesToAdd;
  }

  Collection<T> getItems(String search, int max) {
    return itemSupplier.search(search, max);
  }

  void remove(T t) {
    form.remove(t);
  }

  public boolean isAddAvailable() {
    return PermissionSet.MASTER.hasPermissions(form.addPermission());
  }

  public boolean isEditAvailable() {
    return PermissionSet.MASTER.hasPermissions(form.editPermission());
  }

  public boolean isRemoveAvailable() {
    return PermissionSet.MASTER.hasPermissions(form.removePermission());
  }

  public void submit() {
    if (form.getObjectContainer().applyMode(getCurrentMode())) form.getView().back();
  }
}
