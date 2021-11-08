package kernbeisser.Windows.Inventory;

import java.util.Collection;
import java.util.Locale;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Windows.Inventory.Report.InventoryReportDTO;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class InventoryModel implements IModel<InventoryController> {

  Collection<Shelf> searchShelf(String search, int max) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select s from Shelf s where upper(s.location) like :s or upper(s.comment) like :s",
            Shelf.class)
        .setParameter("s", "%" + search.toUpperCase(Locale.ROOT))
        .getResultList();
  }

  void printInventoryResults() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    InventoryReportDTO.generate(em);
  }

  void printCountingReport() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Collection<Shelf> shelves =
        em.createQuery("select s from Shelf s", Shelf.class).getResultList();
    for (Shelf shelf : shelves) {
      printCountingReport(shelf, shelf.getAllArticles());
    }
  }

  void printInventoryResults(InventoryReportDTO reportData) {}

  void printCountingReport(Shelf shelf, Collection<Article> articles) {}
}
