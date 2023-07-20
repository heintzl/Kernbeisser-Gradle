package kernbeisser.Windows.CatalogImport;

import javax.swing.*;
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
    readErrors =
        new ObjectTable<CatalogImportError>(
            Columns.create("Zeile", CatalogImportError::getLineNumber),
            Columns.create("Feld", CatalogImportError::getField),
            Columns.create("Fehlerbeschreibung", CatalogImportError::getE));
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }
}
