package kernbeisser.Windows.Trasaction;

import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

public class TransactionController implements Controller {
    private TransactionModel model;
    private TransactionView view;
    public TransactionController(Window current,User user){
        model = new TransactionModel();
        view = new TransactionView(current,this);
        view.setFromEnabled(user.hasPermission(Key.ACTION_TRANSACTION_FROM_OTHER));
        view.setFromKB(user.hasPermission(Key.ACTION_TRANSACTION_FROM_KB));
    }
    @Override
    public View getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }

    void transfer() {
        if(!view.confirm())return;
        model.transfer();
        view.success();
        model.getTransactions().clear();
        view.setTransactions(model.getTransactions());
    }

    void addTransaction() {
        Transaction transaction = new Transaction();
        if(view.isFromKB()){
            transaction.setFrom(null);
        }else {
            User from = model.findUser(view.getFrom());
            if(from==null){
                view.invalidFrom();
                return;
            }
        }
        User to = model.findUser(view.getTo());
        if(to==null){
            view.invalidTo();
            return;
        }
        transaction.setTo(to);
        transaction.setValue(view.getValue());
        model.addTransaction(transaction);
        view.setTransactions(model.getTransactions());
    }

    void remove() {
        model.remove(view.getSelectedTransaction());
        view.setTransactions(model.getTransactions());
    }
}
