package kernbeisser.Windows.SoloShoppingMask;

import kernbeisser.Enums.Key;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIView;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;

public class SoloShoppingMaskView extends Window implements View {
    private JPanel main;

    private ShoppingMaskUIView shoppingMaskUIView;

    private SoloShoppingMaskController controller;

    public SoloShoppingMaskView(Window currentWindow,SoloShoppingMaskController controller) {
        super(currentWindow);
        this.controller = controller;
        add(main);
        setSize(1500,1000);
        windowInitialized();
    }

    private void createUIComponents() {
        shoppingMaskUIView = controller.getShoppingMaskView();
    }
}
