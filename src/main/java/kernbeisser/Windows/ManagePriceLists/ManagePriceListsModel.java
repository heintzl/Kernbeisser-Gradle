package kernbeisser.Windows.ManagePriceLists;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Reports.PriceListReport;
import kernbeisser.Reports.Report;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import org.hibernate.Session;

public class ManagePriceListsModel implements IModel<ManagePriceListsController> {

  void savePriceList(String name, PriceList superPriceList) {
    PriceList.savePriceList(name, superPriceList);
  }

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
    em.createQuery("update PriceList set superPriceList = :d where id = :t")
        .setParameter("d", destination)
        .setParameter("t", target.getId())
        .executeUpdate();
    em.flush();
  }

  public void add(Node<PriceList> selectedNode, String requestName) throws PersistenceException {
    PriceList newPriceList = new PriceList();
    newPriceList.setName(requestName);
    newPriceList.setSuperPriceList(selectedNode.getValue());
    Tools.persist(newPriceList);
  }

  public void moveItems(PriceList target, PriceList destination) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createQuery("UPDATE Article set priceList = :d where priceList = :t")
        .setParameter("t", target)
        .setParameter("d", destination)
        .executeUpdate();
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
