package kernbeisser.Reports;

import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.ShoppingItem;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static kernbeisser.Config.ConfigManager.getDirectory;
import static kernbeisser.Config.ConfigManager.getPath;

public class ReportUtil {
    private static final String CONFIG_CATEGORY = "Reports";

    private static Path getReportsFolder() {
        return getDirectory(CONFIG_CATEGORY, "reportFolder");
    }

    private static Path getOutputFolder() {
        return getDirectory(CONFIG_CATEGORY, "outputFolder");
    }

    public static void exportInvoicePDF(Collection<ShoppingItem> shoppingCart, Purchase purchase) throws JRException {
        String outFileName = String.format("%d_%s_%s_%s.pdf", purchase.getSid(),
                                           purchase.getSession().getCustomer().getFirstName(),
                                           purchase.getSession().getCustomer().getSurname(),
                                           purchase.getCreateDate().toString()).replaceAll("[\\\\/:*?\"<>|]", "_");
        JasperDesign jspDesign = JRXmlLoader.load(
                getReportsFolder().resolve(getPath(CONFIG_CATEGORY, "invoiceFileName")).toAbsolutePath().toFile());
        JasperReport jspReport = JasperCompileManager.compileReport(jspDesign);
        Map<String,Object> reportParams = getInvoiceParams(purchase);
        JRDataSource dataSource = new JRBeanCollectionDataSource(shoppingCart);
        JasperPrint jspPrint = JasperFillManager.fillReport(jspReport, reportParams, dataSource);
        JasperExportManager.exportReportToPdfFile(jspPrint,
                                                  getOutputFolder().resolve(outFileName).toAbsolutePath().toString());
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
