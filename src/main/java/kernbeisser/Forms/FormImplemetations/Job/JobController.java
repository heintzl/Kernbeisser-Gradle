package kernbeisser.Forms.FormImplemetations.Job;

import java.util.function.Supplier;
import kernbeisser.DBEntities.Job;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Forms.FormController;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Security.Key;
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
  @Key(PermissionKey.ADD_JOB)
  public void addPermission() {}

  @Override
  @Key(PermissionKey.EDIT_JOB)
  public void editPermission() {}

  @Override
  @Key(PermissionKey.REMOVE_JOB)
  public void removePermission() {}

  @Override
  public ObjectForm<Job> getObjectContainer() {
    return getView().getForm();
  }

  @Override
  public Supplier<Job> defaultFactory() {
    return kernbeisser.DBEntities.Job::new;
  }
}
