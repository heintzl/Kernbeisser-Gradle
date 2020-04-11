package kernbeisser.Windows.InventoryMenu;

import kernbeisser.Windows.Controller;
import kernbeisser.Windows.JFrameWindow;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

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
