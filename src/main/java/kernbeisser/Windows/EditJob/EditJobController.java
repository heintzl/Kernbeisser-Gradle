package kernbeisser.Windows.EditJob;

import kernbeisser.DBEntities.Job;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Proxy;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import org.hibernate.exception.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;

import javax.persistence.PersistenceException;
import javax.swing.*;

public class EditJobController implements Controller<EditJobView, EditJobModel> {

  private final EditJobModel model;
  private EditJobView view;

  public EditJobController(Job job, Mode mode) {
    model = new EditJobModel(job != null ? job : Proxy.getSecureInstance(new Job()), mode);
    try {
      Tools.delete(job);
    }catch (PersistenceException e){
      if (e.getCause() instanceof ConstraintViolationException) {
        JOptionPane.showMessageDialog(null,"Der Job kann nicht gelöscht werden, da dieser noch auf andere Objekte verweisst");
      }
    }
  }

  @NotNull
  @Override
  public EditJobModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {}

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public void commit() {
    if (view.getForm().applyMode(model.getMode())) {
      view.back();
    }
  }
}
