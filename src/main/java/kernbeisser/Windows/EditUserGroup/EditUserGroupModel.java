package kernbeisser.Windows.EditUserGroup;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Collection;
import java.util.Optional;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Exeptions.MissingFullMemberException;
import kernbeisser.Tasks.Users;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Data;

@Data
public class EditUserGroupModel implements IModel<EditUserGroupController> {

  private User user;
  private User caller;

  public EditUserGroupModel(User user, User caller) {
    this.user = user;
    this.caller = caller;
  }

  public Optional<User> getCaller() {
    return Optional.ofNullable(caller);
  }

  public void refreshData() {
    user = User.getById(user.getId());
  }

  boolean changeUserGroup(int user, int destination) throws MissingFullMemberException {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return Users.switchUserGroup(user, em.find(UserGroup.class, destination).getId());
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
