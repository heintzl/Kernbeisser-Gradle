package kernbeisser.Windows.Trasaction;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;

class TransactionView extends Window implements View {
    private JButton transferTransactions;
    private JTextField to;
    private JTextField from;
    private JCheckBox formKBValue;
    private DoubleParseField value;
    private ObjectTable<Transaction> transactions;
    private JButton addTransaction;
    private JPanel main;
    private JButton back;
    private JButton delete;
    private JButton a5€Button;
    private JButton a10€Button;
    private JButton a15€Button;
    private JButton a20€Button;
    private JButton a25€Button;
    private JButton a30€Button;
    private JButton a40€Button;
    private JButton a35€Button;
    private JButton a65€Button;
    private JButton a75€Button;
    private JButton a50€Button;
    private JButton a45€Button;
    private JButton a60€Button;
    private JButton a55€Button;
    private JButton a70€Button;
    private JButton a80€Button;
    private JButton a85€Button;
    private JButton a95€Button;
    private JButton a90€Button;
    private JButton a100€Button;
    private JButton a200€Button;
    private JButton a100€Button1;
    private JButton a300€Button;
    private JButton a400€Button;
    private JButton a500€Button;
    private JButton a600€Button;
    private JButton a700€Button;
    private JButton a800€Button;
    private JButton a900€Button;
    private JButton a1000€Button;

    private SearchBoxView searchBoxView;

    private TransactionController controller;

    TransactionView(Window current, TransactionController controller) {
        super(current, Key.ACTION_TRANSACTION);
        this.controller = controller;
        transferTransactions.addActionListener((e) -> controller.transfer());
        transferTransactions.setIcon(IconFontSwing.buildIcon(FontAwesome.CHECK, 20, Color.GREEN));
        addTransaction.addActionListener(e -> controller.addTransaction());
        addTransaction.setIcon(IconFontSwing.buildIcon(FontAwesome.PLUS, 20, Color.GREEN));
        back.addActionListener(e -> back());
        delete.addActionListener(e -> controller.remove());
        delete.setIcon(IconFontSwing.buildIcon(FontAwesome.TRASH, 20, Color.RED));
        transactions.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    controller.remove();
                }
            }
        });
        add(main);
        windowInitialized();
    }

    void setTransactions(Collection<Transaction> transactions) {
        this.transactions.setObjects(transactions);
    }

    String getTo() {
        return to.getText();
    }

    void setTo(String s) {
        to.setText(s);
    }

    String getFrom() {
        return from.getText();
    }

    void setFrom(String s) {
        from.setText(s);
    }

    boolean isFromKB() {
        return formKBValue.isSelected();
    }

    void setFromKB(boolean b) {
        formKBValue.setSelected(false);
        formKBValue.setEnabled(b);
    }

    void setFromEnabled(boolean b) {
        from.setEnabled(b);
    }

    private void createUIComponents() {
        transactions = new ObjectTable<>(
                Column.create("Von", e -> e.getFrom() == null ? "Kernbeisser" : e.getTo()),
                Column.create("Zu", Transaction::getTo),
                Column.create("Überweissungsbetrag", e -> e.getValue()  + "€")
        );
        SearchBoxController<User> userSearchBoxController = new SearchBoxController<User>(User::defaultSearch,controller::loadUser);
    }

    void success() {
        JOptionPane.showMessageDialog(this, "Die Überweisung/en wurde/n durchgeführt");
    }

    boolean confirm() {
        return JOptionPane.showConfirmDialog(this, "Sollen die eingetragenen überweisungen getätigt werden?") == 0;
    }

    void invalidFrom() {
        JOptionPane.showMessageDialog(this, "Der eingetragen Absender kann nicht gefunden werden!");
    }

    void invalidTo() {
        JOptionPane.showMessageDialog(this, "Der eingetragen Empfänger kann nicht gefunden werden!");
    }

    public int getValue() {
        return (int) (value.getValue() * 100);
    }

    Transaction getSelectedTransaction() {
        return transactions.getSelectedObject();
    }

}
