package kernbeisser.Windows.ManagePriceLists;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Reports.PriceListReport;
import kernbeisser.Reports.Report;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import org.hibernate.Session;

public class ManagePriceListsModel implements IModel<ManagePriceListsController> {

  public void deletePriceList(PriceList toDelete) throws PersistenceException {
    PriceList.deletePriceList(toDelete);
  }

  public void renamePriceList(PriceList toRename, String newName) throws PersistenceException {
    toRename.setName(newName);
    Tools.runInSession(em -> em.unwrap(Session.class).update(toRename));
  }

  public void setSuperPriceList(PriceList target, PriceList destination) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    PriceList targetPriceListInDB = em.find(PriceList.class, target.getId());
    if (destination.getId() == 0) {
      targetPriceListInDB.setSuperPriceList(null);
    } else {
      targetPriceListInDB.setSuperPriceList(em.find(PriceList.class, destination.getId()));
    }
    em.persist(targetPriceListInDB);
    em.flush();
  }

  public void add(Node<PriceList> selectedNode, String requestName) throws PersistenceException {
    PriceList newPriceList = new PriceList(requestName);
    if (selectedNode.getValue().getId() != 0) {
      newPriceList.setSuperPriceList(selectedNode.getValue());
    }
    Tools.persist(newPriceList);
  }

  public void moveItems(Collection<Article> articles, PriceList destination) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (Article a : articles) {
      a.setPriceList(destination);
      em.merge(a);
    }
    em.flush();
  }

  public void print(PriceList selectedList) {
    AtomicBoolean printed = new AtomicBoolean(true);
    PriceListReport report = new PriceListReport(selectedList);
    report.sendToPrinter(
        "Preisliste wird gedruckt...",
        e -> {
          printed.set(false);
          Report.pdfExportException(e);
        });
    // TODO selectedList.setLastPrint(Instant.now()); and commit to database
  }
}
