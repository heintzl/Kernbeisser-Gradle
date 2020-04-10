package kernbeisser.Windows.SoloShoppingMask;

import kernbeisser.Windows.Controller;
import kernbeisser.Windows.JFrameWindow;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIView;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class SoloShoppingMaskView implements View<SoloShoppingMaskController> {

    private JPanel main;

    private ShoppingMaskUIView shoppingMaskUIView;

    private SoloShoppingMaskController controller;

    public SoloShoppingMaskView(SoloShoppingMaskController controller) {
        this.controller = controller;
    }

    private void createUIComponents() {
        shoppingMaskUIView = controller.getShoppingMaskView();
    }

    @Override
    public void initialize(SoloShoppingMaskController controller) {

    }

    @Override
    public @NotNull Dimension getSize() {
        return new Dimension(1500,1000);
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }
}
