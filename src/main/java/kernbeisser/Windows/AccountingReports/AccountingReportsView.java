package kernbeisser.Windows.AccountingReports;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import javax.swing.*;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Enums.StatementType;
import kernbeisser.Exeptions.NoTransactionsFoundException;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class AccountingReportsView extends JDialog implements IView<AccountingReportsController> {

  private JButton cancel;
  @Getter private JComboBox<ExportTypes> exportType;
  private JButton submit;
  private DatePicker tillRollStartDate;
  private DatePicker tillRollEndDate;
  private JPanel main;
  private JRadioButton optTillRoll;
  @Getter private JRadioButton optAccountingReport;
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
  private JRadioButton optPermissionHolders;
  private JCheckBox permissionHoldersWithKeys;
  private JComboBox<String> accountingReportNo;
  private JComboBox<String> userBalanceReportNo;
  private JCheckBox duplexPrint;
  private Map<JComponent, JRadioButton> optionalComponents;

  @Linked private AccountingReportsController controller;

  Instant getDateValue(DatePicker comp) {
    return Instant.from(comp.getDate().atStartOfDay(ZoneId.systemDefault()));
  }

  ExportTypes getExportType() {
    return (ExportTypes) exportType.getSelectedItem();
  }

  boolean getDuplexPrint() {
    return duplexPrint.isSelected();
  }

  void submit(AccountingReportsController controller) {
    if (optTillRoll.isSelected()) {
      controller.exportTillroll(getDateValue(tillRollStartDate), getDateValue(tillRollEndDate));
    } else if (optAccountingReport.isSelected()) {
      controller.exportAccountingReport(
          Long.parseLong(
              ((String) accountingReportNo.getSelectedItem()).replace(" (neu erstellen)", "")),
          accountingReportWithNames.isSelected());
    } else if (optUserBalance.isSelected()) {
      String selectedReport = (String) userBalanceReportNo.getSelectedItem();
      long reportNo;
      if (selectedReport.equals("aktuell")) {
        reportNo = -1;
      } else {
        reportNo = Long.parseLong(selectedReport);
      }
      controller.exportUserBalance(reportNo, userBalanceWithNames.isSelected());
    } else if (optKeyUserList.isSelected()) {
      controller.exportKeyUserList(userKeySortOrder.getSelectedItem().toString());
    } else if (optTransactionStatement.isSelected()) {
      controller.exportTransactionStatement(
          user.getSelected().orElse(null),
          (StatementType) transactionStatementType.getSelectedItem(),
          optCurrent.isSelected());
    } else if (optPermissionHolders.isSelected()) {
      controller.exportPermissionHolders(permissionHoldersWithKeys.isSelected());
    }
  }

  public void setUser(Collection<User> allUser) {
    user.removeAllItems();
    allUser.forEach(user::addItem);
  }

  private void enableComponents() {
    optionalComponents.forEach((c, opt) -> c.setEnabled(opt.isSelected()));
  }

  @Override
  public void initialize(AccountingReportsController controller) {
    optionalComponents = new HashMap<>();
    optionalComponents.put(accountingReportNo, optAccountingReport);
    optionalComponents.put(accountingReportWithNames, optAccountingReport);
    optionalComponents.put(tillRollStartDate, optTillRoll);
    optionalComponents.put(tillRollEndDate, optTillRoll);
    optionalComponents.put(userBalanceWithNames, optUserBalance);
    optionalComponents.put(userBalanceReportNo, optUserBalance);
    optionalComponents.put(user, optTransactionStatement);
    optionalComponents.put(optLast, optTransactionStatement);
    optionalComponents.put(optCurrent, optTransactionStatement);
    optionalComponents.put(transactionStatementType, optTransactionStatement);
    optionalComponents.put(userKeySortOrder, optKeyUserList);
    optionalComponents.put(permissionHoldersWithKeys, optPermissionHolders);

    cancel.addActionListener(e -> back());
    submit.addActionListener(e -> submit(controller));
    ExportTypes[] exportTypes = controller.getExportTypes();
    for (ExportTypes t : exportTypes) {
      exportType.addItem(t);
    }
    exportType.addActionListener(e -> duplexPrint.setEnabled(getExportType() == ExportTypes.PRINT));
    for (String s : controller.getUserKeySortOrders()) {
      userKeySortOrder.addItem(s);
    }
    optCurrent.setSelected(true);

    var now = LocalDate.now(ZoneId.systemDefault());
    tillRollStartDate.setDate(now);
    tillRollEndDate.setDate(now);

    int maxReportNo = (int) Transaction.getLastReportNo();
    for (int i = 1; i <= maxReportNo; i++) {
      accountingReportNo.addItem(Integer.toString(i));
      userBalanceReportNo.addItem(Integer.toString(i));
    }
    userBalanceReportNo.addItem("aktuell");
    userBalanceReportNo.setSelectedIndex(maxReportNo);
    try {
      Transaction.getUnreportedTransactions();
      accountingReportNo.addItem((maxReportNo + 1) + " (neu erstellen)");
      accountingReportNo.setSelectedIndex(maxReportNo);
      userBalanceReportNo.setSelectedIndex(maxReportNo);
    } catch (NoTransactionsFoundException ignored) {
      accountingReportNo.setSelectedIndex(maxReportNo - 1);
    }

    transactionStatementType.setModel(new DefaultComboBoxModel<>(StatementType.values()));
    optionalComponents.values().stream()
        .distinct()
        .forEach(c -> c.addActionListener(e -> enableComponents()));
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

  public void messageEmptyReportNo(long reportNo) {
    JOptionPane.showMessageDialog(
        getContent(),
        "Zur Berichtsnummer " + reportNo + " liegen keine Umsätze vor.",
        "Umsatzbericht",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void messageNoAccountingReport(boolean b) {
    JOptionPane.showMessageDialog(
        getContent(),
        "Der Bericht wurde " + (b ? "" : "nicht") + " erfolgreich erstellt",
        "Umsatzbericht",
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

  private String BonNoAndDate(Purchase p) {
    return p.getId() + " (" + Date.INSTANT_DATE.format(p.getCreateDate()) + ")";
  }

  private void createUIComponents() {
    user = new AdvancedComboBox<>(u -> u.getFullName(true));
    tillRollStartDate = new DatePicker(new DatePickerSettings(Locale.GERMANY));
    tillRollEndDate = new DatePicker(new DatePickerSettings(Locale.GERMANY));
  }

  public void messageDateValues() {
    JOptionPane.showMessageDialog(
        getContent(),
        "Das Startdatum muss vor dem Enddatum liegen!",
        "Ungütlige Eingabe",
        JOptionPane.WARNING_MESSAGE);
  }
}
