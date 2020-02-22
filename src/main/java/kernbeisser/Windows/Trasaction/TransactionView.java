package kernbeisser.Windows.Trasaction;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.DBEntities.Transaction;
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

    TransactionView(Window current, TransactionController controller) {
        super(current);
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
                if (e.getKeyCode() == KeyEvent.VK_DELETE)
                    controller.remove();
            }
        });
        add(main);
    }

    void setTransactions(Collection<Transaction> transactions) {
        this.transactions.setObjects(transactions);
    }

    String getTo() {
        return to.getText();
    }

    String getFrom() {
        return from.getText();
    }

    boolean isFromKB() {
        return formKBValue.isSelected();
    }

    void setTo(String s) {
        to.setText(s);
    }

    void setFrom(String s) {
        from.setText(s);
    }

    void setFromEnabled(boolean b) {
        formKBValue.setSelected(false);
        formKBValue.setEnabled(false);
        from.setEnabled(false);
    }

    private void createUIComponents() {
        transactions = new ObjectTable<>(
                Column.create("Von", e -> e.getFrom() == null ? "Kernbeisser" : e.getTo()),
                Column.create("Zu", Transaction::getTo),
                Column.create("Überweissungsbetrag", e -> e.getValue() / 100f + "€")
        );
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
