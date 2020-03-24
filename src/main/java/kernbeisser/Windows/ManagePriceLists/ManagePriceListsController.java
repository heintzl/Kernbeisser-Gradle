package kernbeisser.Windows.ManagePriceLists;

import kernbeisser.CustomComponents.PriceListTree;
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
        model = new ManagePriceListsModel();
        view = new ManagePriceListsView(this, current);
        refresh();
    }

    @Override
    public void refresh() {
        view.getPriceListChooser().setModel(model.getPriceListTreeModel());
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
