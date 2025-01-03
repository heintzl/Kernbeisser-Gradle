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
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;

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
    if (preOrder.getUser().equals(User.getKernbeisserUser())) {
      CatalogEntry e = em.find(CatalogEntry.class, preOrder.getCatalogEntry().getId());
      em.persist(e);
    }
    em.persist(preOrder);
    et.commit();
  }

  public void edit(PreOrder preOrder, PreOrder newPreOrder) {
    Objects.requireNonNull(newPreOrder.getUser());
    et.begin();
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
    et.commit();
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
      if (article.get().getSupplier().equals(Supplier.KK_SUPPLIER)) {
        return getEntryByKkNumber(article.get().getSuppliersItemNumber());
      }
    }
    return Optional.empty();
  }

  public CatalogEntry getByBarcode(String s) throws NoResultException {
    return CatalogEntry.getByBarcode(s).orElseThrow(NoResultException::new);
  }

  Collection<PreOrder> getAllPreOrders(boolean restricted) {
    if (restricted) {
      return QueryBuilder.selectAll(PreOrder.class)
          .where(PreOrder_.delivery.isNull(), PreOrder_.user.eq(LogInModel.getLoggedIn()))
          .orderBy(PreOrder_.catalogEntry.child(CatalogEntry_.artikelNr).asc())
          .getResultList();
    } else {
      return QueryBuilder.selectAll(PreOrder.class)
          .where(PreOrder_.delivery.isNull())
          .orderBy(
              PreOrder_.user.child(User_.username).asc(),
              PreOrder_.catalogEntry.child(CatalogEntry_.artikelNr).asc())
          .getResultList();
    }
  }

  Collection<PreOrder> getUnorderedPreOrders() {
    return QueryBuilder.selectAll(PreOrder.class)
        .where(PreOrder_.delivery.isNull(), PreOrder_.orderedOn.isNull())
        .orderBy(PreOrder_.catalogEntry.child(CatalogEntry_.artikelNr).asc())
        .getResultList();
  }

  static double containerNetPrice(CatalogEntry entry) throws NullPointerException {
    return entry.getPreis() * entry.getBestelleinheitsMenge();
  }

  public void close() {
    et.begin();
    delivery.forEach(
        p -> {
          if (p.getUser().equals(User.getKernbeisserUser())) {
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
            getAllPreOrders(false).stream()
                .filter(p -> !isDelivered(p))
                .collect(Collectors.toList()));
    report.setDuplexPrint(duplexPrint);
    report.sendToPrinter(
        "Abhakplan wird gedruckt...", UnexpectedExceptionHandler::showUnexpectedErrorWarning);
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
    et.begin();
    for (PreOrder o : getUnorderedPreOrders()) {
      o.setOrderedOn(orderInstant);
      em.merge(o);
    }
    et.commit();
  }
}
