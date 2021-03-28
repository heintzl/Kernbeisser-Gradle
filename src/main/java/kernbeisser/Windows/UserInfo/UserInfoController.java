package kernbeisser.Windows.UserInfo;

import static kernbeisser.Useful.Tools.optional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.StatementType;
import kernbeisser.Forms.FormEditor.FormEditorController;
import kernbeisser.Forms.FormImplemetations.User.UserController;
import kernbeisser.Reports.TransactionStatement;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.Purchase.PurchaseController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.var;
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
    var view = getView();
    switch (view.getSelectedTabIndex()) {
      case 0:
        optional(model.getUser()::getJobs).ifPresent(view::setJobs);
        optional(model.getUser()::getPermissions).ifPresent(view::setPermissions);
        optional(model.getUser()::getUserGroup)
            .flatMap(e -> optional(e::getMembers))
            .ifPresent(view::setUserGroupMembers);
        return;
      case 1:
        optional(model.getUser()::getAllPurchases).ifPresent(view::setShoppingHistory);
        return;
      case 2:
        Collection<Column<Transaction>> columns = new ArrayList<>();
        columns.add(generateTypeColumn());
        columns.add(
            Column.create(
                "Von",
                e -> {
                  if (e.getFromUser() == null) {
                    return "Kenbeisser";
                  } else {
                    return e.getFromUser().getUsername();
                  }
                }));
        columns.add(
            Column.create(
                "An",
                e -> {
                  if (e.getToUser() == null) {
                    return "Kenbeisser";
                  } else {
                    return e.getToUser().getUsername();
                  }
                }));
        columns.add(Column.create("Betrag", e -> String.format("%.2f€", e.getValue())));
        columns.add(generateAfterValueChangeColumn());
        columns.add(Column.create("Info", Transaction::getInfo));
        columns.add(Column.create("Datum", Transaction::getDate));
        view.setValueHistoryColumns(columns);
        view.setValueHistory(model.getUser().getAllValueChanges());
    }
  }

  public Column<Transaction> generateAfterValueChangeColumn() {
    return new Column<Transaction>() {
      double value = 0;

      @Override
      public String getName() {
        value = 0;
        return "Verbleibend";
      }

      @Override
      public Object getValue(Transaction valueChange) {
        value += model.getSignedTransactionValue(valueChange);
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
        if (valueChange.getFromUser() == null) {
          return "Guthabenaufladung";
        }
        if (valueChange.getToUser() == null) {
          return "Einkauf";
        }
        return "Überweisung";
      }
    };
  }

  public void openPurchase() {
    var view = getView();
    new PurchaseController(view.getSelectedPurchase())
        .openIn(new SubWindow(view.traceViewContainer()));
  }

  @Override
  public void fillView(UserInfoView userInfoView) {
    userInfoView.getUserObjectForm().setSource(model.getUser());
    userInfoView.setOptCurrentSelected(true);
    userInfoView.setTransactionStatementTypeItems(Arrays.asList(StatementType.values()));
    User currentUser = LogInModel.getLoggedIn();
    boolean isPermitted =
        currentUser.hasPermission(PermissionKey.ACTION_OPEN_EDIT_USERS)
            || (currentUser.equals(model.getUser())
                && currentUser.hasPermission(PermissionKey.ACTION_EDIT_OWN_DATA));
    userInfoView.getEditUser().setEnabled(isPermitted);
    loadCurrentSite();
  }

  public void printStatement(StatementType statementType, boolean current) {
    new TransactionStatement(model.getUser(), statementType, current)
        .sendToPrinter("Auszug wird erstellt...", Tools::showUnexpectedErrorWarning);
  }

  public void editUser() {
    FormEditorController.open(model.getUser(), new UserController(), Mode.EDIT)
        .openIn(new SubWindow(getView().traceViewContainer()))
        .getLoaded();
    getView().getUserObjectForm().setSource(model.getUser());
  }
}
