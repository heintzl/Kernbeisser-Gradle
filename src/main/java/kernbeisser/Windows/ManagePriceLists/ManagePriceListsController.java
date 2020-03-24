package kernbeisser.Windows.ManagePriceLists;

import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

public class ManagePriceListsController implements Controller {
    private ManagePriceListsModel model;
    private ManagePriceListsView view;

    /*ManagePriceListsController(ManagePriceListsView view) {
        this.view = view;
        this.model = new ManagePriceListsModel();
    }
    */
    public ManagePriceListsController(Window current) {
        view = new ManagePriceListsView(this, current);
        this.model = new ManagePriceListsModel();
    }

    @Override
    public void refresh() {

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
