package kernbeisser.Windows.PreOrder;

import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.ArticleKornkraft;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.MVC.IModel;

public class PreOrderModel implements IModel<PreOrderController> {
  private final EntityManager em = DBConnection.getEntityManager();
  private final EntityTransaction et = em.getTransaction();

  {
    et.begin();
  }

  Collection<User> getAllUser() {
    List<User> result =
        em.createQuery(
                "select u from User u where upper(username) != 'KERNBEISSER' order by firstName,surname asc",
                User.class)
            .getResultList();
    result.add(0, User.getKernbeisserUser());
    return result;
  }

  ArticleKornkraft getItemByKkNumber(int kkNumber) {
    return ArticleKornkraft.getByKkNumber(kkNumber);
  }

  public void add(PreOrder preOrder) {
    em.persist(preOrder);
    em.flush();
  }

  public void remove(PreOrder selected) {
    em.remove(selected);
    em.flush();
  }

  public ArticleKornkraft getByBarcode(String s) {
    return ArticleKornkraft.getByBarcode(Long.parseLong(s));
  }

  Collection<PreOrder> getAllPreOrders() {
    return em.createQuery("select p from PreOrder p", PreOrder.class).getResultList();
  }

  public void close() {
    em.flush();
    et.commit();
    em.close();
  }
}
