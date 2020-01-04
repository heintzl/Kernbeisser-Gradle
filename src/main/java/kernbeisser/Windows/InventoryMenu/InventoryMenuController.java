package kernbeisser.Windows.InventoryMenu;

import kernbeisser.Windows.Controller;

public class InventoryMenuController implements Controller {

    private InventoryMenuModel model;
    private InventoryMenuView view;
    InventoryMenuController(InventoryMenuView view){
        this.view=view;
        model=new InventoryMenuModel();
    }

    @Override
    public void refresh() {

    }

    @Override
    public InventoryMenuView getView() {
        return view;
    }

    @Override
    public InventoryMenuModel getModel() {
        return model;
    }
}
