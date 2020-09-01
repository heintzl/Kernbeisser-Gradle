package kernbeisser.Windows.EditJob;

import kernbeisser.DBEntities.Job;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.MVC.IModel;

public class EditJobModel implements IModel<EditJobController> {
  private final Job job;
  private final Mode mode;

  public EditJobModel(Job job, Mode mode) {
    this.job = job;
    this.mode = mode;
  }

  public Mode getMode() {
    return mode;
  }

  public Job getJob() {
    return job;
  }
}
