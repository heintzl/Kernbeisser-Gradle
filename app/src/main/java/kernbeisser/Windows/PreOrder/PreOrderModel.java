package kernbeisser.Windows.PreOrder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
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
import kernbeisser.Enums.Delivery;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Export.CSVExport;
import kernbeisser.Reports.DeliveryBillReport;
import kernbeisser.Reports.PreOrderChecklist;
import kernbeisser.Reports.Report;
import kernbeisser.Useful.Constants;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class PreOrderModel implements IModel<PreOrderController> {

  private final EntityManager em = DBConnection.getEntityManager();
  private final EntityTransaction et = em.getTransaction();
  @Getter private final Map<PreOrder, Delivery> delivery = new HashMap<>();
  private final Set<PreOrder> dirty = new HashSet<>();

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
    p.setFirstWeekOfDelivery(newPreOrder.getFirstWeekOfDelivery());
    p.setLatestWeekOfDelivery(newPreOrder.getLatestWeekOfDelivery());
    p.setAlternativePermitted(newPreOrder.isAlternativePermitted());
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

  List<PreOrder> getExportablePreorders() {
    List<PreOrder> unexportedPreorders =
        QueryBuilder.selectAll(PreOrder.class)
            .where(PreOrder_.delivery.isNull(), PreOrder_.orderedOn.isNull())
            .orderBy(PreOrder_.catalogEntry.child(CatalogEntry_.artikelNr).asc())
            .getResultStream(em)
            .filter(p -> !isPostponed(p))
            .toList();
    return unexportedPreorders;
  }

  static Double containerNetPrice(CatalogEntry entry) {
    try {
      return entry.getPreis() * entry.getBestelleinheitsMenge();
    } catch (NullPointerException e) {
      return null;
    }
  }

  public boolean isSlowOrder(CatalogEntry catalogEntry) {
    return catalogEntry.getBezeichnung().contains("*V*");
  }

  public void close() {
    et.begin();
    delivery.forEach(
        (p, d) -> {
          if (p.isShopOrder()) {
            removeLazy(p);
          } else {
            p.setDelivery(Instant.now());
            p.setAlternativeDelivery(d == Delivery.ALTERNATIVE_DELIVERED);
            p.setDeliveryType(delivery.get(p));
              p.setDelivery(Instant.now());
            em.merge(p);
          }
          dirty.remove(p);
        });
    dirty.forEach(em::merge);
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
        delivery.put(p, Delivery.DELIVERED);
      }
    }
  }

  public void printDeliveryBills() {
    new DeliveryBillReport()
        .sendToPrinter(
            "Erstelle Lieferscheine", UnexpectedExceptionHandler::showUnexpectedErrorWarning);
  }

  public boolean exportPreOrders(Component parent, List<PreOrder> preOrders) {
    int preorderNr = Setting.LAST_EXPORTED_PREORDER_NR.getIntValue() + 1;
    String defaultFilename = "KornkraftBestellung_" + String.format("%05d.csv", preorderNr);
    boolean result = CSVExport.exportPreOrder(parent, preOrders, defaultFilename);
    if (result) {
      Instant orderInstant = Instant.now();
      et.begin();
      for (PreOrder o : preOrders) {
        o.setOrderedOn(orderInstant);
        em.merge(o);
      }
      et.commit();
      Setting.LAST_EXPORTED_PREORDER_NR.changeValue(preorderNr);
    }
    return result;
  }

  enum toggleResult {
    OK,
    NOT_YET_ORDERED,
    NOT_PERMITTED,
    MISSING_ALTERNATIVE;
  }

  toggleResult toggleDelivery(PreOrder p, Delivery newState) {
    if (p.getOrderedOn() == null) {
      return toggleResult.NOT_YET_ORDERED;
    }
    Delivery currentState = delivery.get(p);
    if (currentState != newState) {
      if (newState == Delivery.ALTERNATIVE_DELIVERED) {
        if (!p.isAlternativePermitted()) {
          return toggleResult.NOT_PERMITTED;
        }
        if (p.getAlternativeCatalogEntry() == null) {
          return toggleResult.MISSING_ALTERNATIVE;
        }
      }
      delivery.put(p, newState);
    } else {
      delivery.remove(p);
    }
    return toggleResult.OK;
  }

  boolean isDelivered(PreOrder p) {
    return delivery.get(p) == Delivery.DELIVERED;
  }

  boolean isAlternativeDelivered(PreOrder p) {
    return delivery.get(p) == Delivery.ALTERNATIVE_DELIVERED;
  }

  public void setAllDelivered(boolean allDelivered) {
    delivery.clear();
    if (allDelivered) {
      for (PreOrder p : getAllPreOrders()) {
        if (p.getOrderedOn() != null) {
          delivery.put(p, Delivery.DELIVERED);
        }
      }
    }
  }

  public void setAmount(PreOrder preOrder, int amount) {
    preOrder.setAmount(amount);
    dirty.add(preOrder);
  }

  public void setComment(PreOrder preOrder, String comment) {
    preOrder.setComment(comment);
    dirty.add(preOrder);
  }

  public void setAlternative(PreOrder p, CatalogEntry entry) {
    p.setAlternativeCatalogEntry(entry);
    dirty.add(p);
  }

  private static int getWeekOfCreation(PreOrder preOrder) {
    return LocalDate.ofInstant(preOrder.getCreateDate(), Date.CURRENT_ZONE)
        .get(ChronoField.ALIGNED_WEEK_OF_YEAR);
  }

  public static boolean isPostponed(PreOrder p) {
    Integer firstWeekOfDelivery = p.getFirstWeekOfDelivery();
    if (firstWeekOfDelivery == null) {
      return false;
    }
    return Constants.CURRENT_WEEK_OF_YEAR < firstWeekOfDelivery + 1
        || firstWeekOfDelivery <= getWeekOfCreation(p);
  }

  public static boolean isOverdue(PreOrder p) {
    Integer latestWeekOfDelivery = p.getLatestWeekOfDelivery();
    if (latestWeekOfDelivery == null) {
      return true;
    }
    return Constants.CURRENT_WEEK_OF_YEAR >= latestWeekOfDelivery
        && latestWeekOfDelivery > getWeekOfCreation(p);
  }

  public static boolean isDateAllowed(LocalDate date, LocalDate compareDate, boolean last) {
    boolean comparesWell =
        Optional.ofNullable(compareDate)
            .map(d -> last ? !date.isAfter(d) : !date.isBefore(d))
            .orElse(true);
    return comparesWell
        && !date.isBefore(LocalDate.now())
        && date.getDayOfWeek() == PreOrderController.getWeekdayOfDelivery();
  }

  @Key(PermissionKey.ACTION_ORDER_OWN_CONTAINER)
  public void checkOrderOwnContainerPermission() {}

  @Key(PermissionKey.ACTION_ORDER_CONTAINER)
  public void checkUserOrderContainerPermission() {}

  @Key(PermissionKey.ACTION_ORDER_CONTAINER)
  public void checkGeneralOrderPlacementPermission() {}
}
