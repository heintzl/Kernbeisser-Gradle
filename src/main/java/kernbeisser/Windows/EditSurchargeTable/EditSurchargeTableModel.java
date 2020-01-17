package kernbeisser.Windows.EditSurchargeTable;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.Supplier;
import kernbeisser.DBEntitys.SurchargeTable;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Collection;

public class EditSurchargeTableModel implements Model {
    private final SurchargeTable surchargeTable;
    private final Mode mode;

    Collection<Supplier> getAllSuppliers(){
        return Supplier.getAll(null);
    }

    public EditSurchargeTableModel(SurchargeTable surchargeTable, Mode mode) {
        this.surchargeTable = surchargeTable;
        this.mode = mode;
    }

    void doAction(SurchargeTable table){
        switch (mode){
            case REMOVE:
                remove(table);
                break;
            case EDIT:
                edit(table);
                break;
            case ADD:
                add(new SurchargeTable(table));
                break;
        }
    }

    private void edit(SurchargeTable surchargeTable){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        SurchargeTable edit = em.find(SurchargeTable.class,surchargeTable);
        edit.setFrom(surchargeTable.getFrom());
        edit.setTo(surchargeTable.getTo());
        edit.setSurcharge(surchargeTable.getSurcharge());
        edit.setSupplier(surchargeTable.getSupplier());
        em.persist(edit);
        em.flush();
        et.commit();
        em.close();
    }
    private void add(SurchargeTable surchargeTable){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.persist(surchargeTable);
        em.flush();
        et.commit();
        em.close();
    }
    private void remove(SurchargeTable surchargeTable){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.remove(surchargeTable);
        em.flush();
        et.commit();
        em.close();
    }

    public SurchargeTable getSurchargeTable() {
        return surchargeTable;
    }
}
