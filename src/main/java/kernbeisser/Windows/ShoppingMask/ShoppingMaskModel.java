package kernbeisser.Windows.ShoppingMask;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleBase;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.Model;

public class ShoppingMaskModel implements Model<ShoppingMaskUIController> {
  private Article selected = null;
  private double value;
  private SaleSession saleSession;

  ShoppingMaskModel(SaleSession saleSession) {
    this.saleSession = saleSession;
    this.value = saleSession.getCustomer().getUserGroup().getValue();
  }

  Article searchItem(String itemNumber) {
    EntityManager em = DBConnection.getEntityManager();
    try {
      return em.createQuery(
              "select i from Article i where kbNumber = '" + itemNumber + "'", Article.class)
          .getSingleResult();
    } catch (NoResultException e) {
      try {
        return em.createQuery(
                "select i from Article i where i.barcode like '%" + itemNumber + "'", Article.class)
            .setMaxResults(1)
            .getSingleResult();
      } catch (NoResultException e1) {
        return null;
      }
    }
  }

  Collection<Article> searchItems(
      String search,
      boolean searchName,
      boolean searchPriceList,
      boolean searchKBNumber,
      boolean searchBarcode) {
    Collection<Article> out = new ArrayList<>();
    if (searchName || searchPriceList || searchKBNumber || searchBarcode) {
      String query =
          "select i from Article i where "
              + (searchBarcode ? "barcode like '%sh' OR " : "")
              + (searchKBNumber ? "kbNumber like 'sh' OR " : "")
              + (searchName ? "name like 'sh%' OR " : "")
              + (searchPriceList ? "priceList.name like 'sh%' OR " : "");
      query = query.substring(0, query.length() - 3).replaceAll("sh", search);
      EntityManager em = DBConnection.getEntityManager();
      out = em.createQuery(query, Article.class).getResultList();
      em.close();
    }
    return out;
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

  public double getValue() {
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

  ShoppingItem getByKbNumber(int kbNumber, int discount, boolean preordered) {
    return new ShoppingItem(Article.getByKbNumber(kbNumber), discount, preordered);
  }

  ShoppingItem getBySupplierItemNumber(int suppliersNumber, int discount, boolean preordered) {

    Article article = Article.getBySuppliersItemNumber(suppliersNumber);
    if (article != null) {
      return new ShoppingItem(article, discount, preordered);
    }
    return preordered
        ? new ShoppingItem(
            ArticleBase.getBySuppliersItemNumber(suppliersNumber), discount, preordered)
        : null;
  }

  ShoppingItem getByBarcode(long barcode, int discount, boolean preordered) {
    Article article = Article.getByBarcode(barcode);
    if (article != null) {
      return new ShoppingItem(article, discount, preordered);
    }
    return preordered
        ? new ShoppingItem(ArticleBase.getByBarcode(barcode), discount, preordered)
        : null;
  }
}
