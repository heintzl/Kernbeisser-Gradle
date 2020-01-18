package kernbeisser.Windows.EditItem;

import kernbeisser.DBEntities.Item;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

public class EditItemController implements Controller {

    private EditItemView view;
    private EditItemModel model;
    public EditItemController(Window current, Item item, Mode mode){
        this.view=new EditItemView(this,current);
        model=new EditItemModel(item,mode);
        view.setPriceLists(model.getAllPriceLists());
        view.setSuppliers(model.getAllSuppliers());
        view.setUnits(model.getAllUnits());
        view.setContainerDefinitions(model.getAllContainerDefinitions());
        view.setVATs(model.getAllVATs());
        view.pasteItem(item);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }

    void finished() {
        model.doAction(view.collectItem(model.getSource()));
    }
}
