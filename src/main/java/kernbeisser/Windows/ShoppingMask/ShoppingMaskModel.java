package kernbeisser.Windows.ShoppingMask;

import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleBase;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.MVC.IModel;
import lombok.Data;

@Data
public class ShoppingMaskModel implements IModel<ShoppingMaskUIController> {
  private Article selected = null;
  private double value;
  private SaleSession saleSession;
  private boolean payWindowOpen;

  ShoppingMaskModel(SaleSession saleSession) {
    this.saleSession = saleSession;
    this.value = saleSession.getCustomer().getUserGroup().getValue();
  }

  ShoppingItem getByKbNumber(int kbNumber, int discount, boolean preordered) {
    Article article = Article.getByKbNumber(kbNumber);
    if (article != null) {
      return new ShoppingItem(article, discount, preordered);
    }
    return null;
  }

  ShoppingItem getBySupplierItemNumber(int suppliersNumber, int discount, boolean preordered) {

    Article article = Article.getBySuppliersItemNumber(suppliersNumber);
    if (article != null) {
      return new ShoppingItem(article, discount, preordered);
    }
    if (preordered) {
      ArticleBase articleBase = ArticleBase.getBySuppliersItemNumber(suppliersNumber);
      if (articleBase != null) {
        return new ShoppingItem(articleBase, discount, preordered);
      }
    }
    return null;
  }

  ShoppingItem getByBarcode(long barcode, int discount, boolean preordered) {
    Article article = Article.getByBarcode(barcode);
    if (article != null) {
      return new ShoppingItem(article, discount, preordered);
    }
    if (preordered) {
      ArticleBase articleBase = ArticleBase.getByBarcode(barcode);
      if (articleBase != null) {
        return new ShoppingItem(articleBase, discount, preordered);
      }
    }
    return null;
  }
}
