package kernbeisser.Windows.AccountingReports;

import javax.swing.*;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class AccountingReportsView extends JDialog implements IView<AccountingReportsController> {

  private JButton cancel;
  private JComboBox exportType;
  private JButton submit;
  private IntegerParseField days;
  private JPanel main;
  private JRadioButton optBonrolle;
  @Getter private JRadioButton optLadendienstEndabrechnung;
  private IntegerParseField startBon;
  private IntegerParseField endBon;
  private JRadioButton optUserBalance;
  private JCheckBox userBalanceWithNames;
  private JCheckBox accountingReportWithNames;
  private JRadioButton optKeyUserList;
  private JComboBox userKeySortOrder;

  @Linked private AccountingReportsController controller;

  int getDays() {
    return days.getSafeValue();
  }

  ExportTypes getExportType() {
    return (ExportTypes) exportType.getSelectedItem();
  }

  void submit(AccountingReportsController controller) {
    if (optBonrolle.isSelected()) {
      controller.exportTillroll(getExportType(), getDays());
    } else if (optLadendienstEndabrechnung.isSelected()) {
      controller.exportAccountingReport(
          getExportType(),
          startBon.getSafeValue(),
          endBon.getSafeValue(),
          accountingReportWithNames.isSelected());
    } else if (optUserBalance.isSelected()) {
      controller.exportUserBalance(getExportType(), userBalanceWithNames.isSelected());
    } else if (optKeyUserList.isSelected()) {
      controller.exportKeyUserList(getExportType(), userKeySortOrder.getSelectedItem().toString());
    }
  }

  @Override
  public void initialize(AccountingReportsController controller) {
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
}
