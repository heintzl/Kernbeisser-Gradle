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
import javax.persistence.NoResultException;
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
            Tools.showUnexpectedErrorWarning(e);
            return false;
        }

    }

    private void removeItem(Article article) {
        Tools.delete(Article.class, article.getIid());
    }

    private void editItem(Article article) {
        Tools.edit(article.getIid(), article);
    }

    int kbNumberExists(int kbNumber) {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("select id from Article where kbNumber = " + kbNumber, Integer.class)
                              .getSingleResult();
        }catch (NoResultException e){
            return -1;
        }finally {
            em.close();
        }
    }

    int barcodeExists(long barcode) {
        EntityManager em = DBConnection.getEntityManager();
        try{
            return em.createQuery("select id from Article where barcode = " + barcode,Integer.class).getSingleResult();
        }catch (NoResultException e){
            return -1;
        }finally {
            em.close();
        }
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

    public int nextUnusedArticleNumber(int kbNumber) {
        EntityManager em = DBConnection.getEntityManager();
        int out = em.createQuery("select i.kbNumber from Article i where i.kbNumber > :last and Not exists (select k from Article k where kbNumber = i.kbNumber+1)",Integer.class)
                    .setMaxResults(1)
                    .setParameter("last",kbNumber)
                    .getSingleResult()+1;
        em.close();
        return out;
    }

    public boolean nameExists(String name) {
        EntityManager em = DBConnection.getEntityManager();
        try {
            em.createQuery("select i from Article i where i.name like :name")
              .setMaxResults(1)
              .setParameter("name", name)
              .getSingleResult();
            em.close();
            return false;
        }catch (NoResultException e){
            em.close();
            return true;
        }
    }
}
