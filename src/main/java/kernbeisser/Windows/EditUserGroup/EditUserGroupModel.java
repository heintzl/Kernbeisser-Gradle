package kernbeisser.Windows.EditUserGroup;

import kernbeisser.DBEntities.User;
import kernbeisser.Windows.MVC.IModel;
import lombok.Data;

@Data
public class EditUserGroupModel implements IModel<EditUserGroupController> {

  private User user;

  public EditUserGroupModel(User user) {
    this.user = user;
  }

  public void refreshData() {
    user = User.getById(user.getId());
  }
}
