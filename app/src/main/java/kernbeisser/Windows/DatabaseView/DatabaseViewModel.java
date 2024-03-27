package kernbeisser.Windows.DatabaseView;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Collection;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class DatabaseViewModel implements IModel<DatabaseViewController> {
  public static final Class<?>[] DATA_ENTITIES =
      new Class[] {
        kernbeisser.DBEntities.Article.class,
        kernbeisser.DBEntities.ArticlePrintPool.class,
        ArticleRepository.class,
        kernbeisser.DBEntities.ArticleStock.class,
        kernbeisser.DBEntities.CatalogEntry.class,
        kernbeisser.DBEntities.IgnoredDialog.class,
        kernbeisser.DBEntities.IgnoredDifference.class,
        kernbeisser.DBEntities.Job.class,
        kernbeisser.DBEntities.Offer.class,
        kernbeisser.DBEntities.Permission.class,
        kernbeisser.DBEntities.Post.class,
        kernbeisser.DBEntities.PreOrder.class,
        kernbeisser.DBEntities.PriceList.class,
        kernbeisser.DBEntities.Purchase.class,
        kernbeisser.DBEntities.SaleSession.class,
        kernbeisser.DBEntities.SettingValue.class,
        kernbeisser.DBEntities.Shelf.class,
        kernbeisser.DBEntities.ShoppingItem.class,
        kernbeisser.DBEntities.Supplier.class,
        kernbeisser.DBEntities.SurchargeGroup.class,
        kernbeisser.DBEntities.SystemSetting.class,
        kernbeisser.DBEntities.Transaction.class,
        kernbeisser.DBEntities.User.class,
        kernbeisser.DBEntities.UserGroup.class,
        kernbeisser.DBEntities.UserSettingValue.class,
      };

  public <T> Collection<T> getAllOfClass(Class<T> clazz, String condition) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    // this is unsafe, but we want the user to specify the where filter, so this the easiest option
    // for now!
    return em.createQuery("select o from " + clazz.getSimpleName() + " o " + condition, clazz)
        .getResultList();
  }
}
