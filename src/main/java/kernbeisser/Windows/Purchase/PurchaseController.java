package kernbeisser.Windows.Purchase;

import kernbeisser.DBEntities.Purchase;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

public class PurchaseController implements Controller {
    private final PurchaseModel model;
    private final PurchaseView view;
    PurchaseController(Window current, Purchase purchase){
        model = new PurchaseModel(purchase);
        view = new PurchaseView(current,this);
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
