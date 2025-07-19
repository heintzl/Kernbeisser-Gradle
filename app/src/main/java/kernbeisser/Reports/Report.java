package kernbeisser.Reports;

import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.Sides;
import javax.swing.*;
import kernbeisser.Config.Config;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.NoTransactionsFoundException;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Useful.Tools;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

@Log4j2
public abstract class Report {

  @Getter(lazy = true)
  private final JasperPrint jspPrint = lazyGetJspPrint();

  private final String reportFileName;

  private static Path getReportsFolder() {
    return Config.getConfig().getReports().getReportDirectory().toPath();
  }

  @Setter private boolean duplexPrint = true;

  protected Report(String reportFileName) {
    this.reportFileName = reportFileName;
  }

  abstract String createOutFileName();

  JasperPrint lazyGetJspPrint() {
    Map<String, Object> params = new HashMap<>();
    Map<String, Object> reportParams = getReportParams();
    if (reportParams != null) params.putAll(reportParams);
    params.put("reportFooter", Setting.REPORT_FOOTLINE.getStringValue());
    try {
      return JasperFillManager.fillReport(
          getJasperReport(), params, new JRBeanCollectionDataSource(getDetailCollection()));
    } catch (JRException e) {
      if (ExceptionUtils.indexOfType(e.getCause(), PrinterAbortException.class) != -1) {
        UnexpectedExceptionHandler.showPrintAbortedWarning(e, true);
      } else {
        throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
      }
      throw new RuntimeException(e);
    }
  }

  private File getReportFile() {
    log.debug("returning report file: {}", reportFileName);
    return getReportsFolder().resolve(reportFileName).toAbsolutePath().toFile();
  }

  private JasperReport getJasperReport() throws JRException {
    JasperDesign jspDesign = JRXmlLoader.load(getReportFile());
    return JasperCompileManager.compileReport(jspDesign);
  }

  private static Path getOutputFolder() {
    return Config.getConfig().getReports().getOutputDirectory().toPath();
  }

  private static Path getCloudOutputFolder() {
    return Config.getConfig().getReports().getCloudOutputDirectory().toPath();
  }

  private static PrintRequestAttributeSet getPageFormatFromReport(JasperPrint jspPrint) {
    PrintRequestAttributeSet result = new HashPrintRequestAttributeSet();
    result.add(
        jspPrint.getOrientation() == OrientationEnum.PORTRAIT
            ? OrientationRequested.PORTRAIT
            : OrientationRequested.LANDSCAPE);
    result.add(
        Math.max(jspPrint.getPageWidth(), jspPrint.getPageHeight()) <= 600
            ? MediaSizeName.ISO_A5
            : MediaSizeName.ISO_A4);
    return result;
  }

  private PrintServiceAttributeSet getPrinter(boolean useOSDefaultPrinter, JasperPrint jspPrint) {
    PrintServiceAttributeSet result = new HashPrintServiceAttributeSet();
    String printer = Setting.ALTERNATIVE_A5_PRINTER.getValue();
    if (printer.isEmpty() || Math.max(jspPrint.getPageWidth(), jspPrint.getPageHeight()) > 600) {
      printer = Setting.PRINTER.getValue();
    }
    if (duplexPrint) {
      printer += Setting.DUPLEX_PRINTER_SUFFIX.getStringValue();
    }
    if (useOSDefaultPrinter || printer.equals("OS_default")) {
      printer = PrinterJob.getPrinterJob().getPrintService().getName();
    }
    result.add(new PrinterName(printer, null));
    return result;
  }

  public void sendToPrinter(String message, Consumer<Throwable> exConsumer) {
    if (Setting.PRINTER.getValue().equals("PDF-Export")) {
      exportPdf(message, Report::pdfExportException);
    } else {
      sendToPrinter(false, message, duplexPrint, exConsumer);
    }
  }

