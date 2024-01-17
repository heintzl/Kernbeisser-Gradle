package kernbeisser.Windows.DatabaseView;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.swing.*;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Windows.MVC.IView;
import org.jetbrains.annotations.NotNull;

public class DatabaseViewView implements IView<DatabaseViewController> {
  private JPanel main;
  private AdvancedComboBox<Class<?>> entitySelection;
  private ObjectTable<Object> entityTable;
  private JButton exportToCsv;
  private JTextField filter;

  @Override
  public void initialize(DatabaseViewController controller) {
    exportToCsv.addActionListener(
        (actionEvent) -> {
          controller.exportToCsv(entityTable);
        });
    entitySelection.addSelectionListener(
        (clazz) -> {
          controller.selectClass(clazz);
        });
    filter.addActionListener(
        (actionEvent) -> {
          entitySelection.getSelected().ifPresent(controller::selectClass);
        });
  }

  Optional<String> getFilter() {
    String text = filter.getText();
    if (text.replaceAll(" ", "").equals("")) {
      return Optional.empty();
    }
    return Optional.of(text);
  }

  void setSelectionEntities(List<Class<?>> classes) {
    entitySelection.setItems(classes);
  }

  void setColumns(List<Column<Object>> columns) {
    entityTable.setColumns(columns);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    entityTable = new ObjectTable<Object>();
    entitySelection = new AdvancedComboBox<>(Class::getSimpleName);
  }

  public void messageCouldNotSaveCSV(IOException e) {
    message(
        "Die CSV-Datei konnte nicht exportiert werden!\nFehler: " + e.getMessage(),
        "Fehler beim Exportieren der CSV-Datei");
  }

  public boolean confirmOverrideOfFile(String name) {
    return confirmDialog(
        "Sind sie sich sicher das sie die Datei " + name + " überschreiben wollen?",
        "Datei existiert bereits!");
  }

  public void setEntities(Collection<Object> allOfClass) {
    entityTable.setObjects(allOfClass);
  }

  @Override
  public String getTitle() {
    return "Datenbank";
  }
}
