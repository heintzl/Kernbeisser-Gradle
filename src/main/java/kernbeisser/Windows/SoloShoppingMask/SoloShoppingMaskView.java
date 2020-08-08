package kernbeisser.Windows.SoloShoppingMask;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.MVC.View;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIController;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIView;
import org.jetbrains.annotations.NotNull;

public class SoloShoppingMaskView implements View<SoloShoppingMaskController> {

  private JPanel main;

  //    private BarcodeCapture barcodeCapture;

  private ShoppingMaskUIView shoppingMaskUIView;

  @Linked private ShoppingMaskUIController shoppingMaskUIController;

  private void createUIComponents() {
    shoppingMaskUIView = shoppingMaskUIController.getView();
  }

  @Override
  public void initialize(SoloShoppingMaskController controller) {
    //        this.barcodeCapture = new BarcodeCapture(c->controller.processBarcode(c));
  }

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
