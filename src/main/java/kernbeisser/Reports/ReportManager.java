package kernbeisser.Reports;

import static kernbeisser.Config.ConfigManager.getDirectory;
import static kernbeisser.Config.ConfigManager.getPath;

import java.awt.print.PrinterJob;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterName;
import javax.swing.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.ShoppingItemSum;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.InvalidVATValueException;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
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
    reportParams.put("VatSumLow", ShoppingItem.getSum(ShoppingItemSum.VAT_VATLOW, items));
    reportParams.put("VatSumHigh", ShoppingItem.getSum(ShoppingItemSum.VAT_VATHIGH, items));

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
  private static Map<String, Object> getAccountingPurchaseParams(List<Purchase> purchases)
      throws InvalidVATValueException {
    double sumTotalPurchased = 0.0;
    double sumVatHiPurchased = 0.0;
    double sumVatLoPurchased = 0.0;
    Timestamp endDate = Timestamp.from(purchases.get(purchases.size() - 1).getCreateDate());

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
    double vatLoValue = getVatValue(purchases, VAT.LOW);
    double vatHiValue = getVatValue(purchases, VAT.HIGH);
    for (Purchase p : purchases) {
      sumVatHiPurchased += p.getVatSum(VAT.HIGH);
      sumVatLoPurchased += p.getVatSum(VAT.LOW);
      sumTotalPurchased += p.getSum();
    }

    Map<String, Object> reportParams = new HashMap<>();
    reportParams.put("end", endDate);
    reportParams.put("vatHiValue", vatHiValue);
    reportParams.put("vatLoValue", vatLoValue);
    reportParams.put("sumTotalPurchased", sumTotalPurchased);
    reportParams.put("sumVatHiPurchased", sumVatHiPurchased);
    reportParams.put("sumVatLoPurchased", sumVatLoPurchased);

    return reportParams;
  }

  private static Map<String, Object> getAccountingTransactionParams(List<Purchase> purchases)
      throws InvalidVATValueException {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    double transactionSaldo = 0.0;
    double transactionCreditPayIn = 0.0;
    double transactionSpecialPayments = 0.0;
    double transactionPurchases = 0.0;
    Instant startDate;
    long lastReportedBonNo = purchases.get(0).getId() - 1;
    Purchase bon = em.find(Purchase.class, lastReportedBonNo);
    if (bon == null) {
      startDate =
          em.createQuery("select t from Transaction t order by t.date asc", Transaction.class)
              .setFirstResult(0)
              .setMaxResults(1)
              .getSingleResult()
              .getDate()
              .minusSeconds(1);
    } else {
      startDate = bon.getSession().getTransaction().getDate();
    }
    Instant endDate = purchases.get(purchases.size() - 1).getCreateDate();
    List<Transaction> transactions =
        em.createQuery(
                "select t from Transaction t where t.date > :from and t.date <= :to",
                Transaction.class)
            .setParameter("from", startDate)
            .setParameter("to", endDate)
            .getResultList();

    for (Transaction t : transactions) {
      User kbUser = User.getKernbeisserUser();
      if (t.getFrom().equals(kbUser) || t.getTo().equals(kbUser)) {
        transactionSaldo += t.getValue();
        switch (t.getTransactionType()) {
          case PURCHASE:
            transactionPurchases += t.getValue();
            break;
          case USER_GENERATED:
            transactionSpecialPayments += t.getValue() * (t.getFrom().equals(kbUser) ? -1.0 : 1.0);
            break;
          case PAYIN:
          case INITIALIZE:
            transactionCreditPayIn += t.getValue();
            break;
        }
      }
    }
    Map<String, Object> reportParams = new HashMap<>();
    reportParams.put("start", Timestamp.from(startDate));
    reportParams.put("transactionSaldo", transactionSaldo);
    reportParams.put("transactionCreditPayIn", transactionCreditPayIn);
    reportParams.put("transactionSpecialPayments", transactionSpecialPayments);
    reportParams.put("transactionPurchases", transactionPurchases);

    return reportParams;
  }

  public static void initAccountingReportPrint(long startBon, long endBon)
      throws JRException, InvalidVATValueException, NoResultException {
    EntityManager em = DBConnection.getEntityManager();

    List<Purchase> purchases =
        em.createQuery("select p from Purchase p where p.id between :from and :to", Purchase.class)
            .setParameter("from", startBon)
            .setParameter("to", endBon)
            .getResultList();
    if (purchases.isEmpty()) {
      throw new NoResultException();
    }

    Map<String, Object> reportParams = getAccountingPurchaseParams(purchases);
    reportParams.putAll(getAccountingTransactionParams(purchases));
    JRDataSource dataSource = new JRBeanCollectionDataSource(purchases);
    jspPrint =
        JasperFillManager.fillReport(
            getJasperReport("accountingReportFileName"), reportParams, dataSource);
    outFileName =
        String.format(
            "KernbeisserBuchhaltungBonUebersicht_%s_%s.pdf",
            reportParams.get("start").toString(), reportParams.get("end").toString());
  }

  // start usergroup balance
}
