package kernbeisser.Forms.FormImplemetations.Job;

import kernbeisser.DBEntities.Job;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingField;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JobView implements IView<JobController> {

  private AccessCheckingField<Job, String> name;
  private JPanel main;
  private AccessCheckingField<Job, String> description;

  private ObjectForm<Job> form;

  @Linked private JobController jobController;

  @Override
  public void initialize(JobController jobController) {
    form = new ObjectForm<>(name, description);
    form.setObjectDistinction("Der Job");
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    name = new AccessCheckingField<>(Job::getName, Job::setName, AccessCheckingField.NOT_NULL);
    description =
        new AccessCheckingField<>(
            Job::getDescription, Job::setDescription, AccessCheckingField.NOT_NULL);
  }

  public ObjectForm<Job> getForm() {
    return form;
  }
}
