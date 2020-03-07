package kernbeisser.Windows.EditSurchargeTable;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeTable;
import kernbeisser.Enums.Mode;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import java.util.Collection;

public class EditSurchargeTableModel implements Model {
    private final SurchargeTable surchargeTable;
    private final Mode mode;

    Collection<Supplier> getAllSuppliers() {
        return Supplier.getAll(null);
    }

    public EditSurchargeTableModel(SurchargeTable surchargeTable, Mode mode) {
        this.surchargeTable = surchargeTable;
        this.mode = mode;
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
                    add(Tools.mergeWithoutId(surchargeTable));
                    break;
            }
            return true;
        } catch (PersistenceException e) {
            e.printStackTrace();
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

    public SurchargeTable getSurchargeTable() {
        return surchargeTable;
    }
}
