package kernbeisser.Windows.EditSurchargeTable;

import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeTable;
import kernbeisser.Enums.Mode;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Model;

public class EditSurchargeTableModel implements Model<EditSurchargeTableController> {
  private final SurchargeTable source;
  private final Mode mode;

  public EditSurchargeTableModel(SurchargeTable surchargeTable, Mode mode) {
    this.source = surchargeTable;
    this.mode = mode;
  }

  Collection<Supplier> getAllSuppliers() {
    return Supplier.getAll(null);
  }

  boolean doAction(SurchargeTable table) {
    try {
      switch (mode) {
        case REMOVE:
          remove(table);
          break;
        case EDIT:
          edit(table);
          break;
        case ADD:
          table.setStid(0);
          add(table);
          break;
      }
      return true;
    } catch (PersistenceException e) {
      Tools.showUnexpectedErrorWarning(e);
      return false;
    }
  }

  private void edit(SurchargeTable surchargeTable) {
    Tools.edit(surchargeTable.getStid(), surchargeTable);
  }

  private void add(SurchargeTable surchargeTable) {
    EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.persist(surchargeTable);
    em.flush();
    et.commit();
    em.close();
  }

  private void remove(SurchargeTable surchargeTable) {
    EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.remove(em.find(SurchargeTable.class, surchargeTable.getStid()));
    em.flush();
    et.commit();
    em.close();
  }

  public SurchargeTable getSource() {
    return source;
  }
}
