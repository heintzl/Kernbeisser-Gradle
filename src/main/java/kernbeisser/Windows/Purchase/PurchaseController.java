package kernbeisser.Windows.Purchase;

import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Price.PriceCalculator;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import java.util.Collection;

public class PurchaseController implements Controller {
    private final PurchaseModel model;
    private final PurchaseView view;

    public PurchaseController(Window current, Purchase purchase) {
        model = new PurchaseModel(purchase);
        view = new PurchaseView(current, this);
        view.setCustomer(purchase.getSession().getCustomer().getUsername());
        view.setSeller(purchase.getSession().getSeller().getUsername());
        view.setDate(purchase.getCreateDate().toString());
        Collection<ShoppingItem> items = model.getAllItems();
        view.setItemCount(items.size());
        view.setSum(purchase.getSum());
        view.setItems(items);
    }


    int getPrice(ShoppingItem item) {
        return PriceCalculator.getShoppingItemPrice(item, model.getLoaded()
                                                               .getSession()
                                                               .getCustomer()
                                                               .getSolidaritySurcharge());
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }
}
