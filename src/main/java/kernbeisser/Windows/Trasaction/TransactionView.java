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

    //Fast transaction buttons only paste there value into the from field
    private JButton a5;
    private JButton a10;
    private JButton a15;
    private JButton a20;
    private JButton a25;
    private JButton a30;
    private JButton a40;
    private JButton a35;
    private JButton a65;
    private JButton a75;
    private JButton a50;
    private JButton a45;
    private JButton a60;
    private JButton a55;
    private JButton a70;
    private JButton a80;
    private JButton a85;
    private JButton a95;
    private JButton a90;
    private JButton a100;
    private JButton a150;
    private JButton a125;
    private JButton a175;
    private JButton a200;
    private JButton a225;
    private JButton a250;
    private JButton a300;
    private JButton a350;
    private JButton a450;
    private JButton a500;
    private JButton a400;

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
        formKBValue.addActionListener(e -> from.setEnabled(!formKBValue.isSelected()));
        a5.addActionListener(e -> {
            value.setText("5.0");
            controller.addTransaction();
        });
        a10.addActionListener(e -> {
            value.setText("10.0");
            controller.addTransaction();
        });
        a15.addActionListener(e -> {
            value.setText("15.0");
            controller.addTransaction();
        });
        a20.addActionListener(e -> {
            value.setText("20.0");
            controller.addTransaction();
        });
        a25.addActionListener(e -> {
            value.setText("25.0");
            controller.addTransaction();
        });
        a30.addActionListener(e -> {
            value.setText("30.0");
            controller.addTransaction();
        });
        a40.addActionListener(e -> {
            value.setText("40.0");
            controller.addTransaction();
        });
        a35.addActionListener(e -> {
            value.setText("35.0");
            controller.addTransaction();
        });
        a65.addActionListener(e -> {
            value.setText("65.0");
            controller.addTransaction();
        });
        a75.addActionListener(e -> {
            value.setText("75.0");
            controller.addTransaction();
        });
        a50.addActionListener(e -> {
            value.setText("50.0");
            controller.addTransaction();
        });
        a45.addActionListener(e -> {
            value.setText("45.0");
            controller.addTransaction();
        });
        a60.addActionListener(e -> {
            value.setText("60.0");
            controller.addTransaction();
        });
        a55.addActionListener(e -> {
            value.setText("55.0");
            controller.addTransaction();
        });
        a70.addActionListener(e -> {
            value.setText("70.0");
            controller.addTransaction();
        });
        a80.addActionListener(e -> {
            value.setText("80.0");
            controller.addTransaction();
        });
        a85.addActionListener(e -> {
            value.setText("85.0");
            controller.addTransaction();
        });
        a95.addActionListener(e -> {
            value.setText("95.0");
            controller.addTransaction();
        });
        a90.addActionListener(e -> {
            value.setText("90.0");
            controller.addTransaction();
        });
        a100.addActionListener(e -> {
            value.setText("100.0");
            controller.addTransaction();
        });
        a150.addActionListener(e -> {
            value.setText("150.0");
            controller.addTransaction();
        });
        a125.addActionListener(e -> {
            value.setText("125.0");
            controller.addTransaction();
        });
        a175.addActionListener(e -> {
            value.setText("175.0");
            controller.addTransaction();
        });
        a200.addActionListener(e -> {
            value.setText("200.0");
            controller.addTransaction();
        });
        a225.addActionListener(e -> {
            value.setText("225.0");
            controller.addTransaction();
        });
        a250.addActionListener(e -> {
            value.setText("250.0");
            controller.addTransaction();
        });
        a300.addActionListener(e -> {
            value.setText("300.0");
            controller.addTransaction();
        });
        a350.addActionListener(e -> {
            value.setText("350.0");
            controller.addTransaction();
        });
        a450.addActionListener(e -> {
            value.setText("450.0");
            controller.addTransaction();
        });
        a500.addActionListener(e -> {
            value.setText("500.0");
            controller.addTransaction();
        });
        a400.addActionListener(e -> {
            value.setText("400.0");
            controller.addTransaction();
        });
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

    void setFromKBEnable(boolean b) {
        formKBValue.setSelected(b);
        formKBValue.setEnabled(b);
        if(b) {
            from.setEnabled(false);
        }
    }

    void setFromEnabled(boolean b) {
        from.setEnabled(b);
        searchBoxView.setVisible(b);
    }

    private void createUIComponents() {
        transactions = new ObjectTable<>(
                Column.create("Von", e -> e.getFrom() == null ? "Kernbeisser" : e.getTo()),
                Column.create("Zu", Transaction::getTo),
                Column.create("Überweissungsbetrag", e -> e.getValue()  + "€")
        );
        searchBoxView = controller.getSearchBoxView();
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

    public double getValue() {
        return value.getValue();
    }

    Transaction getSelectedTransaction() {
        return transactions.getSelectedObject();
    }

    public void setValue(String s) {
        value.setText(s);
    }
}
