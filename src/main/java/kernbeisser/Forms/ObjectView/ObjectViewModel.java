package kernbeisser.Forms.ObjectView;

import java.util.Collection;
import kernbeisser.Enums.Mode;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.FormController;
import kernbeisser.Windows.MVC.IModel;
import kernbeisser.Windows.Searchable;
import lombok.Data;
import lombok.Setter;

@Data
public class ObjectViewModel<T> implements IModel<ObjectViewController<T>> {
  private final FormController<?, ?, T> form;

  private final boolean copyValuesToAdd;

  @Setter private Mode currentMode = null;

  ObjectViewModel(FormController<?, ?, T> maskLoader, boolean copyValuesToAdd) {
    this.form = maskLoader;
    this.copyValuesToAdd = copyValuesToAdd;
  }

  public boolean isAddAvailable() {
    try {
      form.addPermission();
      return true;
    } catch (PermissionKeyRequiredException e) {
      return false;
    }
  }

  public boolean isEditAvailable() {
    try {
      form.editPermission();
      return true;
    } catch (PermissionKeyRequiredException e) {
      return false;
    }
  }

  public boolean isRemoveAvailable() {
    try {
      form.removePermission();
      return true;
    } catch (PermissionKeyRequiredException e) {
      return false;
    }
  }

  public void submit() {
    if (form.getObjectContainer().applyMode(getCurrentMode())) form.getView().back();
  }
}
