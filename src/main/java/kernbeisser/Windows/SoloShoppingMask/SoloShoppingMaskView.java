package kernbeisser.Windows.SoloShoppingMask;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.*;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.Security.StaticMethodTransformer.StaticAccessPoint;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskController;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskView;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class SoloShoppingMaskView implements IView<SoloShoppingMaskController> {

  private JPanel main;

  //    private BarcodeCapture barcodeCapture;

  private ShoppingMaskView shoppingMaskView;

  @Linked private SoloShoppingMaskController controller;

  @Linked private ShoppingMaskController shoppingMaskController;

  private void createUIComponents() {
    shoppingMaskView = shoppingMaskController.getView();
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
  @StaticAccessPoint
  public IconCode getTabIcon() {
    return FontAwesome.SHOPPING_CART;
  }

  @Override
  public Component getFocusOnInitialize() {
    var view = shoppingMaskController.getView();
    return view.getFocusOnInitialize();
  }

  @Override
  public String getTitle() {
    return "Selbsteinkauf";
  }

  {
    // GUI initializer generated by IntelliJ IDEA GUI Designer
    // >>> IMPORTANT!! <<<
    // DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR
   * call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    createUIComponents();
    main = new JPanel();
    main.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    main.add(
        shoppingMaskView.$$$getRootComponent$$$(),
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
  }

  /** @noinspection ALL */
  public JComponent $$$getRootComponent$$$() {
    return main;
  }
}
