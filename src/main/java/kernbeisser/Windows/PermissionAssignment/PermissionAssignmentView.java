package kernbeisser.Windows.PermissionAssignment;

import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.CollectionView.CollectionView;
import kernbeisser.Windows.MVC.IView;
import org.jetbrains.annotations.NotNull;

public class PermissionAssignmentView implements IView<PermissionAssignmentController> {

  private JButton back;
  private JPanel main;
  private AdvancedComboBox<Permission> permissions;
  private CollectionView<User> collectionView;

  @Override
  public void initialize(PermissionAssignmentController controller) {
    back.addActionListener(e -> back());
    permissions.addActionListener(controller);
  }

  void setPermissions(List<Permission> permissions) {
    this.permissions.setItems(permissions);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    permissions = new AdvancedComboBox<>(Permission::getName);
    collectionView = new CollectionView<>();
  }
}
