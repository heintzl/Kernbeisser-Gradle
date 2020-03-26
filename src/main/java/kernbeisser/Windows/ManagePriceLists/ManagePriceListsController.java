package kernbeisser.Windows.ManagePriceLists;

import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;

public class ManagePriceListsController implements Controller {
    private ManagePriceListsModel model;
    private ManagePriceListsView view;

    ManagePriceListsController(ManagePriceListsView view) {
        this.view = view;
        this.model = new ManagePriceListsModel();
    }

    @Override
    public void refresh() {

    }

    @Override
    public ManagePriceListsView getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }
}
