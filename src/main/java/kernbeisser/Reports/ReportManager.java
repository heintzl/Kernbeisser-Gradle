package kernbeisser.Reports;

import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.ShoppingItem;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static kernbeisser.Config.ConfigManager.getDirectory;
import static kernbeisser.Config.ConfigManager.getPath;

public class ReportManager {

    private static JasperPrint jspPrint;

    private static String outFileName;

    private static final String CONFIG_CATEGORY = "Reports";

    private static Path getReportsFolder() {
        return getDirectory(CONFIG_CATEGORY, "reportFolder");
    }

    private static Path getOutputFolder() {
        return getDirectory(CONFIG_CATEGORY, "outputFolder");
    }

    private static PrintRequestAttributeSet getPageFormatFromReport(JasperPrint jpsPrint) {
        PrintRequestAttributeSet result = new HashPrintRequestAttributeSet();
        result.add(jpsPrint.getOrientationValue() == OrientationEnum.PORTRAIT
                   ? OrientationRequested.PORTRAIT
                   : OrientationRequested.LANDSCAPE);
        result.add(Math.max(jpsPrint.getPageWidth(), jpsPrint.getPageHeight()) <= 600
                   ? MediaSizeName.ISO_A5
                   : MediaSizeName.ISO_A4);
        return result;
    }

    public static void initInvoicePrint(Collection<ShoppingItem> shoppingCart, Purchase purchase) throws JRException {
        JasperDesign jspDesign = JRXmlLoader.load(
                getReportsFolder().resolve(getPath(CONFIG_CATEGORY, "invoiceFileName")).toAbsolutePath().toFile());
        JasperReport jspReport = JasperCompileManager.compileReport(jspDesign);
        Map<String,Object> reportParams = getInvoiceParams(purchase);
        JRDataSource dataSource = new JRBeanCollectionDataSource(shoppingCart);
        jspPrint = JasperFillManager.fillReport(jspReport, reportParams, dataSource);
        outFileName = String.format("%d_%s_%s_%s.pdf", purchase.getSid(),
                                    purchase.getSession().getCustomer().getFirstName(),
                                    purchase.getSession().getCustomer().getSurname(),
                                    purchase.getCreateDate().toString());
    }

    public void sendToPrinter() throws JRException {
        JRPrintServiceExporter printExporter = new JRPrintServiceExporter();
        printExporter.setExporterInput(new SimpleExporterInput(jspPrint));
        PrintRequestAttributeSet requestAttributeSet = getPageFormatFromReport(jspPrint);
        SimplePrintServiceExporterConfiguration printConfig = new SimplePrintServiceExporterConfiguration();
        printConfig.setPrintRequestAttributeSet(requestAttributeSet);
        printConfig.setDisplayPrintDialog(true);
        printExporter.setConfiguration(printConfig);
        printExporter.exportReport();
    }

    public void exportPdf() throws JRException {
        JasperExportManager.exportReportToPdfFile(jspPrint, getOutputFolder().resolve(
                outFileName.replaceAll("[\\\\/:*?\"<>|]", "_")).toAbsolutePath().toString());
    }

    @NotNull
    private static Map<String,Object> getInvoiceParams(Purchase purchase) {
        Map<String,Object> reportParams = new HashMap<>();
        reportParams.put("BonNo", purchase.getSid());
        reportParams.put("Customer", purchase.getSession().getCustomer().getFullName());
        reportParams.put("Seller", purchase.getSession().getSeller().getFullName());
        reportParams.put("Credit", purchase.getSession().getCustomer().getUserGroup().getValue());
        reportParams.put("PurchaseDate", purchase.getCreateDate());
        return reportParams;
    }
}
