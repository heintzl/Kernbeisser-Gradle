package kernbeisser.Windows.UserInfo;

import kernbeisser.CustomComponents.Charts.BuyChart;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.Purchase.PurchaseController;
import org.jetbrains.annotations.NotNull;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;

public class UserInfoController implements Controller<UserInfoView,UserInfoModel> {

    private final UserInfoView view;
    private final UserInfoModel model;

    public UserInfoController(User user){
        this.model = new UserInfoModel(user);
        this.view = new UserInfoView(this);
    }

    @Override
    public @NotNull UserInfoView getView() {
        return view;
    }

    @Override
    public @NotNull UserInfoModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {
        if(model.getUser().getId() == LogInModel.getLoggedIn().getId())
            view.pasteWithoutPermissionCheck(model.getUser());
        else view.pasteUser(model.getUser());
        loadCurrentSite();
    }

    @Override
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }

    public void loadCurrentSite() {
        switch (view.getSelectedTabIndex()){
            case 0:
                view.setJobs(model.getUser().getJobs());
                view.setPermissions(model.getUser().getPermissions());
                view.setUserGroupMembers(model.getUser().getUserGroup().getMembers());
                return;
            case 1:
                view.setShoppingHistory(model.getUser().getAllPurchases());
                return;
            case 2:
                Collection<Column<Transaction>> columns = new ArrayList<>();
                columns.add(generateTypeColumn());
                columns.add(Column.create("Von",e -> {
                    if(e.getFrom()==null)return "Kenbeisser";
                    else
                    return e.getFrom().getUsername();
                }, PermissionKey.USER_USERNAME_READ));
                columns.add(Column.create("An",e -> {
                    if(e.getTo()==null)return "Kenbeisser";
                    else
                    return e.getTo().getUsername();
                }, PermissionKey.USER_USERNAME_READ));
                columns.add(Column.create("Betrag",e -> String.format("%.2f€", e.getValue())));
                columns.add(generateAfterValueChangeColumn());
                columns.add(Column.create("Info", Transaction::getInfo, PermissionKey.TRANSACTION_INFO_READ));
                columns.add(Column.create("Datum",Transaction::getDate));
                view.setValueHistoryColumns(columns);
                view.setValueHistory(model.getUser().getAllValueChanges());
                return;
        }
    }

    public Column<Transaction> generateAfterValueChangeColumn(){
        return new Column<Transaction>() {
            double value = 0;
            @Override
            public String getName() {
                value = 0;
                return "Dannach";
            }



            @Override
            public Object getValue(Transaction valueChange) {

                if(valueChange.getTo()!=null&&valueChange.getTo().getId() == LogInModel.getLoggedIn().getId())
                    value += valueChange.getValue();
                else value -= valueChange.getValue();
                return String.format("%.2f€",value);
            }
        };
    }

    private Column<Transaction> generateTypeColumn(){
        return new Column<Transaction>() {
            @Override
            public String getName() {
                return "Type";
            }

            @Override
            public Object getValue(Transaction valueChange) {
                if(valueChange.getFrom()==null)return "Guthabenaufladung";
                if(valueChange.getTo()==null)return "Einkauf";
                return "Überweissung";
            }
        };
    }

    public BuyChart createBuyChart() {
        return new BuyChart(model.getUser(), YearMonth.now().minusMonths(12),YearMonth.now());
    }

    public void openPurchase() {
        new PurchaseController(view.getSelectedPurchase());
    }
}
