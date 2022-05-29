package kernbeisser.Windows.UserInfo;

import static kernbeisser.Useful.Tools.optional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import kernbeisser.CustomComponents.ObjectTable.Adjustors.StripedCellAdjustor;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.Renderer.AdjustableTableCellRenderer;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.StatementType;
import kernbeisser.Exeptions.NoSelectionException;
import kernbeisser.Forms.FormEditor.FormEditorController;
import kernbeisser.Forms.FormImplemetations.User.UserController;
import kernbeisser.Reports.TransactionStatement;
import kernbeisser.Security.Key;
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
        columns.add(Columns.create("Art", Transaction::getTransactionType));
        columns.add(Columns.create("Von", t -> t.getFromUser().getFullName()));
        columns.add(Columns.create("An", t -> t.getToUser().getFullName()));
        columns.add(
            Columns.create(
                "Eingang",
                e -> model.incoming(e) ? String.format("%.2f€", e.getValue()) : "",
                SwingConstants.RIGHT));
        columns.add(
            Columns.create(
                "Ausgang",
                e -> model.incoming(e) ? "" : String.format("%.2f€", e.getValue()),
                SwingConstants.RIGHT));
        columns.add(generateAfterValueChangeColumn());
        columns.add(Columns.create("Info", Transaction::getInfo));
        columns.add(Columns.create("Datum", t -> Date.INSTANT_DATE_TIME.format(t.getDate())));
        view.setValueHistoryColumns(columns);
        view.setValueHistory(model.getUser().getAllValueChanges());
    }
  }

  public Column<Transaction> generateAfterValueChangeColumn() {
    return new Column<Transaction>() {

      @Override
      public TableCellRenderer getRenderer() {
        AdjustableTableCellRenderer<Transaction> renderer = new AdjustableTableCellRenderer<>();
        renderer.addTableCellAdjustor(new StripedCellAdjustor<>());
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
        return renderer;
      }

      @Override
      public String getName() {
        return "Verbleibend";
      }

      @Override
      public Object getValue(Transaction valueChange) {
        return String.format(
            "%.2f€", model.getValueAfterTransaction(valueChange, model.getUser().getUserGroup()));
      }
    };
  }

  public void openPurchase() {
    var view = getView();
    try {
      new PurchaseController(view.getSelectedPurchase().orElseThrow(NoSelectionException::new))
          .openIn(new SubWindow(view.traceViewContainer()));
    } catch (NoSelectionException e) {
      view.messageSelectPurchaseFirst();
    }
  }

  @Key(PermissionKey.ACTION_OPEN_EDIT_USERS)
  private void checkOpenEditUsersPermission() {}

  @Key(PermissionKey.ACTION_EDIT_OWN_DATA)
  private void checkEditOwnDataPermission() {}

  @Override
  public void fillView(UserInfoView userInfoView) {
    userInfoView.getUserObjectForm().setSource(model.getUser());
    userInfoView.setOptCurrentSelected(true);
    userInfoView.setTransactionStatementTypeItems(Arrays.asList(StatementType.values()));
    User currentUser = LogInModel.getLoggedIn();
    boolean isPermitted =
        Tools.canInvoke(this::checkOpenEditUsersPermission)
            || (currentUser.equals(model.getUser())
                && Tools.canInvoke(this::checkEditOwnDataPermission));
    userInfoView.getEditUser().setEnabled(isPermitted);
    loadCurrentSite();
  }

  public void printStatement(StatementType statementType, boolean current) {
    new TransactionStatement(model.getUser(), statementType, current)
        .sendToPrinter("Auszug wird erstellt...", Tools::showUnexpectedErrorWarning);
  }

  public void editUser() {
    FormEditorController.create(model.getUser(), new UserController(), Mode.EDIT)
        .withCloseEvent(this::loadCurrentSite)
        .openIn(new SubWindow(getView().traceViewContainer()));
    getView().getUserObjectForm().setSource(model.getUser());
  }
}
