package kernbeisser.Windows.EditUserGroup;

import java.util.Collection;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Security.Proxy;
import kernbeisser.Tasks.Users;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Data;

@Data
public class EditUserGroupModel implements IModel<EditUserGroupController> {

  private User user;
  private User caller;

  public EditUserGroupModel(User user, User caller) {
    this.user = Proxy.removeProxy(user);
    this.caller = caller;
  }

  public Optional<User> getCaller() {
    return Optional.ofNullable(caller);
  }

  public void refreshData() {
    user = Proxy.removeProxy(User.getById(user.getId()));
    LogInModel.refreshLogInData();
  }

  void changeUserGroup(int user, int destination) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Users.switchUserGroup(user, em.find(UserGroup.class, destination).getId());
  }

  public void changeSoli(double newValue) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    UserGroup userGroup = em.find(UserGroup.class, user.getUserGroup().getId());
    userGroup.setSolidaritySurcharge(newValue);
    em.persist(userGroup);
  }

  public Collection<User> getLogIns(Collection<User> confirmingUsers) {
    getCaller().ifPresent(confirmingUsers::add);
    return confirmingUsers;
  }
}
