package kernbeisser.Windows.EditJob;

import javax.persistence.PersistenceException;
import javax.swing.*;
import kernbeisser.DBEntities.Job;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Proxy;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import lombok.var;
import org.hibernate.exception.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;

public class EditJobController extends Controller<EditJobView, EditJobModel> {

  public EditJobController(Job job, Mode mode) {
    super(new EditJobModel(job != null ? job : Proxy.getSecureInstance(new Job()), mode));
    if (job != null && mode == Mode.REMOVE) {
      try {
        Tools.delete(job);
      } catch (PersistenceException e) {
        if (e.getCause() instanceof ConstraintViolationException) {
          JOptionPane.showMessageDialog(
              null,
              "Der Job kann nicht gelöscht werden, da dieser noch auf andere Objekte verweisst");
        }
      }
    }
  }

  @NotNull
  @Override
  public EditJobModel getModel() {
    return model;
  }

  @Override
  public void fillView(EditJobView editJobView) {
    editJobView.getForm().setSource(getModel().getJob());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    switch (getModel().getMode()) {
      case ADD:
        return new PermissionKey[] {PermissionKey.ADD_JOB};
      case EDIT:
        return new PermissionKey[] {PermissionKey.EDIT_JOB};
      case REMOVE:
        return new PermissionKey[] {PermissionKey.REMOVE_JOB};
    }
    throw new UnsupportedOperationException("undefined mode");
  }

  public void commit() {
    var view = getView();
    if (view.getForm().applyMode(model.getMode())) {
      view.back();
    }
  }
}
