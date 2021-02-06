package kernbeisser.Forms.FormImplemetations.Job;

import java.util.function.Supplier;
import kernbeisser.DBEntities.Job;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Forms.FormController;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import org.jetbrains.annotations.NotNull;

public class JobController extends FormController<JobView, JobModel, Job> {

  public JobController() {
    super(new JobModel());
  }

  @NotNull
  @Override
  public JobModel getModel() {
    return model;
  }

  @Override
  public void fillView(JobView jobView) {}

  @Override
  public PermissionKey[] addPermission() {
    return new PermissionKey[] {PermissionKey.ADD_JOB};
  }

  @Override
  public PermissionKey[] editPermission() {
    return new PermissionKey[] {PermissionKey.EDIT_JOB};
  }

  @Override
  public PermissionKey[] removePermission() {
    return new PermissionKey[] {PermissionKey.REMOVE_JOB};
  }

  @Override
  public ObjectForm<Job> getObjectContainer() {
    return getView().getForm();
  }

  @Override
  public Supplier<Job> defaultFactory() {
    return kernbeisser.DBEntities.Job::new;
  }
}
