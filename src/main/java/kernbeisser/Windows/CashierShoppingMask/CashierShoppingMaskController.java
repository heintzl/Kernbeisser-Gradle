package kernbeisser.Windows.CashierShoppingMask;

import javax.persistence.NoResultException;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.Filters.UserFilter;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.SaleSessionType;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Exeptions.NotEnoughCreditException;
import kernbeisser.Security.Key;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIController;
import kernbeisser.Windows.UserInfo.UserInfoController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class CashierShoppingMaskController
    extends Controller<CashierShoppingMaskView, CashierShoppingMaskModel> {

  @Linked private final SearchBoxController<User> searchBoxController;
  private UserFilter userFilter = new UserFilter(this::changeFilter, UserFilter.FILTER_ACTIVE);

  @Key(PermissionKey.ACTION_OPEN_CASHIER_SHOPPING_MASK)
  public CashierShoppingMaskController() {
    super(new CashierShoppingMaskModel());
    this.searchBoxController =
        new SearchBoxController<>(
            userFilter::searchable,
            Column.create("Vorname", User::getFirstName),
            Column.create("Nachname", User::getSurname),
            Column.create("Benutzername", User::getUsername),
            Column.create("Probemitglied", e -> e.isBeginner() ? "Ja" : "Nein"));
    searchBoxController.addLostSelectionListener(() -> selectUser(null));
    searchBoxController.addSelectionListener(this::selectUser);
    searchBoxController.addDoubleClickListener(e -> openMaskWindow());
    searchBoxController.addExtraComponents(userFilter.createFilterOptionButtons());
  }

  public void changeFilter() {
    searchBoxController.invokeSearch();
  }

  private void selectUser(User tableSelection) {
    var view = getView();
    if (tableSelection != null) {
      view.setOpenShoppingMaskEnabled(!model.isOpenLock());
      view.setUserInfoEnabled(true);
      view.setStartFor(tableSelection.getFirstName(), tableSelection.getSurname());
    } else {
      view.setOpenShoppingMaskEnabled(false);
      view.setUserInfoEnabled(false);
    }
  }

  public void openMaskWindow() {
    if (model.isOpenLock()) {
      getView().messageShoppingMaskAlreadyOpened();
      return;
    }
    boolean allowMultiple =
        UserSetting.ALLOW_MULTIPLE_SHOPPING_MASK_INSTANCES.getBooleanValue(
            LogInModel.getLoggedIn());
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
      if (!allowMultiple) {
        new ShoppingMaskUIController(saleSession)
            .withCloseEvent(() -> setAllowOpen(true))
            .openTab();
        setAllowOpen(false);
      } else new ShoppingMaskUIController(saleSession).openTab();
    } catch (NotEnoughCreditException e) {
      getView().notEnoughCredit();
    }
  }

  public void openUserInfo() {
    new UserInfoController(searchBoxController.getSelectedObject())
        .openIn(new SubWindow(getView().traceViewContainer()));
  }

  private void setAllowOpen(boolean v) {
    model.setOpenLock(!v);
    getView().setOpenShoppingMaskEnabled(v);
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
    cashierShoppingMaskView.setAllSecondarySellers(User.getAllUserFullNames(false));
  }
}
