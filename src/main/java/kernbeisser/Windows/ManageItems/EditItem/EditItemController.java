package kernbeisser.Windows.ManageItems.EditItem;

import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;

public class EditItemController implements Controller {

    private EditItemView view;
    private EditItemModel model;
    EditItemController(EditItemView view){
        this.view=view;
        model=new EditItemModel();
        view.setPriceLists(model.getAllPriceLists());
        view.setSuppliers(model.getAllSuppliers());
        view.setUnits(model.getAllUnits());
        view.setContainerDefinitions(model.getAllContainerDefinitions());
        view.setVATs(model.getAllVATs());
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
