package kernbeisser.Windows.Trasaction;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.LogIn.LogInModel;
import org.jetbrains.annotations.NotNull;

import javax.persistence.NoResultException;

public class TransactionController implements Controller<TransactionView,TransactionModel> {
    private TransactionModel model;
    private TransactionView view;

    private SearchBoxController<User> userSearchBoxController;

    public TransactionController(User user) {
        model = new TransactionModel(user);
        userSearchBoxController = new SearchBoxController<User>(User::defaultSearch, this::loadUser,
                                                            Column.create("Nachname",User::getSurname,Key.USER_SURNAME_READ),
                                                            Column.create("Vorname",User::getFirstName,Key.USER_FIRST_NAME_READ),
                                                            Column.create("Username", User::getUsername,Key.USER_USERNAME_READ)
                                                            );
        userSearchBoxController.initView();
        view = new TransactionView(this);
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
        view.setFromEnabled(model.getOwner().hasPermission(Key.ACTION_TRANSACTION_FROM_OTHER));
        view.setFromKBEnable(model.getOwner().hasPermission(Key.ACTION_TRANSACTION_FROM_KB));
        view.setFrom(LogInModel.getLoggedIn().getUsername());
        refreshTable();
    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }

    void transfer() {
        if (!view.confirm()) {
            return;
        }
        unsafeTransfer();
    }

    void unsafeTransfer(){
        model.transfer();
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
            transaction.setFrom(null);
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

    public boolean isCloseable() {
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
