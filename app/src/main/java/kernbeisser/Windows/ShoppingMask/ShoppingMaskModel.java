package kernbeisser.Windows.ShoppingMask;

import jakarta.persistence.NoResultException;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.EntityWrapper.ObjectState;
import kernbeisser.Exeptions.NotEnoughCreditException;
import kernbeisser.Windows.MVC.IModel;
import lombok.Data;

@Data
public class ShoppingMaskModel implements IModel<ShoppingMaskController> {
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

  Article getArticleByKbNumber(int kbNumber) {
    return ArticleRepository.getByKbNumber(kbNumber, true).map(ObjectState::getValue).orElse(null);
  }

  ShoppingItem getItemByKbNumber(int kbNumber, int discount, boolean preordered) {
    return ArticleRepository.getByKbNumber(kbNumber, true)
        .map(e -> new ShoppingItem(e, discount, preordered))
        .orElse(null);
  }

  Article getArticleBySupplierItemNumber(Supplier supplier, int suppliersNumber) {
    return ArticleRepository.getBySuppliersItemNumber(supplier, suppliersNumber).orElse(null);
  }

  ShoppingItem getItemBySupplierItemNumber(
      Supplier supplier, int suppliersNumber, int discount, boolean preordered) {
    return ArticleRepository.getBySuppliersItemNumber(supplier, suppliersNumber)
        .map(e -> new ShoppingItem(e, discount, preordered))
        .orElse(null);
  }

  Article getByBarcode(long barcode) throws NoResultException {
    return (ArticleRepository.getByBarcode(barcode).orElseThrow(NoResultException::new));
  }
}
