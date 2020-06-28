package kernbeisser.Windows.SoloShoppingMask;

import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIView;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class SoloShoppingMaskView implements View<SoloShoppingMaskController> {

    private JPanel main;

//    private BarcodeCapture barcodeCapture;

    private ShoppingMaskUIView shoppingMaskUIView;

    private SoloShoppingMaskController controller;

    public SoloShoppingMaskView(SoloShoppingMaskController controller) {
        this.controller = controller;
//        this.barcodeCapture = new BarcodeCapture(c->controller.processBarcode(c));
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

    @Override
    public IconCode getTabIcon() {
        return FontAwesome.SHOPPING_CART;
    }

    @Override
    public boolean processKeyboardInput(KeyEvent e) {
        return shoppingMaskUIView.processKeyboardInput(e);
    }

}
