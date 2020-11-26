package kernbeisser.Windows.Trasaction;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.CustomComponents.TextFields.PermissionField;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class TransactionView implements IView<TransactionController> {
  private JButton transferTransactions;
  private JTextField to;
  private JTextField from;
  private JCheckBox fromKBValue;
  private DoubleParseField value;
  private ObjectTable<Transaction> transactions;
  private JButton addTransaction;
  private JPanel main;
  private JButton back;
  private JButton delete;

  // Fast transaction buttons only paste there value into the from field
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

  private SearchBoxView<User> searchBoxView;
  private JLabel sum;
  private JLabel count;
  private PermissionField info;
  private JCheckBox toKernbeisser;

  @Linked private SearchBoxController<User> userSearchBoxController;
  @Linked private TransactionController controller;

  void setTransactions(Collection<Transaction> transactions) {
    this.transactions.setObjects(transactions);
  }

  String getTo() {
    return to.getText();
  }

  void setTo(String s) {
    toKernbeisser.setSelected(false);
    to.setText(s);
  }

  String getFrom() {
    return from.getText();
  }

  void setFrom(String s) {
    from.setText(s);
  }

  void setFromKBEnable(boolean b) {
    fromKBValue.setEnabled(b);
  }

  void setFromEnabled(boolean b) {
    from.setEnabled(b);
    searchBoxView.getContent().setVisible(b);
  }

  private void createUIComponents() {
    transactions =
        new ObjectTable<>(
            Column.create(
                "Von",
                e ->
                    e.getFromUser() == null
                        ? "Kernbeißer"
                        : (e.getFromUser().getSurname() + ", " + e.getFromUser().getFirstName())),
            Column.create(
                "An", e -> e.getToUser().getSurname() + ", " + e.getToUser().getFirstName()),
            Column.create("Überweisungsbetrag", e -> String.format("%.2f€", e.getValue())),
            Column.create("Info", Transaction::getInfo));
    searchBoxView = userSearchBoxController.getView();
  }

  void success() {
    JOptionPane.showMessageDialog(getTopComponent(), "Die Überweisung/en wurde/n durchgeführt");
  }

  boolean confirmExtraHeightTransaction() {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            String.format("Ist der eingegebene Wert von %.2f€ korrekt?", getValue()))
        == 0;
  }

  boolean confirm() {
    return JOptionPane.showConfirmDialog(
            getTopComponent(), "Sollen die eingetragenen Überweisungen getätigt werden?")
        == 0;
  }

  void invalidFrom() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Der eingetragen Absender kann nicht gefunden werden!");
  }

  void invalidTo() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Der eingetragen Empfänger kann nicht gefunden werden!");
  }

  public double getValue() {
    return value.getSafeValue();
  }

  Transaction getSelectedTransaction() {
    return transactions.getSelectedObject();
  }

  public void setValue(String s) {
    value.setText(s);
  }

  void setCount(int count) {
    this.count.setText(count + " Überweisungen");
  }

  void setSum(double sum) {
    this.sum.setText(String.format("%.2f€", sum));
  }

  public void invalidValue() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Der eingegebene Betrag muss größer als 0€ sein");
  }

  public boolean requestUserTransactionCommit() {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Der angegebene Preis ist negativ, das entspricht einer Auszahlung.\n"
                + "Ist das korrekt?")
        == 0;
  }

  public int commitUnsavedTransactions() {
    return JOptionPane.showConfirmDialog(
        getTopComponent(),
        "Sollen die eingegebenen Überweisungen getätigt werden?",
        "Achtung: Überweisungen wurden noch nicht übernommen",
        JOptionPane.YES_NO_CANCEL_OPTION);
  }

  public void transactionsDeleted() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Die eingegeben Überweisungen wurden nicht übernommen");
  }

  public String getInfo() {
    return info.getText();
  }

  private boolean lastFocusOnFrom = true;

  @Override
  public void initialize(TransactionController controller) {
    transferTransactions.addActionListener((e) -> controller.transfer());
    transferTransactions.setIcon(IconFontSwing.buildIcon(FontAwesome.CHECK, 20, Color.GREEN));
    addTransaction.addActionListener(e -> controller.addTransaction());
    addTransaction.setIcon(IconFontSwing.buildIcon(FontAwesome.PLUS, 20, Color.GREEN));
    back.addActionListener(e -> back());
    back.setIcon(IconFontSwing.buildIcon(FontAwesome.ARROW_LEFT, 20, Color.ORANGE));
    delete.addActionListener(e -> controller.remove());
    delete.setIcon(IconFontSwing.buildIcon(FontAwesome.TRASH, 20, Color.RED));
    transactions.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
              controller.remove();
            }
          }
        });
    info.setRequiredWriteKeys(PermissionKey.TRANSACTION_INFO_WRITE);
    fromKBValue.addChangeListener(
        new ChangeListener() {
          private boolean lastState = false;

          @Override
          public void stateChanged(ChangeEvent e) {
            if (lastState == fromKBValue.isSelected()) return;
            lastState = !lastState;
            if (fromKBValue.isSelected()) {
              toKernbeisser.setSelected(false);
              from.setText(controller.getKernbeisserUsername());
              from.setEnabled(false);
            } else {
              from.setText(controller.getLoggedInUsername());
              from.setEnabled(true);
            }
          }
        });
    toKernbeisser.addChangeListener(
        new ChangeListener() {
          private boolean lastState = false;

          @Override
          public void stateChanged(ChangeEvent e) {
            if (lastState == toKernbeisser.isSelected()) return;
            lastState = !lastState;
            if (toKernbeisser.isSelected()) {
              fromKBValue.setSelected(false);
              to.setText(controller.getKernbeisserUsername());
              to.setEnabled(false);
            } else {
              to.setText("");
              to.setEnabled(true);
            }
          }
        });
    from.addFocusListener(
        new FocusAdapter() {
          @Override
          public void focusGained(FocusEvent e) {
            lastFocusOnFrom = true;
          }
        });
    to.addFocusListener(
        new FocusAdapter() {
          @Override
          public void focusGained(FocusEvent e) {
            lastFocusOnFrom = false;
          }
        });
    // Sets the ActionListeners for the instant Transaction Buttons
    {
      a10.addActionListener(
          e -> {
            value.setText("10");
            controller.addTransaction();
          });
      a20.addActionListener(
          e -> {
            value.setText("20");
            controller.addTransaction();
          });
      a25.addActionListener(
          e -> {
            value.setText("25");
            controller.addTransaction();
          });
      a30.addActionListener(
          e -> {
            value.setText("30");
            controller.addTransaction();
          });
      a40.addActionListener(
          e -> {
            value.setText("40");
            controller.addTransaction();
          });
      a75.addActionListener(
          e -> {
            value.setText("75");
            controller.addTransaction();
          });
      a50.addActionListener(
          e -> {
            value.setText("50");
            controller.addTransaction();
          });
      a60.addActionListener(
          e -> {
            value.setText("60");
            controller.addTransaction();
          });
      a70.addActionListener(
          e -> {
            value.setText("70");
            controller.addTransaction();
          });
      a80.addActionListener(
          e -> {
            value.setText("80");
            controller.addTransaction();
          });
      a90.addActionListener(
          e -> {
            value.setText("90");
            controller.addTransaction();
          });
      a100.addActionListener(
          e -> {
            value.setText("100");
            controller.addTransaction();
          });
      a150.addActionListener(
          e -> {
            value.setText("150");
            controller.addTransaction();
          });
      a125.addActionListener(
          e -> {
            value.setText("125");
            controller.addTransaction();
          });
      a175.addActionListener(
          e -> {
            value.setText("175");
            controller.addTransaction();
          });
      a200.addActionListener(
          e -> {
            value.setText("200");
            controller.addTransaction();
          });
      a225.addActionListener(
          e -> {
            value.setText("225");
            controller.addTransaction();
          });
      a250.addActionListener(
          e -> {
            value.setText("250");
            controller.addTransaction();
          });
      a300.addActionListener(
          e -> {
            value.setText("300");
            controller.addTransaction();
          });
      a350.addActionListener(
          e -> {
            value.setText("350");
            controller.addTransaction();
          });
      a450.addActionListener(
          e -> {
            value.setText("450");
            controller.addTransaction();
          });
      a500.addActionListener(
          e -> {
            value.setText("500");
            controller.addTransaction();
          });
      a400.addActionListener(
          e -> {
            value.setText("400");
            controller.addTransaction();
          });
      a275.addActionListener(
          e -> {
            value.setText("275");
            controller.addTransaction();
          });
      a325.addActionListener(
          e -> {
            value.setText("325");
            controller.addTransaction();
          });
      a375.addActionListener(
          e -> {
            value.setText("375");
            controller.addTransaction();
          });
      a550.addActionListener(
          e -> {
            value.setText("550");
            controller.addTransaction();
          });
      a600.addActionListener(
          e -> {
            value.setText("600");
            controller.addTransaction();
          });
      a650.addActionListener(
          e -> {
            value.setText("650");
            controller.addTransaction();
          });
      a700.addActionListener(
          e -> {
            value.setText("700");
            controller.addTransaction();
          });
      a750.addActionListener(
          e -> {
            value.setText("750");
            controller.addTransaction();
          });
      a800.addActionListener(
          e -> {
            value.setText("800");
            controller.addTransaction();
          });
      a850.addActionListener(
          e -> {
            value.setText("850");
            controller.addTransaction();
          });
      a900.addActionListener(
          e -> {
            value.setText("900");
            controller.addTransaction();
          });
      a1000.addActionListener(
          e -> {
            value.setText("1000");
            controller.addTransaction();
          });
    }
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  public void transactionRejected() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Die eingegeben Überweisungen könnnen nicht getätigt werden,\n"
            + "da einige der Benutzer nicht die Berechtigung haben, unter das minimale\n"
            + "Guthaben von "
            + String.format("%.2f€", Setting.DEFAULT_MIN_VALUE.getDoubleValue())
            + " zu gehen,\n"
            + "oder der Absender der gleiche ist wie der Empfänger");
  }

  public void transactionAdded() {
    if (!toKernbeisser.isSelected()) to.setText("");
  }

  @Override
  public String getTitle() {
    return "Überweisungen (" + controller.getTransactionTypeName() + ")";
  }

  public void pastUsername(String username) {
    if (lastFocusOnFrom) {
      if (from.isEnabled()) {
        from.setText(username);
        return;
      }
      if (to.isEnabled()) {
        to.setText(username);
      }
    } else {
      if (to.isEnabled()) {
        to.setText(username);
        return;
      }
      if (from.isEnabled()) {
        from.setText(username);
      }
    }
  }
}
