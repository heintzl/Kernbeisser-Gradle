package kernbeisser.StartUp.DataImport;

import kernbeisser.Windows.Controller;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DataImportView extends Window implements View {
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

    private DataImportController controller;

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new DataImportView(null);
    }

    public DataImportView(Window currentWindow) {
        super(currentWindow);
        controller = new DataImportController(this);
        add(main);
        pack();
        setLocationRelativeTo(currentWindow);
        importData.addActionListener(e -> controller.importData());
        dataPath.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                controller.checkDataSource();
            }
        });
        search.addActionListener(e -> controller.openFileExplorer());
    }

    String getFilePath() {
        return dataPath.getText();
    }

    void setValidDataSource(boolean is) {
        dataPath.setForeground(is ? Color.GREEN : Color.RED);
    }

    void setFilePath(String s) {
        dataPath.setText(s);
    }


    void itemSourceFound(boolean is) {
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
        currentActionUser.setText("Benutzer: " + (i < 2 ? "Jobs" : "Benutzer") + " " + (i % 2 == 0 ? "zur Datenbank gespeichert" : "werden konvertiert") + "...");
        itemProgress.setValue(i);
        String target = "";
        String status = i % 2 == 1 ? "auf der Datenbank gespeichert" : "werden konvertiert";
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
        String status = i % 2 == 1 ? "auf der Datenbank gespeichert" : "werden konvertiert";
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

    @Override
    public Controller getController() {
        return controller;
    }

    void itemSourcesNotExists() {
        JOptionPane.showMessageDialog(this, "Der Artikeldatensatz beinhalted Pfade von Dateien die nicht exesistieren!", "Artikeldatensatz unvollst\u00e4ndig", JOptionPane.ERROR_MESSAGE);
    }

    void userSourcesNotExists() {
        JOptionPane.showMessageDialog(this, "Der Nutzerdatensatz beinhalted Pfade von Dateien die nicht exesistieren!", "Nutzerdatensatz unvollst\u00e4ndig", JOptionPane.ERROR_MESSAGE);
    }

}
