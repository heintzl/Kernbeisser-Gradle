package kernbeisser.Windows.AccountingReports;

import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;
import javax.swing.*;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Enums.StatementType;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingDatePicker;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import lombok.var;
import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jetbrains.annotations.NotNull;

public class AccountingReportsView extends JDialog implements IView<AccountingReportsController> {

  private JButton cancel;
  private JComboBox<ExportTypes> exportType;
  private JButton submit;
  private JDatePickerImpl tillRollStartDate;
  private JDatePickerImpl tillRollEndDate;
  private JPanel main;
  private JRadioButton optTillRoll;
  @Getter private JRadioButton optAccountingReport;
  private AdvancedComboBox<Purchase> startBon;
  private AdvancedComboBox<Purchase> endBon;
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
  private Map<JComponent, JRadioButton> optionalComponents;

  @Linked private AccountingReportsController controller;

  Calendar getDateValue(JDatePickerImpl comp) {
    return (Calendar) comp.getModel().getValue();
  }

  ExportTypes getExportType() {
    return (ExportTypes) exportType.getSelectedItem();
  }

  void submit(AccountingReportsController controller) {
    if (optTillRoll.isSelected()) {
      controller.exportTillroll(
          getExportType(), getDateValue(tillRollStartDate), getDateValue(tillRollEndDate));
    } else if (optAccountingReport.isSelected()) {
      controller.exportAccountingReport(
          getExportType(),
          startBon.getSelected(),
          endBon.getSelected(),
          accountingReportWithNames.isSelected());
    } else if (optUserBalance.isSelected()) {
      controller.exportUserBalance(getExportType(), userBalanceWithNames.isSelected());
    } else if (optKeyUserList.isSelected()) {
      controller.exportKeyUserList(getExportType(), userKeySortOrder.getSelectedItem().toString());
    } else if (optTransactionStatement.isSelected()) {
      controller.exportTransactionStatement(
          getExportType(),
          user.getSelected().orElse(null),
          (StatementType) transactionStatementType.getSelectedItem(),
          optCurrent.isSelected());
    } else if (optPermissionHolders.isSelected()) {
      controller.exportPermissionHolders(getExportType(), permissionHoldersWithKeys.isSelected());
    }
  }

  public void setUser(Collection<User> allUser) {
    user.removeAllItems();
    allUser.forEach(user::addItem);
  }

  public void setBons(List<Purchase> allPurchases) {
    startBon.removeAllItems();
    endBon.removeAllItems();
    startBon.setItems(Lists.reverse(allPurchases));
    endBon.setItems(Lists.reverse(allPurchases));
    //    Purchase.getLastBon().ifPresent(p -> endBon.setSelectedItem(p));
  }

  private void enableComponents() {
    optionalComponents.forEach(
        (c, opt) -> {
          boolean enabled = opt.isSelected();
          if (c instanceof JDatePickerImpl) {
            var datePicker = (JDatePickerImpl) c;
            datePicker.setTextEditable(enabled);
            datePicker.getComponent(1).setEnabled(enabled);
          }
          c.setEnabled(enabled);
        });
  }

  @Override
  public void initialize(AccountingReportsController controller) {
    optionalComponents = new HashMap<>();
    optionalComponents.put(startBon, optAccountingReport);
    optionalComponents.put(endBon, optAccountingReport);
    optionalComponents.put(accountingReportWithNames, optAccountingReport);
    optionalComponents.put(tillRollStartDate, optTillRoll);
    optionalComponents.put(tillRollEndDate, optTillRoll);
    optionalComponents.put(userBalanceWithNames, optUserBalance);
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
    for (String s : controller.getUserKeySortOrders()) {
      userKeySortOrder.addItem(s);
    }
    optCurrent.setSelected(true);
    var now = LocalDate.now(ZoneId.systemDefault());
    tillRollStartDate
        .getModel()
        .setDate(
            now.get(ChronoField.YEAR),
            now.get(ChronoField.MONTH_OF_YEAR) - 1,
            now.get(ChronoField.DAY_OF_MONTH) - 1);
    tillRollStartDate.getModel().setSelected(true);
    tillRollEndDate
        .getModel()
        .setDate(
            now.get(ChronoField.YEAR),
            now.get(ChronoField.MONTH_OF_YEAR) - 1,
            now.get(ChronoField.DAY_OF_MONTH));
    tillRollEndDate.getModel().setSelected(true);
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
    startBon = new AdvancedComboBox<>(this::BonNoAndDate);
    endBon = new AdvancedComboBox<>(this::BonNoAndDate);
    user = new AdvancedComboBox<>(User::getFullName);
    tillRollStartDate =
        new JDatePickerImpl(
            new JDatePanelImpl(
                AccessCheckingDatePicker.createDateModel(),
                AccessCheckingDatePicker.getI18nStrings(Locale.getDefault())),
            new DateComponentFormatter());
    tillRollEndDate =
        new JDatePickerImpl(
            new JDatePanelImpl(
                AccessCheckingDatePicker.createDateModel(),
                AccessCheckingDatePicker.getI18nStrings(Locale.getDefault())),
            new DateComponentFormatter());
  }

  public void messageBonValues() {
    JOptionPane.showMessageDialog(
        getContent(),
        "Die Startbon-Nummer muss kleiner sein, als die Endbon-Nummer!",
        "Ungütlige Eingabe",
        JOptionPane.WARNING_MESSAGE);
  }
}
