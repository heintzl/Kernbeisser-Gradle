package kernbeisser.Export;

import com.opencsv.CSVWriter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.*;
import kernbeisser.Config.Config;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.var;

public class CSVExport {

  public static CSVWriter chooseFile(Component parent, String defaultFilename) throws IOException {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setSelectedFile(new File(Tools.userDefaultPath() + defaultFilename));
    if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
      return initWriter(fileChooser.getSelectedFile().getAbsolutePath());
    } else {
      return null;
    }
  }

  public static CSVWriter initWriter(String Filepath) throws IOException {
    return new CSVWriter(
        new FileWriter(Filepath),
        ';',
        CSVWriter.NO_QUOTE_CHARACTER,
        CSVWriter.NO_ESCAPE_CHARACTER,
        CSVWriter.DEFAULT_LINE_END);
  }

  private static List<String[]> getOrdersFileContent(Collection<PreOrder> preOrders) {
    Map<Integer, List<PreOrder>> orderMap =
        preOrders.stream()
            .filter(p -> p.getArticle().getSupplier().equals(Supplier.getKKSupplier()))
            .collect(Collectors.groupingBy(p -> p.getArticle().getSuppliersItemNumber()));
    List<String[]> orders = new ArrayList<>();
    orderMap.forEach(
        (i, preOrderList) ->
            orders.add(
                new String[] {
                  Integer.toString(i),
                  Long.toString(
                      preOrderList.stream()
                          .collect(Collectors.summarizingInt(PreOrder::getAmount))
                          .getSum()),
                  preOrderList.stream()
                      .map(p -> p.isShopOrder() ? "KB" : "VB")
                      .distinct()
                      .sorted()
                      .collect(Collectors.joining(",")),
                }));
    orders.add(0, new String[] {"artnr", "menge", "kommentar"});
    return orders;
  }

  public static boolean exportPreOrder(
      Component parent, Collection<PreOrder> preOrders, String defaultFilename) {
    Path exportPath = Config.getConfig().getPreorders().getExportDirectory().toPath();
    var fileContent = getOrdersFileContent(preOrders);
    try {
      if (!Files.exists(exportPath)) {
        throw new IOException();
      }
      @Cleanup
      CSVWriter csvWriter =
          initWriter(exportPath + (exportPath.endsWith("/") ? "" : "/") + defaultFilename);
      csvWriter.writeAll(fileContent);
      return true;
    } catch (IOException e) {
      boolean choice =
          JOptionPane.showConfirmDialog(
                  parent,
                  "Das Exportverzeichnis kann nicht gefunden werden.\n"
                      + "Der USB-Stick fehlt anscheinend.\n"
                      + "Soll der Export noch einmal versucht werden?",
                  "Exportfehler",
                  JOptionPane.YES_NO_OPTION)
              == JOptionPane.YES_OPTION;
      if (choice) {
        return exportPreOrder(parent, preOrders, defaultFilename);
      } else {
        choice =
            JOptionPane.showConfirmDialog(
                    parent,
                    "Notfalls kann auch in ein anderes Verzeichnis exportiert werden.\n"
                        + "ACHTUNG: In diesem Fall muss die Exportdatei manuell auf den\n"
                        + "USB-Stick kopiert werden. Soll die Datei exportiert und die\n"
                        + "Vorbestellungen als 'bestellt' gekennzeichnet werden?",
                    "Exportfehler",
                    JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
        if (choice) {
          try {
            @Cleanup CSVWriter manualCsvWriter = chooseFile(parent, defaultFilename);
            if (manualCsvWriter == null) {
              return false;
            } else {
              manualCsvWriter.writeAll(fileContent);
              return true;
            }
          } catch (IOException f) {
            return false;
          }
        } else {
          return false;
        }
      }
    }
  }
}
