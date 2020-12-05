package kernbeisser.Windows.AccountingReports;

import java.util.*;
import javax.swing.*;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Enums.StatementType;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class AccountingReportsView extends JDialog implements IView<AccountingReportsController> {

  private JButton cancel;
  private JComboBox<ExportTypes> exportType;
  private JButton submit;
  private IntegerParseField days;
  private JPanel main;
  private JRadioButton optTillRoll;
  @Getter private JRadioButton optAccountingReport;
  private IntegerParseField startBon;
  private IntegerParseField endBon;
  private JRadioButton optUserBalance;
  private JCheckBox userBalanceWithNames;
  private JCheckBox accountingReportWithNames;
  private JRadioButton optKeyUserList;
  private JComboBox<String> userKeySortOrder;
  private JRadioButton optTransactionStatement;
  private AdvancedComboBox<User> user;
  private JComboBox<StatementType> transactionStatementType;
  private JRadioButton optLast;
  private JRadioButton optCurrent;
  private Map<JComponent, JRadioButton> optionalComponents;

  @Linked private AccountingReportsController controller;

  int getDays() {
    return days.getSafeValue();
  }

  ExportTypes getExportType() {
    return (ExportTypes) exportType.getSelectedItem();
  }

  void submit(AccountingReportsController controller) {
    if (optTillRoll.isSelected()) {
      controller.exportTillroll(getExportType(), getDays());
    } else if (optAccountingReport.isSelected()) {
      controller.exportAccountingReport(
          getExportType(),
          startBon.getSafeValue(),
          endBon.getSafeValue(),
          accountingReportWithNames.isSelected());
    } else if (optUserBalance.isSelected()) {
      controller.exportUserBalance(getExportType(), userBalanceWithNames.isSelected());
    } else if (optKeyUserList.isSelected()) {
      controller.exportKeyUserList(getExportType(), userKeySortOrder.getSelectedItem().toString());
    } else if (optTransactionStatement.isSelected()) {
      controller.exportTransactionStatement(
          getExportType(),
          user.getSelected(),
          (StatementType) transactionStatementType.getSelectedItem(),
          optCurrent.isSelected());
    }
  }

  public void setUser(Collection<User> allUser) {
    user.removeAllItems();
    allUser.forEach(user::addItem);
  }

  private void enableComponents() {
    optionalComponents.forEach(
        (c, opt) -> {
          c.setEnabled(opt.isSelected());
        });
  }

  @Override
  public void initialize(AccountingReportsController controller) {
    optionalComponents = new HashMap<>();
    optionalComponents.put(startBon, optAccountingReport);
    optionalComponents.put(endBon, optAccountingReport);
    optionalComponents.put(accountingReportWithNames, optAccountingReport);
    optionalComponents.put(days, optTillRoll);
    optionalComponents.put(userBalanceWithNames, optUserBalance);
    optionalComponents.put(user, optTransactionStatement);
    optionalComponents.put(optLast, optTransactionStatement);
    optionalComponents.put(optCurrent, optTransactionStatement);
    optionalComponents.put(transactionStatementType, optTransactionStatement);
    optionalComponents.put(userKeySortOrder, optKeyUserList);

    days.setText("1");
    cancel.addActionListener(e -> back());
    submit.addActionListener(e -> submit(controller));
    ExportTypes[] exportTypes = controller.getExportTypes();
    for (ExportTypes t : exportTypes) {
      exportType.addItem(t);
    }
    for (String s : controller.getUserKeySortOrders()) {
      userKeySortOrder.addItem(s);
    }
    optCurrent.setSelected(true);
    transactionStatementType.setModel(new DefaultComboBoxModel<>(StatementType.values()));
    optKeyUserList.addActionListener(e -> enableComponents());
    optTillRoll.addActionListener(e -> enableComponents());
    optUserBalance.addActionListener(e -> enableComponents());
    optTransactionStatement.addActionListener(e -> enableComponents());
    optAccountingReport.addActionListener(e -> enableComponents());
    enableComponents();
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  public void messageNoItems(String title) {
    JOptionPane.showMessageDialog(
        getContent(),
        "Im angegebenen Zeitraum liegen keine Umsätze vor.",
        title,
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void messageNotImplemented(ExportTypes exportType) {
    JOptionPane.showMessageDialog(
        getContent(),
        exportType.getName() + ": Diese Methode ist noch nicht verfügbar!",
        "Ausgabefehler",
        JOptionPane.WARNING_MESSAGE);
  }

  @Override
  public String getTitle() {
    return "Buchhaltungsberichte";
  }

  private void createUIComponents() {
    user = new AdvancedComboBox<>(User::getFullName);
  }
}
