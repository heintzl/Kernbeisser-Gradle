package kernbeisser.Windows.PreOrder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.*;
import kernbeisser.DBEntities.CatalogEntry_;
import kernbeisser.DBEntities.PreOrder_;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.DBEntities.User_;
import kernbeisser.EntityWrapper.ObjectState;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Export.CSVExport;
import kernbeisser.Reports.PreOrderChecklist;
import kernbeisser.Reports.Report;
import kernbeisser.Useful.Constants;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class PreOrderModel implements IModel<PreOrderController> {

  private final EntityManager em = DBConnection.getEntityManager();
  private final EntityTransaction et = em.getTransaction();
  @Getter private final Set<PreOrder> delivery = new HashSet<>();

  Optional<CatalogEntry> getEntryByKkNumber(Integer kkNumber) {
    List<CatalogEntry> entries = CatalogEntry.getByArticleNo(kkNumber.toString(), true, false);
    return entries.stream().findFirst();
  }

  public void add(PreOrder preOrder) {
    Objects.requireNonNull(preOrder.getUser());
    et.begin();
    if (preOrder.getUser().isShopUser()) {
      CatalogEntry e = em.find(CatalogEntry.class, preOrder.getCatalogEntry().getId());
      em.persist(e);
    }
    em.persist(preOrder);
    et.commit();
  }

  public PreOrder edit(PreOrder preOrder, PreOrder newPreOrder) {
    Objects.requireNonNull(newPreOrder.getUser());
    et.begin();
    PreOrder p = em.find(PreOrder.class, preOrder.getId());
    p.setAmount(newPreOrder.getAmount());
    p.setCatalogEntry(newPreOrder.getCatalogEntry());
    p.setUser(newPreOrder.getUser());
    p.setInfo(newPreOrder.getInfo());
    p.setLatestWeekOfDelivery(newPreOrder.getLatestWeekOfDelivery());
    p.setAlternativeCatalogEntry(newPreOrder.getAlternativeCatalogEntry());
    p.setComment(newPreOrder.getComment());
    if (preOrder.getUser().isShopUser()) {
      CatalogEntry e = em.find(CatalogEntry.class, newPreOrder.getCatalogEntry().getId());
      em.persist(e);
    }
    em.merge(p);
    et.commit();
    return p;
  }

  private void removeLazy(PreOrder selected) {
    em.remove(em.find(PreOrder.class, selected.getId()));
  }

  public boolean remove(PreOrder selected, boolean force) {
    if (force || selected.getOrderedOn() == null) {
      delivery.remove(selected);
      et.begin();
      removeLazy(selected);
      et.commit();
      return true;
    }
    return false;
  }

  public Optional<CatalogEntry> findEntriesByShopNumber(int shopNumber) {
    Optional<Article> article =
        ArticleRepository.getByKbNumber(shopNumber, false).map(ObjectState::getValue);
    if (article.isPresent()) {
      if (article.get().getSupplier().equals(Constants.KK_SUPPLIER)) {
        return getEntryByKkNumber(article.get().getSuppliersItemNumber());
      }
    }
    return Optional.empty();
  }

  public CatalogEntry getByBarcode(String s) throws NoResultException {
    return CatalogEntry.getByBarcode(s).orElseThrow(NoResultException::new);
  }

  Collection<PreOrder> getPreOrdersByUser(User user) {
    return QueryBuilder.selectAll(PreOrder.class)
        .where(PreOrder_.delivery.isNull(), PreOrder_.user.eq(user))
        .orderBy(PreOrder_.catalogEntry.child(CatalogEntry_.artikelNr).asc())
        .getResultList();
  }

  Collection<PreOrder> getAllPreOrders() {
    return QueryBuilder.selectAll(PreOrder.class)
        .where(PreOrder_.delivery.isNull())
        .orderBy(
            PreOrder_.user.child(User_.username).asc(),
            PreOrder_.catalogEntry.child(CatalogEntry_.artikelNr).asc())
        .getResultList();
  }

  Collection<PreOrder> getUnorderedPreOrders() {
    return QueryBuilder.selectAll(PreOrder.class)
        .where(PreOrder_.delivery.isNull(), PreOrder_.orderedOn.isNull())
        .orderBy(PreOrder_.catalogEntry.child(CatalogEntry_.artikelNr).asc())
        .getResultList();
  }

  static Double containerNetPrice(CatalogEntry entry) {
    try {
        return entry.getPreis() * entry.getBestelleinheitsMenge();
    } catch (NullPointerException e) {
        return null;
    }
  }

  public void close() {
    et.begin();
    delivery.forEach(
        p -> {
          if (p.getUser().isShopUser()) {
            removeLazy(p);
          } else {
            p.setDelivery(Instant.now());
            em.merge(p);
          }
        });
    et.commit();
    em.close();
  }

  public void printCheckList(LocalDate deliveryDate, boolean duplexPrint) {
    // saveData();
    Report report =
        new PreOrderChecklist(
            deliveryDate,
            getAllPreOrders().stream().filter(p -> !isDelivered(p)).collect(Collectors.toList()));
    report.setDuplexPrint(duplexPrint);
    report.sendToPrinter(
        "Abhakplan wird gedruckt...", UnexpectedExceptionHandler::showUnexpectedErrorWarning);
    for (PreOrder p : getAllPreOrders()) {
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
      delivery.addAll(getAllPreOrders());
    }
  }

  public void setAmount(PreOrder preOrder, int amount) {
    preOrder.setAmount(amount);
  }

  private void setAllExported() {
    Instant orderInstant = Instant.now();
    et.begin();
    for (PreOrder o : getUnorderedPreOrders()) {
      o.setOrderedOn(orderInstant);
      em.merge(o);
    }
    et.commit();
  }

  @Key(PermissionKey.ACTION_ORDER_OWN_CONTAINER)
  public void checkOrderOwnContainerPermission() {}

  @Key(PermissionKey.ACTION_ORDER_CONTAINER)
  public void checkUserOrderContainerPermission() {}

  @Key(PermissionKey.ACTION_ORDER_CONTAINER)
  public void checkGeneralOrderPlacementPermission() {}
}
