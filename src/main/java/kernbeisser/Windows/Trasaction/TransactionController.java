package kernbeisser.Windows.Trasaction;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.Collections;
import java.util.function.Predicate;
import javax.persistence.NoResultException;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Exeptions.NoSelectionException;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import lombok.var;

public class TransactionController extends Controller<TransactionView, TransactionModel> {

  @Key(PermissionKey.ACTION_OPEN_TRANSACTION)
  public TransactionController(User user, TransactionType transactionType) {
    super(new TransactionModel(user, transactionType));
  }

  private void loadPreSettings(TransactionType transactionType) {
    switch (transactionType) {
      case PAYIN:
        getView().setFromKBEnable(false);
        getView().setToKBEnable(false);
        getView().setFromEnabled(false);
        getView().setFrom(User.getKernbeisserUser());
        break;
    }
  }

  void transfer() {
    var view = getView();
    if (!view.confirm(model.getCount())) {
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
    int count = model.getCount();
    if (count > 0) {
      view.success(count);
    }
    model.getTransactions().clear();
    view.setTransactions(model.getTransactions());
    refreshTable();
    refreshTooltip(view.getFromControl());
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
    try {
      transaction.setFromUser(view.getFrom());
    } catch (NoResultException | NullPointerException e) {
      view.invalidFrom();
      return;
    }
    try {
      transaction.setToUser(view.getTo());
    } catch (NoResultException | NullPointerException e) {
      view.invalidTo();
      return;
    }
    if (model.getTransactionType() != TransactionType.PAYIN
        && view.getFrom().equals(User.getKernbeisserUser())
        && isNullOrEmpty(view.getInfo())) {
      view.invalidPayin();
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
    try {
      model.remove(view.getSelectedTransaction().orElseThrow(NoSelectionException::new));
    } catch (NoSelectionException e) {
      view.messageSelectTransactionFirst();
      refreshTable();
    }
  }

  private void refreshTable() {
    var view = getView();
    view.setTransactions(model.getTransactions());
    view.setCount(model.getCount());
    view.setSum(model.getSum());
    view.transactionAdded();
  }

  private void setToolTip(AdvancedComboBox<User> box, Predicate<User> condition) {
    User u = (User) box.getSelectedItem();
    if (condition.test(u)) {
      if (u == null || u.isKernbeisser()) {
        box.setToolTipText("");
      } else {
        box.setToolTipText(String.format("Aktuelles Guthaben: %.2f€", u.getUserGroup().getValue()));
      }
    }
  }

  private void refreshTooltip(AdvancedComboBox<User> box) {
    if (box.getToolTipText() != null && !box.getToolTipText().equals("")) {
      setToolTip(box, u -> true);
    }
  }

  private void addUserTooltip(AdvancedComboBox<User> box, Predicate<User> condition) {
    box.addActionListener(e -> setToolTip(box, condition));
  }

  @Key(PermissionKey.ACTION_TRANSACTION_FROM_OTHER)
  private void checkTransactionFromOtherPermission() {}

  @Key(PermissionKey.ACTION_TRANSACTION_FROM_KB)
  private void checkTransactionFromKBPermission() {}

  @Key(PermissionKey.USER_GROUP_VALUE_READ)
  private void checkUserGroupValueReadPermission() {}

  @Override
  public void fillView(TransactionView transactionView) {
    var view = getView();
    AdvancedComboBox<User> fromControl = view.getFromControl();
    AdvancedComboBox<User> toControl = view.getToControl();
    fillUsers(true);
    view.setFromEnabled(Tools.canInvoke(this::checkTransactionFromOtherPermission));
    view.setFromKBEnable(Tools.canInvoke(this::checkTransactionFromKBPermission));
    refreshTable();
    loadPreSettings(model.getTransactionType());
    boolean allowUserGroupValue = Tools.canInvoke(this::checkUserGroupValueReadPermission);
    if (allowUserGroupValue) {
      addUserTooltip(toControl, u -> true);
    }
    if (model.getTransactionType() != TransactionType.PAYIN) {
      addUserTooltip(fromControl, u -> (allowUserGroupValue || u.equals(getLoggedInUser())));
    }
    view.setFrom(LogInModel.getLoggedIn());
  }

  void fillUsers(boolean hidden) {
    TransactionView view = getView();
    Predicate<User> filter = hidden ? User::isActive : e -> true;
    User.populateUserComboBox(view.getToControl(), true, filter);
    if (model.getTransactionType() == TransactionType.PAYIN) {
      view.getFromControl().setItems(Collections.singleton(User.getKernbeisserUser()));
    } else {
      User.populateUserComboBox(view.getFromControl(), true, filter);
    }
  }

  @Override
  public boolean commitClose() {
    var view = getView();
    if (model.getTransactions().size() > 0) {
      switch (view.commitUnsavedTransactions(model.getCount())) {
        case 0:
          unsafeTransfer();
          return true;
        case 1:
          view.transactionsDeleted(model.getCount());
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
