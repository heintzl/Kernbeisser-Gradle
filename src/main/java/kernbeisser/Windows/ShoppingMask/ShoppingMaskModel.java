package kernbeisser.Windows.ShoppingMask;

import javax.persistence.NoResultException;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Exeptions.NotEnoughCreditException;
import kernbeisser.Windows.MVC.IModel;
import lombok.Data;

@Data
public class ShoppingMaskModel implements IModel<ShoppingMaskUIController> {
  private Article selected = null;
  private double value;
  private SaleSession saleSession;
  private boolean payWindowOpen;

  ShoppingMaskModel(SaleSession saleSession) throws NotEnoughCreditException {
    if (saleSession.getCustomer().getUserGroup().getValue() <= 0
        && !saleSession.getCustomer().mayGoUnderMin()) {
      throw new NotEnoughCreditException();
    }
    this.saleSession = saleSession;
    this.value = saleSession.getCustomer().getUserGroup().getValue();
  }

  ShoppingItem getByKbNumber(int kbNumber, int discount, boolean preordered) {
    return Article.getByKbNumber(kbNumber, true)
        .map(e -> new ShoppingItem(e, discount, preordered))
        .orElse(null);
  }

  ShoppingItem getBySupplierItemNumber(
      Supplier supplier, int suppliersNumber, int discount, boolean preordered) {
    return Article.getBySuppliersItemNumber(supplier, suppliersNumber)
        .map(e -> new ShoppingItem(e, discount, preordered))
        .orElse(null);
  }

  ShoppingItem getByBarcode(long barcode, int discount, boolean preordered)
      throws NoResultException {
    return new ShoppingItem(Article.getByBarcode(barcode), discount, preordered);
  }
}
