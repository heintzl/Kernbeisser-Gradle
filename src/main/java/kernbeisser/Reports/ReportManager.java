package kernbeisser.Reports;

import static kernbeisser.Config.ConfigManager.getDirectory;
import static kernbeisser.Config.ConfigManager.getPath;

import java.awt.print.PrinterJob;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterName;
import javax.swing.*;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.ShoppingItemSum;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.InvalidVATValueException;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import org.jetbrains.annotations.NotNull;

public class ReportManager {

  private static JasperPrint jspPrint;

  private static String outFileName;

  private static final String CONFIG_CATEGORY = "Reports";

  private static Path getReportsFolder() {
    return getDirectory(CONFIG_CATEGORY, "reportDirectory");
  }

  static JasperReport getJasperReport(String key) throws JRException {
    JasperDesign jspDesign =
        JRXmlLoader.load(
            getReportsFolder().resolve(getPath(CONFIG_CATEGORY, key)).toAbsolutePath().toFile());
    return JasperCompileManager.compileReport(jspDesign);
  }

  static Path getOutputFolder() {
    return getDirectory(CONFIG_CATEGORY, "outputDirectory");
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

  public void sendToPrinter() throws JRException {
    sendToPrinter(false);
  }

  public void sendToPrinter(boolean useOSDefaultPrinter) throws JRException {

    JRPrintServiceExporter printExporter = new JRPrintServiceExporter();
    printExporter.setExporterInput(new SimpleExporterInput(jspPrint));
    PrintRequestAttributeSet requestAttributeSet = getPageFormatFromReport(jspPrint);
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
          sendToPrinter(true);
        } else {
          Tools.showPrintAbortedWarning(e, false);
        }
      } else {
        throw e;
      }
    }
  }

  public void exportPdf() throws JRException {

    Path filePath = getOutputFolder().resolve(getSafeOutFileName()).toAbsolutePath();

    JasperExportManager.exportReportToPdfFile(jspPrint, filePath.toString());
    Tools.openFile(filePath.toFile());
  }

  @NotNull
  private String getSafeOutFileName() {
    return outFileName.replaceAll("[\\\\/:*?\"<>|]", "_");
  }

  // start Invoice
  private static Map<String, Object> getInvoiceParams(Purchase purchase) {
    Collection<ShoppingItem> items = purchase.getAllItems();
    double credit =
        purchase.getSession().getCustomer().getUserGroup().getValue() - purchase.getSum();
    Map<String, Object> reportParams = new HashMap<>();
    reportParams.put("BonNo", purchase.getId());
    reportParams.put("Customer", purchase.getSession().getCustomer().getFullName());
    reportParams.put("Seller", purchase.getSession().getSeller().getFullName());
    reportParams.put("Credit", credit);
    reportParams.put("PurchaseDate", purchase.getCreateDate());
    reportParams.put("CreditWarning", credit <= Setting.CREDIT_WARNING_THRESHOLD.getDoubleValue());
    reportParams.put("VatValueLow", purchase.guessVatValue(VAT.LOW));
    reportParams.put("VatValueHigh", purchase.guessVatValue(VAT.HIGH));
    reportParams.put("SumTotal", purchase.getSum());
    reportParams.put("VatSumLow", ShoppingItem.getSum(ShoppingItemSum.RETAILPRICE_VATLOW, items));
    reportParams.put("VatSumHigh", ShoppingItem.getSum(ShoppingItemSum.RETAILPRICE_VATHIGH, items));

    return reportParams;
  }

  public static void initInvoicePrint(Collection<ShoppingItem> shoppingCart, Purchase purchase)
      throws JRException {
    Map<String, Object> reportParams = getInvoiceParams(purchase);
    JRDataSource dataSource = new JRBeanCollectionDataSource(shoppingCart);
    jspPrint =
        JasperFillManager.fillReport(getJasperReport("invoiceFileName"), reportParams, dataSource);
    outFileName =
        String.format(
            "%d_%s_%s_%s.pdf",
            purchase.getId(),
            purchase.getSession().getCustomer().getFirstName(),
            purchase.getSession().getCustomer().getSurname(),
            purchase.getCreateDate().toString());
  }

  // start Tillroll
  public static void initTillrollPrint(
      Collection<ShoppingItem> tillroll, Instant start, Instant end) throws JRException {
    Timestamp startDate = Timestamp.from(start);
    Timestamp endDate = Timestamp.from(end);
    Map<String, Object> reportParams = new HashMap<>();
    reportParams.put("start", startDate);
    reportParams.put("ende", endDate);
    JRDataSource dataSource = new JRBeanCollectionDataSource(tillroll);
    jspPrint =
        JasperFillManager.fillReport(getJasperReport("tillrollFileName"), reportParams, dataSource);
    outFileName =
        String.format("KernbeisserBonrolle_%s_%s.pdf", startDate.toString(), endDate.toString());
  }

  // start Accounting Report
  static long countVatValues(Collection<Purchase> purchases, VAT vat) {
    return purchases.stream()
        .flatMap(p -> p.getAllItems().stream())
        .filter(s -> s.getVat() == vat)
        .mapToDouble(ShoppingItem::getVatValue)
        .distinct()
        .count();
  }

  static double getVatValue(Collection<Purchase> purchases, VAT vat) {
    return purchases.stream()
        .flatMap(p -> p.getAllItems().stream())
        .filter(s -> s.getVat() == vat)
        .mapToDouble(ShoppingItem::getVatValue)
        .findFirst()
        .orElse(vat.getValue());
  }

  @NotNull
  private static Map<String, Object> getAccountingParams(Collection<Purchase> purchases)
      throws InvalidVATValueException {
    double vatHiValue;
    double vatLoValue;
    double sumTotalPurchased = 0.0;
    double sumVatHiPurchased = 0.0;
    double sumVatLoPurchased = 0.0;
    double transactionSaldo = 0.0;
    double transactionCreditPayIn = 0.0;
    double transactionSpecialPayments = 0.0;
    double transactionPurchases = 0.0;
    long t_high = countVatValues(purchases, VAT.HIGH);
    long t_low = countVatValues(purchases, VAT.LOW);
    if (t_high > 1 || t_low > 1) {
      String message = "";
      if (t_low > 1) {
        message += "Mehrere Werte für niedrigen MWSt.-Satz gefunden \n";
      }
      if (t_high > 1) {
        message += "Mehrere Werte für hohen MWSt.-Satz gefunden \n";
      }
      message +=
          "Gab es zwischenzeitlich einen Wechsel des Steuersatzes?\n"
              + "Der Bericht muss für einen Zeitraum mit eindeutigen Steuersätzen ausgegeben werden!";
      JOptionPane.showMessageDialog(
          JOptionPane.getRootFrame(),
          message,
          "Uneindeutige Steuersätze",
          JOptionPane.ERROR_MESSAGE);
      throw new InvalidVATValueException(1000.0);
    }
    vatLoValue = getVatValue(purchases, VAT.LOW);
    vatHiValue = getVatValue(purchases, VAT.HIGH);
    for (Purchase p : purchases) {
      sumVatHiPurchased += p.getVatSum(VAT.HIGH);
      sumVatLoPurchased += p.getVatSum(VAT.LOW);
      sumTotalPurchased += p.getSum();
    }
    Map<String, Object> reportParams = new HashMap<>();
    reportParams.put("vatHiValue", vatHiValue);
    reportParams.put("vatLoValue", vatLoValue);
    reportParams.put("sumTotalPurchased", sumTotalPurchased);
    reportParams.put("sumVatHiPurchased", sumVatHiPurchased);
    reportParams.put("sumVatLoPurchased", sumVatLoPurchased);
    reportParams.put("transactionSaldo", transactionSaldo);
    reportParams.put("transactionCreditPayIn", transactionCreditPayIn);
    reportParams.put("transactionSpecialPayments", transactionSpecialPayments);
    reportParams.put("transactionPurchases", transactionPurchases);

    return reportParams;
  }

  public static void initAccountingReportPrint(
      Collection<Purchase> purchases, Instant start, Instant end)
      throws JRException, InvalidVATValueException {
    Timestamp startDate = Timestamp.from(start);
    Timestamp endDate = Timestamp.from(end);
    JasperDesign jspDesign =
        JRXmlLoader.load(
            getReportsFolder()
                .resolve(getPath(CONFIG_CATEGORY, "accountingReportFileName"))
                .toAbsolutePath()
                .toFile());
    JasperReport jspReport = JasperCompileManager.compileReport(jspDesign);
    Map<String, Object> reportParams = getAccountingParams(purchases);
    reportParams.put("start", startDate);
    reportParams.put("ende", endDate);

    JRDataSource dataSource = new JRBeanCollectionDataSource(purchases);
    jspPrint = JasperFillManager.fillReport(jspReport, reportParams, dataSource);
    outFileName =
        String.format(
            "KernbeisserBuchhaltungBonUebersicht_%s_%s.pdf",
            startDate.toString(), endDate.toString());
  }

  // start usergroup balance
}
