package kernbeisser.Windows.SoloShoppingMask;

import java.awt.*;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIController;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIView;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class SoloShoppingMaskView implements IView<SoloShoppingMaskController> {

  private JPanel main;

  //    private BarcodeCapture barcodeCapture;

  private ShoppingMaskUIView shoppingMaskUIView;

  @Linked private SoloShoppingMaskController controller;

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
  public Component getFocusOnInitialize() {
    var view = shoppingMaskUIController.getView();
    return view.getFocusOnInitialize();
  }

  @Override
  public String getTitle() {
    return "Selbsteinkauf";
  }
}
