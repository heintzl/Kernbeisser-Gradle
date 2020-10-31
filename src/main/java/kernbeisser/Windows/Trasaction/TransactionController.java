package kernbeisser.Windows.Trasaction;

import javax.persistence.NoResultException;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;

public class TransactionController extends Controller<TransactionView, TransactionModel> {

  @Linked private final SearchBoxController<User> userSearchBoxController;

  public TransactionController(User user) {
    super(new TransactionModel(user));
    userSearchBoxController =
        new SearchBoxController<User>(
            User::defaultSearch,
            Column.create("Nachname", User::getSurname),
            Column.create("Vorname", User::getFirstName),
            Column.create("Username", User::getUsername),
            Column.create("Guthaben", User::getRoundedValue));
    userSearchBoxController.addSelectionListener(e -> getView().setTo(e.toString()));
  }

  void transfer() {
    if (!getView().confirm()) {
      return;
    }
    unsafeTransfer();
  }

  void unsafeTransfer() {
    try {
      model.transfer();
    } catch (InvalidTransactionException e) {
      getView().transactionRejected();
      return;
    }
    getView().success();
    model.getTransactions().clear();
    getView().setTransactions(model.getTransactions());
    refreshTable();
  }

  void addTransaction() {
    Transaction transaction = new Transaction();
    if (getView().getValue() > Setting.WARN_OVER_TRANSACTION_VALUE.getDoubleValue()
        && !getView().confirmExtraHeightTransaction()) {
      return;
    }
    if (getView().getValue() <= 0) {
      getView().invalidValue();
      return;
    }
    if (getView().getValue() < 0 && !getView().requestUserTransactionCommit()) {
      return;
    }
    try {
      transaction.setFrom(model.findUser(getView().getFrom()));
    } catch (NoResultException e) {
      getView().invalidFrom();
      return;
    }
    try {
      transaction.setTo(model.findUser(getView().getTo()));
    } catch (NoResultException e) {
      getView().invalidTo();
      return;
    }
    transaction.setValue(getView().getValue());
    transaction.setInfo(getView().getInfo());
    model.addTransaction(transaction);
    refreshTable();
    getView().setValue("");
  }

  void remove() {
    model.remove(getView().getSelectedTransaction());
    refreshTable();
  }

  private void refreshTable() {
    getView().setTransactions(model.getTransactions());
    getView().setCount(model.getCount());
    getView().setSum(model.getSum());
    getView().transactionAdded();
    userSearchBoxController.refreshLoadSolutions();
  }

  public SearchBoxView<User> getSearchBoxView() {
    return userSearchBoxController.getView();
  }

  @Override
  public void fillView(TransactionView transactionView) {
    getView()
        .setFromEnabled(
            model.getOwner().hasPermission(PermissionKey.ACTION_TRANSACTION_FROM_OTHER));
    getView()
        .setFromKBEnable(model.getOwner().hasPermission(PermissionKey.ACTION_TRANSACTION_FROM_KB));
    getView().setFrom(LogInModel.getLoggedIn().getUsername());
    refreshTable();
  }

  @Override
  public boolean commitClose() {
    if (model.getTransactions().size() > 0) {
      switch (getView().commitUnsavedTransactions()) {
        case 0:
          unsafeTransfer();
          return true;
        case 1:
          getView().transactionsDeleted();
          return true;
        case 2:
          return false;
        default:
          return true;
      }
    } else {
      return true;
    }
  }

  String getKernbeisserUsername() {
    return User.getKernbeisserUser().getUsername();
  }

  public String getLoggedInUsername() {
    return LogInModel.getLoggedIn().getUsername();
  }
}
