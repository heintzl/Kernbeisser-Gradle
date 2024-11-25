package kernbeisser.Windows.Inventory;

import static kernbeisser.DBConnection.ExpressionFactory.lower;
import static kernbeisser.DBConnection.PredicateFactory.like;
import static kernbeisser.DBConnection.PredicateFactory.or;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.ArticleStock;
import kernbeisser.DBEntities.ArticleStock_;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.DBEntities.Shelf_;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class InventoryModel implements IModel<InventoryController> {

  Map<Integer, InventoryShelf> shelfValueMap = new HashMap<>();

  public double getShelfNetValue(Shelf shelf) {
    InventoryShelf inventoryShelf = shelfValueMap.get(shelf.getId());
    if (inventoryShelf == null) {
      return 0.0;
    }
    return inventoryShelf.getNetValue();
  }

  public double getShelfDepositValue(Shelf shelf) {
    InventoryShelf inventoryShelf = shelfValueMap.get(shelf.getId());
    if (inventoryShelf == null) {
      return 0.0;
    }
    return inventoryShelf.getDepositValue();
  }

  public void updateShelfValueMap() {
    shelfValueMap.clear();
    for (Shelf shelf : DBConnection.getAll(Shelf.class)) {
      shelfValueMap.put(shelf.getId(), new InventoryShelf(shelf));
    }
  }

  Collection<Shelf> searchShelf(String search, int max) {
    int searchInt = Integer.MIN_VALUE;
    try {
      searchInt = Integer.parseInt(search);
    } catch (NumberFormatException ignored) {
    } catch (Exception e) {
      UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
    }
    String searchPattern = "%" + search.toLowerCase(Locale.ROOT);
    return QueryBuilder.selectAll(Shelf.class)
        .where(
            or(
                like(lower(Shelf_.location), searchPattern),
                like(lower(Shelf_.comment), searchPattern),
                Shelf_.shelfNo.eq(searchInt)))
        .orderBy(Shelf_.shelfNo.asc())
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
                ArticleStock.class, ArticleStock_.inventoryDate.eq(inventoryDate))
            .stream()
            .filter(
                s ->
                    s.getCreateDate()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .isBefore(inventoryDate))
            .toList();
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (ArticleStock stock : stocksToRemove) {
      em.remove(em.find(ArticleStock.class, stock.getId()));
    }
  }
}
