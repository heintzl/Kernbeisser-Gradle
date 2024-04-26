package kernbeisser.Windows.AccountingReports;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Enums.StatementType;
import kernbeisser.Exeptions.NoTransactionsFoundException;
import kernbeisser.Reports.UserNameObfuscation;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class AccountingReportsView extends JDialog implements IView<AccountingReportsController> {

    private JButton cancel;
    @Getter
    private JComboBox<ExportTypes> exportType;
    private JButton submit;
    private DatePicker tillRollStartDate;
    private DatePicker tillRollEndDate;
    private JPanel main;
    private JRadioButton optTillRoll;
    @Getter
    private JRadioButton optAccountingReport;
    private JRadioButton optUserBalance;
    private JCheckBox userBalanceWithNames;
    private JComboBox<UserNameObfuscation> accountingReportWithNames;
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

    @Linked
    private AccountingReportsController controller;

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
                    (UserNameObfuscation) accountingReportWithNames.getSelectedItem());
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
        for (ExportTypes t : controller.getExportTypes()) {
            exportType.addItem(t);
        }
        for (UserNameObfuscation t : controller.getUserNameObfuscations()) {
            accountingReportWithNames.addItem(t);
        }
        exportType.addActionListener(e -> duplexPrint.setEnabled(getExportType() == ExportTypes.PRINT));
        for (String s : controller.getUserKeySortOrders()) {
            userKeySortOrder.addItem(s);
        }
        optCurrent.setSelected(true);

        LocalDate now = LocalDate.now(ZoneId.systemDefault());
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
        main.setLayout(new GridLayoutManager(1, 2, new Insets(5, 10, 5, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        main.add(panel1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, 18, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setHorizontalAlignment(0);
        label1.setHorizontalTextPosition(0);
        label1.setText("Druck-Ausgaben für die Buchhaltung");
        panel1.add(label1, BorderLayout.NORTH);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 6, new Insets(5, 5, 5, 5), -1, -1));
        panel1.add(panel2, BorderLayout.SOUTH);
        cancel = new JButton();
        cancel.setText("Schließen");
        panel2.add(cancel, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        submit = new JButton();
        submit.setText("OK");
        panel2.add(submit, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Ausgabe als");
        panel2.add(label2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exportType = new JComboBox();
        panel2.add(exportType, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        duplexPrint = new JCheckBox();
        duplexPrint.setSelected(true);
        duplexPrint.setText("Doppelseitig drucken");
        panel2.add(duplexPrint, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        panel1.add(panel3, BorderLayout.CENTER);
        final JLabel label3 = new JLabel();
        label3.setText("von:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 10, 6, 10);
        panel3.add(label3, gbc);
        optTillRoll = new JRadioButton();
        optTillRoll.setText("Bonrolle");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel3.add(optTillRoll, gbc);
        optAccountingReport = new JRadioButton();
        optAccountingReport.setText("Umsatzbericht");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel3.add(optAccountingReport, gbc);
        optUserBalance = new JRadioButton();
        optUserBalance.setText("Guthabenstände");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel3.add(optUserBalance, gbc);
        optKeyUserList = new JRadioButton();
        optKeyUserList.setText("Benutzerschlüssel");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel3.add(optKeyUserList, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Sortierung:");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 10, 6, 10);
        panel3.add(label4, gbc);
        optTransactionStatement = new JRadioButton();
        optTransactionStatement.setText("Kontoauszug");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel3.add(optTransactionStatement, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Mitglied:");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 10, 6, 10);
        panel3.add(label5, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 5, 0, 5);
        panel3.add(user, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Umfang:");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 10, 6, 10);
        panel3.add(label6, gbc);
        transactionStatementType = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);
        panel3.add(transactionStatementType, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 5, 0, 5);
        panel3.add(tillRollStartDate, gbc);
        userKeySortOrder = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        userKeySortOrder.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.gridwidth = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 5, 0, 5);
        panel3.add(userKeySortOrder, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel3.add(spacer2, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("Nr.");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 10, 6, 10);
        panel3.add(label7, gbc);
        optLast = new JRadioButton();
        optLast.setText("letzer");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel3.add(optLast, gbc);
        optCurrent = new JRadioButton();
        optCurrent.setText("aktueller");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        panel3.add(optCurrent, gbc);
        optPermissionHolders = new JRadioButton();
        optPermissionHolders.setText("Rolleninhaber");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel3.add(optPermissionHolders, gbc);
        permissionHoldersWithKeys = new JCheckBox();
        permissionHoldersWithKeys.setText("inkl. Schlüsselinhabern, Vollmitgliedern und Basis-Anwendern");
        permissionHoldersWithKeys.setVerticalAlignment(3);
        permissionHoldersWithKeys.setVerticalTextPosition(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 5, 0, 5);
        panel3.add(permissionHoldersWithKeys, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("bis:");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 10, 6, 10);
        panel3.add(label8, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 5, 0, 5);
        panel3.add(tillRollEndDate, gbc);
        accountingReportNo = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 5, 0, 5);
        panel3.add(accountingReportNo, gbc);
        accountingReportWithNames = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 5, 0, 5);
        panel3.add(accountingReportWithNames, gbc);
        userBalanceWithNames = new JCheckBox();
        userBalanceWithNames.setText("Klarnamen ausgeben");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 5, 0, 5);
        panel3.add(userBalanceWithNames, gbc);
        userBalanceReportNo = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 5, 0, 5);
        panel3.add(userBalanceReportNo, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("Nr.");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 10, 6, 10);
        panel3.add(label9, gbc);
        label5.setLabelFor(user);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(optAccountingReport);
        buttonGroup.add(optTillRoll);
        buttonGroup.add(optUserBalance);
        buttonGroup.add(optKeyUserList);
        buttonGroup.add(optTransactionStatement);
        buttonGroup.add(optPermissionHolders);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(optLast);
        buttonGroup.add(optCurrent);
    }

  /**
   * @noinspection ALL
   */
  private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
        return main;
    }

}
