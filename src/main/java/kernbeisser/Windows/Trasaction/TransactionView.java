package kernbeisser.Windows.Trasaction;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Optional;
import javax.persistence.NoResultException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.CustomComponents.TextFields.PermissionField;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Setting;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class TransactionView implements IView<TransactionController> {

  private JButton transferTransactions;
  private AdvancedComboBox<User> to;
  private AdvancedComboBox<User> from;
  private JCheckBox fromKBValue;
  private DoubleParseField value;
  private ObjectTable<Transaction> transactions;
  private JButton addTransaction;
  private JPanel main;
  private JButton back;
  private JButton delete;

  // Fast transaction buttons only paste their value into the from field
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

  private JLabel sum;
  private JLabel count;
  private PermissionField info;
  private JCheckBox toKBValue;
  private JCheckBox hideInactive;

  @Linked
  private SearchBoxController<User> userSearchBoxController;
  @Linked
  private TransactionController controller;

  void setTransactions(Collection<Transaction> transactions) {
    this.transactions.setObjects(transactions);
  }

  User getTo() {
    return to.getSelected().orElseThrow(NoResultException::new);
  }

  AdvancedComboBox<User> getToControl() {
    return to;
  }

  void setTo(User u) {
    to.getModel().setSelectedItem(u);
    to.setSelectedItem(u);
  }

  User getFrom() {
    return from.getSelected().orElseThrow(NoResultException::new);
  }

  AdvancedComboBox<User> getFromControl() {
    return from;
  }

  void setFrom(User u) {
    from.getModel().setSelectedItem(u);
    from.setSelectedItem(u);
  }

  void setInfo(String text) {
    info.setText(text);
  }

  void setFromKBEnable(boolean b) {
    fromKBValue.setEnabled(b);
  }

  void setToKBEnable(boolean b) {
    toKBValue.setEnabled(b);
  }

  void setFromEnabled(boolean b) {
    from.setEnabled(b);
  }

  void setTransferTransactionsEnabled(boolean b) {
    transferTransactions.setEnabled(b);
  }

  private void createUIComponents() {
    transactions =
        new ObjectTable<>(
            Columns.create(
                "Von",
                e -> e.getFromUser() == null ? "Kernbeißer" : (e.getFromUser().getFullName(true))),
            Columns.create("An", e -> e.getToUser().getFullName(true)),
            Columns.create("Überweisungsbetrag", e -> String.format("%.2f€", e.getValue())),
            Columns.create("Info", Transaction::getInfo));
    from = new AdvancedComboBox<>(User::getFullName);
    to = new AdvancedComboBox<>(User::getFullName);
  }

  void success(int count) {
    String suffix = "", suffix1 = "";
    if (count != 1) {
      suffix = "en";
      suffix1 = "n";
    }
    JOptionPane.showMessageDialog(
        getTopComponent(), "Die Überweisung" + suffix + " wurde" + suffix1 + " durchgeführt");
  }

  boolean confirmExtraHeightTransaction() {
    return JOptionPane.showConfirmDialog(
        getTopComponent(),
        String.format("Ist der eingegebene Wert von %.2f€ korrekt?", getValue()))
        == 0;
  }

  boolean confirm(int count) {
    String suffix = "", suffix1 = "";
    if (count != 1) {
      suffix = "en";
      suffix1 = "n";
    }
    return JOptionPane.showConfirmDialog(
        getTopComponent(),
        "Soll"
            + suffix
            + " die aufgelistete"
            + suffix1
            + " Überweisung"
            + suffix
            + " getätigt werden?")
        == 0;
  }

  void invalidFrom() {
    JOptionPane.showMessageDialog(getTopComponent(), "Der Absender kann nicht gefunden werden!");
  }

  void invalidTo() {
    JOptionPane.showMessageDialog(getTopComponent(), "Der Empfänger kann nicht gefunden werden!");
  }

  public double getValue() {
    return value.getSafeValue();
  }

  Optional<Transaction> getSelectedTransaction() {
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

  public void fromEqualsTo() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Überweisungen innerhalb einer Benutzergruppe können nicht durchgeführt werden");
  }

  public void invalidPayin() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Wenn diese Überweisung eine Guthabeneinzahlung ist, muss sie über den "
            + "Menüpunkt \"Guthaben buchen\" durchgeführt werden.\nWenn es eine andere Art "
            + "der Übertragung vom Kernbeißerkonto ist, muss das im Info-Feld vermerkt werden, "
            + "z.B. durch Angabe einer Belegnummer");
  }

  public int commitUnsavedTransactions(int count) {
    String suffix = "", suffix1 = "";
    if (count != 1) {
      suffix = "en";
      suffix1 = "n";
    }
    return JOptionPane.showConfirmDialog(
        getTopComponent(),
        "Soll"
            + suffix
            + " die eingegebene"
            + suffix1
            + " Überweisung"
            + suffix
            + " getätigt werden?",
        "Achtung: Überweisung" + suffix + " wurde" + suffix1 + " noch nicht übernommen",
        JOptionPane.YES_NO_CANCEL_OPTION);
  }

  public void transactionsDeleted(int count) {
    String suffix = "", suffix1 = "";
    if (count != 1) {
      suffix = "en";
      suffix1 = "n";
    }
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Die eingegebene"
            + suffix1
            + " Überweisung"
            + suffix
            + " wurde"
            + suffix1
            + " nicht übernommen");
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
    back.setIcon(IconFontSwing.buildIcon(FontAwesome.POWER_OFF, 20, Color.DARK_GRAY));
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
    info.setEnabled(Tools.canInvoke(() -> new Transaction().setInfo("")));
    fromKBValue.addChangeListener(
        new ChangeListener() {
          private boolean lastState = false;

          @Override
          public void stateChanged(ChangeEvent e) {
            if (lastState == fromKBValue.isSelected()) {
              return;
            }
            lastState = !lastState;
            if (fromKBValue.isSelected()) {
              toKBValue.setSelected(false);
              setFrom(controller.getKernbeisserUser());
              from.setEnabled(false);
            } else {
              setFrom(controller.getLoggedInUser());
              from.setEnabled(true);
            }
          }
        });
    toKBValue.addChangeListener(
        new ChangeListener() {
          private boolean lastState = false;

          @Override
          public void stateChanged(ChangeEvent e) {
            if (lastState == toKBValue.isSelected()) {
              return;
            }
            lastState = !lastState;
            if (toKBValue.isSelected()) {
              fromKBValue.setSelected(false);
              setTo(controller.getKernbeisserUser());
              to.setEnabled(false);
            } else {
              setTo(null);
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
    hideInactive.addActionListener(e -> controller.fillUsers(hideInactive.isSelected()));
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
    message(
        "Die eingegeben Überweisungen können nicht getätigt werden,\n"
            + "da einige der Benutzer nicht die Berechtigung haben, unter das minimale\n"
            + "Guthaben von "
            + String.format("%.2f€", Setting.DEFAULT_MIN_VALUE.getDoubleValue())
            + " zu gehen,\n"
            + "oder der Absender der gleiche ist wie der Empfänger");
  }

  public void transactionAdded() {
    if (!toKBValue.isSelected()) {
      setTo(null);
      to.repaint();
    }
  }

  @Override
  public String getTitle() {
    return "Überweisungen (" + controller.getTransactionTypeName() + ")";
  }

  public void messageSelectTransactionFirst() {
    message("Bitte wähle zuerst eine Überweisung aus.", "Keine Überweisung ausgewählt");
  }

}
