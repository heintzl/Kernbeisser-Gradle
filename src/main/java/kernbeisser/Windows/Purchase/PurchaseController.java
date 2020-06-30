package kernbeisser.Windows.Purchase;

import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PurchaseController implements Controller<PurchaseView,PurchaseModel> {
    private final PurchaseModel model;
    private final PurchaseView view;

    public PurchaseController(Purchase purchase) {
        model = new PurchaseModel(purchase);
        view = new PurchaseView(this);
    }


    double getPrice(ShoppingItem item) {
        return item.getRetailPrice();
    }

    @Override
    public @NotNull PurchaseView getView() {
        return view;
    }

    @Override
    public @NotNull PurchaseModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {
        view.setCustomer(model.getLoaded().getSession().getCustomer().getUsername());
        view.setSeller(model.getLoaded().getSession().getSeller().getUsername());
        view.setDate(Date.INSTANT_FORMAT.format(model.getLoaded().getCreateDate()));
        Collection<ShoppingItem> items = model.getAllItems();
        view.setItemCount(items.size());
        view.setSum(model.getSum());
        view.setItems(items);
    }

    @Override
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }
}
