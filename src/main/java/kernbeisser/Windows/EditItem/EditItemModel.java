package kernbeisser.Windows.EditItem;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.ContainerDefinition;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.VAT;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Collection;

public class EditItemModel implements Model<EditItemController> {
    private final Mode mode;
    private Article article;

    EditItemModel(Article article, Mode mode) {
        this.mode = mode;
        this.article = article;
    }

    Article getSource() {
        return article;
    }

    boolean doAction(Article article) {
        try {
            switch (mode) {
                case ADD:
                    addItem(article);
                    break;
                case EDIT:
                    editItem(article);
                    break;
                case REMOVE:
                    removeItem(article);
                    break;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private void removeItem(Article article) {
        Tools.delete(Article.class, article.getIid());
    }

    private void editItem(Article article) {
        Tools.edit(article.getIid(), article);
    }

    boolean kbNumberExists(int kbNumber) {
        EntityManager em = DBConnection.getEntityManager();
        boolean exists = em.createQuery("select id from Article where kbNumber = " + kbNumber).getResultList().size() > 0;
        em.close();
        return exists;
    }

    boolean barcodeExists(long barcode) {
        EntityManager em = DBConnection.getEntityManager();
        boolean exists = em.createQuery("select id from Article where barcode = " + barcode).getResultList().size() > 0;
        em.close();
        return exists;
    }

    private void addItem(Article article) {
        article.setSurcharge(article.getSurchargeTable().getSurcharge());
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.persist(Tools.createNewPersistenceInstance(article,Article::new));
        em.flush();
        et.commit();
        em.close();
    }

    MetricUnits[] getAllUnits() {
        return MetricUnits.values();
    }

    ContainerDefinition[] getAllContainerDefinitions() {
        return ContainerDefinition.values();
    }

    VAT[] getAllVATs() {
        return VAT.values();
    }

    Collection<Supplier> getAllSuppliers() {
        return Supplier.getAll(null);
    }

    Collection<PriceList> getAllPriceLists() {
        return PriceList.getAll(null);
    }

    public Mode getMode() {
        return mode;
    }
}
