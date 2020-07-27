package kernbeisser.Windows.SoloShoppingMask;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIView;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

public class SoloShoppingMaskView implements View<SoloShoppingMaskController> {

  private JPanel main;

  //    private BarcodeCapture barcodeCapture;

  private ShoppingMaskUIView shoppingMaskUIView;

  private final SoloShoppingMaskController controller;

  public SoloShoppingMaskView(SoloShoppingMaskController controller) {
    this.controller = controller;
    //        this.barcodeCapture = new BarcodeCapture(c->controller.processBarcode(c));
  }

  private void createUIComponents() {
    shoppingMaskUIView = controller.getShoppingMaskView();
  }

  @Override
  public void initialize(SoloShoppingMaskController controller) {}

  @Override
  public @NotNull Dimension getSize() {
    return new Dimension(1500, 1000);
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
