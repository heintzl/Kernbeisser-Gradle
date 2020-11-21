package kernbeisser.Windows.PreOrder;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.ArticleKornkraft;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class PreOrderModel implements IModel<PreOrderController> {
  private final Collection<PreOrder> newPreOrders = new ArrayList<>();

  void saveChanges() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    newPreOrders.forEach(em::persist);
    em.flush();
    et.commit();
    em.close();
  }

  ArticleKornkraft getItemByKbNumber(int kbNumber) {
    return ArticleKornkraft.getByKbNumber(kbNumber);
  }

  ArticleKornkraft getItemByKkNumber(int kkNumber) {
    return ArticleKornkraft.getByKkNumber(kkNumber);
  }

  public void add(PreOrder preOrder) {
    newPreOrders.add(preOrder);
  }

  public void remove(PreOrder selected) {
    newPreOrders.remove(selected);
  }
}
