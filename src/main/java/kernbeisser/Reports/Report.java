package kernbeisser.Reports;

import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterName;
import javax.swing.*;
import kernbeisser.Config.Config;
import kernbeisser.Enums.Setting;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;
import lombok.Getter;
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

public abstract class Report {

  @Getter(lazy = true)
  private final JasperPrint jspPrint = lazyGetJspPrint();

  private final String reportDefinition;

  private final String outFileName;

  private static Path getReportsFolder() {
    return Config.getConfig().getReports().getReportDirectory().toPath();
  }

  protected Report(String reportDefinition, String outFileName) {
    this.outFileName = outFileName;
    this.reportDefinition = reportDefinition;
  }

  private JasperPrint lazyGetJspPrint() {
    try {
      return JasperFillManager.fillReport(
          getJasperReport(reportDefinition),
          getReportParams(),
          new JRBeanCollectionDataSource(getDetailCollection()));
    } catch (JRException e) {
      if (ExceptionUtils.indexOfType(e.getCause(), PrinterAbortException.class) != -1) {
        Tools.showPrintAbortedWarning(e, true);
        throw new RuntimeException(e);
      } else {
        Tools.showUnexpectedErrorWarning(e);
        throw new RuntimeException(e);
      }
    }
  }

  private static File getReportFile(String key) {
    String value = Config.getConfig().getReports().getReports().get(key);
    if (value == null) {
      throw new UnsupportedOperationException(
          "cannot find JRE file path in config reports map for [" + key + "]");
    }
    return getReportsFolder().resolve(value).toAbsolutePath().toFile();
  }

  private static JasperReport getJasperReport(String key) throws JRException {
    JasperDesign jspDesign = JRXmlLoader.load(getReportFile(key));
    return JasperCompileManager.compileReport(jspDesign);
  }

  private static Path getOutputFolder() {
    return Config.getConfig().getReports().getOutputDirectory().toPath();
  }

  private static PrintRequestAttributeSet getPageFormatFromReport(JasperPrint jpsPrint) {
    PrintRequestAttributeSet result = new HashPrintRequestAttributeSet();
    result.add(
        jpsPrint.getOrientationValue() == OrientationEnum.PORTRAIT
            ? OrientationRequested.PORTRAIT
            : OrientationRequested.LANDSCAPE);
    result.add(
        Math.max(jpsPrint.getPageWidth(), jpsPrint.getPageHeight()) <= 600
            ? MediaSizeName.ISO_A5
            : MediaSizeName.ISO_A4);
    return result;
  }

  private static PrintServiceAttributeSet getPrinter(boolean useOSDefaultPrinter) {
    PrintServiceAttributeSet result = new HashPrintServiceAttributeSet();
    String printer = Setting.PRINTER.getValue();
    if (useOSDefaultPrinter || printer.equals("OS_default")) {
      printer = PrinterJob.getPrinterJob().getPrintService().getName();
    }
    result.add(new PrinterName(printer, null));
    return result;
  }

  public void sendToPrinter(String message, Consumer<Throwable> exConsumer) {
    sendToPrinter(false, message, exConsumer);
  }

  public void sendToPrinter(
      boolean useOSDefaultPrinter, String message, Consumer<Throwable> exConsumer) {

    new Thread(
            () -> {
              ProgressMonitor pm =
                  new ProgressMonitor(null, message, "Initialisiere Druckerservice...", 0, 2);
              pm.setProgress(1);
              pm.setNote("Exportiere PDF..");
              JRPrintServiceExporter printExporter = new JRPrintServiceExporter();
              printExporter.setExporterInput(new SimpleExporterInput(getJspPrint()));
              PrintRequestAttributeSet requestAttributeSet = getPageFormatFromReport(getJspPrint());
              PrintServiceAttributeSet serviceAttributeSet = getPrinter(useOSDefaultPrinter);
              SimplePrintServiceExporterConfiguration printConfig =
                  new SimplePrintServiceExporterConfiguration();
              printConfig.setPrintRequestAttributeSet(requestAttributeSet);
              printConfig.setPrintServiceAttributeSet(serviceAttributeSet);
              printConfig.setDisplayPrintDialog(false);
              printConfig.setDisplayPageDialog(false);
              printExporter.setConfiguration(printConfig);

              try {
                printExporter.exportReport();
                pm.setNote("Fertig");
                pm.close();
              } catch (JRException e) {

                if (e.getMessageKey().equals("export.print.service.not.found")) {
                  Main.logger.error(e.getMessage(), e);
                  if (JOptionPane.showConfirmDialog(
                          null,
                          "Der konfigurierte Drucker kann nicht gefunden werden!\n"
                              + "Soll stattdessen der Standarddrucker verwendet werden?",
                          "Drucken",
                          JOptionPane.OK_CANCEL_OPTION)
                      == JOptionPane.OK_OPTION) {
                    sendToPrinter(true, message, exConsumer);
                  } else {
                    Tools.showPrintAbortedWarning(e, false);
                  }
                } else {
                  exConsumer.accept(e);
                }
              }
            })
        .start();
  }

  public void exportPdf(String message, Consumer<Throwable> exConsumer) {

    Path filePath = getOutputFolder().resolve(getSafeOutFileName() + ".pdf").toAbsolutePath();
    new Thread(
            () -> {
              ProgressMonitor pm =
                  new ProgressMonitor(null, message, "Initialisiere Druckerservice...", 0, 2);
              pm.setProgress(1);
              pm.setNote("Exportiere PDF..");
              // pm.setMillisToPopup(50);
              try {
                JasperExportManager.exportReportToPdfFile(getJspPrint(), filePath.toString());
                Tools.openFile(filePath.toFile());
                pm.setNote("Fertig");
                pm.close();
              } catch (JRException e) {
                if (ExceptionUtils.indexOfType(e.getCause(), PrinterAbortException.class) != -1) {
                  Tools.showPrintAbortedWarning(e, true);
                } else {
                  Tools.showUnexpectedErrorWarning(e);
                }
              } catch (Exception e) {
                exConsumer.accept(e);
              }
            })
        .start();
  }

  public static void pdfExportException(Throwable e) {
    try {
      throw e.getCause();
    } catch (FileNotFoundException f) {
      Main.logger.error(e.getMessage(), e);
      JOptionPane.showMessageDialog(
          null,
          "Die Datei kann nicht geschrieben werden,\nweil sie in einer anderen Anwendung geöffnet ist.\n"
              + "Bitte die Datei schließen und den Export erneut aufrufen!\n",
          "Fehler beim Dateizugriff",
          JOptionPane.ERROR_MESSAGE);
      throw new RuntimeException(f);
    } catch (Throwable t) {
      Tools.showUnexpectedErrorWarning(t);
    }
  }

  @NotNull
  private String getSafeOutFileName() {
    return outFileName.replaceAll("[\\\\/:*?\"<>|]", "_");
  }

  abstract Map<String, Object> getReportParams();

  abstract Collection<?> getDetailCollection();
}
