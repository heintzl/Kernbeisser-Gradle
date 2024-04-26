package kernbeisser.StartUp.LogIn;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Config.Config;
import kernbeisser.Config.Config.DBAccess;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.apache.commons.collections.KeyValue;
import org.jetbrains.annotations.NotNull;

public class DBLogInView implements IView<DBLogInController> {

    private JButton logIn;
    private JTextField url;
    private JTextField username;
    private JPasswordField password;
    private JButton cancel;
    private JPanel main;
    private JTextField encoding;
    private JButton cSVImport;
    private JTextField filePath;
    private JButton fileChooser;
    private JScrollPane logPane;
    private ObjectTable<KeyValue> logTable;

    @Linked
    private DBLogInController controller;

    private void createUIComponents() {
        logTable =
                new ObjectTable<>(
                        Columns.<KeyValue>create("Level", e -> e.getKey().toString()).withPreferredWidth(80),
                        Columns.create("Meldungen", KeyValue::getValue));
    }

    @Override
    public void initialize(DBLogInController controller) {
        DBAccess access = Config.getConfig().getDBAccessData();
        url.setText(access.getUrl());
        username.setText(access.getUsername());
        logIn.addActionListener(e -> controller.logIn());
        username.addActionListener(e -> controller.logIn());
        password.addActionListener(e -> controller.logIn());
        password.setText(access.getPassword());
        cancel.addActionListener(e -> cancel());
        fileChooser.addActionListener(e -> openFileExplorer());
        fileChooser.setIcon(IconFontSwing.buildIcon(FontAwesome.FOLDER, 20, new Color(255, 192, 3)));
        cSVImport.addActionListener(e -> controller.readFile(filePath.getText()));
        logTable.addComponentListener(
                new ComponentAdapter() {
                    public void componentResized(ComponentEvent e) {
                        logTable.scrollRectToVisible(logTable.getCellRect(logTable.getRowCount() - 1, 0, true));
                    }
                });
    }

    private void cancel() {
        if (controller.isStartUp()) {
            System.exit(0);
        } else {
            back();
        }
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

    @Override
    public String getTitle() {
        return "Datenbankverbindung";
    }

    void connectionRefused() {
        JOptionPane.showMessageDialog(
                getTopComponent(),
                "Es kann leider keine Verbindung hergestellt werden,\n bitte \u00fcberpr\u00fcfen sie die Eingaben nach Fehlern");
    }

    public DBAccess getDBAccess() {
        return new DBAccess(
                url.getText(), username.getText(), new String(password.getPassword()), encoding.getText());
    }

    void openFileExplorer() {
        Path importPath = Config.getConfig().getDefaultBnnInboxDir().toPath();
        String chooserRoot = Files.exists(importPath) ? importPath.toString() : ".";
        JFileChooser jFileChooser = new JFileChooser("");
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setFileFilter(
                new FileNameExtensionFilter("CSV-Datei", "ASC", "asc", "CSV", "csv", "TXT", "txt"));
        jFileChooser.addActionListener(
                e -> {
                    if (jFileChooser.getSelectedFile() == null) {
                        return;
                    }
                    String choosenFile = jFileChooser.getSelectedFile().getAbsolutePath();
                    filePath.setText(choosenFile);
                });
        jFileChooser.showOpenDialog(getContent());
    }

    public void clearLogMessages() {
        logTable.clear();
        // logTable.repaint();
    }

    public void showLogMessages(java.util.List<KeyValue> messages) {

        logPane.setVisible(true);
        logTable.addAll(messages);
        // logTable.repaint();
    }

    public void messagePathNotFound(String path) {
        JOptionPane.showMessageDialog(
                getContent(),
                "Die Datei \"" + path + "\" konnte nicht gefunden werden.",
                "Datei nicht gefunden",
                JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirmCSVImport() {
        return JOptionPane.showConfirmDialog(
                getContent(),
                "Mit diesem Import-Werkzeug können große Datenmengen innerhalb von Sekunden \n"
                        + "geschrottet werden. Du solltest wirklich genau wissen, was du tust und \n"
                        + "sicherstellen, dass direkt vor dem Import ein Datenbank-Backup erstellt wurde.\n"
                        + "Schau dir genau das Importprotokoll an und prüfe anschließend das Importergebnis\n"
                        + "in den entsprechend Objektansichten, bevor wieder irgendwelche Daten erfasst \n"
                        + "oder geändert werden, denn diese gehen bei einem Rollback auf das Backup verloren!\n\n"
                        + "Willst du wirklich fortfahren?",
                "Ich weiß genau, was ich tue!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE)
                == JOptionPane.YES_OPTION;
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
        createUIComponents();
        main = new JPanel();
        main.setLayout(new GridLayoutManager(12, 3, new Insets(10, 10, 10, 10), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Datenbank URL");
        main.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        url = new JTextField();
        main.add(url, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Nutzername");
        main.add(label2, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        username = new JTextField();
        main.add(username, new GridConstraints(6, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Password");
        main.add(label3, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        password = new JPasswordField();
        main.add(password, new GridConstraints(8, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        Font label4Font = this.$$$getFont$$$(null, -1, 16, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        label4.setText("Datenbankverbindung");
        main.add(label4, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logIn = new JButton();
        logIn.setForeground(new Color(-12797137));
        logIn.setText("Anmelden");
        main.add(logIn, new GridConstraints(11, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancel = new JButton();
        cancel.setForeground(new Color(-4486589));
        cancel.setText("Abbrechen");
        main.add(cancel, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Datenbank Encoding");
        main.add(label5, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        encoding = new JTextField();
        main.add(encoding, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(10, 0, 0, 0), -1, -1));
        main.add(panel1, new GridConstraints(9, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cSVImport = new JButton();
        cSVImport.setMargin(new Insets(2, 2, 2, 2));
        cSVImport.setText("CSV-Datenimport");
        panel1.add(cSVImport, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        filePath = new JTextField();
        panel1.add(filePath, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fileChooser = new JButton();
        fileChooser.setText("");
        panel1.add(fileChooser, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logPane = new JScrollPane();
        logPane.setEnabled(true);
        logPane.setVisible(true);
        main.add(logPane, new GridConstraints(10, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        logPane.setViewportView(logTable);
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
