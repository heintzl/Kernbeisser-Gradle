package kernbeisser.StartUp.DataImport;

import java.awt.*;
import javax.swing.*;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class DataImportView implements IView<DataImportController> {

    private JPanel main;
    private JButton importData;
    private JButton cancel;
    private JCheckBox importItems;
    private JButton search;
    private JTextField dataPath;
    private JCheckBox importUser;
    private JProgressBar itemProgress;
    private JProgressBar userProgress;
    private JLabel currentActionItems;
    private JLabel currentActionUser;
    private JCheckBox importStandardAdmin;
    @Getter
    private JPasswordField adminPassword;
    private JCheckBox exportPasswordCSV;

    @Linked
    private DataImportController controller;

    boolean createStandardAdmin() {
        return importStandardAdmin.isSelected();
    }

    String getFilePath() {
        return dataPath.getText();
    }

    String getAdminPassword() {
        return new String(adminPassword.getPassword());
    }

    void setFilePath(String s) {
        dataPath.setText(s);
    }

    void setValidDataSource(boolean is) {
        dataPath.setForeground(is ? Color.GREEN : Color.RED);
    }

    void articleSourceFound(boolean is) {
        importItems.setSelected(is);
        importItems.setEnabled(is);
    }

    void userSourceFound(boolean is) {
        importUser.setSelected(is);
        importUser.setEnabled(is);
    }

    void setUserProgress(int i) {
        userProgress.setValue(i);
        currentActionUser.setVisible(true);
        currentActionUser.setText(
                "Benutzer: "
                        + (i < 2 ? "Jobs" : "Benutzer")
                        + " "
                        + (i % 2 == 0 ? "in der Datenbank gespeichert" : "werden konvertiert")
                        + "...");
        itemProgress.setValue(i);
        String target = "";
        String status = i % 2 == 1 ? "in der Datenbank gespeichert" : "werden konvertiert";
        switch (i) {
            case 0:
                currentActionUser.setVisible(true);
            case 1:
                target = "Jobs";
                break;
            case 2:
            case 3:
                target = "Nutzer";
                break;
            case 4:
                currentActionUser.setText("Nutzer Fertig");
                return;
        }
        currentActionUser.setText("Nutzer: " + target + " " + status + "...");
    }

    void setItemProgress(int i) {
        itemProgress.setValue(i);
        String target = "";
        String status = i % 2 == 1 ? "in der Datenbank gespeichert" : "werden konvertiert";
        switch (i) {
            case 0:
                currentActionItems.setVisible(true);
            case 1:
                target = "Lieferanten";
                break;
            case 2:
            case 3:
                target = "Preislisten";
                break;
            case 4:
            case 5:
                target = "Artikel";
                break;
            case 6:
                currentActionItems.setText("Artikel Fertig");
                return;
        }
        currentActionItems.setText("Artikel: " + target + " " + status + "...");
    }

    boolean importUser() {
        return importUser.isSelected();
    }

    boolean importItems() {
        return importItems.isSelected();
    }

    void itemSourcesNotExists() {
        JOptionPane.showMessageDialog(
                getTopComponent(),
                "Der Artikeldatensatz beinhaltet Pfade von Dateien die nicht existieren!",
                "Artikeldatensatz unvollst\u00e4ndig",
                JOptionPane.ERROR_MESSAGE);
    }

    void userSourcesNotExists() {
        JOptionPane.showMessageDialog(
                getTopComponent(),
                "Der Nutzerdatensatz beinhaltet Pfade von Dateien die nicht existieren!",
                "Nutzerdatensatz unvollst\u00e4ndig",
                JOptionPane.ERROR_MESSAGE);
    }

    void messageMissingPassword() {
        JOptionPane.showMessageDialog(
                getTopComponent(),
                "Das Passwort darf nicht leer sein!",
                "Passwort fehlt",
                JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void initialize(DataImportController controller) {
        importData.addActionListener(e -> controller.importData());
    /*dataPath.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            controller.checkDataSource();
        }
    });*/
        search.addActionListener(e -> controller.openFileExplorer());
        cancel.addActionListener(e -> controller.cancel());
        importStandardAdmin.setSelected(true);
        importStandardAdmin.addActionListener(
                e -> adminPassword.setEnabled(importStandardAdmin.isSelected()));
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

    @Override
    public String getTitle() {
        return "Daten importieren";
    }

    public boolean shouldExportPasswordsAsCsv() {
        return exportPasswordCSV.isSelected();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        main = new JPanel();
        main.setLayout(new BorderLayout(0, 0));
        main.setMaximumSize(new Dimension(-1, -1));
        main.setMinimumSize(new Dimension(350, 300));
        main.setPreferredSize(new Dimension(351, 300));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel1, BorderLayout.SOUTH);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cancel = new JButton();
        cancel.setText("Abbrechen");
        panel2.add(cancel);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        panel1.add(panel3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        importData = new JButton();
        importData.setText("Daten importieren");
        panel3.add(importData);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        main.add(panel4, BorderLayout.CENTER);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(11, 5, new Insets(10, 10, 10, 10), -1, -1));
        panel5.setMinimumSize(new Dimension(100, 124));
        panel5.setPreferredSize(new Dimension(150, 124));
        panel4.add(panel5, BorderLayout.CENTER);
        final Spacer spacer1 = new Spacer();
        panel5.add(spacer1, new GridConstraints(10, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        importItems = new JCheckBox();
        importItems.setEnabled(false);
        importItems.setText("Artikel");
        panel5.add(importItems, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Dateipfad:");
        panel5.add(label1, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dataPath = new JTextField();
        panel5.add(dataPath, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(100, -1), null, 0, false));
        search = new JButton();
        search.setText("Suchen");
        panel5.add(search, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(100, -1), 0, false));
        importUser = new JCheckBox();
        importUser.setEnabled(false);
        importUser.setText("Nutzer");
        panel5.add(importUser, new GridConstraints(4, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("");
        panel5.add(label2, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        itemProgress = new JProgressBar();
        itemProgress.setMaximum(6);
        panel5.add(itemProgress, new GridConstraints(3, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(200, -1), null, 0, false));
        userProgress = new JProgressBar();
        userProgress.setMaximum(4);
        panel5.add(userProgress, new GridConstraints(5, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(200, -1), null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel5.add(spacer2, new GridConstraints(10, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        currentActionItems = new JLabel();
        currentActionItems.setText("Artikel:");
        currentActionItems.setVisible(false);
        panel5.add(currentActionItems, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        currentActionUser = new JLabel();
        currentActionUser.setText("Nutzer:");
        currentActionUser.setVisible(false);
        panel5.add(currentActionUser, new GridConstraints(9, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        importStandardAdmin = new JCheckBox();
        importStandardAdmin.setText("Standard Admin erzeugen");
        panel5.add(importStandardAdmin, new GridConstraints(7, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Passwort:");
        panel5.add(label3, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        adminPassword = new JPasswordField();
        panel5.add(adminPassword, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        exportPasswordCSV = new JCheckBox();
        exportPasswordCSV.setText("Password-CSV exportieren");
        panel5.add(exportPasswordCSV, new GridConstraints(6, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main;
    }
}
