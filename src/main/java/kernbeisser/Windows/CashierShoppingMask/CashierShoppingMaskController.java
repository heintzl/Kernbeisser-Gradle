package kernbeisser.Windows.CashierShoppingMask;

import javax.persistence.NoResultException;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIController;
import org.jetbrains.annotations.NotNull;

public class CashierShoppingMaskController
    implements IController<CashierShoppingMaskView, CashierShoppingMaskModel> {
  private final CashierShoppingMaskModel model;
  private CashierShoppingMaskView view;

  @Linked private final SearchBoxController<User> searchBoxController;

  public CashierShoppingMaskController() {
    this.searchBoxController =
        new SearchBoxController<>(
            User::defaultSearch,
            Column.create("Vorname", User::getFirstName),
            Column.create("Nachname", User::getSurname),
            Column.create("Benutzername", User::getUsername));
    searchBoxController.getView();
    searchBoxController.addLostSelectionListener(() -> selectUser(null));
    searchBoxController.addSelectionListener(this::selectUser);
    searchBoxController.addDoubleClickListener(e -> openMaskWindow());
    model = new CashierShoppingMaskModel();
  }

  private void selectUser(User tableSelection) {
    if (tableSelection != null) {
      view.setOpenShoppingMaskEnabled(true);
      view.setStartFor(tableSelection.getFirstName(), tableSelection.getSurname());
    } else {
      view.setOpenShoppingMaskEnabled(false);
    }
  }

  public void openMaskWindow() {
    SaleSession saleSession = new SaleSession();
    saleSession.setCustomer(searchBoxController.getSelectedObject());
    saleSession.setSeller(LogInModel.getLoggedIn());
    if (!view.getSecondSeller().toString().equals("Keiner")) {
      try {
        saleSession.setSecondSeller(view.getSecondSeller());
      } catch (NoResultException e) {
        return;
      }
    }
    new ShoppingMaskUIController(saleSession)
        .openTab(
            "Einkauf für "
                + saleSession.getCustomer().getSurname()
                + ", "
                + saleSession.getCustomer().getFirstName());
  }

  public SearchBoxView<User> getSearchBoxView() {
    return searchBoxController.getView();
  }

  @Override
  public @NotNull CashierShoppingMaskModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setAllSecondarySellers(User.getAll(null));
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
