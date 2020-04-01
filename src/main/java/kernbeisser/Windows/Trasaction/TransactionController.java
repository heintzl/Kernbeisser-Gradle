package kernbeisser.Windows.Trasaction;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.Window;

import javax.persistence.NoResultException;

public class TransactionController implements Controller {
    private TransactionModel model;
    private TransactionView view;

    private SearchBoxController<User> userSearchBoxController;

    public TransactionController(Window current, User user) {
        model = new TransactionModel();
        userSearchBoxController = new SearchBoxController<>(User::defaultSearch, this::loadUser,
                                                            Column.create("Username", User::getUsername,Key.USER_USERNAME_READ),
                                                            Column.create("Vorname",User::getFirstName,Key.USER_FIRST_NAME_READ),
                                                            Column.create("Nachname",User::getSurname,Key.USER_SURNAME_READ));
        view = new TransactionView(current, this);
        view.setFromEnabled(user.hasPermission(Key.ACTION_TRANSACTION_FROM_OTHER));
        view.setFromKBEnable(user.hasPermission(Key.ACTION_TRANSACTION_FROM_KB));
        view.setFrom(LogInModel.getLoggedIn().getUsername());
    }

    @Override
    public TransactionView getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }

    void transfer() {
        if (!view.confirm()) {
            return;
        }
        model.transfer();
        view.success();
        model.getTransactions().clear();
        view.setTransactions(model.getTransactions());
    }

    void addTransaction() {
        Transaction transaction = new Transaction();
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
        }
        transaction.setValue(view.getValue());
        model.addTransaction(transaction);
        view.setTransactions(model.getTransactions());
        view.setValue("0.00");
    }

    void remove() {
        model.remove(view.getSelectedTransaction());
        view.setTransactions(model.getTransactions());
    }

    void loadUser(User user) {
        view.setTo(user.getUsername());
    }

    public SearchBoxView<User> getSearchBoxView() {
        return userSearchBoxController.getView();
    }
}
