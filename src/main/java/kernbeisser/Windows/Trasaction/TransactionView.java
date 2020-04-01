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
    private JButton a10;
    private JButton a20;
    private JButton a25;
    private JButton a30;
    private JButton a40;
    private JButton a75;
    private JButton a50;
    private JButton a60;
    private JButton a70;
    private JButton a80;
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
    private JButton a275;
    private JButton a325;
    private JButton a375;
    private JButton a550;
    private JButton a600;
    private JButton a650;
    private JButton a700;
    private JButton a750;
    private JButton a800;
    private JButton a850;
    private JButton a900;
    private JButton a1000;

    private SearchBoxView searchBoxView;
    private JLabel sum;
    private JLabel count;


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
        //Sets the ActionListeners for the instant Transaction Buttons
        {
            a10.addActionListener(e -> {
                value.setText("10");
                controller.addTransaction();
            });
            a20.addActionListener(e -> {
                value.setText("20");
                controller.addTransaction();
            });
            a25.addActionListener(e -> {
                value.setText("25");
                controller.addTransaction();
            });
            a30.addActionListener(e -> {
                value.setText("30");
                controller.addTransaction();
            });
            a40.addActionListener(e -> {
                value.setText("40");
                controller.addTransaction();
            });
            a75.addActionListener(e -> {
                value.setText("75");
                controller.addTransaction();
            });
            a50.addActionListener(e -> {
                value.setText("50");
                controller.addTransaction();
            });
            a60.addActionListener(e -> {
                value.setText("60");
                controller.addTransaction();
            });
            a70.addActionListener(e -> {
                value.setText("70");
                controller.addTransaction();
            });
            a80.addActionListener(e -> {
                value.setText("80");
                controller.addTransaction();
            });
            a90.addActionListener(e -> {
                value.setText("90");
                controller.addTransaction();
            });
            a100.addActionListener(e -> {
                value.setText("100");
                controller.addTransaction();
            });
            a150.addActionListener(e -> {
                value.setText("150");
                controller.addTransaction();
            });
            a125.addActionListener(e -> {
                value.setText("125");
                controller.addTransaction();
            });
            a175.addActionListener(e -> {
                value.setText("175");
                controller.addTransaction();
            });
            a200.addActionListener(e -> {
                value.setText("200");
                controller.addTransaction();
            });
            a225.addActionListener(e -> {
                value.setText("225");
                controller.addTransaction();
            });
            a250.addActionListener(e -> {
                value.setText("250");
                controller.addTransaction();
            });
            a300.addActionListener(e -> {
                value.setText("300");
                controller.addTransaction();
            });
            a350.addActionListener(e -> {
                value.setText("350");
                controller.addTransaction();
            });
            a450.addActionListener(e -> {
                value.setText("450");
                controller.addTransaction();
            });
            a500.addActionListener(e -> {
                value.setText("500");
                controller.addTransaction();
            });
            a400.addActionListener(e -> {
                value.setText("400");
                controller.addTransaction();
            });
            a275.addActionListener(e -> {
                value.setText("275");
                controller.addTransaction();
            });
            a325.addActionListener(e -> {
                value.setText("325");
                controller.addTransaction();
            });
            a375.addActionListener(e -> {
                value.setText("375");
                controller.addTransaction();
            });
            a550.addActionListener(e -> {
                value.setText("550");
                controller.addTransaction();
            });
            a600.addActionListener(e -> {
                value.setText("600");
                controller.addTransaction();
            });
            a650.addActionListener(e -> {
                value.setText("650");
                controller.addTransaction();
            });
            a700.addActionListener(e -> {
                value.setText("700");
                controller.addTransaction();
            });
            a750.addActionListener(e -> {
                value.setText("750");
                controller.addTransaction();
            });
            a800.addActionListener(e -> {
                value.setText("800");
                controller.addTransaction();
            });
            a850.addActionListener(e -> {
                value.setText("850");
                controller.addTransaction();
            });
            a900.addActionListener(e -> {
                value.setText("900");
                controller.addTransaction();
            });
            a1000.addActionListener(e -> {
                value.setText("1000");
                controller.addTransaction();
            });

        }
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
                Column.create("Von", e -> e.getFrom() == null ? "Kernbeisser" : e.getFrom()),
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

    void setCount(int count){
        this.count.setText(count+" Überweisungen");
    }

    void setSum(double sum){
        this.sum.setText(String.format("%.2f€",sum));
    }

    public void invalidValue() {
        JOptionPane.showMessageDialog(this,"Der eingegebene Betrag is zu klein er muss mindestens 0.01€ groß sein");
    }
}
