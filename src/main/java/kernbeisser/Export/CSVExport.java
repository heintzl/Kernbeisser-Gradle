package kernbeisser.Export;

import com.opencsv.CSVWriter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import kernbeisser.Config.Config;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.var;

public class CSVExport {

  public static Optional<CSVWriter> chooseFile(Component parent, String defaultFilename)
      throws IOException {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setSelectedFile(new File(Tools.userDefaultPath() + defaultFilename));
    if (fileChooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
      return Optional.empty();
    }
    return Optional.of(initWriter(fileChooser.getSelectedFile().getAbsolutePath()));
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
            .collect(Collectors.groupingBy(p -> p.getCatalogEntry().getArtikelNrInt()));
    List<String[]> orders = new ArrayList<>();
    orderMap.forEach(
        (i, preOrderList) ->
            orders.add(
                new String[] {
                  Integer.toString(i),
                  Long.toString(
                      preOrderList.stream()
                          .collect(Collectors.summarizingInt(PreOrder::getAmount))
                          .getSum())
                }));
    orders.add(0, new String[] {"artnr", "menge"});
    return orders;
  }

  public static boolean exportPreOrder(
      Component parent, Collection<PreOrder> preOrders, String defaultFilename) {
    var fileContent = getOrdersFileContent(preOrders);
    try {
      Path exportPath = Config.getConfig().getPreorders().getExportDirectory().toPath();
      if (!Files.exists(exportPath)) {
        handleInvalidFilePath(parent, preOrders, defaultFilename, fileContent);
      }
      @Cleanup
      CSVWriter csvWriter =
          initWriter(exportPath + (exportPath.endsWith("/") ? "" : "/") + defaultFilename);
      csvWriter.writeAll(fileContent);
      return true;
    } catch (IOException | InvalidPathException e) {
      return handleInvalidFilePath(parent, preOrders, defaultFilename, fileContent);
    }
  }

  public static boolean handleInvalidFilePath(
      Component parent,
      Collection<PreOrder> preOrders,
      String defaultFilename,
      List<String[]> fileContent) {
    boolean retryExportChoice =
        JOptionPane.showConfirmDialog(
                parent,
                "Das Exportverzeichnis kann nicht gefunden werden.\n"
                    + "Der USB-Stick fehlt anscheinend.\n"
                    + "Soll der Export noch einmal versucht werden?",
                "Exportfehler",
                JOptionPane.YES_NO_OPTION)
            == JOptionPane.YES_OPTION;
    if (retryExportChoice) {
      return exportPreOrder(parent, preOrders, defaultFilename);
    }
    boolean copyManuel =
        JOptionPane.showConfirmDialog(
                parent,
                "Notfalls kann auch in ein anderes Verzeichnis exportiert werden.\n"
                    + "ACHTUNG: In diesem Fall muss die Exportdatei manuell auf den\n"
                    + "USB-Stick kopiert werden. Soll die Datei exportiert und die\n"
                    + "Vorbestellungen als 'bestellt' gekennzeichnet werden?",
                "Exportfehler",
                JOptionPane.YES_NO_OPTION)
            == JOptionPane.YES_OPTION;
    if (!copyManuel) {
      return false;
    }
    try {
      Optional<CSVWriter> manualCsvWriterOpt = chooseFile(parent, defaultFilename);
      if (!manualCsvWriterOpt.isPresent()) return false;
      @Cleanup CSVWriter manualCsvWriter = manualCsvWriterOpt.get();
      manualCsvWriter.writeAll(fileContent);
      return true;
    } catch (IOException f) {
      Tools.showUnexpectedErrorWarning(f);
      return false;
    }
  }
}
