package kernbeisser.Windows.Transaction;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.persistence.NoResultException;
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

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Optional;

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

  @Linked private SearchBoxController<User> userSearchBoxController;
  @Linked private TransactionController controller;

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

  void resetTo() {
    if (to.isEnabled()) setTo(null);
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
                e ->
                    e.getFromUser() == null
                        ? Setting.STORE_NAME.getStringValue()
                        : (e.getFromUser().getFullName(true))),
            Columns.create("An", e -> e.getToUser().getFullName(true)),
            Columns.create("Überweisungsbetrag", e -> String.format("%.2f€", e.getValue())),
            Columns.create("Info", Transaction::getInfo));
    from = new AdvancedComboBox<>(u -> u.getFullName(true));
    to = new AdvancedComboBox<>(u -> u.getFullName(true));
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
            + "der Übertragung vom "
            + Setting.STORE_NAME.getStringValue()
            + "konto ist, muss das "
            + "im Info-Feld vermerkt werden, z.B. durch Angabe einer Belegnummer");
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
    fromKBValue.setText("Von " + Setting.STORE_NAME.getStringValue());
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
    toKBValue.setText("An " + Setting.STORE_NAME.getStringValue());
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
    return "Guthaben übertragen (" + controller.getTransactionTypeName() + ")";
  }

  public void messageSelectTransactionFirst() {
    message("Bitte wähle zuerst eine Überweisung aus.", "Keine Überweisung ausgewählt");
  }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

  /**
   * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR
   * call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
        createUIComponents();
        main = new JPanel();
        main.setLayout(new GridLayoutManager(2, 3, new Insets(5, 5, 5, 5), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 2, new Insets(10, 10, 10, 0), -1, -1));
        main.add(panel1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setViewportView(transactions);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(10, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("An");
        panel2.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(to, new GridConstraints(3, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Betrag (Euro)");
        panel2.add(label2, new GridConstraints(4, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        value = new DoubleParseField();
        panel2.add(value, new GridConstraints(5, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        addTransaction = new JButton();
        addTransaction.setText("Auftrag hinzufügen");
        panel2.add(addTransaction, new GridConstraints(8, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(10, 4, new Insets(5, 5, 5, 5), -1, -1));
        panel2.add(panel3, new GridConstraints(9, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        a10 = new JButton();
        a10.setText("10€");
        panel3.add(a10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        a20 = new JButton();
        a20.setText("20€");
        panel3.add(a20, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a25 = new JButton();
        a25.setText("25€");
        panel3.add(a25, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a30 = new JButton();
        a30.setText("30€");
        panel3.add(a30, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a40 = new JButton();
        a40.setText("40€");
        panel3.add(a40, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a50 = new JButton();
        a50.setText("50€");
        panel3.add(a50, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a60 = new JButton();
        a60.setText("60€");
        panel3.add(a60, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a70 = new JButton();
        a70.setText("70€");
        panel3.add(a70, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a75 = new JButton();
        a75.setText("75€");
        panel3.add(a75, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a80 = new JButton();
        a80.setText("80€");
        panel3.add(a80, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a90 = new JButton();
        a90.setText("90€");
        panel3.add(a90, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a100 = new JButton();
        a100.setText("100€");
        panel3.add(a100, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a125 = new JButton();
        a125.setText("125€");
        panel3.add(a125, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a150 = new JButton();
        a150.setText("150€");
        panel3.add(a150, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a175 = new JButton();
        a175.setText("175€");
        panel3.add(a175, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a200 = new JButton();
        a200.setText("200€");
        panel3.add(a200, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a300 = new JButton();
        a300.setText("300€");
        panel3.add(a300, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a400 = new JButton();
        a400.setText("400€");
        panel3.add(a400, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a225 = new JButton();
        a225.setText("225€");
        panel3.add(a225, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a250 = new JButton();
        a250.setText("250€");
        panel3.add(a250, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a275 = new JButton();
        a275.setText("275€");
        panel3.add(a275, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a325 = new JButton();
        a325.setText("325€");
        panel3.add(a325, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a350 = new JButton();
        a350.setText("350€");
        panel3.add(a350, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a375 = new JButton();
        a375.setText("375€");
        panel3.add(a375, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a450 = new JButton();
        a450.setText("450€");
        panel3.add(a450, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a500 = new JButton();
        a500.setText("500€");
        panel3.add(a500, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a550 = new JButton();
        a550.setText("550€");
        panel3.add(a550, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a600 = new JButton();
        a600.setText("600€");
        panel3.add(a600, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a650 = new JButton();
        a650.setText("650€");
        panel3.add(a650, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a700 = new JButton();
        a700.setText("700€");
        panel3.add(a700, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a750 = new JButton();
        a750.setText("750€");
        panel3.add(a750, new GridConstraints(7, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a800 = new JButton();
        a800.setText("800€");
        panel3.add(a800, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a850 = new JButton();
        a850.setText("850€");
        panel3.add(a850, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a900 = new JButton();
        a900.setText("900€");
        panel3.add(a900, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1000 = new JButton();
        a1000.setText("1000€");
        panel3.add(a1000, new GridConstraints(8, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Von");
        panel2.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(from, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        info = new PermissionField();
        panel2.add(info, new GridConstraints(7, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Info");
        panel2.add(label4, new GridConstraints(6, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromKBValue = new JCheckBox();
        fromKBValue.setText("Von Kernbeißer");
        panel2.add(fromKBValue, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toKBValue = new JCheckBox();
        toKBValue.setText("An Kernbeißer");
        panel2.add(toKBValue, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hideInactive = new JCheckBox();
        hideInactive.setSelected(true);
        hideInactive.setText("");
        panel2.add(hideInactive, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("inaktive ausblenden");
        panel2.add(label5, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel4, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        delete = new JButton();
        delete.setText("Ausgewählte Überweisung löschen(Entf)");
        panel4.add(delete, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Summe:");
        panel5.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Anzahl:");
        panel5.add(label7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sum = new JLabel();
        sum.setText("sum");
        panel5.add(sum, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        count = new JLabel();
        count.setText("count");
        panel5.add(count, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel5.add(spacer3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        transferTransactions = new JButton();
        transferTransactions.setText("Überweisungen tätigen");
        main.add(transferTransactions, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        main.add(spacer4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        back = new JButton();
        back.setText("Schließen");
        main.add(back, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
        return main;
    }

}
