package kernbeisser.Windows.PermissionGranterAssignment;

import java.util.List;
import java.util.Optional;
import javax.swing.*;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.CollectionView.CollectionView;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class PermissionGranterAssignmentView
    implements IView<PermissionGranterAssignmentController> {

  private JPanel main;
  private AdvancedComboBox<Permission> permissions;
  private CollectionView<User> collectionView;

  @Linked private CollectionController<User> user;

  @Override
  public void initialize(PermissionGranterAssignmentController controller) {
    permissions.addActionListener(controller::loadPermission);
  }

  void setPermissions(List<Permission> permissions) {
    this.permissions.setItems(permissions);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    permissions = new AdvancedComboBox<>(Permission::getNeatName);
    collectionView = user.getView();
  }

  Optional<Permission> getSelectedPermission() {
    return permissions.getSelected();
  }

  public boolean confirmChanges() {
    return JOptionPane.showConfirmDialog(
            getContent(),
            "Sollen die vorgenommenen Änderungen gespeichert werden?",
            "Wechsel der bearbeiteten Rolle",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE)
        == JOptionPane.YES_OPTION;
  }

  @Override
  public String getTitle() {
    return "Berechtigungweitergabe";
  }
}
