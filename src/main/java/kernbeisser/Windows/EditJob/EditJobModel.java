package kernbeisser.Windows.EditJob;

import kernbeisser.DBEntities.Job;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.Model;

public class EditJobModel implements Model<EditJobController> {
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