  private void sendToPrinter(
      boolean useOSDefaultPrinter,
      String message,
      boolean duplexPrint,
      Consumer<Throwable> exConsumer) {

    final AtomicInteger progressStep = new AtomicInteger(1);
    ProgressMonitor pm =
        new ProgressMonitor(null, message, "Initialisiere Druckerservice...", 0, 3);
    pm.setMillisToDecideToPopup(0);
    pm.setMillisToPopup(0);
    pm.setProgress(0);
    new SwingWorker<Void, String>() {
      @Override
      protected Void doInBackground() throws JRException {
        publish("Erstelle Ausdruck ...");
        JasperPrint jasperPrint = getJspPrint();
        JRPrintServiceExporter printExporter = new JRPrintServiceExporter();
        printExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        PrintRequestAttributeSet requestAttributeSet = getPageFormatFromReport(jasperPrint);
        if (duplexPrint) {
          requestAttributeSet.add(Sides.DUPLEX);
        }

        PrintServiceAttributeSet serviceAttributeSet = getPrinter(useOSDefaultPrinter, jasperPrint);
        SimplePrintServiceExporterConfiguration printConfig =
            new SimplePrintServiceExporterConfiguration();
        printConfig.setPrintRequestAttributeSet(requestAttributeSet);
        printConfig.setPrintServiceAttributeSet(serviceAttributeSet);
        printConfig.setDisplayPrintDialog(false);
        printConfig.setDisplayPageDialog(false);
        printExporter.setConfiguration(printConfig);
        publish("Drucke ...");
        printExporter.exportReport();
        return null;
      }

      @Override
      protected void process(List<String> progressMessages) {
        pm.setNote(progressMessages.getLast());
        pm.setProgress(progressStep.addAndGet(1));
      }

      @Override
      protected void done() {

        pm.close();
        try {
          get();
        } catch (Exception e) {
          String errorMessage = e.getMessage().toLowerCase(Locale.ROOT);
          Collection<String> printerNotFoundMessages =
              Arrays.asList(
                  "not a 2d print service", // ubuntu
                  "no suitable print service found" // windows
                  );
          if (printerNotFoundMessages.stream().anyMatch(errorMessage::contains)) {
            log.error(e.getMessage(), e);
            if (JOptionPane.showConfirmDialog(
                    null,
                    "Der konfigurierte Drucker \""
                        + "\"\nkann nicht gefunden werden!\n"
                        + "Soll stattdessen der Standarddrucker verwendet werden?",
                    "Drucken",
                    JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {
              sendToPrinter(true, message, duplexPrint, exConsumer);
            } else {
              UnexpectedExceptionHandler.showPrintAbortedWarning(e, false);
            }
          } else {
            exConsumer.accept(e);
          }
        }
      }
    }.execute();
  }

  public void exportPdfToCloud(String message, Consumer<Throwable> exConsumer) {
    exportPdfToCloudAndThen(message, exConsumer, () -> {});
  }

  public void exportPdfToCloudAndThen(
      String message, Consumer<Throwable> exConsumer, Runnable then) {
    Path filePath = getCloudOutputFolder().resolve(getSafeOutFileName() + ".pdf").toAbsolutePath();
    exportPdf(message, exConsumer, filePath, false, then);
  }

  public void exportPdf(String message, Consumer<Throwable> exConsumer) {
    Path filePath = getOutputFolder().resolve(getSafeOutFileName() + ".pdf").toAbsolutePath();
    exportPdf(message, exConsumer, filePath, true, () -> {});
  }

  private void exportPdf(
      String message,
      Consumer<Throwable> exConsumer,
      Path filePath,
      boolean openFile,
      Runnable callback) {

    Path outputFolder = getOutputFolder();
    final AtomicInteger progressStep = new AtomicInteger(1);
    if (!Files.exists(outputFolder)) {
      try {
        Files.createDirectories(outputFolder);
      } catch (IOException e) {
        UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
      }
    }
    ProgressMonitor pm =
        new ProgressMonitor(null, message, "Initialisiere Druckerservice...", 0, 3);
    pm.setMillisToPopup(0);
    pm.setMillisToDecideToPopup(0);
    pm.setProgress(0);
    new SwingWorker<Void, String>() {
      @Override
      protected Void doInBackground() throws Exception {
        publish("Exportiere PDF...");
        JasperExportManager.exportReportToPdfFile(getJspPrint(), filePath.toString());
        publish("Fertig");
        if (openFile) {
          Tools.openFile(filePath.toFile());
        }
        return null;
      }

      @Override
      protected void process(List<String> progressMessages) {
        pm.setNote(progressMessages.getLast());
        pm.setProgress(progressStep.addAndGet(1));
      }

      @Override
      protected void done() {
        pm.close();
        try {
          get();
          callback.run();
        } catch (Exception e) {
          if (ExceptionUtils.indexOfType(e.getCause(), PrinterAbortException.class) != -1) {
            UnexpectedExceptionHandler.showPrintAbortedWarning(e, true);
          } else {
            exConsumer.accept(e);
          }
        }
      }
    }.execute();
  }

  public static void pdfExportException(Throwable e) {
    try {
      throw e;
    } catch (FileNotFoundException f) {
      log.error(e.getMessage(), e);
      JOptionPane.showMessageDialog(
          null,
          "Die Datei kann nicht geschrieben werden,\nweil sie in einer anderen Anwendung geöffnet ist.\n"
              + "Bitte die Datei schließen und den Export erneut aufrufen!\n",
          "Fehler beim Dateizugriff",
          JOptionPane.ERROR_MESSAGE);
      throw new RuntimeException(f);
    } catch (NoTransactionsFoundException n) {
      JOptionPane.showMessageDialog(
          null,
          "Keine Bons gefunden, die den Kriterien entsprechen!",
          "Keine Bons",
          JOptionPane.ERROR_MESSAGE);
    } catch (Throwable t) {
      UnexpectedExceptionHandler.showUnexpectedErrorWarning(t);
    }
  }

  @NotNull
  public String getSafeOutFileName() {
    return createOutFileName().replaceAll("[\\\\/:*?\"<>|]", "_");
  }

  abstract Map<String, Object> getReportParams();

  abstract Collection<?> getDetailCollection();
}
