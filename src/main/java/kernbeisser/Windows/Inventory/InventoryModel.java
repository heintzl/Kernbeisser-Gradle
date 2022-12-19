package kernbeisser.Windows.Inventory;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class InventoryModel implements IModel<InventoryController> {

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
        PriceList.getAll(null).stream()
            .filter(p -> p.getAllArticles().size() > 0)
            .sorted(Comparator.comparing(PriceList::getName))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    for (Shelf s : Shelf.getAll()) {
      result.removeAll(s.getPriceLists());
    }
    return result;
  }
}
