package kernbeisser.Windows.CashierShoppingMask;

import javax.persistence.NoResultException;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.SaleSessionType;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.NotEnoughCreditException;
import kernbeisser.Security.Requires;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIController;
import lombok.var;
import org.jetbrains.annotations.NotNull;

@Requires(PermissionKey.ACTION_OPEN_CASHIER_SHOPPING_MASK)
public class CashierShoppingMaskController
    extends Controller<CashierShoppingMaskView, CashierShoppingMaskModel> {

  @Linked private final SearchBoxController<User> searchBoxController;

  public CashierShoppingMaskController() {
    super(new CashierShoppingMaskModel());
    this.searchBoxController =
        new SearchBoxController<>(
            User::defaultSearch,
            Column.create("Vorname", User::getFirstName),
            Column.create("Nachname", User::getSurname),
            Column.create("Benutzername", User::getUsername),
            Column.create("Probemitglied", e -> e.isBeginner() ? "Ja" : "Nein"));
    searchBoxController.addLostSelectionListener(() -> selectUser(null));
    searchBoxController.addSelectionListener(this::selectUser);
    searchBoxController.addDoubleClickListener(e -> openMaskWindow());
  }

  private void selectUser(User tableSelection) {
    var view = getView();
    if (tableSelection != null) {
      view.setOpenShoppingMaskEnabled(true);
      view.setStartFor(tableSelection.getFirstName(), tableSelection.getSurname());
    } else {
      view.setOpenShoppingMaskEnabled(false);
    }
  }

  public void openMaskWindow() {
    SaleSession saleSession = new SaleSession(SaleSessionType.ASSISTED);
    saleSession.setCustomer(searchBoxController.getSelectedObject());
    saleSession.setSeller(LogInModel.getLoggedIn());
    if (!getView().getSecondSeller().toString().equals("Keiner")) {
      try {
        saleSession.setSecondSeller(getView().getSecondSeller());
      } catch (NoResultException e) {
        return;
      }
    }
    try {
      new ShoppingMaskUIController(saleSession).openTab();
    } catch (NotEnoughCreditException e) {
      getView().notEnoughCredit();
    }
  }

  @Override
  protected boolean commitClose() {
    if (Purchase.getLastBonNo() <= Setting.LAST_PRINTED_BON_NR.getLongValue()) return true;
    if (!getView().commitClose()) return false;
    model.printTillRoll(this::handleResult);
    return true;
  }

  private void handleResult(Boolean b) {
    if (!b) {
      long missedBons = Purchase.getLastBonNo() - Setting.LAST_PRINTED_BON_NR.getLongValue();
      if (missedBons > 20) {
        getView().messageDoPanic(missedBons);
      } else {
        getView().messageDontPanic();
      }
    }
  }

  public SearchBoxView<User> getSearchBoxView() {
    return searchBoxController.getView();
  }

  @Override
  public @NotNull CashierShoppingMaskModel getModel() {
    return model;
  }

  @Override
  public void fillView(CashierShoppingMaskView cashierShoppingMaskView) {
    cashierShoppingMaskView.setAllSecondarySellers(User.getAll(null));
  }
}
