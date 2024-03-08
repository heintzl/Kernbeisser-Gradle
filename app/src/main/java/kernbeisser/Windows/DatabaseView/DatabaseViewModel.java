package kernbeisser.Windows.DatabaseView;

import java.util.Collection;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;

public class DatabaseViewModel implements IModel<DatabaseViewController> {
  public static final Class<?>[] DATA_ENTITIES =
      new Class[] {
        kernbeisser.DBEntities.Article.class,
        kernbeisser.DBEntities.ArticlePrintPool.class,
        kernbeisser.DBEntities.Articles.class,
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

  public Collection<?> getAllOfClass(Class<?> clazz, String condition) {
    return Tools.getAll(clazz, condition);
  }
}
