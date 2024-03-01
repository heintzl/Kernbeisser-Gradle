package kernbeisser.Windows.UserInfo;

import static kernbeisser.Useful.Tools.optional;

import java.util.Arrays;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.StatementType;
import kernbeisser.Exeptions.NoSelectionException;
import kernbeisser.Forms.FormEditor.FormEditorController;
import kernbeisser.Forms.FormImplemetations.User.UserController;
import kernbeisser.Reports.TransactionStatement;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.Purchase.PurchaseController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import org.jetbrains.annotations.NotNull;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class UserInfoController extends Controller<UserInfoView, UserInfoModel> {

  public UserInfoController(User user) {
    super(new UserInfoModel(user));
  }

  @Override
  public @NotNull UserInfoModel getModel() {
    return model;
  }

  public void loadCurrentSite() {
    UserInfoView view = getView();
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
        view.setValueHistory(model.getUserTransactions());
    }
  }

  public Double getTransactionSum(Transaction t) {
    return model.getTransactionSums().get(t.getId());
  }

  public void openPurchase() {
    UserInfoView view = getView();
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
