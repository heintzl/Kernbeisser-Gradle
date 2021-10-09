package kernbeisser.Windows.PreOrder;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Export.CSVExport;
import kernbeisser.Reports.PreOrderChecklist;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;

public class PreOrderModel implements IModel<PreOrderController> {
  private final EntityManager em = DBConnection.getEntityManager();
  private EntityTransaction et = em.getTransaction();
  @Getter private final Set<PreOrder> delivery = new HashSet<>();

  {
    et.begin();
  }

  Optional<Article> getItemByKkNumber(int kkNumber) {
    return Articles.getBySuppliersItemNumber(Supplier.getKKSupplier(), kkNumber);
  }

  public void add(PreOrder preOrder) {
    Objects.requireNonNull(preOrder.getUser());
    if (preOrder.getUser().equals(User.getKernbeisserUser())) {
      Article a = em.find(Article.class, preOrder.getArticle().getId());
      em.persist(a);
    }
    em.persist(preOrder);
    em.flush();
  }

  private void removeLazy(PreOrder selected) {
    em.remove(selected);
    em.flush();
  }

  public boolean remove(PreOrder selected) {
    if (selected.getOrderedOn() == null) {
      delivery.remove(selected);
      removeLazy(selected);
      return true;
    }
    return false;
  }

  public Article getByBarcode(String s) throws NoResultException {
    return Articles.getByBarcode(Long.parseLong(s)).orElseThrow(NoResultException::new);
  }

  Collection<PreOrder> getAllPreOrders(boolean restricted) {
    if (restricted) {
      return em.createQuery("select p from PreOrder p where p.user = :u", PreOrder.class)
          .setParameter("u", LogInModel.getLoggedIn())
          .getResultList();
    } else {
      return em.createQuery("select p from PreOrder p", PreOrder.class).getResultList();
    }
  }

  Collection<PreOrder> getUnorderedPreOrders() {
    return em.createQuery("select p from PreOrder p where orderedOn is null", PreOrder.class)
        .getResultList();
  }

  static double containerNetPrice(Article article) {
    return new ShoppingItem(article, 0, 0, true).getItemNetPrice() * article.getContainerSize();
  }

  public void close() {
    delivery.forEach(
        p -> {
          Article article = p.getArticle();
          if (p.getUser().equals(User.getKernbeisserUser())
              && !article.getShopRange().isVisible()) {
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
    new PreOrderChecklist(getAllPreOrders(false))
        .sendToPrinter("Abhakplan wird gedruckt...", Tools::showUnexpectedErrorWarning);
  }

  public int exportPreOrder(Component parent) throws IOException {
    return CSVExport.exportPreOrder(parent, getUnorderedPreOrders());
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
      delivery.addAll(getAllPreOrders(false));
    }
  }

  public void setAmount(PreOrder preOrder, int amount) {
    preOrder.setAmount(amount);
  }

  public void setAllExported() {
    Instant orderInstant = Instant.now();
    for (PreOrder o : getUnorderedPreOrders()) {
      o.setOrderedOn(orderInstant);
    }
  }
}
