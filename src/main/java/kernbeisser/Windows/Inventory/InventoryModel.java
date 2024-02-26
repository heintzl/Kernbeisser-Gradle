package kernbeisser.Windows.Inventory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.FieldCondition;
import kernbeisser.DBEntities.ArticleStock;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Enums.Setting;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class InventoryModel implements IModel<InventoryController> {

  Map<Shelf, InventoryShelf> shelfValueMap = new HashMap<>();

  public double getShelfNetValue(Shelf shelf) {
    InventoryShelf inventoryShelf = shelfValueMap.get(shelf);
    if (inventoryShelf == null) {
      return 0.0;
    }
    return inventoryShelf.getNetValue();
  }

  public double getShelfDepositValue(Shelf shelf) {
    InventoryShelf inventoryShelf = shelfValueMap.get(shelf);
    if (inventoryShelf == null) {
      return 0.0;
    }
    return inventoryShelf.getDepositValue();
  }

  public void updateShelfValueMap() {
    shelfValueMap.clear();
    for (Shelf shelf : DBConnection.getAll(Shelf.class)) {
      shelfValueMap.put(shelf, new InventoryShelf(shelf));
    }
  }

  Collection<Shelf> searchShelf(String search, int max) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    int searchInt = Integer.MIN_VALUE;
    ;
    try {
      searchInt = Integer.parseInt(search);
    } catch (NumberFormatException n) {
    } catch (Exception e) {
      Tools.showUnexpectedErrorWarning(e);
    }
    et.begin();
    return em.createQuery(
            "select s from Shelf s where upper(s.location) like :s or upper(s.comment) like :s or shelfNo = :i order by shelfNo",
            Shelf.class)
        .setParameter("s", "%" + search.toUpperCase(Locale.ROOT))
        .setParameter("i", searchInt)
        .getResultList();
  }

  public static Set<PriceList> priceListsWithoutShelf() {
    Set<PriceList> result =
        DBConnection.getAll(PriceList.class).stream()
            .filter(p -> p.getAllArticles().isEmpty())
            .sorted(Comparator.comparing(PriceList::getName))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    for (Shelf s : Shelf.getAll()) {
      result.removeAll(s.getPriceLists());
    }
    return result;
  }

  public void clearInventory() {
    LocalDate inventoryDate = Setting.INVENTORY_SCHEDULED_DATE.getDateValue();
    if (inventoryDate.isBefore(LocalDate.now())) {
      return;
    }
    List<ArticleStock> stocksToRemove =
        DBConnection.getConditioned(
                ArticleStock.class, new FieldCondition("inventoryDate", inventoryDate))
            .stream()
            .filter(
                s ->
                    s.getCreateDate()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .isBefore(inventoryDate))
            .collect(Collectors.toList());
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (ArticleStock stock : stocksToRemove) {
      em.remove(em.find(ArticleStock.class, stock.getId()));
    }
  }
}
