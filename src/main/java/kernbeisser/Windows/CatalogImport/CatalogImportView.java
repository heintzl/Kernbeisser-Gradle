package kernbeisser.Windows.CatalogImport;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import kernbeisser.Config.Config;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Tasks.Catalog.CatalogImportError;
import kernbeisser.Windows.MVC.IView;
import org.jetbrains.annotations.NotNull;

public class CatalogImportView implements IView<CatalogImportController> {
  private JTextField filePath;
  private JButton fileChooser;
  private ObjectTable readErrors;
  private JButton close;
  private JButton readFile;
  private JButton applyChanges;
  private JPanel main;

  @Override
  public void initialize(CatalogImportController controller) {
    close.addActionListener(e -> back());
    readFile.addActionListener(e -> controller.readFile(filePath.getText()));
    fileChooser.addActionListener(e -> openFileExplorer());
  }

  private void createUIComponents() {
    readErrors =
        new ObjectTable<CatalogImportError>(
            Columns.create("Zeile", CatalogImportError::getLineNumber)
                .withSorter(Column.NUMBER_SORTER)
                .withPreferredWidth(100),
            Columns.create("Fehlerbeschreibung", CatalogImportError::getE));
  }

  public void setReadErrors(List<CatalogImportError> errors) {
    readErrors.setObjects(errors);
    readErrors.repaint();
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public String getTitle() {
    return "Katalog einlesen";
  }

  public void messageFormatError(String message) {
    JOptionPane.showMessageDialog(
        getContent(), message, "Fehler in der Katalogdatei", JOptionPane.ERROR_MESSAGE);
  }

  public void messagePathNotFound(String path) {
    JOptionPane.showMessageDialog(
        getContent(),
        "Die Datei \"" + path + "\" konnte nicht gefunden werden.",
        "Datei nicht gefunden",
        JOptionPane.ERROR_MESSAGE);
  }

  void openFileExplorer() {
    Path importPath = Config.getConfig().getDefaultBnnInboxDir().toPath();
    String chooserRoot = Files.exists(importPath) ? importPath.toString() : ".";
    JFileChooser jFileChooser = new JFileChooser(chooserRoot);
    jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    jFileChooser.setFileFilter(
        new FileNameExtensionFilter("Katalog", "BNN", "bnn", "CSV", "csv", "TXT", "txt"));
    jFileChooser.addActionListener(
        e -> {
          if (jFileChooser.getSelectedFile() == null) {
            return;
          }
          filePath.setText(jFileChooser.getSelectedFile().getAbsolutePath());
        });
    jFileChooser.showOpenDialog(getContent());
  }
}
