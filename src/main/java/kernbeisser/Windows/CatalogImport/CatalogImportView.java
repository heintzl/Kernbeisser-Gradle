package kernbeisser.Windows.CatalogImport;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Config.Config;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Tasks.Catalog.CatalogImportError;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import org.jetbrains.annotations.NotNull;

public class CatalogImportView implements IView<CatalogImportController> {
  private JTextField filePath;
  private JButton fileChooser;
  private ObjectTable<CatalogImportError> protocol;
  private JButton close;
  private JButton applyChanges;

  private JPanel main;
  private JTextField scope;
  private JTextField description;
  private JTextField validFrom;
  private JTextField validTo;
  private JTextField createdDate;
  private JTextField createdTime;
  private JPanel infoPanel;
  private JScrollPane protocolPane;
  private JLabel protocolCaption;
  private JTextField lastCatalogCreationDate;
  private JTextField lastCatalogValidDate;
  private JProgressBar loadingIndicator;
  private CatalogImportController controller;

  @Override
  public void initialize(CatalogImportController controller) {
    this.controller = controller;
    close.addActionListener(e -> back());
    // readFile.addActionListener(e -> controller.readFile(filePath.getText()));
    filePath.addActionListener(e -> controller.readFile(filePath.getText()));
    applyChanges.addActionListener(e -> controller.applyChanges());
    applyChanges.setEnabled(false);
    fileChooser.addActionListener(e -> openFileExplorer());
    fileChooser.setIcon(IconFontSwing.buildIcon(FontAwesome.FOLDER, 20, new Color(255, 192, 3)));
    applyChanges.setIcon(IconFontSwing.buildIcon(FontAwesome.DOWNLOAD, 20, new Color(26, 49, 134)));
    close.setIcon(IconFontSwing.buildIcon(FontAwesome.WINDOW_CLOSE, 20, new Color(133, 0, 16)));
  }

  public void setScope(String t) {
    scope.setText(t);
  }

  public void setDescription(String t) {
    description.setText(t);
  }

  public void setCreatedDate(Instant date) {
    createdDate.setText(Date.safeDateFormat(date, Date.INSTANT_DATE));
  }

  public void setCreatedTime(Instant time) {
    createdTime.setText(Date.safeDateFormat(time, Date.INSTANT_TIME));
  }

  public void setValidFrom(Instant date) {
    validFrom.setText(Date.safeDateFormat(date, Date.INSTANT_DATE));
  }

  public void setValidTo(Instant date) {
    validTo.setText(Date.safeDateFormat(date, Date.INSTANT_DATE));
  }

  public void setLastCatalogInfo(Instant lastCreationDate, Instant lastValidDate) {
    lastCatalogCreationDate.setText(Date.safeDateFormat(lastCreationDate, Date.INSTANT_DATE));
    lastCatalogValidDate.setText(Date.safeDateFormat(lastValidDate, Date.INSTANT_DATE));
  }

  public void setApplyChangesEnabled(boolean b) {
    applyChanges.setEnabled(b);
  }

  public void indicateLoading(boolean b) {
    applyChanges.setEnabled(!b);
    loadingIndicator.setVisible(b);
  }

  private void createUIComponents() {
    protocol =
        new ObjectTable<CatalogImportError>(
            Columns.create("Zeile / Artikelnummer", CatalogImportError::getLineNumber)
                .withSorter(Column.NUMBER_SORTER)
                .withPreferredWidth(100),
            Columns.<CatalogImportError>create(
                    "Fehlerbeschreibung", e -> e.getE().getLocalizedMessage())
                .withPreferredWidth(1200),
            Columns.createIconColumn(
                "Details",
                e -> IconFontSwing.buildIcon(FontAwesome.INFO_CIRCLE, 18, Color.DARK_GRAY),
                e -> Tools.showErrorWarning(e.getE(), "Import-Meldung:"),
                e -> {
                  return;
                },
                70));
  }

  public void setReadErrors(List<CatalogImportError> errors) {
    protocol.setObjects(errors);
    protocol.repaint();
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
          String choosenFile = jFileChooser.getSelectedFile().getAbsolutePath();
          filePath.setText(choosenFile);
          controller.readFile(choosenFile);
        });
    jFileChooser.showOpenDialog(getContent());
  }

  public boolean confirmImportInValidCatalog(String s) {
    return JOptionPane.showConfirmDialog(
            getContent(),
            s + "\nSoll der Katalog wirklich eingelesen werden?",
            "Katalog nicht aktuell",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE)
        == JOptionPane.YES_OPTION;
  }

  public boolean confirmMergeCatalog() {
    return JOptionPane.showConfirmDialog(
            getContent(),
            "Der Katalog ist als \"Vollständig\" gekennzeichnet."
                + "\nSollen alte Einträge beibehalten werden, die in diesem Katalog nicht enthalten sind?"
                + "\nMit \"Nein\" wird der Katalog vollständig ersetzt.",
            "Katalog zusammenführen",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE)
        == JOptionPane.YES_OPTION;
  }
}
