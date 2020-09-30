package kernbeisser.Windows.ManagePriceLists;

import javax.persistence.PersistenceException;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import org.hibernate.Session;

public class ManagePriceListsModel implements IModel<ManagePriceListsController> {

  void savePriceList(String name, PriceList superPriceList) {
    PriceList.savePriceList(name, superPriceList);
  }

  public void deletePriceList(PriceList toDelete) throws PersistenceException {
    PriceList.deletePriceList(toDelete);
  }

  public void renamePriceList(PriceList toRename, String newName) {
    toRename.setName(newName);
    Tools.runInSession(em -> em.unwrap(Session.class).update(toRename));
  }
}
