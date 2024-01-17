package kernbeisser.Windows.DatabaseView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Useful.CSV;
import kernbeisser.Windows.MVC.Controller;
import lombok.SneakyThrows;

public class DatabaseViewController extends Controller<DatabaseViewView, DatabaseViewModel> {
  public DatabaseViewController() throws PermissionKeyRequiredException {
    super(new DatabaseViewModel());
  }

  @Override
  public void fillView(DatabaseViewView databaseViewView) {
    databaseViewView.setSelectionEntities(Arrays.asList(DatabaseViewModel.DATA_ENTITIES));
  }

  public void exportToCsv(ObjectTable<Object> table) {
    JFileChooser fileChooser = new JFileChooser();
    if (fileChooser.showOpenDialog(getView().getContent()) != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File file = fileChooser.getSelectedFile();
    if (file.isDirectory()) {
      file = new File(file, "output.csv");
    } else {
      if (file.exists() && !getView().confirmOverrideOfFile(file.getName())) return;
    }
    try {
      CSV.dumpIntoCsv(table, file);
    } catch (IOException e) {
      getView().messageCouldNotSaveCSV(e);
    }
  }

  @SneakyThrows
  public String getValueOfField(Field field, Object o) {
    return String.valueOf(field.get(o));
  }

  public void selectClass(Class<?> clazz) {
    getView().setEntities(new ArrayList<>());
    List<Column<Object>> columnList =
        Arrays.stream(clazz.getDeclaredFields())
            .peek(e -> e.setAccessible(true))
            .map(field -> Columns.create(field.getName(), e -> getValueOfField(field, e)))
            .peek(e -> e.withColumnAdjustor(c -> c.setPreferredWidth(150)))
            .collect(Collectors.toList());
    getView().setColumns(columnList);
    getView()
        .setEntities(
            (Collection<Object>) model.getAllOfClass(clazz, getView().getFilter().orElse(null)));
  }
}
