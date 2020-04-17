package kernbeisser.Windows.InventoryMenu;

import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class InventoryMenuView implements View<InventoryMenuController> {

    private InventoryMenuController controller;

    public InventoryMenuView(InventoryMenuController controller){
        this.controller = controller;
    }

    @Override
    public void initialize(InventoryMenuController controller) {

    }

    @Override
    public @NotNull JComponent getContent() {
        return null;
    }


}
