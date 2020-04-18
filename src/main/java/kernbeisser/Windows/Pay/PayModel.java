package kernbeisser.Windows.Pay;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Model;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PayModel implements Model<PayController> {
    private final SaleSession saleSession;
    private final Collection<ShoppingItem> shoppingCart;
    private final Runnable transferCompleted;

    PayModel(SaleSession saleSession, Collection<ShoppingItem> shoppingCart, Runnable transferCompleted) {
        this.shoppingCart = shoppingCart;
        this.saleSession = saleSession;
        this.transferCompleted = transferCompleted;
    }

    Collection<ShoppingItem> getShoppingCart() {
        return shoppingCart;
    }

    SaleSession getSaleSession() {
        return saleSession;
    }

    double shoppingCartSum() {
        return shoppingCart.stream()
                           .mapToDouble(ShoppingItem::getRetailPrice)
                           .sum() * (1+saleSession.getCustomer().getSolidaritySurcharge());
    }

    boolean pay(SaleSession saleSession, Collection<ShoppingItem> items, double sum) {
        //Build connection by default
        EntityManager em = DBConnection.getEntityManager();

        //Start transaction
        EntityTransaction et = em.getTransaction();
        et.begin();

        try {
            //Create saleSession if not exists
            SaleSession db = em.find(SaleSession.class, saleSession.getId());
            if (db == null) {
                em.persist(saleSession);
                db = saleSession;
            }

            //Save ShoppingItems in Purchase
            Purchase purchase = new Purchase();
            purchase.setSession(db);
            em.persist(purchase);
            items.forEach(e -> {
                ShoppingItem shoppingItem = Tools.removeLambda(e,ShoppingItem::new);
                shoppingItem.setPurchase(purchase);
                em.persist(shoppingItem);
            });

            //Change value from UserGroup
            UserGroup userGroup = em.find(UserGroup.class, db.getCustomer().getUserGroup().getId());
            userGroup.setValue(userGroup.getValue() - sum);
            em.persist(userGroup);

            //Persist changes
            et.commit();

            //Close EntityManager
            em.close();

            //Success
            return true;
        } catch (PersistenceException e) {
            e.printStackTrace();

            //Undo transaction
            et.rollback();

            //Close EntityManager
            em.close();

            //Failed
            return false;
        }
    }

    void runTransferCompleted() {
        transferCompleted.run();
    }

    PrintService[] getAllPrinters() {
        return PrintServiceLookup.lookupPrintServices(null, null);
    }

    void print(PrintService printService) {
        try {
            String basePath = "/home/timos/JaspersoftWorkspace/MyReports";
            JasperDesign jspDesign = JRXmlLoader.load(
                    Paths.get(basePath, "Blank_A4.jrxml").toFile());
            JasperReport jspReport = JasperCompileManager.compileReport(jspDesign);

            Map<String,Object> reportParamMap = new HashMap<>();
            reportParamMap.put("BonNo", 47);

            JRDataSource dataSource = new JRBeanCollectionDataSource(shoppingCart);

            JasperPrint jspPrint = JasperFillManager.fillReport(jspReport, reportParamMap, dataSource);
            JRSaver.saveObject(jspPrint, Paths.get(basePath, "Blank_A4.jrprint").toFile());
//            JasperPrintManager.printReport(jspPrint, false);
//            JRPdfExporter pdfExporter = new JRPdfExporter();
            JasperExportManager.exportReportToPdfFile(jspPrint, Paths.get(basePath, "report.pdf").toString());
        } catch (JRException e) {
            e.printStackTrace();
        }
//        try {
//            //Creates new PrinterJob
//            PrinterJob p = PrinterJob.getPrinterJob();
//
//            //Sets selected PrintService
//            p.setPrintService(printService);
//
//
//        } catch (PrinterException e) {
//            e.printStackTrace();
//        }
    }

    PrintService getDefaultPrinter() {
        return PrintServiceLookup.lookupDefaultPrintService();
    }
}
