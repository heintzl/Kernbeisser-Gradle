package kernbeisser.Windows.EditJobs;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Job;
import kernbeisser.Windows.EditJob.EditJobController;
import kernbeisser.Windows.ObjectView.ObjectViewController;

public class EditJobs extends ObjectViewController<Job> {
  public EditJobs() {
    super(
        "Jobs bearbeiten",
        EditJobController::new,
        Job::defaultSearch,
        true,
        Column.create("Name", Job::getName),
        Column.create("Beschreibung", Job::getDescription),
        Column.create("Erstellungsdatum", Job::getCreateDate),
        Column.create("Änderungsdatum", Job::getUpdateDate));
  }
}
