package kernbeisser.Windows.Pay;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Reporting.PrintHandler;
import kernbeisser.Windows.Model;
import net.sf.jasperreports.engine.JRException;

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
        Map<String,Object> receiptParamMap = new HashMap<>();
        receiptParamMap.put("BonNo", 47);
        PrintHandler printHandler = new PrintHandler("Kerni_Rechnung", receiptParamMap, shoppingCart);
        printHandler.exportToPdf();
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
