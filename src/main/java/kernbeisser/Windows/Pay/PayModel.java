package kernbeisser.Windows.Pay;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Price.PriceCalculator;
import kernbeisser.Windows.Model;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.nio.file.Paths;
import java.util.*;

import static net.sf.jasperreports.engine.JasperCompileManager.compileReport;

class PayModel implements Model {
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

    int shoppingCartSum() {
        return shoppingCart.stream()
                           .mapToInt(e -> PriceCalculator.getShoppingItemPrice(e, saleSession.getCustomer()
                                                                                             .getSolidaritySurcharge()))
                           .sum();
    }

    boolean pay(SaleSession saleSession, Collection<ShoppingItem> items, int sum) {
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
                e.setPurchase(purchase);
                em.persist(e);
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
            String basePath = "/home/timo/JaspersoftWorkspace/MyReports";
            JasperDesign jspDesign = JRXmlLoader.load(
                    Paths.get(basePath, "Blank_A4.jrxml").toFile());
            JasperReport jspReport = compileReport(jspDesign);
            Map<String,Object> reportParamMap = new HashMap<>();
            reportParamMap.put("BonNo", 47);
            List<String> amounts = new ArrayList<String>();
            amounts.add("1x");
            amounts.add("2x");
            reportParamMap.put("ItemAmount", amounts);
            JasperPrint jspPrint = JasperFillManager.fillReport(jspReport, reportParamMap);
            JRSaver.saveObject(jspPrint, Paths.get(basePath, "Blank_A4.jrprint").toFile());
//            JasperPrintManager.printReport(jspPrint, false);
        } catch (JRException e) {
            e.printStackTrace();
        }
        try {
            //Creates new PrinterJob
            PrinterJob p = PrinterJob.getPrinterJob();

            //Sets selected PrintService
            p.setPrintService(printService);


        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }

    PrintService getDefaultPrinter() {
        return PrintServiceLookup.lookupDefaultPrintService();
    }
}
