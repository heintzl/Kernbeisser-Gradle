package kernbeisser.Windows.CashierShoppingMask;

import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.CustomComponents.PermissionButton;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIView;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.jws.soap.SOAPBinding;
import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class CashierShoppingMaskView implements View<CashierShoppingMaskController> {
    private JPanel main;
    private SearchBoxView<User> searchBoxView;
    private PermissionButton openShoppingMask;
    private kernbeisser.CustomComponents.PermissionComboBox<String> secondSellerUsername;

    private final CashierShoppingMaskController controller;

    CashierShoppingMaskView(CashierShoppingMaskController controller){
        this.controller = controller;
    }

    private void createUIComponents() {
        searchBoxView =  controller.getSearchBoxView();
    }

    void setSearchBoxView(SearchBoxView<User> userSearchBoxView){
        this.searchBoxView = userSearchBoxView;
    }

    public void setStartFor(String username) {
        openShoppingMask.setText("Einkauf für "+username+" beginnen");
    }

    public String getSecondSeller(){
        return (String) secondSellerUsername.getSelectedItem();
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
        return new Dimension(1440,950);
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

    @Override
    public IconCode getTabIcon() {
        return FontAwesome.SHOPPING_CART;
    }

    public void setAllSecondarySellers(Collection<User> users){
        secondSellerUsername.removeAllItems();
        secondSellerUsername.addItem("Keiner");
        users.forEach(e -> secondSellerUsername.addItem(e.getUsername()));
    }
}
