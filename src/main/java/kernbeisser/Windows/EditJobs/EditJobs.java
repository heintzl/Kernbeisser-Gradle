package kernbeisser.Windows.EditJobs;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Job;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Forms.FormImplemetations.Job.JobController;
import kernbeisser.Forms.ObjectView.ObjectViewController;
import kernbeisser.Security.Key;

public class EditJobs extends ObjectViewController<Job> {
  @Key(PermissionKey.ACTION_OPEN_EDIT_JOBS)
  public EditJobs() {
    super(
        "Jobs bearbeiten",
        new JobController(),
        Job::defaultSearch,
        true,
        Column.create("Name", Job::getName),
        Column.create("Beschreibung", Job::getDescription),
        Column.create("Erstellungsdatum", Job::getCreateDate),
        Column.create("Änderungsdatum", Job::getUpdateDate));
  }
}
