package kernbeisser.Windows.Trasaction;

import javax.persistence.NoResultException;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import lombok.var;

public class TransactionController extends Controller<TransactionView, TransactionModel> {

  @Linked private final SearchBoxController<User> userSearchBoxController;

  public TransactionController(User user, TransactionType transactionType) {
    super(new TransactionModel(user, transactionType));
    userSearchBoxController =
        new SearchBoxController<>(
            User::defaultSearch,
            Column.create("Nachname", User::getSurname),
            Column.create("Vorname", User::getFirstName),
            Column.create("Username", User::getUsername),
            Column.create("Guthaben", User::getRoundedValue));
    userSearchBoxController.addSelectionListener(e -> getView().pastUser(e));
  }

  private void loadPreSettings(TransactionType transactionType) {
    switch (transactionType) {
      case PAYIN:
        getView().setFromEnabled(false);
        getView().setFrom(User.getKernbeisserUser());
        break;
    }
  }

  void transfer() {
    var view = getView();
    if (!view.confirm()) {
      return;
    }
    unsafeTransfer();
  }

  void unsafeTransfer() {
    var view = getView();
    try {
      model.transfer();
    } catch (InvalidTransactionException e) {
      view.transactionRejected();
      return;
    }
    view.success();
    model.getTransactions().clear();
    view.setTransactions(model.getTransactions());
    refreshTable();
  }

  void addTransaction() {
    Transaction transaction = new Transaction();
    var view = getView();
    if (view.getValue() > Setting.WARN_OVER_TRANSACTION_VALUE.getDoubleValue()
        && !view.confirmExtraHeightTransaction()) {
      return;
    }
    if (view.getValue() <= 0) {
      view.invalidValue();
      return;
    }
    if (view.getValue() < 0 && !view.requestUserTransactionCommit()) {
      return;
    }
    try {
      transaction.setFromUser(view.getFrom());
    } catch (NoResultException e) {
      view.invalidFrom();
      return;
    }
    try {
      transaction.setToUser(view.getTo());
    } catch (NoResultException e) {
      view.invalidTo();
      return;
    }
    transaction.setValue(view.getValue());
    transaction.setInfo(view.getInfo());
    model.addTransaction(transaction);
    refreshTable();
    view.setValue("");
  }

  void remove() {
    var view = getView();
    model.remove(view.getSelectedTransaction());
    refreshTable();
  }

  private void refreshTable() {
    var view = getView();
    view.setTransactions(model.getTransactions());
    view.setCount(model.getCount());
    view.setSum(model.getSum());
    view.transactionAdded();
    userSearchBoxController.invokeSearch();
  }

  public SearchBoxView<User> getSearchBoxView() {
    return userSearchBoxController.getView();
  }

  @Override
  public void fillView(TransactionView transactionView) {
    var view = getView();
    User.populateUserComboBox(view.getToControl(), false, e -> true);
    User.populateUserComboBox(view.getFromControl(), true, e -> true);
    view.setFromEnabled(
        model.getOwner().hasPermission(PermissionKey.ACTION_TRANSACTION_FROM_OTHER));
    view.setFromKBEnable(model.getOwner().hasPermission(PermissionKey.ACTION_TRANSACTION_FROM_KB));
    view.setFrom(LogInModel.getLoggedIn());
    refreshTable();
    loadPreSettings(model.getTransactionType());
  }

  @Override
  public boolean commitClose() {
    var view = getView();
    if (model.getTransactions().size() > 0) {
      switch (view.commitUnsavedTransactions()) {
        case 0:
          unsafeTransfer();
          return true;
        case 1:
          view.transactionsDeleted();
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

  User getKernbeisserUser() {
    return User.getKernbeisserUser();
  }

  public User getLoggedInUser() {
    return LogInModel.getLoggedIn();
  }

  public String getTransactionTypeName() {
    return model.getTransactionType().toString();
  }
}
