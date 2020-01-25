package kernbeisser.Windows.Trasaction;

import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

public class TransactionController implements Controller {
    private TransactionModel model;
    private TransactionView view;
    TransactionController(Window current){
        model = new TransactionModel();
        view = new TransactionView(current,this);
    }
    @Override
    public View getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }

    public void transfer() {
        model.transfer();
    }

    public void addTransaction() {
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
}
