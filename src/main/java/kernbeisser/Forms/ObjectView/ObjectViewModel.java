package kernbeisser.Forms.ObjectView;

import kernbeisser.Enums.Mode;
import kernbeisser.Forms.FormController;
import kernbeisser.Useful.ActuallyCloneable;
import kernbeisser.Windows.MVC.IModel;
import lombok.Data;
import lombok.Setter;
import rs.groump.AccessDeniedException;

@Data
public class ObjectViewModel<T extends ActuallyCloneable>
    implements IModel<ObjectViewController<T>> {
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
    } catch (AccessDeniedException e) {
      return false;
    }
  }

  public boolean isEditAvailable() {
    try {
      form.editPermission();
      return true;
    } catch (AccessDeniedException e) {
      return false;
    }
  }

  public boolean isRemoveAvailable() {
    try {
      form.removePermission();
      return true;
    } catch (AccessDeniedException e) {
      return false;
    }
  }

  public void submit() {
    if (form.getObjectContainer().applyMode(getCurrentMode())) form.getView().back();
  }
}
