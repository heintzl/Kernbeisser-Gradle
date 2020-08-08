package kernbeisser.Windows.EditJob;

import javax.swing.*;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingField;
import kernbeisser.CustomComponents.AccessChecking.ObjectForm;
import kernbeisser.DBEntities.Job;
import kernbeisser.Windows.MVC.View;
import org.jetbrains.annotations.NotNull;

public class EditJobView implements View<EditJobController> {

  private JButton commit;
  private JButton cancel;
  private AccessCheckingField<Job, String> name;
  private JPanel main;
  private AccessCheckingField<Job, String> description;

  private ObjectForm<Job> form;

  @Override
  public void initialize(EditJobController controller) {
    form = new ObjectForm<>(controller.getModel().getJob(), name, description);
    cancel.addActionListener(e -> back());
    commit.addActionListener(e -> controller.commit());
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
