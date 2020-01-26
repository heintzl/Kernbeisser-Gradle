package kernbeisser.Windows.Trasaction;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.util.Collection;

class TransactionView extends Window implements View {
    private JButton transferTransactions;
    private JTextField from;
    private JTextField to;
    private JCheckBox formKBValue;
    private kernbeisser.CustomComponents.TextFields.DoubleParseField value;
    private ObjectTable<Transaction> transactions;
    private JButton addTransaction;

    TransactionView(Window current,TransactionController controller) {
        super(current);
        transferTransactions.addActionListener((e) -> controller.transfer());
        addTransaction.addActionListener(e -> controller.addTransaction());
    }

    void setTransactions(Collection<Transaction> transactions){
        this.transactions.setObjects(transactions);
    }

    String getFrom(){
        return from.getText();
    }

    String getTo(){
        return to.getText();
    }

    boolean isFromKB(){
        return formKBValue.isSelected();
    }

    private void createUIComponents() {
        transactions = new ObjectTable<>(
                Column.create("Von",e -> e.getFrom()==null ? "Kernbeisser" : e.getTo()),
                Column.create("Zu",Transaction::getFrom),
                Column.create("Überweissungsbetrag",e -> e.getValue()/100f+"€")
        );
    }
    

    public void invalidFrom() {

    }

    public void invalidTo() {

    }

    public int getValue() {
        return (int)(value.getValue()*100);
    }
}
