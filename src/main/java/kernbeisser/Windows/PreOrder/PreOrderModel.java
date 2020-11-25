package kernbeisser.Windows.PreOrder;

import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.ArticleKornkraft;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.Reports.PreOrderChecklist;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;

public class PreOrderModel implements IModel<PreOrderController> {
  private final EntityManager em = DBConnection.getEntityManager();
  private EntityTransaction et = em.getTransaction();

  {
    et.begin();
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

  private void saveData() {
    em.flush();
    et.commit();
    et = em.getTransaction();
    et.begin();
  }

  public void printCheckList() {
    saveData();
    PreOrderChecklist checklist = new PreOrderChecklist(getAllPreOrders());
    checklist.sendToPrinter("Abhakplan wird gedruckt...", Tools::showUnexpectedErrorWarning);
  }
}
