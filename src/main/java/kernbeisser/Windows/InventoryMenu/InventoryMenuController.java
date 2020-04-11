package kernbeisser.Windows.InventoryMenu;

import kernbeisser.Enums.Key;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

public class InventoryMenuController implements Controller<InventoryMenuView,InventoryMenuModel> {

    private InventoryMenuModel model;
    private InventoryMenuView view;

    InventoryMenuController(InventoryMenuView view) {
        this.view = view;
        model = new InventoryMenuModel();
    }


    @Override
    public @NotNull InventoryMenuView getView() {
        return view;
    }

    @Override
    public @NotNull InventoryMenuModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {

    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }
}
