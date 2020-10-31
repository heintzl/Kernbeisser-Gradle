package kernbeisser.Windows.UserInfo;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import kernbeisser.CustomComponents.Charts.BuyChart;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.Purchase.PurchaseController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import org.jetbrains.annotations.NotNull;

public class UserInfoController extends Controller<UserInfoView, UserInfoModel> {

  public UserInfoController(User user) {
    super(new UserInfoModel(user));
  }

  @Override
  public @NotNull UserInfoModel getModel() {
    return model;
  }

  public void loadCurrentSite() {
    switch (getView().getSelectedTabIndex()) {
      case 0:
        getView().setJobs(model.getUser().getJobs());
        getView().setPermissions(model.getUser().getPermissions());
        getView().setUserGroupMembers(model.getUser().getUserGroup().getMembers());
        return;
      case 1:
        getView().setShoppingHistory(model.getUser().getAllPurchases());
        return;
      case 2:
        Collection<Column<Transaction>> columns = new ArrayList<>();
        columns.add(generateTypeColumn());
        columns.add(
            Column.create(
                "Von",
                e -> {
                  if (e.getFrom() == null) {
                    return "Kenbeisser";
                  } else {
                    return e.getFrom().getUsername();
                  }
                }));
        columns.add(
            Column.create(
                "An",
                e -> {
                  if (e.getTo() == null) {
                    return "Kenbeisser";
                  } else {
                    return e.getTo().getUsername();
                  }
                }));
        columns.add(Column.create("Betrag", e -> String.format("%.2f€", e.getValue())));
        columns.add(generateAfterValueChangeColumn());
        columns.add(Column.create("Info", Transaction::getInfo));
        columns.add(Column.create("Datum", Transaction::getDate));
        getView().setValueHistoryColumns(columns);
        getView().setValueHistory(model.getUser().getAllValueChanges());
        return;
    }
  }

  public Column<Transaction> generateAfterValueChangeColumn() {
    return new Column<Transaction>() {
      double value = 0;

      @Override
      public String getName() {
        value = 0;
        return "Dannach";
      }

      @Override
      public Object getValue(Transaction valueChange) {

        if (valueChange.getTo() != null
            && valueChange.getTo().getId() == LogInModel.getLoggedIn().getId()) {
          value += valueChange.getValue();
        } else {
          value -= valueChange.getValue();
        }
        return String.format("%.2f€", value);
      }
    };
  }

  private Column<Transaction> generateTypeColumn() {
    return new Column<Transaction>() {
      @Override
      public String getName() {
        return "Type";
      }

      @Override
      public Object getValue(Transaction valueChange) {
        if (valueChange.getFrom() == null) {
          return "Guthabenaufladung";
        }
        if (valueChange.getTo() == null) {
          return "Einkauf";
        }
        return "Überweissung";
      }
    };
  }

  public BuyChart createBuyChart() {
    return new BuyChart(model.getUser(), YearMonth.now().minusMonths(12), YearMonth.now());
  }

  public void openPurchase() {
    new PurchaseController(getView().getSelectedPurchase())
        .openIn(new SubWindow(getView().traceViewContainer()));
  }

  @Override
  public void fillView(UserInfoView userInfoView) {
    if (model.getUser().getId() == LogInModel.getLoggedIn().getId()) {
      userInfoView.pasteWithoutPermissionCheck(model.getUser());
    } else {
      userInfoView.pasteUser(model.getUser());
    }
    loadCurrentSite();
  }
}
