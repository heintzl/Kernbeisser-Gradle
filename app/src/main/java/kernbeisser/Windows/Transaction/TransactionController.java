package kernbeisser.Windows.Transaction;

import static com.google.common.base.Strings.isNullOrEmpty;

import jakarta.persistence.NoResultException;
import java.util.*;
import java.util.function.Predicate;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Exeptions.NoSelectionException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import rs.groump.Key;
import rs.groump.PermissionKey;

@Log4j2
public class TransactionController extends Controller<TransactionView, TransactionModel> {

  @Key(PermissionKey.ACTION_OPEN_TRANSACTION)
  public TransactionController(User user, TransactionType transactionType) {
    super(new TransactionModel(user, transactionType));
  }

  public static TransactionController sharedContainerTransaction(
      User user, double value, String infoMessage) {

    TransactionController transactionController =
        new TransactionController(null, TransactionType.SHARED_CONTAINER);
    TransactionView view = transactionController.getView();
    view.setTo(user);
    view.resetFrom();
    view.setToKBEnable(false);
    view.setFromKBEnable(false);
    view.setInfo(infoMessage);
    view.setValue("%.2f".formatted(value));
    view.setToEnabled(false);

    return transactionController;
  }

  private void loadPreSettings(TransactionType transactionType) {
    if (transactionType == TransactionType.PAYIN) {
      TransactionView view = getView();
      view.setFromKBEnable(false);
      view.setToKBEnable(false);
      view.setFromEnabled(false);
      view.setFrom(User.getKernbeisserUser());
    }
  }

  void transfer() {
    TransactionView view = getView();
    if (!view.confirm(model.getCount())) {
      return;
    }
    unsafeTransfer();
  }

  void unsafeTransfer() {
    TransactionView view = getView();
    Map<Transaction, Optional<Exception>> transactionExceptions = model.transfer();

    int count = model.getCount();
    long fail = transactionExceptions.values().stream().filter(Optional::isPresent).count();
    Map<Transaction, String> transactionMessages = new HashMap<>();
    for (Map.Entry<Transaction, Optional<Exception>> entry : transactionExceptions.entrySet()) {
      String message = getResultMessage(entry);
      transactionMessages.put(entry.getKey(), message);
    }
    view.setFailed(transactionMessages);
    if (count > 0) {
      view.success(count, fail);
    }
    transactionExceptions.entrySet().stream()
        .filter(e -> e.getValue().isEmpty())
        .forEach(e -> model.remove(e.getKey()));
    view.removeErrorColumn();
    refreshTable();
    view.setTransactions(model.getTransactions());
    refreshTooltip(view.getFromControl());
  }

  private static @NotNull String getResultMessage(
      Map.Entry<Transaction, Optional<Exception>> entry) {
    String message = "";
    if (entry.getValue().isPresent()) {
      Exception exception = entry.getValue().get();
      message = exception.getMessage();
      if (exception instanceof InvalidTransactionException) {
        if (message.contains("Permission")) {
          message = "Kein Ausreichendes Guthaben";
        } else if (message.contains("UserGroup")) {
          message = "Überweisung innerhalb einer Benutzergruppe";
        }
      }
    } else {
      message = "Erfolgreich";
    }
    return message;
  }

  void addTransaction() {
    Transaction transaction = new Transaction();
    TransactionView view = getView();
    TransactionType transactionType = model.getTransactionType();
    if (view.getValue() > Setting.WARN_OVER_TRANSACTION_VALUE.getDoubleValue()
        && !view.confirmExtraHeightTransaction()) {
      return;
    }
    if (view.getValue() <= 0) {
      view.invalidValue();
      return;
    }
    if (view.getFrom().getUserGroup().equals(view.getTo().getUserGroup())) {
      view.fromEqualsTo();
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
    if (transactionType != TransactionType.PAYIN
        && view.getFrom().equals(User.getKernbeisserUser())
        && isNullOrEmpty(view.getInfo())) {
      view.invalidPayin();
      return;
    }
    transaction.setValue(view.getValue());
    transaction.setInfo(view.getInfo());
    model.addTransaction(transaction);
    refreshTable();
    if (transactionType == TransactionType.SHARED_CONTAINER) {
      view.resetFrom();
      return;
    }
    view.setValue("");
    view.resetTo();
    view.setInfo("");
  }

  void remove() {
    TransactionView view = getView();
    try {
      model.remove(view.getSelectedTransaction().orElseThrow(NoSelectionException::new));
    } catch (NoSelectionException e) {
      view.messageSelectTransactionFirst();
    } finally {
      refreshTable();
    }
  }

  private void refreshTable() {
    TransactionView view = getView();
    view.setTransactions(model.getTransactions());
    view.setCount(model.getCount());
    view.setSum(model.getSum());
    view.transactionAdded();
    view.setTransferTransactionsEnabled(model.getCount() > 0);
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
    if (box.getToolTipText() != null && !box.getToolTipText().isEmpty()) {
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
    TransactionView view = getView();
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
      view.setFrom(LogInModel.getLoggedIn());
    }
  }

  void fillUsers(boolean hidden) {
    TransactionView view = getView();
    Predicate<User> filter = hidden ? u -> !u.isTestOnly() && u.isActive() : u -> !u.isTestOnly();
    User.populateUserComboBox(view.getToControl(), true, true, filter);
    if (model.getTransactionType() == TransactionType.PAYIN) {
      view.getFromControl().setItems(Collections.singleton(User.getKernbeisserUser()));
    } else {
      User.populateUserComboBox(view.getFromControl(), true, true, filter);
    }
  }

  @Override
  public boolean commitClose() {
    TransactionView view = getView();
    if (!model.getTransactions().isEmpty()) {
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
