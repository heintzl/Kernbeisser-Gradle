package kernbeisser.Windows.PreOrder;

import java.awt.*;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Export.CSVExport;
import kernbeisser.Reports.PreOrderChecklist;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;

public class PreOrderModel implements IModel<PreOrderController> {
  private final EntityManager em = DBConnection.getEntityManager();
  private EntityTransaction et = em.getTransaction();
  private final Set<PreOrder> delivery = new HashSet<>();

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

  private void removeLazy(PreOrder selected) {
    em.remove(selected);
    em.flush();
  }

  public void remove(PreOrder selected) {
    delivery.remove(selected);
    removeLazy(selected);
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
    delivery.forEach(
        p -> {
          Article article = p.getArticle();
          if (p.getUser().equals(User.getKernbeisserUser()) && !article.isShopRange()) {
            article.setShopRange(ShopRange.IN_RANGE);
            em.merge(article);
          }
          removeLazy(p);
        });
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
    new PreOrderChecklist(getAllPreOrders())
        .sendToPrinter("Abhakplan wird gedruckt...", Tools::showUnexpectedErrorWarning);
  }

  public int exportPreOrder(Component parent) throws IOException {
    return CSVExport.exportPreOrder(parent, getAllPreOrders());
  }

  void toggleDelivery(PreOrder p) {
    if (!delivery.remove(p)) delivery.add(p);
  }

  boolean isDelivered(PreOrder p) {
    return delivery.contains(p);
  }

  public void setAllDelivered(boolean allDelivered) {
    delivery.clear();
    if (allDelivered) {
      delivery.addAll(getAllPreOrders());
    }
  }

  public void setAmount(PreOrder preOrder, int amount) {
    preOrder.setAmount(amount);
  }
}
