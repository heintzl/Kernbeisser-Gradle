package kernbeisser.Windows.CashierShoppingMask;

import java.awt.*;
import java.util.Collection;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.CustomComponents.PermissionButton;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.User;
import kernbeisser.Security.StaticMethodTransformer.StaticAccessPoint;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class CashierShoppingMaskView implements IView<CashierShoppingMaskController> {
  private JPanel main;
  private SearchBoxView<User> searchBoxView;
  private PermissionButton openShoppingMask;
  private kernbeisser.CustomComponents.PermissionComboBox<User> secondSellerUsername;

  @Linked private CashierShoppingMaskController controller;

  @Linked private SearchBoxController<User> searchBoxController;

  private void createUIComponents() {
    searchBoxView = searchBoxController.getView();
  }

  public void setStartFor(String firstName, String surname) {
    openShoppingMask.setText("Einkauf für " + surname + ", " + firstName + " beginnen");
  }

  public User getSecondSeller() {
    return (User) secondSellerUsername.getSelectedItem();
  }

  public void setOpenShoppingMaskEnabled(boolean b) {
    openShoppingMask.setEnabled(b);
  }

  @Override
  public void initialize(CashierShoppingMaskController controller) {
    openShoppingMask.addActionListener(e -> controller.openMaskWindow());
  }

  @Override
  public @NotNull Dimension getSize() {
    return new Dimension(1440, 950);
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

  public void setAllSecondarySellers(Collection<User> users) {
    secondSellerUsername.removeAllItems();
    User user = new User();
    user.setUsername("Keiner");
    secondSellerUsername.addItem(user);
    users.forEach(e -> secondSellerUsername.addItem(e));
  }

  public void notEnoughCredit() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Der Ausgewählte Benutzer hat nicht genung Guthaben, um einen Einkauf zu beginnen!");
  }

  @Override
  public String getTitle() {
    return "Ladendienst-Menü";
  }

  public boolean commitClose() {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Bist du dir sicher das du den Ladendienst beenden\n"
                + "und den Ladendienst-Report ausdrucken möchtest?")
        == 0;
  }
}
