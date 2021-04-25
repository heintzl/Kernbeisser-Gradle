package kernbeisser.Windows.UserInfo;

import static kernbeisser.Useful.Tools.optional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.StatementType;
import kernbeisser.Forms.FormEditor.FormEditorController;
import kernbeisser.Forms.FormImplemetations.User.UserController;
import kernbeisser.Reports.TransactionStatement;
import kernbeisser.Useful.Date;
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
        optional(model.getUser()::getJobsAsAvailable).ifPresent(view::setJobs);
        optional(model.getUser()::getPermissionsAsAvailable).ifPresent(view::setPermissions);
        optional(model.getUser()::getUserGroup)
            .flatMap(e -> optional(e::getMembers))
            .ifPresent(view::setUserGroupMembers);
        return;
      case 1:
        optional(model.getUser()::getAllPurchases).ifPresent(view::setShoppingHistory);
        return;
      case 2:
        Collection<Column<Transaction>> columns = new ArrayList<>();
        columns.add(Column.create("Art", Transaction::getTransactionType));
        columns.add(Column.create("Von", t -> t.getFromUser().getFullName()));
        columns.add(Column.create("An", t -> t.getToUser().getFullName()));
        columns.add(
            Column.create(
                "Betrag", e -> String.format("%.2f€", e.getValue()), SwingConstants.RIGHT));
        columns.add(generateAfterValueChangeColumn());
        columns.add(Column.create("Info", Transaction::getInfo));
        columns.add(Column.create("Datum", t -> Date.INSTANT_DATE_TIME.format(t.getDate())));
        view.setValueHistoryColumns(columns);
        view.setValueHistory(model.getUser().getAllValueChanges());
    }
  }

  public Column<Transaction> generateAfterValueChangeColumn() {
    return new Column<Transaction>() {
      double value = 0;

      @Override
      public TableCellRenderer getRenderer() {
        DefaultTableCellRenderer renderer =
            (DefaultTableCellRenderer) Column.DEFAULT_STRIPED_RENDERER;
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
        return renderer;
      }

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
        .withCloseEvent(this::loadCurrentSite)
        .openIn(new SubWindow(getView().traceViewContainer()))
        .getLoaded();
    getView().getUserObjectForm().setSource(model.getUser());
  }
}
