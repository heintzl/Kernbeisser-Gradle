package kernbeisser.Windows.PreOrder;

import java.awt.*;
import java.io.IOException;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Export.CSVExport;
import kernbeisser.Reports.PreOrderChecklist;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;

public class PreOrderModel implements IModel<PreOrderController> {
  private final EntityManager em = DBConnection.getEntityManager();
  private EntityTransaction et = em.getTransaction();

  {
    et.begin();
  }

  Article getItemByKkNumber(int kkNumber) {
    return Article.getBySuppliersItemNumber(Supplier.getKKSupplier(), kkNumber);
  }

  public void add(PreOrder preOrder) {
    em.persist(preOrder);
    em.flush();
  }

  public void remove(PreOrder selected) {
    em.remove(selected);
    em.flush();
  }

  public Article getByBarcode(String s) {
    return Article.getByBarcode(Long.parseLong(s));
  }

  Collection<PreOrder> getAllPreOrders() {
    return em.createQuery("select p from PreOrder p", PreOrder.class).getResultList();
  }

  static double containerNetPrice(Article article) {
    return new ShoppingItem(article, 0, true).getItemNetPrice() * article.getContainerSize();
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

  public int exportPreOrder(Component parent) throws IOException {
    return CSVExport.exportPreOrder(parent, getAllPreOrders());
  }
}
