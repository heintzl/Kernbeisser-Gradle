package kernbeisser.Windows.ManagePriceLists;

import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.Window;

public class ManagePriceListsController implements Controller {
    private ManagePriceListsModel model;
    private ManagePriceListsView view;

    public ManagePriceListsController(Window current) {
        this.view = new ManagePriceListsView(current,this){
            @Override
            public void finish() {
                ManagePriceListsController.this.finish();
            }
        };
        this.model = new ManagePriceListsModel();
    }

    @Override
    public void refresh() {

    }

    //Only to override
    public void finish(){}

    @Override
    public ManagePriceListsView getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }

    public void addTriggered() {
    }

    public void editTriggered() {
    }

    public void deleteTriggered() {
    }

    public void priceListSelected() {

    }
}
