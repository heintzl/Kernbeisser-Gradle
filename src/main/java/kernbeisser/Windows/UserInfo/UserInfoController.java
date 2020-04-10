package kernbeisser.Windows.UserInfo;

import kernbeisser.CustomComponents.Charts.BuyChart;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.ValueChange;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.Purchase.PurchaseController;
import kernbeisser.Windows.Window;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;

public class UserInfoController implements Controller {

    private UserInfoView view;
    private UserInfoModel model;

    public UserInfoController(Window current, User user){
        this.model = new UserInfoModel(user);
        this.view = new UserInfoView(current,this);
        if(user.getId() == LogInModel.getLoggedIn().getId())
            view.pasteWithoutPermissionCheck(model.getUser());
        else view.pasteUser(model.getUser());
    }

    @Override
    public UserInfoView getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
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
                Collection<Column<ValueChange>> columns = new ArrayList<>();
                columns.add(generateTypeColumn());
                columns.add(Column.create("Von",e -> {
                    if(e==null)return "Kenbeisser";
                    else
                    return e.getFrom().getUsername();
                }, Key.USER_USERNAME_READ));
                columns.add(Column.create("An",e -> {
                    if(e==null)return "Kenbeisser";
                    else
                    return e.getFrom().getUsername();
                }, Key.USER_USERNAME_READ));
                columns.add(Column.create("Betrag",e -> String.format("%.2f€", e.getValue())));
                columns.add(generateAfterValueChangeColumn());
                view.setValueHistoryColumns(columns);
                view.setValueHistory(model.getUser().getAllValueChanges());
                return;
        }
    }

    public Column<ValueChange> generateAfterValueChangeColumn(){
        return new Column<ValueChange>() {
            double value = 0;
            @Override
            public String getName() {
                value = 0;
                return "Dannach";
            }

            @Override
            public Object getValue(ValueChange valueChange) {
                if(valueChange.getTo().getId() == LogInModel.getLoggedIn().getId())
                    value -= valueChange.getValue();
                else value += valueChange.getValue();
                return String.format("%.2f€",value);
            }
        };
    }

    private Column<ValueChange> generateTypeColumn(){
        return new Column<ValueChange>() {
            @Override
            public String getName() {
                return "Type";
            }

            @Override
            public Object getValue(ValueChange valueChange) {
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
        new PurchaseController(view.getParentWindow(), view.getSelectedPurchase());
    }
}
