package kernbeisser.Windows.PreOrder;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.Setting;
import kernbeisser.Export.CSVExport;
import kernbeisser.Reports.PreOrderChecklist;
import kernbeisser.Reports.Report;
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

  Optional<CatalogEntry> getEntryByKkNumber(Integer kkNumber) {
    List<CatalogEntry> entriesFound = CatalogEntry.getByArticleNo(kkNumber.toString());
    switch (entriesFound.size()) {
      case 1:
        return Optional.of(entriesFound.get(0));
      case 0:
        return Optional.ofNullable(null);
      default:
        return entriesFound.stream().filter(CatalogEntry::getAktionspreis).findFirst();
    }
  }

  public void add(PreOrder preOrder) {
    Objects.requireNonNull(preOrder.getUser());
    if (preOrder.getUser().equals(User.getKernbeisserUser())) {
      CatalogEntry e = em.find(CatalogEntry.class, preOrder.getCatalogEntry().getId());
      em.persist(e);
    }
    em.persist(preOrder);
    em.flush();
  }

  public void edit(PreOrder preOrder, PreOrder newPreOrder) {
    Objects.requireNonNull(newPreOrder.getUser());
    PreOrder p = em.find(PreOrder.class, preOrder.getId());
    p.setAmount(newPreOrder.getAmount());
    p.setCatalogEntry(newPreOrder.getCatalogEntry());
    p.setUser(newPreOrder.getUser());
    p.setInfo(newPreOrder.getInfo());
    if (preOrder.getUser().equals(User.getKernbeisserUser())) {
      CatalogEntry e = em.find(CatalogEntry.class, newPreOrder.getCatalogEntry().getId());
      em.persist(e);
    }
    em.merge(preOrder);
    em.flush();
  }

  private void removeLazy(PreOrder selected) {
    em.remove(selected);
    em.flush();
  }

  public boolean remove(PreOrder selected, boolean force) {
    if (force || selected.getOrderedOn() == null) {
      delivery.remove(selected);
      removeLazy(selected);
      return true;
    }
    return false;
  }

  public CatalogEntry getByBarcode(String s) throws NoResultException {
    return CatalogEntry.getByBarcode(s).orElseThrow(NoResultException::new);
  }

  Collection<PreOrder> getAllPreOrders(boolean restricted) {
    if (restricted) {
      return em.createQuery(
              "select p from PreOrder p where delivery IS NULL AND p.user = :u order by p.catalogEntry.artikelNr",
              PreOrder.class)
          .setParameter("u", LogInModel.getLoggedIn())
          .getResultList();
    } else {
      return em.createQuery(
              "select p from PreOrder p where delivery IS NULL order by p.user.username, p.catalogEntry.artikelNr",
              PreOrder.class)
          .getResultList();
    }
  }

  Collection<PreOrder> getUnorderedPreOrders() {
    return em.createQuery(
            "select p from PreOrder p where delivery is null and orderedOn is null order by p.catalogEntry.artikelNr",
            PreOrder.class)
        .getResultList();
  }

  static double containerNetPrice(CatalogEntry entry) throws NullPointerException {
    return entry.getPreis() * entry.getBestelleinheitsMenge();
  }

  public void close() {
    // TODO move to Supply
    /*
    delivery.forEach(
        p -> {
          Article article = p.getCatalogEntry();
          if (p.getUser().equals(User.getKernbeisserUser())) {
            removeLazy(p);
            if (!article.getShopRange().isVisible()) {
              article.setShopRange(ShopRange.IN_RANGE);
              em.merge(article);
            }
          } else {
            p.setDelivery(Instant.now());
            em.merge(p);
          }
        });*/
    et.commit();
    em.close();
  }

  private void saveData() {
    et.commit();
    et = em.getTransaction();
    et.begin();
  }

  public void printCheckList(LocalDate deliveryDate, boolean duplexPrint) {
    saveData();
    Report report =
        new PreOrderChecklist(
            deliveryDate,
            getAllPreOrders(false).stream()
                .filter(p -> !isDelivered(p))
                .collect(Collectors.toList()));
    report.setDuplexPrint(duplexPrint);
    report.sendToPrinter("Abhakplan wird gedruckt...", Tools::showUnexpectedErrorWarning);
    for (PreOrder p : getAllPreOrders(false)) {
      if (p.getOrderedOn() != null && p.isShopOrder()) {
        delivery.add(p);
      }
    }
  }

  public boolean exportPreOrder(Component parent) {
    int preorderNr = Setting.LAST_EXPORTED_PREORDER_NR.getIntValue() + 1;
    String defaultFilename = "KornkraftBestellung_" + String.format("%05d.csv", preorderNr);
    boolean result = CSVExport.exportPreOrder(parent, getUnorderedPreOrders(), defaultFilename);
    if (result) {
      setAllExported();
      Setting.LAST_EXPORTED_PREORDER_NR.changeValue(preorderNr);
    }
    return result;
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

  private void setAllExported() {
    Instant orderInstant = Instant.now();
    for (PreOrder o : getUnorderedPreOrders()) {
      o.setOrderedOn(orderInstant);
      em.merge(o);
    }
  }
}
