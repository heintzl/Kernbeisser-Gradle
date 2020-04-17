package kernbeisser.Reporting;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import javax.print.PrintService;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PrintHandler {
    String basePath = "reports";
    JasperDesign jspDesign;
    JasperReport jspReport;
    JRDataSource dataSource;
    JasperPrint jspPrint;

    public PrintHandler (String jspDesignName, Map<String,Object> reportParamMap, Collection<?> sourceCollection) {
        try {
            jspDesign = JRXmlLoader.load(
                    Paths.get(basePath, jspDesignName + ".jrxml").toFile());
            jspReport = JasperCompileManager.compileReport(jspDesign);
            dataSource = new JRBeanCollectionDataSource(sourceCollection);
            jspPrint = JasperFillManager.fillReport(jspReport, reportParamMap, dataSource);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public void exportToPdf() {
        try {
            JRSaver.saveObject(jspPrint, Paths.get(basePath, "Blank_A4.jrprint").toFile());
//            JasperPrintManager.printReport(jspPrint, false);
//            JRPdfExporter pdfExporter = new JRPdfExporter();
            JasperExportManager.exportReportToPdfFile(jspPrint, Paths.get(basePath, "report.pdf").toString());
        } catch (JRException e) {
            e.printStackTrace();
        } finally {
            return;
        }
    }
}
