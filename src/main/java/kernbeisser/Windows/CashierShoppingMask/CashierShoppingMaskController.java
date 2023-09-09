package kernbeisser.Windows.CashierShoppingMask;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.NoResultException;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.SearchBox.Filters.UserFilter;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Post;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.*;
import kernbeisser.Exeptions.NoSelectionException;
import kernbeisser.Exeptions.NoTransactionsFoundException;
import kernbeisser.Exeptions.NotEnoughCreditException;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Users;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.PostPanel.PostPanelController;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskController;
import kernbeisser.Windows.UserInfo.UserInfoController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class CashierShoppingMaskController
    extends Controller<CashierShoppingMaskView, CashierShoppingMaskModel> {

  @Linked private final SearchBoxController<User> searchBoxController;
  private final UserFilter userFilter =
      new UserFilter(this::changeFilter, UserFilter.FILTER_ACTIVE);

  private List<User> searchable(String s, int max) {
    return userFilter.searchable(s, max).stream()
        .filter(u -> !u.isTestOnly())
        .collect(Collectors.toList());
  }

  @Key(PermissionKey.ACTION_OPEN_CASHIER_SHOPPING_MASK)
  public CashierShoppingMaskController() {
    super(new CashierShoppingMaskModel());
    this.searchBoxController =
        new SearchBoxController<>(
            this::searchable,
            Columns.create("Vorname", User::getFirstName),
            Columns.create("Nachname", User::getSurname),
            Columns.create("Benutzername", User::getUsername),
            Columns.create("Mitgliedschaft", Users::getMembership));
    searchBoxController.addLostSelectionListener(() -> selectUser(null));
    searchBoxController.addSelectionListener(this::selectUser);
    searchBoxController.addDoubleClickListener(e -> openMaskWindow());
    searchBoxController.addExtraComponents(userFilter.createFilterUIComponents());
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
    saleSession.setCustomer(searchBoxController.getSelectedObject().get());
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
        new ShoppingMaskController(saleSession).withCloseEvent(() -> setAllowOpen(true)).openTab();
        setAllowOpen(false);
      } else new ShoppingMaskController(saleSession).openTab();
    } catch (NotEnoughCreditException e) {
      getView().notEnoughCredit();
    }
  }

  public void openUserInfo() throws NoSelectionException {
    new UserInfoController(
            searchBoxController.getSelectedObject().orElseThrow(NoSelectionException::new))
        .openIn(new SubWindow(getView().traceViewContainer()));
  }

  private void setAllowOpen(boolean v) {
    model.setOpenLock(!v);
    getView().setOpenShoppingMaskEnabled(v);
  }

  public void close() {
    CashierShoppingMaskView view = getView();
    new PostPanelController(PostContext.ON_SALE_SESSION_CLOSE)
        .openIn(new SubWindow(view.traceViewContainer()));
    view.back();
  }

  @Override
  protected boolean commitClose() {
    try {
      List<Transaction> unreportedTransactions = Transaction.getUnreportedTransactions();
      if (!getView().commitClose()) return false;
      model.printAccountingReports(unreportedTransactions, this::handleResult);
      return true;
    } catch (NoTransactionsFoundException e) {
      return true;
    }
  }

  private void handleResult(Boolean b) {
    if (!b) {
      long missedPurchases = Transaction.getUnreportedTransactions().size();
      if (missedPurchases > 40) {
        getView().messageDoPanic(missedPurchases);
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
    cashierShoppingMaskView.setAllSecondarySellers(User.getAllUserFullNames(false, false));
  }
}
