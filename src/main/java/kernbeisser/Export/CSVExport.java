package kernbeisser.Export;

import com.opencsv.CSVWriter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;

public class CSVExport {

  private static CSVWriter initWriter(Component parent, String defaultFilename) throws IOException {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setSelectedFile(new File(Tools.userDefaultPath() + defaultFilename + ".csv"));
    if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
      return new CSVWriter(
          new FileWriter(fileChooser.getSelectedFile()),
          ';',
          CSVWriter.NO_QUOTE_CHARACTER,
          CSVWriter.NO_ESCAPE_CHARACTER,
          CSVWriter.DEFAULT_LINE_END);
    } else {
      return null;
    }
  }

  public static int exportPreOrder(Component parent, Collection<PreOrder> preOrders)
      throws IOException {
    String defaultFilename = "KornkraftBestellung_" + Date.INSTANT_DATE.format(Instant.now());
    @Cleanup CSVWriter csvWriter = initWriter(parent, defaultFilename);
    if (csvWriter == null) {
      return JFileChooser.CANCEL_OPTION;
    } else {
      List<String[]> orders =
          preOrders.stream()
              .map(
                  p ->
                      new String[] {
                        Integer.toString(p.getKBNumber()),
                        Integer.toString(p.getAmount()),
                        (p.getUser().getFullName() + ": " + p.getArticle().getName())
                            .replace(';', '#')
                      })
              .collect(Collectors.toList());
      orders.add(0, new String[] {"artnr", "menge", "kommentar"});
      csvWriter.writeAll(orders);
      return JFileChooser.APPROVE_OPTION;
    }
  }
}
