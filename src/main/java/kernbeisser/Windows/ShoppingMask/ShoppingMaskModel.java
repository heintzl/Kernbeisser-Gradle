package kernbeisser.Windows.ShoppingMask;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Price.PriceCalculator;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Collection;

public class ShoppingMaskModel implements Model {
    private Article selected = null;
    private int value;
    private Collection<ShoppingItem> shoppingCart = new ArrayList<>();
    private SaleSession saleSession;

    ShoppingMaskModel(SaleSession saleSession) {
        this.saleSession = saleSession;
    }

    Article searchItem(String itemNumber) {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("select i from Article i where i.kbNumber = '" + itemNumber + "'", Article.class)
                     .getSingleResult();
        } catch (NoResultException e) {
            try {
                return em.createQuery("select i from Article i where i.barcode like '%" + itemNumber + "'", Article.class)
                         .setMaxResults(1)
                         .getSingleResult();
            } catch (NoResultException e1) {
                return null;
            }
        }
    }

    Collection<ShoppingItem> getShoppingCart() {
        return shoppingCart;
    }

    int calculateTotalPrice() {
        int out = 0;
        for (ShoppingItem item : shoppingCart) {
            out += PriceCalculator.getShoppingItemPrice(item, saleSession.getCustomer().getSolidaritySurcharge());
        }
        return out;
    }

    Collection<Article> searchItems(String search, boolean searchName, boolean searchPriceList, boolean searchKBNumber,
                                    boolean searchBarcode) {
        Collection<Article> out = new ArrayList<>();
        if (searchName || searchPriceList || searchKBNumber || searchBarcode) {
            String query = "select i from Article i where " +
                           (searchBarcode ? "barcode like '%sh' OR " : "") +
                           (searchKBNumber ? "kbNumber like 'sh' OR " : "") +
                           (searchName ? "name like 'sh%' OR " : "") +
                           (searchPriceList ? "priceList.name like 'sh%' OR " : "");
            query = query.substring(0, query.length() - 3).replaceAll("sh", search);
            EntityManager em = DBConnection.getEntityManager();
            out = em.createQuery(query, Article.class).getResultList();
            em.close();
        }
        return out;
    }

    Article getByKbNumber(int kbNumber) {
        return Article.getByKbNumber(kbNumber);
    }

    boolean editBarcode(int itemId, long newBarcode) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        Article update = em.find(Article.class, itemId);
        update.setBarcode(newBarcode);
        try {
            em.persist(update);
            em.flush();
        } catch (Exception e) {
            et.rollback();
            em.close();
            return false;
        }
        et.commit();
        em.close();
        return true;
    }


    Collection<Article> getAllItemsWithoutBarcode() {
        return Article.getAll("where barcode is null order by name asc");
    }

    public Article getSelected() {
        return selected;
    }

    public void setSelected(Article selected) {
        this.selected = selected;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public SaleSession getSaleSession() {
        return saleSession;
    }

    public void setSaleSession(SaleSession saleSession) {
        this.saleSession = saleSession;
    }

    Article getBySupplierItemNumber(int suppliersNumber) {
        return Article.getBySuppliersItemNumber(suppliersNumber);
    }
}
