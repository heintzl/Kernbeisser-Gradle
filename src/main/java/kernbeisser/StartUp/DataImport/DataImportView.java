package kernbeisser.StartUp.DataImport;

import java.awt.*;
import javax.swing.*;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
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

  @Linked private DataImportController controller;

  boolean createStandardAdmin() {
    return importStandardAdmin.isSelected();
  }

  String getFilePath() {
    return dataPath.getText();
  }

  void setFilePath(String s) {
    dataPath.setText(s);
  }

  void setValidDataSource(boolean is) {
    dataPath.setForeground(is ? Color.GREEN : Color.RED);
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
    currentActionUser.setText(
        "Benutzer: "
            + (i < 2 ? "Jobs" : "Benutzer")
            + " "
            + (i % 2 == 0 ? "zur Datenbank gespeichert" : "werden konvertiert")
            + "...");
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

  void itemSourcesNotExists() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Der Artikeldatensatz beinhalted Pfade von Dateien die nicht exesistieren!",
        "Artikeldatensatz unvollst\u00e4ndig",
        JOptionPane.ERROR_MESSAGE);
  }

  void userSourcesNotExists() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Der Nutzerdatensatz beinhalted Pfade von Dateien die nicht exesistieren!",
        "Nutzerdatensatz unvollst\u00e4ndig",
        JOptionPane.ERROR_MESSAGE);
  }

  String requestPassword() {
    return JOptionPane.showInputDialog(
        getTopComponent(), "Bitte geben sie ein Password für den automatisch erzeugten Admin ein");
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
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }
}
