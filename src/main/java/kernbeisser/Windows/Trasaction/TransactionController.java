package kernbeisser.Windows.Trasaction;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.Enums.TransactionType;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.LogIn.LogInModel;
import org.jetbrains.annotations.NotNull;

import javax.persistence.NoResultException;

public class TransactionController implements Controller<TransactionView,TransactionModel> {
    private final TransactionModel model;
    private final TransactionView view;

    private final SearchBoxController<User> userSearchBoxController;

    public TransactionController(User user) {
        model = new TransactionModel(user);
        userSearchBoxController = new SearchBoxController<User>(User::defaultSearch,
                                                                Column.create("Nachname", User::getSurname,
                                                                              PermissionKey.USER_SURNAME_READ),
                                                            Column.create("Vorname", User::getFirstName, PermissionKey.USER_FIRST_NAME_READ),
                                                            Column.create("Username", User::getUsername, PermissionKey.USER_USERNAME_READ)
                                                            );
        userSearchBoxController.initView();
        view = new TransactionView(this);
        userSearchBoxController.addSelectionListener(e -> view.setTo(e.toString()));
    }

    @Override
    public @NotNull TransactionView getView() {
        return view;
    }

    @Override
    public @NotNull TransactionModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {
        view.setFromEnabled(model.getOwner().hasPermission(PermissionKey.ACTION_TRANSACTION_FROM_OTHER));
        view.setFromKBEnable(model.getOwner().hasPermission(PermissionKey.ACTION_TRANSACTION_FROM_KB));
        view.setFrom(LogInModel.getLoggedIn().getUsername());
        refreshTable();
    }

    @Override
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }

    void transfer() {
        if (!view.confirm()) {
            return;
        }
        unsafeTransfer();
    }

    void unsafeTransfer(){
        try {
            model.transfer();
        } catch (AccessDeniedException e) {
            view.userHasNotEnoughValue();
            return;
        }
        view.success();
        model.getTransactions().clear();
        view.setTransactions(model.getTransactions());
        refreshTable();
    }

    void addTransaction() {
        Transaction transaction = new Transaction();
        if(view.getValue()==0){
            view.invalidValue();
            return;
        }
        if(view.getValue()<0&&!view.requestUserTransactionCommit()){
            return;
        }
        if (view.isFromKB()) {
            transaction.setTransactionType(TransactionType.KB_TO_USER);
            transaction.setFrom(User.getKernbeisserUser());
        } else {
            try {
                transaction.setFrom(model.findUser(view.getFrom()));
            }catch (NoResultException e){
                view.invalidFrom();
                return;
            }
        }
        try {
            transaction.setTo(model.findUser(view.getTo()));
        }catch (NoResultException e){
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
        model.remove(view.getSelectedTransaction());
        refreshTable();
    }

    private void refreshTable(){
        view.setTransactions(model.getTransactions());
        view.setCount(model.getCount());
        view.setSum(model.getSum());
        view.setTo("");
        userSearchBoxController.refreshLoadSolutions();
    }

    void loadUser(User user) {
        view.setTo(user.getUsername());
    }

    public SearchBoxView<User> getSearchBoxView() {
        return userSearchBoxController.getView();
    }

    @Override
    public boolean commitClose() {
        if (model.getTransactions().size() > 0) {
            switch (view.commitUnsavedTransactions()){
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
        }else return true;
    }
}
