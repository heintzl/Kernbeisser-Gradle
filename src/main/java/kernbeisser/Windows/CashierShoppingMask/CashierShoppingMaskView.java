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
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.MVC.View;
import org.jetbrains.annotations.NotNull;

public class CashierShoppingMaskView implements View<CashierShoppingMaskController> {
  private JPanel main;
  private SearchBoxView<User> searchBoxView;
  private PermissionButton openShoppingMask;
  private kernbeisser.CustomComponents.PermissionComboBox<User> secondSellerUsername;

  @Linked
  private CashierShoppingMaskController controller;

  @Linked
  private SearchBoxController<User> searchBoxController;

  private void createUIComponents() {
    searchBoxView = searchBoxController.getView();
  }

  void setSearchBoxView(SearchBoxView<User> userSearchBoxView) {
    this.searchBoxView = userSearchBoxView;
  }

  public void setStartFor(String username) {
    openShoppingMask.setText("Einkauf für " + username + " beginnen");
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
}
