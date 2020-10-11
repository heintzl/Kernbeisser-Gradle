package kernbeisser.Windows.EditUserGroup;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javax.persistence.EntityManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Exeptions.CannotLogInException;
import kernbeisser.Security.Proxy;
import kernbeisser.Tasks.Users;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Data;

@Data
public class EditUserGroupModel implements IModel<EditUserGroupController> {

  private User user;

  public EditUserGroupModel(User user) {
    this.user = Proxy.removeProxy(user);
  }

  public void refreshData() {
    user = Proxy.removeProxy(User.getById(user.getId()));
  }

  void changeUserGroup(int user, int destination, String password) throws CannotLogInException {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    User dbUser = em.find(User.class, user);
    if (BCrypt.verifyer()
        .verify(password.toCharArray(), dbUser.getPassword().toCharArray())
        .verified) {
      Users.switchUserGroup(user, em.find(UserGroup.class, destination).getId());
    } else throw new CannotLogInException();
  }
}
