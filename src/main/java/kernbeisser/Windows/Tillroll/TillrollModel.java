package kernbeisser.Windows.Tillroll;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.persistence.EntityManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;

public class TillrollModel implements IModel<TillrollController> {

  @Getter private final ExportTypes[] exportTypes = ExportTypes.values();

  private List<ShoppingItem> getTillrollitems(Instant start, Instant end) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    List<ShoppingItem> items =
        em.createQuery(
                "select si from ShoppingItem si where si.purchase.createDate between :stdate and :endate",
                ShoppingItem.class)
            .setParameter("stdate", start)
            .setParameter("endate", end)
            .getResultList();
    em.close();
    return items;
  }

  public TillrollModel() {}

  public void exportTillroll(ExportTypes exportType, int days)
      throws UnsupportedOperationException {
    Instant now = Instant.now();
    List<ShoppingItem> items =
        getTillrollitems(now.minus(days, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS), now);
    switch (exportType) {
      case CSV:
        throw new UnsupportedOperationException();
      case JSON:
        throw new UnsupportedOperationException();
      case PDF:
        throw new UnsupportedOperationException();
      case PRINT:
        throw new UnsupportedOperationException();
    }
  }
}
