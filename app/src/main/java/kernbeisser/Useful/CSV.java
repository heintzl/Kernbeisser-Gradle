package kernbeisser.Useful;

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;

public class CSV {
  public static <T> void dumpIntoCsv(ObjectTable<T> table, File outFile) throws IOException {
    Collection<Column<T>> columns = table.getColumns();
    try (CSVWriter writer = new CSVWriter(new FileWriter(outFile))) {
      writer.writeNext(columns.stream().map(Column::getName).toArray(String[]::new));
      for (T value : table) {
        writer.writeNext(
            columns.stream()
                .map(e -> e.getValue(value))
                .map(Object::toString)
                .toArray(String[]::new));
      }
    }
  }
}
