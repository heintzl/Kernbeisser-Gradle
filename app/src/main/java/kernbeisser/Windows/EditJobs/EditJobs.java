package kernbeisser.Windows.EditJobs;

import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.DBEntities.Job;
import kernbeisser.Forms.FormImplemetations.Job.JobController;
import kernbeisser.Forms.ObjectView.ObjectViewController;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class EditJobs extends ObjectViewController<Job> {
  @Key(PermissionKey.ACTION_OPEN_EDIT_JOBS)
  public EditJobs() {
    super(
        "Jobs bearbeiten",
        new JobController(),
        Job::defaultSearch,
        true,
        Columns.create("Name", Job::getName),
        Columns.create("Beschreibung", Job::getDescription));
  }
}
